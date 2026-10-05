# Campus Student Management System: Day 10 Method-by-Method Architecture & Visual Flowcharts

---

## 🏛️ 1. Real-World Analogy & Simple Explanation of Every File and Folder

Imagine running a **Modern Smart University Campus Office**:

| File / Folder | Real-World Role | Simple Everyday Explanation |
| :--- | :--- | :--- |
| **`pom.xml`** | 📦 **The Construction Blueprint & Supplier Contract** | Tells Maven which tools and workers (Hibernate, PostgreSQL Driver, JSTL) to download and hire, and runs our web server on port `8081`. |
| **`persistence.xml`** | 🔑 **The Master Key & Address Book** | Holds the database login keys (username: `postgres`, password: `password`), the warehouse address (`localhost:5432/campus_db`), and tells Hibernate which entity forms exist. |
| **`JPAUtil.java`** | 🏭 **The Power Station & Connection Dispenser** | Starts the heavy generator (`EntityManagerFactory`) once when the university opens, and quickly vends a lightweight pass (`EntityManager`) to whoever needs to talk to the database. |
| **`Department.java`** | 🏢 **The Academic Department Entity** | Represents real departments (like CSE, ECE, MECH). One department has a roster of many students (`@OneToMany`). |
| **`Student.java`** | 🎓 **The Student Admission Profile** | Represents a student with name, age, and an official badge linking them to their department (`@ManyToOne` with `@JoinColumn(name="department_id")`). |
| **`DepartmentJPADAO.java`** | 📁 **Department Filing Cabinet Clerk** | Directly accesses department records (fetches all departments for dropdown menus, finds department by ID or name). |
| **`StudentJPADAO.java`** | 📁 **Student Records Vault Clerk** | The only worker who directly writes, updates, deletes, and searches student records using Hibernate and JPQL queries. |
| **`StudentService.java`** | 👔 **The Academic Dean / Coordinator** | The manager who sits between the front office (Servlets) and the file clerks (DAOs). Validates business logic and coordinates between student records and departments. |
| **`StudentServlet.java`** | 🛎️ **The Reception Desk (Traffic Controller)** | Receives visitors' requests from the browser, inspects their ticket (`action`), asks the Dean (`StudentService`) to do the work, and sends visitors to the right office room (`student.jsp` or `update-student.jsp`). |
| **`student.jsp`** | 📋 **The Main Campus Hub Display Board** | The big interactive screen where users can see the student list, pick a department from a dropdown to add a student, or filter students by department. |
| **`update-student.jsp`** | ✏️ **The Student Profile Edit Form** | The individual student form pre-filled with the student's current information, letting the user change their name, age, or department. |
| **`student.html`** | 📄 **The Fast New Admission Counter** | A quick standalone HTML page with an add-student form for rapid data entry. |
| **`index.html`** | 🚪 **The Campus Main Entrance** | The welcome doorway with clean navigation links to the Students Hub and Add Form. |

---

## 🗺️ 2. High-Level System Architecture

```mermaid
graph TD
    subgraph Browser ["User Browser (Presentation Layer)"]
        UI1["student.jsp (List & Filter)"]
        UI2["update-student.jsp (Edit Form)"]
        UI3["student.html (Add Form)"]
    end

    subgraph ControllerLayer ["Web Controller Layer"]
        Servlet["StudentServlet.java<br/>(switch action)"]
    end

    subgraph BusinessLayer ["Business Service Layer"]
        Service["StudentService.java<br/>(Business Coordinator)"]
    end

    subgraph DAOLayer ["Data Access Layer (ORM DAOs)"]
        SDAO["StudentJPADAO.java"]
        DDAO["DepartmentJPADAO.java"]
    end

    subgraph JPALayer ["JPA & Hibernate Core"]
        JPAU["JPAUtil.java"]
        EM["EntityManager (Session)"]
    end

    subgraph DB ["PostgreSQL Database"]
        Postgres[("campus_db<br/>(students & departments)")]
    end

    UI1 -->|GET / POST| Servlet
    UI2 -->|POST /students| Servlet
    UI3 -->|POST /students| Servlet
    Servlet -->|Delegates Work| Service
    Service -->|Student Queries| SDAO
    Service -->|Department Queries| DDAO
    SDAO -->|Requests Connection| JPAU
    DDAO -->|Requests Connection| JPAU
    JPAU -->|Creates| EM
    EM -->|Translates to SQL| Postgres
    Postgres -.->|Result Sets| EM
    EM -.->|Entities| SDAO
    EM -.->|Entities| DDAO
    SDAO -.->|List of Students| Service
    DDAO -.->|List of Departments| Service
    Service -.->|Sets Attributes| Servlet
    Servlet -->|Forwards Model| UI1
    Servlet -->|Forwards Model| UI2
```

