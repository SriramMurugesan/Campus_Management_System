# Campus Student Management System --- Day 10 Complete Notes

## Java Full Stack Training --- JPA, Hibernate, ORM, Entity Relationships, JPQL and Transactions

This document is the complete Day 10 learning material for the **Campus
Student Management System**.

It contains:

-   Complete Day 10 project code
-   JPA and Hibernate theory
-   ORM concepts
-   Entity concepts and entity types
-   Primary keys and key-generation strategies
-   Entity relationships
-   Owning side and inverse side
-   `@JoinColumn`
-   Fetch types
-   Cascade types
-   Orphan removal
-   Entity lifecycle
-   Persistence Context
-   JPQL
-   Transactions
-   Dirty checking
-   First-level cache
-   `persist`, `find`, `merge`, `remove`, `detach`
-   SQL vs JPQL
-   Common interview questions
-   Viva questions
-   Common mistakes
-   Debugging checklist
-   Suggested classroom explanation

------------------------------------------------------------------------

# 1. Day 10 Goal

Day 9 introduced:

``` text
Java Object
     ↓
JPA
     ↓
Hibernate
     ↓
PostgreSQL
```

Day 10 extends the project from one entity to multiple related entities:

``` text
Department
     |
     | 1
     |
     | *
   Student
```

The final architecture is:

``` text
Browser
   ↓
Servlet / Controller
   ↓
Service
   ↓
DAO
   ↓
JPA
   ↓
Hibernate
   ↓
JDBC
   ↓
PostgreSQL
```

------------------------------------------------------------------------

# 2. What We Are Building

The application manages:

-   Students
-   Departments

Example:

``` text
CSE
 ├── Arun
 ├── Karthik
 └── Ravi

ECE
 ├── Priya
 └── Divya

IT
 └── Manoj
```

This represents:

``` text
One Department → Many Students
Many Students → One Department
```

Therefore:

``` java
@OneToMany
```

and

``` java
@ManyToOne
```

are used.

------------------------------------------------------------------------

# 3. Project Structure

``` text
campus-student-management/
│
├── pom.xml
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── campus/
        │           │
        │           ├── model/
        │           │   ├── Student.java
        │           │   └── Department.java
        │           │
        │           ├── dao/
        │           │   ├── StudentJPADAO.java
        │           │   └── DepartmentJPADAO.java
        │           │
        │           ├── service/
        │           │   └── StudentService.java
        │           │
        │           ├── controller/
        │           │   └── StudentServlet.java
        │           │
        │           └── util/
        │               └── JPAUtil.java
        │
        ├── resources/
        │   └── META-INF/
        │       └── persistence.xml
        │
        └── webapp/
            ├── index.html
            ├── student.html
            ├── students.jsp
            └── edit-student.jsp
```

------------------------------------------------------------------------

# 4. Database Setup

Create database:

``` sql
CREATE DATABASE campus_db;
```

Connect:

``` sql
\c campus_db
```

## Department table

``` sql
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

Insert:

``` sql
INSERT INTO departments (name)
VALUES
('CSE'),
('ECE'),
('IT');
```

## Student table

``` sql
CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    department_id INT,

    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);
```

Insert sample data:

``` sql
INSERT INTO students (name, age, department_id)
VALUES
('Arun', 21, 1),
('Priya', 20, 2),
('Karthik', 22, 1);
```

Test:

``` sql
SELECT * FROM departments;

SELECT * FROM students;
```

Relationship query:

``` sql
SELECT
    s.id,
    s.name,
    s.age,
    d.name AS department
FROM students s
JOIN departments d
ON s.department_id = d.id;
```

------------------------------------------------------------------------

# 5. What Is ORM?

ORM means:

**Object Relational Mapping**

It maps:

``` text
Java Object
      ↕
Database Row
```

Example:

Java:

``` java
Student student = new Student();
student.setName("Arun");
student.setAge(21);
```

Database:

``` text
students
--------------------------------
id | name | age | department_id
--------------------------------
1  | Arun | 21  | 1
```

ORM connects the two worlds.

------------------------------------------------------------------------

# 6. Why ORM?

Without ORM, we commonly write:

``` java
Connection
PreparedStatement
ResultSet
SQL
```

Example:

``` java
PreparedStatement ps =
    connection.prepareStatement(
        "SELECT * FROM students"
    );
```

With JPA/Hibernate:

``` java
Student student =
    em.find(Student.class, 1);
```

Hibernate handles much of the SQL generation and object mapping.

------------------------------------------------------------------------

# 7. JPA vs Hibernate

This is a very common interview question.

## JPA

JPA means:

**Jakarta Persistence API**

It is a specification/API.

It defines concepts such as:

``` text
@Entity
@Id
@OneToMany
@ManyToOne
EntityManager
```

## Hibernate

Hibernate is an implementation/framework that implements the JPA
specification.

Simple analogy:

``` text
JPA       = Rules / Specification
Hibernate = Actual Implementation
```

Another analogy:

``` text
JPA       = Interface
Hibernate = Implementation
```

Important:

> JPA is not the database.

> JPA is not Hibernate.

> Hibernate is one implementation of JPA.

------------------------------------------------------------------------

# 8. Complete Application Architecture

``` text
                  Browser
                     |
                     v
              StudentServlet
                     |
                     v
              StudentService
                     |
                     v
               StudentDAO
                     |
                     v
              EntityManager
                     |
                     v
                   JPA
                     |
                     v
                 Hibernate
                     |
                     v
                  JDBC
                     |
                     v
                PostgreSQL
```

------------------------------------------------------------------------

# 9. pom.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="
         http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.campus</groupId>
    <artifactId>campus-student-management</artifactId>
    <version>1.0</version>

    <packaging>war</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>

        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>jakarta.servlet.jsp.jstl</groupId>
            <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
            <version>3.0.0</version>
        </dependency>

        <dependency>
            <groupId>org.glassfish.web</groupId>
            <artifactId>jakarta.servlet.jsp.jstl</artifactId>
            <version>3.0.1</version>
        </dependency>

        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.6.5.Final</version>
        </dependency>

        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.4</version>
        </dependency>

    </dependencies>

    <build>

        <finalName>campus-student-management</finalName>

        <plugins>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>

        </plugins>

    </build>

</project>
```

------------------------------------------------------------------------

# 10. Student Entity

``` java
package com.campus.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int age;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Student() {
    }

    public Student(String name, int age, Department department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
```

------------------------------------------------------------------------

# 11. Department Entity

``` java
package com.campus.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Student> students = new ArrayList<>();

    public Department() {
    }

    public Department(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
```

------------------------------------------------------------------------

# 12. Understanding @Entity

``` java
@Entity
```

means:

> This Java class is a persistent entity managed by JPA.

Example:

``` java
@Entity
public class Student {
}
```

JPA treats Student as a persistent class.

Usually an entity maps to a database table.

``` text
Student class
      ↓
students table
```

------------------------------------------------------------------------

# 13. What Is a Persistent Entity?

A persistent entity is an entity whose state can be stored in the
database through a persistence mechanism such as JPA/Hibernate.

Example:

``` java
Student student =
    em.find(Student.class, 1);
```

The Student object represents persistent database data.

------------------------------------------------------------------------

# 14. Entity Requirements

A JPA entity normally needs:

1.  `@Entity`
2.  An identifier using `@Id`
3.  A no-argument constructor
4.  A class that is suitable for JPA enhancement/proxying
5.  Persistent fields/properties accessible to JPA

Example:

``` java
@Entity
public class Student {

    @Id
    private int id;

    public Student() {
    }
}
```

The no-argument constructor is important because JPA needs to
instantiate entities.

------------------------------------------------------------------------

# 15. Entity Types / Entity Concepts

There are several useful ways to classify entities.

## 15.1 Entity

Normal persistent entity:

``` java
@Entity
public class Student {
}
```

Mapped to a table.

## 15.2 Root Entity

An entity that acts as the main entry point of an aggregate/domain
structure.

For example:

``` text
Department
    |
    └── Students
```

The exact domain meaning depends on application design.

