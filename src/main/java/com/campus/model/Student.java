package com.campus.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
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

    public Student(int id, String name, int age, Department department) {
        this.id = id;
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

    @Override
    public String toString() {
        return id + " - " + name + " (" + (department != null ? department.getName() : "None") + ", Age: " + age + ")";
    }
}