---

## 🏭 3. `JPAUtil.java` Method Flowcharts

### 3.1 `getEntityManager()`
* **Real-World Use Case:** A student walks in to update their profile. The clerk needs an active session stamp (`EntityManager`) to access the database vault.

```mermaid
flowchart TD
    Start([Call JPAUtil.getEntityManager]) --> CheckFactory{Is factory initialized?}
    CheckFactory -->|Yes| Vend["factory.createEntityManager()"]
    CheckFactory -->|No (First Call)| InitFactory["Persistence.createEntityManagerFactory('campusPU')<br/>Reads persistence.xml, builds pool"]
    InitFactory --> Vend
    Vend --> ReturnEM([Return active EntityManager])
```

### 3.2 `close()`
* **Real-World Use Case:** End of the semester or application shutdown. Close all database pools cleanly to prevent memory and connection leaks.

```mermaid
flowchart TD
    Start([Call JPAUtil.close]) --> CheckOpen{factory != null AND factory.isOpen?}
    CheckOpen -->|Yes| CloseF["factory.close()"]
    CheckOpen -->|No| Done([Already closed])
    CloseF --> Done
```

---

## 📁 4. `DepartmentJPADAO.java` Method Flowcharts

### 4.1 `getAllDepartments()`
* **Real-World Use Case:** Populating the `<select>` dropdown in forms so users can pick from `"CSE"`, `"ECE"`, `"EEE"`, `"MECH"`, `"CIVIL"`.

```mermaid
flowchart TD
    Start([getAllDepartments Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> JPQL["query = em.createQuery('SELECT d FROM Department d ORDER BY d.id', Department.class)"]
    JPQL --> Exec["list = query.getResultList()"]
    Exec --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnList([Return List<Department>])
```

### 4.2 `getDepartmentById(int id)`
* **Real-World Use Case:** When a student form is submitted with `departmentId=2`, fetch the full `Department` entity (ECE) to link to the `Student`.

```mermaid
flowchart TD
    Start([getDepartmentById Called with ID]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> Find["dept = em.find(Department.class, id)"]
    Find --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnDept([Return Department or null])
```

### 4.3 `getDepartmentByName(String name)`
* **Real-World Use Case:** Legacy support: When an old form submits `"CSE"` as a string, find the matching department record without error.

```mermaid
flowchart TD
    Start([getDepartmentByName Called with Name]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> JPQL["em.createQuery('SELECT d FROM Department d WHERE LOWER(d.name) = LOWER(:name)', Department.class)"]
    JPQL --> Bind["query.setParameter('name', name.trim())"]
    Bind --> Exec["list = query.getResultList()"]
    Exec --> CheckEmpty{list.isEmpty()?}
    CheckEmpty -->|Yes| SetNull["result = null"]
    CheckEmpty -->|No| SetFirst["result = list.get(0)"]
    SetNull --> FinallyBlock[Finally Block]
    SetFirst --> FinallyBlock
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnResult([Return Department or null])
```

### 4.4 `addDepartment(Department department)`
* **Real-World Use Case:** Admin creates a new department (e.g. "Data Science") in the database.

```mermaid
flowchart TD
    Start([addDepartment Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> GetTx["tx = em.getTransaction()"]
    GetTx --> TryBlock[Try Block]
    TryBlock --> Begin["tx.begin()"]
    Begin --> Persist["em.persist(department)"]
    Persist --> Commit["tx.commit()"]
    Commit --> FinallyBlock[Finally Block]

    TryBlock -. Error .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckTx{tx.isActive()?}
    CheckTx -->|Yes| Rollback["tx.rollback()"]
    CheckTx -->|No| LogErr["e.printStackTrace()"]
    Rollback --> LogErr
    LogErr --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> End([Done])
```