## 15.3 Embeddable Type

Not an independent entity.

Example:

``` java
@Embeddable
public class Address {

    private String city;
    private String state;
}
```

Used inside another entity:

``` java
@Entity
public class Student {

    @Embedded
    private Address address;
}
```

An `@Embeddable` object does not normally have its own identity/table.

## 15.4 Mapped Superclass

Used to share persistent fields among entities.

``` java
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
}
```

Then:

``` java
@Entity
public class Student extends BaseEntity {
}
```

The superclass itself is not an entity table.

------------------------------------------------------------------------

# 16. Entity Identity

Every entity needs an identity.

JPA uses:

``` java
@Id
```

Example:

``` java
@Id
private int id;
```

This corresponds conceptually to:

``` sql
PRIMARY KEY
```

------------------------------------------------------------------------

# 17. Types of Database Keys

This is important for interviews.

## Primary Key

Uniquely identifies a row.

``` sql
id SERIAL PRIMARY KEY
```

Example:

``` text
1 → Arun
2 → Priya
3 → Karthik
```

## Foreign Key

References another table's primary key.

``` sql
department_id INT
```

references:

``` sql
departments(id)
```

## Candidate Key

A column or set of columns that can uniquely identify a row.

Example:

``` text
student_id
email
```

If both are unique, both can be candidate keys.

## Alternate Key

A candidate key that was not selected as the primary key.

Example:

``` text
id       → Primary Key
email    → Alternate Key
```

## Composite Key

A key made from multiple columns.

Example:

``` text
student_id + course_id
```

Useful for an enrollment table:

``` text
student_id | course_id
-----------+----------
1          | 101
1          | 102
2          | 101
```

In JPA, composite keys can be represented using:

``` java
@EmbeddedId
```

or:

``` java
@IdClass
```

------------------------------------------------------------------------

# 18. @GeneratedValue

``` java
@GeneratedValue
```

tells JPA how the identifier should be generated.

Common strategies:

``` text
IDENTITY
SEQUENCE
TABLE
AUTO
```

## IDENTITY

Database generates the ID.

``` java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

PostgreSQL example:

``` sql
id SERIAL
```

## SEQUENCE

Uses a database sequence.

``` java
@GeneratedValue(strategy = GenerationType.SEQUENCE)
```

## TABLE

Uses a database table to manage generated identifiers.

Less commonly used in modern applications.

## AUTO

Provider chooses a suitable strategy.

``` java
@GeneratedValue(strategy = GenerationType.AUTO)
```

------------------------------------------------------------------------

# 19. Why We Use IDENTITY Here

Our PostgreSQL table uses:

``` sql
id SERIAL PRIMARY KEY
```

So:

``` java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

is a natural match.

------------------------------------------------------------------------

# 20. @Table

``` java
@Table(name = "students")
```

maps the entity to a particular table.

Without explicit `@Table`, JPA can use its naming/default rules.

With it:

``` java
@Entity
@Table(name = "students")
public class Student {
}
```

we clearly specify the table.

------------------------------------------------------------------------

# 21. @Column

We can explicitly map a field to a column:

``` java
@Column(name = "student_name")
private String name;
```

Other useful options:

``` java
@Column(
    name = "student_name",
    nullable = false,
    unique = true,
    length = 100
)
private String name;
```

------------------------------------------------------------------------

# 22. Relationships in JPA

There are four main relationship types:

``` text
@OneToOne
@OneToMany
@ManyToOne
@ManyToMany
```

------------------------------------------------------------------------

# 23. One-to-One

One person has one passport.

``` text
Person 1 ───── 1 Passport
```

Example:

``` java
@OneToOne
private Passport passport;
```

------------------------------------------------------------------------

# 24. One-to-Many

One department has many students.

``` text
Department 1 ───── * Student
```

Example:

``` java
@OneToMany
private List<Student> students;
```

------------------------------------------------------------------------

# 25. Many-to-One

Many students belong to one department.

``` text
Student * ───── 1 Department
```

Example:

``` java
@ManyToOne
private Department department;
```

This is the relationship used in our project.

------------------------------------------------------------------------

# 26. Many-to-Many

Many students can take many courses.

``` text
Student * ───── * Course
```

Example:

``` java
@ManyToMany
private List<Course> courses;
```

Usually a join table is required:

``` text
student_course
----------------------
student_id
course_id
```

------------------------------------------------------------------------

# 27. Why @ManyToOne Is on Student

Our database contains:

``` text
students
-------------------------
id
name
age
department_id
```

The foreign key is:

``` text
department_id
```

It exists in the Student table.

Therefore Student is the owning side.

``` java
@ManyToOne
@JoinColumn(name = "department_id")
private Department department;
```

------------------------------------------------------------------------

# 28. Owning Side vs Inverse Side

This is an important interview topic.

Student:

``` java
@ManyToOne
@JoinColumn(name = "department_id")
private Department department;
```

Student owns the relationship.

Department:

``` java
@OneToMany(mappedBy = "department")
private List<Student> students;
```

Department is the inverse/non-owning side.

Why?

Because:

``` text
Student
    ↓
department_id
```

contains the actual foreign-key mapping.

------------------------------------------------------------------------

# 29. What Does mappedBy Mean?

``` java
@OneToMany(mappedBy = "department")
```

means:

> The relationship is already mapped by the `department` field inside
> Student.

It prevents JPA from creating another unnecessary relationship mapping.

`mappedBy` points to the **Java field name**, not the database column
name.

Correct:

``` java
mappedBy = "department"
```

Not:

``` java
mappedBy = "department_id"
```

------------------------------------------------------------------------

# 30. @JoinColumn

``` java
@JoinColumn(name = "department_id")
```

tells JPA which database column joins the entities.

Conceptually:

``` text
Student.department
       ↓
students.department_id
       ↓
departments.id
```

------------------------------------------------------------------------

# 31. Fetch Types

Fetch type determines when related data is loaded.

Two major values:

``` text
EAGER
LAZY
```

## EAGER

Load the related data immediately.

Conceptually:

``` text
Load Student
    ↓
Immediately load Department
```

## LAZY

Load related data only when needed.

Conceptually:

``` text
Load Student
    ↓
Department not necessarily loaded yet
    ↓
Access student.getDepartment()
    ↓
Hibernate loads it if the persistence context allows it
```

------------------------------------------------------------------------

# 32. Default Fetch Types

A useful interview fact:

``` text
@ManyToOne → EAGER by default
@OneToOne  → EAGER by default

@OneToMany → LAZY by default
@ManyToMany → LAZY by default
```

In real applications, developers often deliberately choose fetch
strategies to avoid unnecessary database work.

------------------------------------------------------------------------

# 33. Setting Fetch Type

Example:

``` java
@ManyToOne(fetch = FetchType.LAZY)
private Department department;
```

or:

``` java
@OneToMany(fetch = FetchType.LAZY)
private List<Student> students;
```

------------------------------------------------------------------------

# 34. Cascade Types

Cascade determines which operations should propagate from one entity to
related entities.

Common cascade options:

``` text
PERSIST
MERGE
REMOVE
REFRESH
DETACH
ALL
```

Example:

``` java
@OneToMany(
    mappedBy = "department",
    cascade = CascadeType.ALL
)
private List<Student> students;
```

This means certain operations on Department can cascade to its Students.

------------------------------------------------------------------------

# 35. CascadeType.PERSIST

If Department is persisted:

``` java
em.persist(department);
```

the persist operation can cascade to its related students.

------------------------------------------------------------------------

# 36. CascadeType.MERGE

Merge operation can cascade to related entities.

``` java
em.merge(department);
```

------------------------------------------------------------------------

# 37. CascadeType.REMOVE

Deleting the parent can propagate removal to related entities.

Be careful.

If:

``` text
Department
   |
   ├── Student 1
   ├── Student 2
   └── Student 3
```

and REMOVE cascades, deleting the Department can delete the students.

This is why cascade configuration must be chosen carefully.

------------------------------------------------------------------------

# 38. CascadeType.ALL

Equivalent conceptually to:

``` text
PERSIST
MERGE
REMOVE
REFRESH
DETACH
```

Use it only when the lifecycle relationship actually requires it.

Do not blindly put:

``` java
cascade = CascadeType.ALL
```

on every relationship.

------------------------------------------------------------------------

# 39. orphanRemoval

Example:

``` java
@OneToMany(
    mappedBy = "department",
    orphanRemoval = true
)
private List<Student> students;
```

Orphan removal means that when a child is removed from the parent's
managed relationship, JPA can remove that child from the database.

Example:

``` text
Department
   |
   ├── Arun
   ├── Priya
   └── Karthik
```

If Priya is removed from the collection and orphan removal applies,
Priya may be deleted from the database.

Important:

> `orphanRemoval` and `CascadeType.REMOVE` are related concepts but are
> not identical.

------------------------------------------------------------------------

# 40. Entity Lifecycle

An entity can move through different states.

Main states:

``` text
Transient
Managed
Detached
Removed
```

------------------------------------------------------------------------

# 41. Transient

A newly created Java object that is not managed by JPA.

``` java
Student student =
    new Student();
```

At this point:

``` text
Java object
     ↓
Transient
```

It is not yet associated with the persistence context.

------------------------------------------------------------------------

# 42. Managed

After:

``` java
em.persist(student);
```

the entity becomes managed.

``` text
Student
   ↓
EntityManager
   ↓
Persistence Context
   ↓
Managed
```

Hibernate tracks it.

------------------------------------------------------------------------

# 43. Detached

If the entity is no longer associated with the current persistence
context, it becomes detached.

Example:

``` java
em.close();
```

The entity object may still exist in Java, but the EntityManager no
longer manages it.

------------------------------------------------------------------------

# 44. Removed

After:

``` java
em.remove(student);
```

the entity is marked for removal.

The actual database DELETE is synchronized with the database during the
transaction/flush process.

------------------------------------------------------------------------

# 45. Entity Lifecycle Diagram

``` text
             new Student()
                  |
                  v
             TRANSIENT
                  |
             em.persist()
                  |
                  v
              MANAGED
             /       \
            /         \
       em.detach()    em.close()
          |               |
          v               v
      DETACHED         DETACHED
          |
      em.merge()
          |
          v
       MANAGED

MANAGED
   |
em.remove()
   |
   v
REMOVED
```

------------------------------------------------------------------------

# 46. EntityManager

`EntityManager` is one of the most important JPA interfaces.

It provides operations such as:

``` java
persist()
find()
merge()
remove()
detach()
```

------------------------------------------------------------------------

# 47. persist()

Used to make a new entity persistent/managed.

``` java
em.persist(student);
```

Main use:

``` text
CREATE
```

Conceptually:

``` text
Transient object
      ↓
persist()
      ↓
Managed entity
      ↓
INSERT
```

------------------------------------------------------------------------

# 48. find()

Used to retrieve an entity by primary key.

``` java
Student student =
    em.find(Student.class, 1);
```

Conceptually:

``` text
ID = 1
 ↓
Database
 ↓
Student object
```

------------------------------------------------------------------------

# 49. merge()

Used to merge the state of a detached entity into the persistence
context.

``` java
em.merge(student);
```

Important interview point:

> `merge()` returns a managed instance.

For example:

``` java
Student managedStudent =
    em.merge(detachedStudent);
```

Do not assume the original detached object itself becomes managed.

------------------------------------------------------------------------

# 50. remove()

Used to mark a managed entity for deletion.

``` java
em.remove(student);
```

Usually:

``` java
Student student =
    em.find(Student.class, id);

em.remove(student);
```

------------------------------------------------------------------------

# 51. detach()

Removes an entity from the persistence context.

``` java
em.detach(student);
```

After detach:

``` text
Managed
   ↓
detach()
   ↓
Detached
```

------------------------------------------------------------------------

# 52. Persistence Context

Persistence Context is the environment in which JPA manages entity
instances.

Think of it as:

``` text
EntityManager
      |
      v
Persistence Context
      |
      +---- Student 1
      +---- Student 2
      +---- Department 1
```

Hibernate tracks managed entities here.

------------------------------------------------------------------------

# 53. First-Level Cache

The persistence context also acts as the JPA/Hibernate first-level
cache.

Example:

``` java
Student s1 =
    em.find(Student.class, 1);

Student s2 =
    em.find(Student.class, 1);
```

Within the same persistence context, JPA/Hibernate can reuse the managed
entity rather than necessarily performing the same database retrieval
again.

Important:

> First-level cache is associated with the persistence context /
> EntityManager.

------------------------------------------------------------------------

# 54. Dirty Checking

Dirty checking is an important Hibernate concept.

Suppose:

``` java
Student student =
    em.find(Student.class, 1);

student.setAge(25);
```

You may not explicitly call:

``` java
em.update(student);
```

JPA/Hibernate tracks changes to managed entities.

During flush, it can detect the changed state and generate an UPDATE.

Conceptually:

``` text
Database
   ↓
find()
   ↓
Managed Student
   ↓
setAge(25)
   ↓
Hibernate detects change
   ↓
UPDATE
```

This is called:

**Dirty Checking**

------------------------------------------------------------------------

# 55. Flush

Flush synchronizes changes in the persistence context with the database.

Conceptually:

``` text
Persistence Context
        ↓
      flush
        ↓
SQL statements
        ↓
Database
```

Flush does not mean the same thing as commit.

``` text
flush  = synchronize changes
commit = complete transaction
```

A commit normally causes required synchronization/flush as part of
transaction completion.

------------------------------------------------------------------------

# 56. Transactions

A transaction is a group of database operations treated as one logical
unit.

Basic structure:

``` java
EntityTransaction transaction =
        em.getTransaction();

try {

    transaction.begin();

    // database operations

    transaction.commit();

} catch (Exception e) {

    if (transaction.isActive()) {
        transaction.rollback();
    }

}
```

------------------------------------------------------------------------

# 57. ACID Properties

Transactions are commonly explained using ACID.

## Atomicity

All operations succeed or the transaction is rolled back.

``` text
All or Nothing
```

## Consistency

The database should move from one valid state to another valid state.

## Isolation

Concurrent transactions should not improperly interfere with each other.

## Durability

After successful commit, changes should persist.

------------------------------------------------------------------------

# 58. Transaction Example

Imagine:

``` text
Create Order
Reduce Stock
Record Payment
```

Without proper transaction handling:

``` text
Order created
Stock reduced
Payment failed
```

With transaction:

``` text
BEGIN

Create Order
Reduce Stock
Record Payment

If all succeed:
    COMMIT

If one fails:
    ROLLBACK
```

------------------------------------------------------------------------

# 59. StudentJPADAO.java

``` java
package com.campus.dao;

import com.campus.model.Student;
import com.campus.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class StudentJPADAO {

    public void addStudent(Student student) {

        EntityManager em =
                JPAUtil.getEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            em.persist(student);

            transaction.commit();

        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }


    public List<Student> getAllStudents() {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT s FROM Student s ORDER BY s.id",
                    Student.class
            ).getResultList();

        } finally {

            em.close();
        }
    }


    public Student getStudentById(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.find(
                    Student.class,
                    id
            );

        } finally {

            em.close();
        }
    }


    public void updateStudent(Student student) {

        EntityManager em =
                JPAUtil.getEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            em.merge(student);

            transaction.commit();

        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }


    public void deleteStudent(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        EntityTransaction transaction =
                em.getTransaction();

        try {

            transaction.begin();

            Student student =
                    em.find(
                            Student.class,
                            id
                    );

            if (student != null) {

                em.remove(student);
            }

            transaction.commit();

        } catch (Exception e) {

            if (transaction.isActive()) {
                transaction.rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }


    public List<Student> findStudentsByAge(
            int age) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT s FROM Student s " +
                    "WHERE s.age = :age",
                    Student.class
            )
            .setParameter("age", age)
            .getResultList();

        } finally {

            em.close();
        }
    }


    public List<Student> findStudentsByDepartment(
            String departmentName) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT s FROM Student s " +
                    "WHERE s.department.name = :dept",
                    Student.class
            )
            .setParameter(
                    "dept",
                    departmentName
            )
            .getResultList();

        } finally {

            em.close();
        }
    }


    public List<Student> searchByName(
            String name) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT s FROM Student s " +
                    "WHERE LOWER(s.name) " +
                    "LIKE LOWER(:name)",
                    Student.class
            )
            .setParameter(
                    "name",
                    "%" + name + "%"
            )
            .getResultList();

        } finally {

            em.close();
        }
    }


    public long getStudentCount() {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT COUNT(s) FROM Student s",
                    Long.class
            ).getSingleResult();

        } finally {

            em.close();
        }
    }
}
```