---

## 📁 5. `StudentJPADAO.java` Method Flowcharts

### 5.1 `addStudent(Student student)` — Inserting New Entity
* **Real-World Use Case:** A newly admitted student submits their enrollment form.

```mermaid
flowchart TD
    Start([addStudent Called with Student]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> GetTx["tx = em.getTransaction()"]
    GetTx --> TryBlock[Try Block]
    TryBlock --> Begin["tx.begin()"]
    Begin --> Persist["em.persist(student)"]
    Persist --> Commit["tx.commit()"]
    Commit --> FinallyBlock[Finally Block]

    TryBlock -. Error Occurs .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckActive{tx.isActive()?}
    CheckActive -->|Yes| Rollback["tx.rollback()"]
    CheckActive -->|No| LogErr["e.printStackTrace()"]
    Rollback --> LogErr
    LogErr --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> Done([Student Saved to Database])
```

### 5.2 `getAllStudents()` — Listing All Records
* **Real-World Use Case:** Opening `/students` to show the master student roster table.

```mermaid
flowchart TD
    Start([getAllStudents Called]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> CreateQ["query = em.createQuery('SELECT s FROM Student s ORDER BY s.id', Student.class)"]
    CreateQ --> Exec["students = query.getResultList()"]
    Exec --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnStudents([Return List<Student>])
```

### 5.3 `getStudentById(int id)` — Finding Single Student
* **Real-World Use Case:** Clicking **Edit** next to student #3 loads their existing profile into the edit form.

```mermaid
flowchart TD
    Start([getStudentById Called with ID]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> Find["student = em.find(Student.class, id)"]
    Find --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnStudent([Return Student or null])
```

### 5.4 `updateStudent(Student student)` — Updating Changes
* **Real-World Use Case:** Changing a student's age from 21 to 22 or switching their department from CSE to ECE.

```mermaid
flowchart TD
    Start([updateStudent Called with Modified Student]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> GetTx["tx = em.getTransaction()"]
    GetTx --> TryBlock[Try Block]
    TryBlock --> Begin["tx.begin()"]
    Begin --> Merge["em.merge(student)"]
    Merge --> Commit["tx.commit()"]
    Commit --> FinallyBlock[Finally Block]

    TryBlock -. Error Occurs .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckActive{tx.isActive()?}
    CheckActive -->|Yes| Rollback["tx.rollback()"]
    CheckActive -->|No| LogErr["e.printStackTrace()"]
    Rollback --> LogErr
    LogErr --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> Done([Changes Saved])
```

### 5.5 `deleteStudent(int id)` — Deleting a Record
* **Real-World Use Case:** Student graduated or transferred out; delete button removes their record from PostgreSQL.

```mermaid
flowchart TD
    Start([deleteStudent Called with ID]) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> GetTx["tx = em.getTransaction()"]
    GetTx --> TryBlock[Try Block]
    TryBlock --> Begin["tx.begin()"]
    Begin --> Find["student = em.find(Student.class, id)"]
    Find --> CheckFound{student != null?}
    CheckFound -->|Yes| Remove["em.remove(student)"]
    CheckFound -->|No| Commit
    Remove --> Commit["tx.commit()"]
    Commit --> FinallyBlock[Finally Block]

    TryBlock -. Error .-> CatchBlock[Catch Exception]
    CatchBlock --> CheckTx{tx.isActive()?}
    CheckTx -->|Yes| Rollback["tx.rollback()"]
    CheckTx -->|No| LogErr["e.printStackTrace()"]
    Rollback --> LogErr
    LogErr --> FinallyBlock

    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> Done([Record Removed])
```

### 5.6 `findStudentsByDepartment(String deptName)` — Filter Query
* **Real-World Use Case:** Department Head of CSE clicks "Filter" to view only CSE students.

```mermaid
flowchart TD
    Start([findStudentsByDepartment Called with 'CSE']) --> GetEM["em = JPAUtil.getEntityManager()"]
    GetEM --> TryBlock[Try Block]
    TryBlock --> CreateQ["query = em.createQuery('SELECT s FROM Student s WHERE s.department.name = :dept ORDER BY s.id', Student.class)"]
    CreateQ --> BindParam["query.setParameter('dept', deptName)"]
    BindParam --> Exec["results = query.getResultList()"]
    Exec --> FinallyBlock[Finally Block]
    FinallyBlock --> CloseEM["em.close()"]
    CloseEM --> ReturnFiltered([Return Filtered List<Student>])
```

### 5.7 `searchByName(String name)` & `getStudentCount()`
```mermaid
flowchart LR
    subgraph SearchByName ["searchByName('Arun')"]
        S1([Call]) --> S2["em.createQuery('... WHERE LOWER(s.name) LIKE LOWER(:name)', Student.class)"]
        S2 --> S3[".setParameter('name', '%Arun%')"]
        S3 --> S4["Return Matched Students"]
    end

    subgraph GetStudentCount ["getStudentCount()"]
        C1([Call]) --> C2["em.createQuery('SELECT COUNT(s) FROM Student s', Long.class)"]
        C2 --> C3["Return Total Count (e.g. 52)"]
    end
```

---

## 👔 6. `StudentService.java` Method Delegation

* **Real-World Use Case:** Centralized business coordinator. Whether requests come with raw IDs/Strings (from older forms) or entity objects, the service resolves dependencies and delegates to DAOs cleanly.

```mermaid
flowchart TD
    subgraph ServiceMethods ["StudentService Public Methods"]
        M1["getAllStudents() / getStudents()"]
        M2["getStudentById(id)"]
        M3["getAllDepartments()"]
        M4["getDepartmentById(id)"]
        M5["addStudent(Student student)"]
        M6["addStudent(name, deptName, age)"]
        M7["updateStudent(Student student)"]
        M8["updateStudent(id, name, deptName, age)"]
        M9["deleteStudent(id)"]
        M10["findStudentsByDepartment(dept)"]
    end

    subgraph ServiceCoordination ["Service Logic & Lookups"]
        L1["Direct query"]
        L2["Direct query"]
        L3["Direct query"]
        L4["Direct query"]
        L5["Direct save"]
        L6["Lookup Department by name via departmentDAO<br/>If not found, auto-create Department<br/>Construct Student(name, age, dept)"]
        L7["Direct update"]
        L8["Lookup Department by name via departmentDAO<br/>Construct Student(id, name, age, dept)"]
        L9["Direct delete"]
        L10["Direct filter"]
    end

    subgraph DAOs ["DAOs"]
        SDAO["StudentJPADAO"]
        DDAO["DepartmentJPADAO"]
    end

    M1 --> L1 --> SDAO
    M2 --> L2 --> SDAO
    M3 --> L3 --> DDAO
    M4 --> L4 --> DDAO
    M5 --> L5 --> SDAO
    M6 --> L6 --> SDAO
    M7 --> L7 --> SDAO
    M8 --> L8 --> SDAO
    M9 --> L9 --> SDAO
    M10 --> L10 --> SDAO
```

---

## 🛎️ 7. `StudentServlet.java` Controller Flowcharts

### 7.1 `doGet(request, response)` — Handling Page Loads & Actions
* **Real-World Use Case:** Browser navigation, showing lists, filtering by department, opening edit forms, or deleting records.