------------------------------------------------------------------------

# 60. DepartmentJPADAO.java

``` java
package com.campus.dao;

import com.campus.model.Department;
import com.campus.util.JPAUtil;

import jakarta.persistence.EntityManager;

import java.util.List;

public class DepartmentJPADAO {

    public List<Department> getAllDepartments() {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT d FROM Department d " +
                    "ORDER BY d.name",
                    Department.class
            ).getResultList();

        } finally {

            em.close();
        }
    }


    public Department getDepartmentById(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.find(
                    Department.class,
                    id
            );

        } finally {

            em.close();
        }
    }
}
```

------------------------------------------------------------------------

# 61. StudentService.java

``` java
package com.campus.service;

import com.campus.dao.DepartmentJPADAO;
import com.campus.dao.StudentJPADAO;
import com.campus.model.Department;
import com.campus.model.Student;

import java.util.List;

public class StudentService {

    private final StudentJPADAO studentDAO =
            new StudentJPADAO();

    private final DepartmentJPADAO departmentDAO =
            new DepartmentJPADAO();


    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }


    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }


    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }


    public Department getDepartmentById(int id) {
        return departmentDAO.getDepartmentById(id);
    }


    public void addStudent(Student student) {
        studentDAO.addStudent(student);
    }


    public void updateStudent(Student student) {
        studentDAO.updateStudent(student);
    }


    public void deleteStudent(int id) {
        studentDAO.deleteStudent(id);
    }


    public List<Student> findStudentsByDepartment(
            String departmentName) {

        return studentDAO.findStudentsByDepartment(
                departmentName
        );
    }


    public List<Student> searchByName(
            String name) {

        return studentDAO.searchByName(name);
    }


    public long getStudentCount() {
        return studentDAO.getStudentCount();
    }
}
```

------------------------------------------------------------------------

# 62. StudentServlet.java

``` java
package com.campus.controller;

import com.campus.model.Department;
import com.campus.model.Student;
import com.campus.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private StudentService service;


    @Override
    public void init() {
        service = new StudentService();
    }


    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");


        if (action == null ||
                action.equals("list")) {

            List<Student> students =
                    service.getAllStudents();

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "students",
                    students
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.getRequestDispatcher(
                    "/students.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }


        if (action.equals("edit")) {

            int id = Integer.parseInt(
                    request.getParameter("id")
            );

            Student student =
                    service.getStudentById(id);

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "student",
                    student
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.getRequestDispatcher(
                    "/edit-student.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }


        if (action.equals("delete")) {

            int id = Integer.parseInt(
                    request.getParameter("id")
            );

            service.deleteStudent(id);

            response.sendRedirect(
                    request.getContextPath()
                    + "/students"
            );

            return;
        }


        if (action.equals("department")) {

            String department =
                    request.getParameter(
                            "department"
                    );

            List<Student> students =
                    service.findStudentsByDepartment(
                            department
                    );

            List<Department> departments =
                    service.getAllDepartments();

            request.setAttribute(
                    "students",
                    students
            );

            request.setAttribute(
                    "departments",
                    departments
            );

            request.setAttribute(
                    "selectedDepartment",
                    department
            );

            request.getRequestDispatcher(
                    "/students.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }


    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action =
                request.getParameter("action");


        if ("update".equals(action)) {

            int id =
                    Integer.parseInt(
                            request.getParameter("id")
                    );

            String name =
                    request.getParameter("name");

            int age =
                    Integer.parseInt(
                            request.getParameter("age")
                    );

            int departmentId =
                    Integer.parseInt(
                            request.getParameter(
                                    "departmentId"
                            )
                    );

            Department department =
                    service.getDepartmentById(
                            departmentId
                    );

            Student student =
                    new Student(
                            name,
                            age,
                            department
                    );

            student.setId(id);

            service.updateStudent(student);

            response.sendRedirect(
                    request.getContextPath()
                    + "/students"
            );

            return;
        }


        String name =
                request.getParameter("name");

        int age =
                Integer.parseInt(
                        request.getParameter("age")
                );

        int departmentId =
                Integer.parseInt(
                        request.getParameter(
                                "departmentId"
                        )
                );

        Department department =
                service.getDepartmentById(
                        departmentId
                );

        Student student =
                new Student(
                        name,
                        age,
                        department
                );

        service.addStudent(student);

        response.sendRedirect(
                request.getContextPath()
                + "/students"
        );
    }
}
```

------------------------------------------------------------------------

# 63. JPAUtil.java

``` java
package com.campus.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory(
                    "campusPU"
            );

    public static EntityManager getEntityManager() {

        return factory.createEntityManager();
    }

    public static void close() {

        factory.close();
    }
}
```

------------------------------------------------------------------------

# 64. persistence.xml

Location:

``` text
src/main/resources/META-INF/persistence.xml
```

``` xml
<?xml version="1.0" encoding="UTF-8"?>

<persistence
        xmlns="https://jakarta.ee/xml/ns/persistence"
        version="3.1">

    <persistence-unit
            name="campusPU"
            transaction-type="RESOURCE_LOCAL">

        <provider>
            org.hibernate.jpa.HibernatePersistenceProvider
        </provider>

        <class>com.campus.model.Student</class>
        <class>com.campus.model.Department</class>

        <properties>

            <property
                    name="jakarta.persistence.jdbc.driver"
                    value="org.postgresql.Driver"/>

            <property
                    name="jakarta.persistence.jdbc.url"
                    value="jdbc:postgresql://localhost:5432/campus_db"/>

            <property
                    name="jakarta.persistence.jdbc.user"
                    value="postgres"/>

            <property
                    name="jakarta.persistence.jdbc.password"
                    value="YOUR_PASSWORD"/>

            <property
                    name="hibernate.dialect"
                    value="org.hibernate.dialect.PostgreSQLDialect"/>

            <property
                    name="hibernate.show_sql"
                    value="true"/>

            <property
                    name="hibernate.format_sql"
                    value="true"/>

        </properties>

    </persistence-unit>

</persistence>
```

------------------------------------------------------------------------

# 65. students.jsp

``` jsp
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html>

<head>
    <title>Students</title>
</head>

<body>

<h1>Campus Students</h1>


<h2>Add Student</h2>

<form action="students" method="post">

    <label>Name:</label>

    <input
        type="text"
        name="name"
        required
    >

    <br><br>

    <label>Age:</label>

    <input
        type="number"
        name="age"
        required
    >

    <br><br>

    <label>Department:</label>

    <select name="departmentId" required>

        <option value="">
            Select Department
        </option>

        <c:forEach
            var="department"
            items="${departments}">

            <option value="${department.id}">
                ${department.name}
            </option>

        </c:forEach>

    </select>

    <br><br>

    <button type="submit">
        Add Student
    </button>

</form>


<hr>


<h2>Filter by Department</h2>

<form action="students" method="get">

    <input
        type="hidden"
        name="action"
        value="department"
    >

    <select name="department">

        <option value="">
            Select Department
        </option>

        <c:forEach
            var="department"
            items="${departments}">

            <option value="${department.name}">
                ${department.name}
            </option>

        </c:forEach>

    </select>

    <button type="submit">
        Filter
    </button>

</form>


<hr>


<h2>Student List</h2>

<table border="1" cellpadding="10">

    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Age</th>
        <th>Department</th>
        <th>Actions</th>
    </tr>


    <c:forEach
        var="student"
        items="${students}">

        <tr>

            <td>${student.id}</td>

            <td>${student.name}</td>

            <td>${student.age}</td>

            <td>
                ${student.department.name}
            </td>

            <td>

                <a href="students?action=edit&id=${student.id}">
                    Edit
                </a>

                |

                <a
                    href="students?action=delete&id=${student.id}"
                    onclick="return confirm('Delete this student?');"
                >
                    Delete
                </a>

            </td>

        </tr>

    </c:forEach>

</table>


<br>

<a href="index.html">
    Home
</a>

</body>

</html>
```

------------------------------------------------------------------------

# 66. edit-student.jsp

``` jsp
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html>

<head>
    <title>Edit Student</title>
</head>

<body>

<h1>Edit Student</h1>

<form action="students" method="post">

    <input
        type="hidden"
        name="action"
        value="update"
    >

    <input
        type="hidden"
        name="id"
        value="${student.id}"
    >

    <label>Name:</label>

    <input
        type="text"
        name="name"
        value="${student.name}"
        required
    >

    <br><br>

    <label>Age:</label>

    <input
        type="number"
        name="age"
        value="${student.age}"
        required
    >

    <br><br>

    <label>Department:</label>

    <select name="departmentId" required>

        <c:forEach
            var="department"
            items="${departments}">

            <option
                value="${department.id}"
                ${department.id == student.department.id
                    ? 'selected' : ''}>

                ${department.name}

            </option>

        </c:forEach>

    </select>

    <br><br>

    <button type="submit">
        Update Student
    </button>

</form>

<br>

<a href="students">
    Back to Students
</a>

</body>

</html>
```

------------------------------------------------------------------------

# 67. index.html

``` html
<!DOCTYPE html>
<html>

<head>
    <title>Campus Management System</title>
</head>

<body>

<h1>Campus Student Management System</h1>

<a href="students">
    View Students
</a>

</body>

</html>
```

------------------------------------------------------------------------

# 68. student.html

``` html
<!DOCTYPE html>
<html>

<head>
    <title>Add Student</title>
</head>

<body>

<h1>Add Student</h1>

<form action="students" method="post">

    <label>Name:</label>

    <input
        type="text"
        name="name"
        required
    >

    <br><br>

    <label>Age:</label>

    <input
        type="number"
        name="age"
        required
    >

    <br><br>

    <label>Department ID:</label>

    <input
        type="number"
        name="departmentId"
        required
    >

    <br><br>

    <button type="submit">
        Add Student
    </button>

</form>

<br>

<a href="students">
    Back to Students
</a>

</body>

</html>
```

------------------------------------------------------------------------

# 69. JPQL

JPQL means:

**Jakarta Persistence Query Language**

It is used to query entities.

Important difference:

``` text
SQL  → tables and columns
JPQL → entities and fields
```

------------------------------------------------------------------------

# 70. SQL vs JPQL

SQL:

``` sql
SELECT *
FROM students;
```

JPQL:

``` java
SELECT s
FROM Student s
```

SQL:

``` sql
SELECT *
FROM students
WHERE age > 20;
```

JPQL:

``` java
SELECT s
FROM Student s
WHERE s.age > 20
```

------------------------------------------------------------------------

# 71. Why SELECT s Instead of SELECT \*?

JPQL is object-oriented.

``` java
Student s
```

represents an entity object.

So:

``` java
SELECT s
FROM Student s
```

means:

> Select Student entity objects.

------------------------------------------------------------------------

# 72. Named Parameters

Example:

``` java
SELECT s
FROM Student s
WHERE s.age = :age
```

Then:

``` java
query.setParameter("age", 21);
```

Benefits:

-   Easier to read
-   Safer than string concatenation
-   Helps prevent SQL injection when used correctly
-   Reusable query structure

------------------------------------------------------------------------

# 73. Search by Department

``` java
SELECT s
FROM Student s
WHERE s.department.name = :dept
```

Parameter:

``` java
.setParameter("dept", "CSE")
```

This demonstrates navigating an entity relationship.

------------------------------------------------------------------------

# 74. JOIN in JPQL

``` java
SELECT s
FROM Student s
JOIN s.department d
WHERE d.name = :dept
```

Notice:

``` text
JOIN s.department
```

not:

``` text
JOIN departments
```

because JPQL works with entity relationships.

------------------------------------------------------------------------

# 75. LIKE

``` java
SELECT s
FROM Student s
WHERE LOWER(s.name)
LIKE LOWER(:name)
```

Parameter:

``` java
.setParameter(
    "name",
    "%arun%"
)
```

This can find names containing "arun", ignoring case.

------------------------------------------------------------------------

# 76. ORDER BY

``` java
SELECT s
FROM Student s
ORDER BY s.name
```

Descending:

``` java
SELECT s
FROM Student s
ORDER BY s.name DESC
```

------------------------------------------------------------------------

# 77. COUNT

``` java
SELECT COUNT(s)
FROM Student s
```

Result:

``` java
Long count
```

------------------------------------------------------------------------

# 78. Aggregate Functions

JPQL supports aggregate operations such as:

``` text
COUNT
SUM
AVG
MIN
MAX
```

Example:

``` java
SELECT AVG(s.age)
FROM Student s
```

------------------------------------------------------------------------

# 79. Persistence.xml vs application configuration

`persistence.xml` tells JPA/Hibernate things such as:

``` text
Persistence unit name
Provider
Database driver
Database URL
Username
Password
Dialect
Managed entity classes
```

The persistence unit:

``` xml
<persistence-unit name="campusPU">
```

is then referenced by:

``` java
Persistence.createEntityManagerFactory(
    "campusPU"
);
```

------------------------------------------------------------------------

# 80. EntityManagerFactory

Usually created once for an application/unit of work.

``` java
EntityManagerFactory factory =
    Persistence.createEntityManagerFactory(
        "campusPU"
    );
```

It creates EntityManagers:

``` java
EntityManager em =
    factory.createEntityManager();
```

Conceptually:

``` text
EntityManagerFactory
        |
        +---- EntityManager
        +---- EntityManager
        +---- EntityManager
```

------------------------------------------------------------------------

# 81. EntityManager Lifecycle

Typical pattern:

``` java
EntityManager em =
    factory.createEntityManager();

try {

    // use em

} finally {

    em.close();
}
```

Never keep an EntityManager open unnecessarily.

------------------------------------------------------------------------

# 82. EntityManagerFactory vs EntityManager

Interview question:

### EntityManagerFactory

-   Creates EntityManagers
-   Expensive to create
-   Generally long-lived

### EntityManager

-   Performs persistence operations
-   Usually short-lived
-   Associated with a persistence context
-   Not something to casually share across unrelated concurrent
    operations

Simple analogy:

``` text
Factory = Bank branch
EntityManager = Counter/service desk
```

------------------------------------------------------------------------

# 83. Lazy Loading

Suppose:

``` java
Student student =
    em.find(Student.class, 1);
```

and Department is configured LAZY.

The Department may not be loaded until needed:

``` java
student.getDepartment();
```

This can improve performance when related data is not always needed.

------------------------------------------------------------------------

# 84. N+1 Query Problem

Important interview concept.

Suppose we load:

``` text
100 students
```

and then access:

``` java
student.getDepartment()
```

for every student.

Depending on mappings and query strategy, this can result in many
additional queries.

Conceptually:

``` text
1 query → get students
100 queries → get departments
```

Total:

``` text
101 queries
```

This is called the **N+1 query problem**.

Possible solutions include:

-   Fetch joins
-   Entity graphs
-   Batch fetching
-   Appropriate query design

------------------------------------------------------------------------

# 85. Fetch Join

JPQL:

``` java
SELECT s
FROM Student s
JOIN FETCH s.department
```

This tells the provider to fetch the related Department as part of the
query.

It can help avoid certain N+1 patterns.

------------------------------------------------------------------------

# 86. `JOIN` vs `JOIN FETCH`

Normal:

``` java
JOIN
```

is primarily for joining/querying based on the relationship.

`JOIN FETCH` additionally asks the provider to fetch the associated
entity as part of the query result loading.