```mermaid
flowchart TD
    Start([User sends GET /students]) --> ReadAction[Read 'action' parameter]
    ReadAction --> ActionCheck{What is action?}

    ActionCheck -->|null OR 'list'| ListCase[List All Students]
    ListCase --> FetchAll["service.getAllStudents()<br/>service.getAllDepartments()"]
    FetchAll --> SetAttr1["request.setAttribute('students', ...)<br/>request.setAttribute('departments', ...)"]
    SetAttr1 --> FwdList["Forward to /student.jsp"]

    ActionCheck -->|'edit'| EditCase[Edit Student Profile]
    EditCase --> ParseId["id = Integer.parseInt(request.getParameter('id'))"]
    ParseId --> FetchSingle["service.getStudentById(id)<br/>service.getAllDepartments()"]
    FetchSingle --> SetAttr2["request.setAttribute('student', ...)<br/>request.setAttribute('departments', ...)"]
    SetAttr2 --> FwdEdit["Forward to /update-student.jsp"]

    ActionCheck -->|'delete'| DelCase[Delete Student]
    DelCase --> ParseDelId["id = Integer.parseInt(request.getParameter('id'))"]
    ParseDelId --> CallDel["service.deleteStudent(id)"]
    CallDel --> RedirectDel["response.sendRedirect(contextPath + '/students')"]

    ActionCheck -->|'filter' OR 'department'| FilterCase[Filter by Department]
    FilterCase --> ReadDept["dept = request.getParameter('department')"]
    ReadDept --> CheckDeptEmpty{dept is empty or '-- All Departments --'?}
    CheckDeptEmpty -->|Yes| GetAllFiltered["students = service.getAllStudents()"]
    CheckDeptEmpty -->|No| GetByDept["students = service.findStudentsByDepartment(dept)"]
    GetAllFiltered --> SetAttr3["request.setAttribute('students', ...)<br/>request.setAttribute('departments', ...)<br/>request.setAttribute('selectedDepartment', dept)"]
    GetByDept --> SetAttr3
    SetAttr3 --> FwdFiltered["Forward to /student.jsp"]

    ActionCheck -->|'reset' OR default| ResetCase[Reset View]
    ResetCase --> RedirectReset["response.sendRedirect(contextPath + '/students')"]
```

---

### 7.2 `doPost(request, response)` — Handling Form Submissions
* **Real-World Use Case:** Submitting the "Add Student" form or submitting changes on the "Edit Student" form.

```mermaid
flowchart TD
    Start([User submits form via POST /students]) --> ReadPostAction[Read 'action' parameter]
    ReadPostAction --> CheckPostAction{action == ?}

    CheckPostAction -->|'add' OR null| AddStudentPath[Add New Student]
    AddStudentPath --> ReadAddParams["Read: name, age, departmentId (or department)"]
    ReadAddParams --> CheckDeptId{Has departmentId?}
    CheckDeptId -->|Yes: Entity Mode| FetchDeptAdd["dept = service.getDepartmentById(departmentId)<br/>student = new Student(name, age, dept)"]
    FetchDeptAdd --> CallAddEntity["service.addStudent(student)"]
    CheckDeptId -->|No: String Mode| CallAddLegacy["service.addStudent(name, departmentName, age)"]
    CallAddEntity --> RedirectAdd["response.sendRedirect(contextPath + '/students')"]
    CallAddLegacy --> RedirectAdd

    CheckPostAction -->|'update'| UpdateStudentPath[Update Existing Student]
    UpdateStudentPath --> ReadUpParams["Read: id, name, age, departmentId (or department)"]
    ReadUpParams --> CheckUpDeptId{Has departmentId?}
    CheckUpDeptId -->|Yes: Entity Mode| FetchDeptUp["dept = service.getDepartmentById(departmentId)<br/>student = new Student(id, name, age, dept)"]
    FetchDeptUp --> CallUpEntity["service.updateStudent(student)"]
    CheckUpDeptId -->|No: String Mode| CallUpLegacy["service.updateStudent(id, name, departmentName, age)"]
    CallUpEntity --> RedirectUp["response.sendRedirect(contextPath + '/students')"]
    CallUpLegacy --> RedirectUp

    CheckPostAction -->|'delete'| DeletePostPath[Delete via POST]
    DeletePostPath --> ParsePostDelId["id = Integer.parseInt(request.getParameter('id'))"]
    ParsePostDelId --> CallPostDel["service.deleteStudent(id)"]
    CallPostDel --> RedirectDelPost["response.sendRedirect(contextPath + '/students')"]
```

---

## 🎨 8. User Interaction Flows (JSP & Web Pages)