Example:

``` java
SELECT s
FROM Student s
JOIN FETCH s.department
```

------------------------------------------------------------------------

# 87. Database Relationship vs Java Relationship

Database:

``` text
students.department_id
        ↓
departments.id
```

Java:

``` java
student.department
```

JPA connects these two worlds.

``` text
Database relationship
        ↓
JPA mapping
        ↓
Java object relationship
```

------------------------------------------------------------------------

# 88. Foreign Key

A foreign key maintains a relationship between tables.

Student:

``` text
department_id
```

references:

``` text
departments.id
```

So:

``` text
students.department_id
        ↓
departments.id
```

This also helps enforce referential integrity.

------------------------------------------------------------------------

# 89. Referential Integrity

Suppose:

``` text
department_id = 999
```

but department 999 does not exist.

A properly configured foreign key can reject the invalid reference.

This keeps related data consistent.

------------------------------------------------------------------------

# 90. Why Use Service Layer?

Instead of:

``` text
Servlet → DAO
```

we use:

``` text
Servlet → Service → DAO
```

The service layer is useful for:

-   Business logic
-   Validation
-   Combining multiple DAO operations
-   Transaction boundaries in appropriate architectures
-   Keeping controllers thin

Example:

``` java
public void addStudent(Student student) {

    // validation/business rules

    studentDAO.addStudent(student);
}
```

------------------------------------------------------------------------

# 91. Why DAO?

DAO means:

**Data Access Object**

DAO handles database/persistence operations.

Example:

``` java
studentDAO.getAllStudents();
studentDAO.addStudent(student);
studentDAO.deleteStudent(id);
```

This keeps database code away from the servlet.

------------------------------------------------------------------------

# 92. MVC

Our project also follows MVC ideas.

``` text
Model
  ↓
Student
Department

View
  ↓
JSP

Controller
  ↓
StudentServlet
```

Additional service/DAO layers sit between controller and persistence.

Complete:

``` text
Browser
   ↓
Controller
   ↓
Service
   ↓
DAO
   ↓
JPA/Hibernate
   ↓
Database
```

------------------------------------------------------------------------

# 93. Complete CREATE Flow

``` text
User fills form
      ↓
POST /students
      ↓
StudentServlet
      ↓
Read parameters
      ↓
Find Department
      ↓
Create Student object
      ↓
StudentService
      ↓
StudentDAO
      ↓
begin transaction
      ↓
em.persist(student)
      ↓
Hibernate
      ↓
SQL INSERT
      ↓
PostgreSQL
      ↓
commit
      ↓
redirect
```

------------------------------------------------------------------------

# 94. Complete READ Flow

``` text
GET /students
      ↓
StudentServlet
      ↓
Service
      ↓
DAO
      ↓
JPQL
      ↓
Hibernate
      ↓
PostgreSQL
      ↓
List<Student>
      ↓
request.setAttribute()
      ↓
JSP
      ↓
EL/JSTL
      ↓
Browser
```

------------------------------------------------------------------------

# 95. Complete UPDATE Flow

``` text
Edit
 ↓
GET student by ID
 ↓
Display form
 ↓
POST updated data
 ↓
Create/update Student state
 ↓
Service
 ↓
DAO
 ↓
Transaction
 ↓
merge()
 ↓
Hibernate
 ↓
UPDATE
 ↓
commit
```

------------------------------------------------------------------------

# 96. Complete DELETE Flow

``` text
Delete
 ↓
Servlet
 ↓
Service
 ↓
DAO
 ↓
find()
 ↓
remove()
 ↓
DELETE
 ↓
commit
 ↓
redirect
```

------------------------------------------------------------------------

# 97. Common JPA Annotations

  Annotation            Purpose
  --------------------- ------------------------------
  `@Entity`             Marks persistent entity
  `@Table`              Maps entity to table
  `@Id`                 Primary key
  `@GeneratedValue`     ID generation
  `@Column`             Maps field to column
  `@Transient`          Field not persisted
  `@Embeddable`         Value/embedded type
  `@Embedded`           Embeds value type
  `@MappedSuperclass`   Shared persistent superclass
  `@OneToOne`           One-to-one relationship
  `@OneToMany`          One-to-many relationship
  `@ManyToOne`          Many-to-one relationship
  `@ManyToMany`         Many-to-many relationship
  `@JoinColumn`         Foreign-key join column
  `@JoinTable`          Join table mapping

------------------------------------------------------------------------

# 98. @Transient

If a field should not be stored in the database:

``` java
@Transient
private int temporaryValue;
```

JPA ignores it for persistence.

Do not confuse:

``` java
@Transient
```

with Java's `transient` keyword. They serve different purposes.

------------------------------------------------------------------------

# 99. Embedded Objects

Example:

``` java
@Embeddable
public class Address {

    private String city;
    private String state;
}
```

Entity:

``` java
@Entity
public class Student {

    @Id
    private int id;

    @Embedded
    private Address address;
}
```

The address fields can be stored as columns of the Student table rather
than requiring a separate Address entity/table.

------------------------------------------------------------------------

# 100. Composite Primary Key

Two common JPA approaches:

``` java
@EmbeddedId
```

and:

``` java
@IdClass
```

Example:

``` java
@Embeddable
public class EnrollmentId {

    private int studentId;
    private int courseId;
}
```

Then:

``` java
@EmbeddedId
private EnrollmentId id;
```

Use composite keys only when the domain/database design actually
requires them.

------------------------------------------------------------------------

# 101. Optimistic vs Pessimistic Locking

Advanced interview topic.

## Optimistic Locking

Assumes conflicts are relatively uncommon.

A version field can be used:

``` java
@Version
private long version;
```

If two transactions modify the same entity, version checking can detect
conflicting updates.

## Pessimistic Locking

Uses database locking to prevent conflicting access during a
transaction.

JPA supports lock modes such as:

``` text
PESSIMISTIC_READ
PESSIMISTIC_WRITE
```

For Day 10, understand the concept rather than implementing it.

------------------------------------------------------------------------

# 102. `@Version`

Example:

``` java
@Version
private long version;
```

Hibernate can use this for optimistic concurrency control.

Conceptually:

``` text
Version 1
   ↓
User A reads
User B reads
   ↓
User A updates → Version 2
User B tries update using Version 1
   ↓
Conflict detected
```

------------------------------------------------------------------------

# 103. SQL Injection

Bad:

``` java
String sql =
    "SELECT * FROM students WHERE name='"
    + name
    + "'";
```

This can be dangerous.

With JPQL:

``` java
SELECT s
FROM Student s
WHERE s.name = :name
```

and:

``` java
query.setParameter("name", name);
```

we use a parameterized query approach.

------------------------------------------------------------------------

# 104. Why Prepared/Parameterized Queries Matter

Never build database queries by blindly concatenating user input.

Prefer:

``` java
.setParameter(
    "name",
    name
)
```

This improves safety and query handling.

------------------------------------------------------------------------

# 105. JPA Does Not Mean No SQL

Important interview question:

> If we use JPA, is SQL completely gone?

Answer:

**No.**

JPA is an abstraction.

Hibernate still communicates with the relational database using SQL/JDBC
underneath.

Conceptually:

``` text
JPQL
 ↓
Hibernate
 ↓
SQL
 ↓
JDBC
 ↓
PostgreSQL
```

------------------------------------------------------------------------

# 106. Hibernate Is Not a Database

Hibernate does not replace PostgreSQL.

Hibernate is an ORM framework.

``` text
Hibernate → talks to → PostgreSQL
```

------------------------------------------------------------------------

# 107. JDBC vs JPA/Hibernate

## JDBC

You write more database interaction code:

``` java
Connection
PreparedStatement
ResultSet
```

## JPA/Hibernate

You work more with:

``` java
Entity
EntityManager
JPQL
Relationships
Persistence Context
```

Hibernate handles much of the SQL and object mapping.

------------------------------------------------------------------------

# 108. JPA CRUD Mapping

  Operation   JPA
  ----------- -----------------------------
  Create      `persist()`
  Read        `find()` / JPQL
  Update      managed changes / `merge()`
  Delete      `remove()`

Remember:

``` text
CREATE → persist
READ   → find/query
UPDATE → merge/managed state
DELETE → remove
```

------------------------------------------------------------------------

# 109. Interview Questions

## Beginner

### 1. What is JPA?

JPA is a Jakarta specification/API for object-relational persistence in
Java applications.

### 2. What is Hibernate?

Hibernate is an ORM framework and a popular implementation of JPA.

### 3. What is ORM?

ORM maps Java objects to relational database data.

### 4. What is an Entity?

A class managed by JPA for persistence.

### 5. What is @Entity?

It marks a class as a persistent entity.

### 6. What is @Id?

It marks the entity identifier/primary key.

### 7. What is @GeneratedValue?

It defines how the entity ID is generated.

### 8. What is EntityManager?

It provides APIs for managing persistent entities and interacting with
the persistence context.

### 9. What is persist()?

It makes a new entity managed/persistent.

### 10. What is find()?

It retrieves an entity by primary key.

### 11. What is merge()?

It merges detached state into the current persistence context and
returns a managed instance.

### 12. What is remove()?

It marks a managed entity for deletion.

### 13. What is JPQL?

An object-oriented query language for JPA entities.

### 14. What is a foreign key?

A column that references a key in another table.

### 15. What is a transaction?

A logical unit of database work that can be committed or rolled back.

------------------------------------------------------------------------

# 110. Intermediate Interview Questions

### 16. JPA vs Hibernate?

JPA is a specification; Hibernate is an implementation/framework that
supports JPA.

### 17. SQL vs JPQL?

SQL works with database tables/columns. JPQL works with entity classes
and entity attributes.

### 18. What is @ManyToOne?

It represents many entities related to one entity.

### 19. What is @OneToMany?

It represents one entity related to many entities.

### 20. What is mappedBy?

It identifies the field on the owning side that already defines the
relationship.

### 21. What is @JoinColumn?

It specifies the column used for a relationship join, commonly a foreign
key.

### 22. What is persistence context?

It is the set/environment in which EntityManager manages entity
instances.

### 23. What is dirty checking?

Hibernate detects changes to managed entities and synchronizes them with
the database during flush.

### 24. What is first-level cache?

The persistence context provides a first-level cache for managed
entities.

### 25. What is lazy loading?

Related data is loaded when needed rather than immediately.

### 26. What is eager loading?

Related data is loaded immediately according to the mapping/fetch
strategy.

### 27. What is cascade?

It propagates selected entity operations to related entities.

### 28. What is orphanRemoval?

It can cause an orphaned child entity to be removed from the database
when removed from the parent's managed relationship.

### 29. What is N+1?

A query pattern where one query loads a collection and additional
queries are executed for related data, potentially causing poor
performance.

### 30. How can N+1 be reduced?

Fetch joins, entity graphs, batch fetching, and appropriate query design
are common approaches.

------------------------------------------------------------------------

# 111. Advanced Interview Questions

### 31. Difference between persist and merge?

`persist()` is normally used for a new entity and makes that instance
managed.

`merge()` copies state into a managed instance and returns that managed
instance. It is commonly used when working with detached entities.

### 32. Does merge make the original object managed?

Not necessarily.

The returned object is the managed instance:

``` java
Student managed =
    em.merge(student);
```

### 33. What is dirty checking?

Hibernate tracks managed entity state and detects changes during flush.

### 34. Does flush mean commit?

No.

``` text
flush  = synchronize persistence-context changes with DB
commit = successfully complete transaction
```

### 35. What is the owning side?

The side responsible for maintaining the relationship
mapping/foreign-key association.

In our project:

``` java
Student.department
```

is the owning side.

### 36. Why is Student the owning side?

Because the Student table contains:

``` text
department_id
```

the foreign key.

### 37. What is the default fetch type of @ManyToOne?

EAGER by default.

### 38. What is the default fetch type of @OneToMany?

LAZY by default.

### 39. What is a composite key?

A primary key made from multiple columns/attributes.

### 40. What is optimistic locking?

A concurrency strategy that detects conflicting updates, commonly using
`@Version`.

------------------------------------------------------------------------

# 112. Viva Questions for Students

Ask these verbally:

1.  What does ORM stand for?
2.  What is JPA?
3.  What is Hibernate?
4.  Is JPA a framework?
5.  Is Hibernate a database?
6.  What is an Entity?
7.  Why do we need `@Id`?
8.  What is a primary key?
9.  What is a foreign key?
10. Why do we use `@ManyToOne`?
11. Why do we use `@OneToMany`?
12. What does `mappedBy` mean?
13. What is `@JoinColumn`?
14. What is JPQL?
15. SQL vs JPQL?
16. What is `EntityManager`?
17. What does `persist()` do?
18. What does `find()` do?
19. What does `merge()` do?
20. What does `remove()` do?
21. What is a persistence context?
22. What is dirty checking?
23. What is a transaction?
24. What is commit?
25. What is rollback?
26. What is lazy loading?
27. What is eager loading?
28. What is cascade?
29. What is orphan removal?
30. What is the N+1 problem?

------------------------------------------------------------------------

# 113. Common Mistakes

## Mistake 1

Forgetting:

``` java
@Entity
```

Result: JPA does not treat the class as an entity.

------------------------------------------------------------------------

## Mistake 2

Forgetting:

``` java
@Id
```

Every entity needs an identifier.

------------------------------------------------------------------------

## Mistake 3

No no-argument constructor.

``` java
public Student() {
}
```

Keep it.

------------------------------------------------------------------------

## Mistake 4

Wrong mappedBy:

``` java
mappedBy = "department_id"
```

Wrong.

Use the Java field:

``` java
mappedBy = "department"
```

------------------------------------------------------------------------

## Mistake 5

Wrong JPQL:

``` java
SELECT *
FROM students
```

That is SQL, not JPQL.

Correct:

``` java
SELECT s
FROM Student s
```

------------------------------------------------------------------------

## Mistake 6

Using table column names instead of Java field names in JPQL.

Wrong:

``` java
s.department_id
```

Correct:

``` java
s.department
```

or:

``` java
s.department.name
```

------------------------------------------------------------------------

## Mistake 7

Forgetting the transaction for write operations.

``` java
em.persist()
em.merge()
em.remove()
```

normally need an active transaction in a resource-local setup.

------------------------------------------------------------------------

## Mistake 8

Wrong database password.

Check:

``` xml
<property
    name="jakarta.persistence.jdbc.password"
    value="YOUR_PASSWORD"/>
```

------------------------------------------------------------------------

## Mistake 9

Wrong database URL.

``` text
jdbc:postgresql://localhost:5432/campus_db
```

Check:

``` text
localhost
5432
campus_db
```

------------------------------------------------------------------------

# 114. Debugging Checklist

If application doesn't start:

### Check Java

``` bash
java -version
```

### Check Maven

``` bash
mvn -version
```

### Check PostgreSQL

Make sure PostgreSQL is running.

### Check database

``` sql
\l
```

### Check tables

``` sql
\dt
```

### Check data

``` sql
SELECT * FROM students;
SELECT * FROM departments;
```

### Check password

Verify `persistence.xml`.

### Check dependency download

``` bash
mvn clean package
```

### Check Tomcat

Make sure the WAR is deployed.

------------------------------------------------------------------------

# 115. Useful Hibernate SQL Logging

Already enabled:

``` xml
<property
    name="hibernate.show_sql"
    value="true"/>
```

and:

``` xml
<property
    name="hibernate.format_sql"
    value="true"/>
```

When the application runs, Hibernate can print generated SQL.

This is extremely useful while teaching.

Tell students:

> "We write Java/JPA code, but Hibernate eventually generates SQL to
> communicate with PostgreSQL."

------------------------------------------------------------------------

# 116. How Hibernate Understands Student

We write:

``` java
@Entity
@Table(name = "students")
public class Student {

    @Id
    private int id;

    private String name;

    private int age;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

Hibernate understands approximately:

``` text
Student
   ↓
students table

id
   ↓
primary key