### 8.1 Filter & Reset Interaction Flow
```mermaid
sequenceDiagram
    autonumber
    actor User as Student / Teacher
    participant JSP as student.jsp
    participant Servlet as StudentServlet
    participant Service as StudentService
    participant DAO as StudentJPADAO
    participant DB as PostgreSQL

    User->>JSP: Selects "ECE" from dropdown and clicks "Filter"
    JSP->>Servlet: HTTP GET /students?action=filter&department=ECE
    Servlet->>Service: findStudentsByDepartment("ECE")
    Service->>DAO: findStudentsByDepartment("ECE")
    DAO->>DB: SELECT * FROM students s JOIN departments d ON ... WHERE d.name = 'ECE'
    DB-->>DAO: Returns matching ECE student rows
    DAO-->>Service: List<Student> (ECE only)
    Service-->>Servlet: Filtered students + All departments
    Servlet->>JSP: Forward model with selectedDepartment="ECE"
    JSP-->>User: Displays only ECE students, "ECE" remains selected in dropdown

    User->>JSP: Clicks "Reset" button
    JSP->>Servlet: HTTP GET /students
    Servlet->>Service: getAllStudents()
    Service->>DAO: getAllStudents()
    DAO->>DB: SELECT * FROM students ORDER BY id
    DB-->>DAO: Returns all student records
    DAO-->>Servlet: Full Student List
    Servlet->>JSP: Forward model to student.jsp
    JSP-->>User: Displays full student roster
```

---

### 8.2 Edit & Update Student Interaction Flow
```mermaid
sequenceDiagram
    autonumber
    actor User as User
    participant JSP1 as student.jsp
    participant Servlet as StudentServlet
    participant JSP2 as update-student.jsp
    participant Service as StudentService
    participant DAO as StudentJPADAO
    participant DB as PostgreSQL

    User->>JSP1: Clicks "Edit" on Student #1 (Arun)
    JSP1->>Servlet: HTTP GET /students?action=edit&id=1
    Servlet->>Service: getStudentById(1) & getAllDepartments()
    Service->>DAO: em.find(Student.class, 1)
    DAO->>DB: SELECT * FROM students WHERE id = 1
    DB-->>DAO: Row data
    DAO-->>Servlet: Student object (name="Arun", age=21, department="CSE")
    Servlet->>JSP2: Forwards to update-student.jsp
    JSP2-->>User: Renders form pre-filled: Name="Arun", Age=21, Dept dropdown has "CSE" selected

    User->>JSP2: Changes Age to 22, selects Dept "ECE", clicks "Update Student"
    JSP2->>Servlet: HTTP POST /students (action=update, id=1, name="Arun", age=22, departmentId=2)
    Servlet->>Service: getDepartmentById(2)
    Servlet->>Service: updateStudent(Student(id=1, name="Arun", age=22, dept=ECE))
    Service->>DAO: em.merge(student)
    DAO->>DB: UPDATE students SET name='Arun', age=22, department_id=2 WHERE id=1
    DB-->>DAO: Success
    Servlet-->>User: HTTP 302 Redirect to /students
```

---

## 💡 9. Summary Quick Reference for Classroom Teaching

| Classroom Question | Simple Answer |
| :--- | :--- |
| **Why did we switch from Day 8 to Day 10?** | In Day 8 (JDBC), we manually wrote raw SQL strings, manually extracted columns from `ResultSet`, and handled connections by hand. In Day 10 (JPA/Hibernate), we work with pure Java objects, and Hibernate generates the SQL automatically. |
| **Why is `@ManyToOne` on Student instead of Department?** | Because the foreign key column (`department_id`) is physically in the `students` table. The table holding the foreign key is always the **owning side** in JPA. |
| **What does `mappedBy="department"` in Department mean?** | It tells Hibernate: *"I am the inverse side. Look at the `department` field inside `Student.java` to find the foreign key mapping."* |
| **Why do we need `em.getTransaction().begin()` for adding/updating/deleting, but not for finding?** | Reading data does not alter the database state (it's safe and read-only). Any operation that changes data (`persist`, `merge`, `remove`) must be atomic inside a transaction so it can rollback if an error occurs. |
| **Why use `${pageContext.request.contextPath}/students` in forms?** | It ensures every link and form submission resolves to the exact root application path, preventing `404 Not Found` errors caused by relative URLs when query parameters or slashes change. |