name
   ↓
name column

age
   ↓
age column

department
   ↓
department_id foreign key
```

------------------------------------------------------------------------

# 117. One Important Mental Model

Remember:

``` text
Java class
    ↓
@Entity
    ↓
Database table

Java field
    ↓
@Column
    ↓
Database column

@Id
    ↓
Primary key

@ManyToOne
    ↓
Relationship

@JoinColumn
    ↓
Foreign key column
```

This is one of the best ways to understand JPA.

------------------------------------------------------------------------

# 118. Day 10 Classroom Sequence

Recommended teaching order:

## Part 1 --- Revision

Review Day 9:

``` text
ORM
JPA
Hibernate
Entity
EntityManager
persist
find
merge
remove
```

## Part 2 --- Database Relationship

Create:

``` text
departments
students
```

Explain:

``` text
department_id
```

## Part 3 --- Entity Relationship

Teach:

``` java
@ManyToOne
@JoinColumn
@OneToMany
mappedBy
```

## Part 4 --- JPQL

Start with:

``` java
SELECT s FROM Student s
```

Then:

``` text
WHERE
ORDER BY
LIKE
JOIN
relationship navigation
COUNT
```

## Part 5 --- Transactions

Teach:

``` text
begin
commit
rollback
```

## Part 6 --- Deep Understanding

Cover:

``` text
Persistence Context
Entity lifecycle
Dirty checking
First-level cache
Fetch types
Cascade
orphanRemoval
N+1
```

## Part 7 --- Full Project

Run:

``` text
CREATE
READ
UPDATE
DELETE
FILTER
```

------------------------------------------------------------------------

# 119. Day 10 Final Revision Sheet

``` text
ORM
 ↓
Maps Java objects ↔ database data

JPA
 ↓
Persistence specification/API

Hibernate
 ↓
JPA implementation/ORM framework

@Entity
 ↓
Persistent entity

@Id
 ↓
Primary key

@GeneratedValue
 ↓
ID generation

@ManyToOne
 ↓
Many → One

@OneToMany
 ↓
One → Many

@JoinColumn
 ↓
Foreign key mapping

mappedBy
 ↓
Inverse side

EntityManager
 ↓
Persistence operations

persist()
 ↓
Create

find()
 ↓
Read

merge()
 ↓
Merge detached state

remove()
 ↓
Delete

JPQL
 ↓
Query entities

Transaction
 ↓
begin → commit / rollback

Persistence Context
 ↓
Managed entity environment

Dirty Checking
 ↓
Detect changes automatically

Lazy
 ↓
Load when needed

Eager
 ↓
Load immediately

Cascade
 ↓
Propagate operations

orphanRemoval
 ↓
Remove orphaned child

N+1
 ↓
Potential excessive related queries
```

------------------------------------------------------------------------

# 120. Final Day 10 Architecture

The entire project can now be explained as:

``` text
                         BROWSER
                            |
                            v
                    StudentServlet
                            |
                            v
                    StudentService
                            |
                            v
                      StudentDAO
                            |
                            v
                     EntityManager
                            |
                            v
                          JPA
                            |
                            v
                       Hibernate
                            |
                            v
                          JDBC
                            |
                            v
                       PostgreSQL
                       /          \
                      /            \
             departments          students
                  |                    |
                  | 1                  | *
                  +--------------------+
                       Relationship
```

The key relationship is:

``` text
Department 1 -------- * Student
```

with:

``` java
Department:
@OneToMany(mappedBy = "department")

Student:
@ManyToOne
@JoinColumn(name = "department_id")
```

------------------------------------------------------------------------

# 121. Day 10 Interview Master Answer

If an interviewer asks:

**"Explain how JPA/Hibernate works in your project."**

Answer:

> "In my Campus Student Management System, I use JPA for persistence and
> Hibernate as the JPA implementation. Student and Department are JPA
> entities mapped to PostgreSQL tables. A Department has a one-to-many
> relationship with Students, while Student has a many-to-one
> relationship with Department. The Student entity owns the relationship
> because the students table contains the department_id foreign key. I
> use EntityManager for persistence operations and JPQL for entity-based
> queries. For write operations I use transactions with begin, commit
> and rollback. Hibernate manages the persistence context, performs
> dirty checking, and generates SQL that is ultimately executed against
> PostgreSQL through JDBC."

That answer covers a large portion of the Day 10 interview material.

------------------------------------------------------------------------

# 122. Day 10 Must-Know List

Before moving to Day 11, students should confidently understand:

``` text
[ ] ORM
[ ] JPA
[ ] Hibernate
[ ] Entity
[ ] Persistent entity
[ ] @Entity
[ ] @Table
[ ] @Column
[ ] @Id
[ ] Primary key
[ ] Foreign key
[ ] Candidate key
[ ] Alternate key
[ ] Composite key
[ ] @GeneratedValue
[ ] IDENTITY
[ ] SEQUENCE
[ ] @OneToOne
[ ] @OneToMany
[ ] @ManyToOne
[ ] @ManyToMany
[ ] @JoinColumn
[ ] mappedBy
[ ] Owning side
[ ] Inverse side
[ ] FetchType.LAZY
[ ] FetchType.EAGER
[ ] Cascade
[ ] orphanRemoval
[ ] Entity lifecycle
[ ] Transient
[ ] Managed
[ ] Detached
[ ] Removed
[ ] EntityManager
[ ] EntityManagerFactory
[ ] Persistence Context
[ ] First-level cache
[ ] Dirty checking
[ ] Flush
[ ] persist()
[ ] find()
[ ] merge()
[ ] remove()
[ ] detach()
[ ] JPQL
[ ] Named parameters
[ ] JOIN
[ ] JOIN FETCH
[ ] Transactions
[ ] ACID
[ ] Commit
[ ] Rollback
[ ] N+1 problem
[ ] Optimistic locking
[ ] @Version
[ ] DAO
[ ] Service layer
[ ] MVC
```

------------------------------------------------------------------------

# 123. One-Line Memory Tricks

``` text
JPA       = Specification
Hibernate = Implementation

ORM       = Object ↔ Relational data

@Entity   = This class is persistent

@Id       = Identity

@ManyToOne = Many students → One department

@OneToMany = One department → Many students

@JoinColumn = Which FK column?

mappedBy   = Relationship already owned elsewhere

persist    = New entity

find       = Find by ID

merge      = Merge detached state

remove     = Delete

JPQL       = Query Java entities

Transaction = All or nothing

commit     = Save transaction

rollback   = Undo transaction

LAZY       = Later

EAGER      = Immediately

Cascade    = Propagate operation

Dirty checking = Hibernate notices changes

Persistence Context = Managed entity space

N+1 = Too many related queries
```

------------------------------------------------------------------------

# 124. Day 10 Completion Criteria

Day 10 is complete when the student can:

1.  Create a Department entity.
2.  Create a Student entity.
3.  Map Student → Department.
4.  Explain foreign keys.
5.  Explain owning and inverse sides.
6.  Use `@ManyToOne`.
7.  Use `@OneToMany`.
8.  Write basic JPQL.
9.  Use named parameters.
10. Query through relationships.
11. Explain transactions.
12. Explain commit and rollback.
13. Explain persistence context.
14. Explain entity lifecycle.
15. Explain dirty checking.
16. Explain lazy/eager loading.
17. Explain cascade.
18. Explain orphan removal.
19. Explain N+1 at a basic level.
20. Run the complete Campus Student Management System.

------------------------------------------------------------------------

# 125. Final Day 10 Message for Students

The most important idea is not memorizing annotations.

Understand the chain:

``` text
Java Object
     ↓
Entity
     ↓
JPA Mapping
     ↓
Hibernate
     ↓
SQL
     ↓
Database
```

And for relationships:

``` text
Java Relationship
     ↓
JPA Relationship Annotation
     ↓
Foreign Key
     ↓
Database Relationship
```

And for queries:

``` text
JPQL
     ↓
Hibernate
     ↓
SQL
     ↓
PostgreSQL
```

Once these three flows are clear, JPA/Hibernate becomes much easier to
understand.
