package com.campus.model;

import com.campus.contract.StudentOperations;

public abstract class Student  implements  StudentOperations {
    //Encapsulation - data hiding
    // instance variables
    private int studentid;
    private String studentname;
    private int age;
    private String department;
    private int[] marks;
    
    // static variables
    static int studentCount=0;

    // Default constructor
    public Student() {
        studentCount++;
    }

    // parameterized constructor
    public Student(int studentid, String studentname, int age, String department, int[] marks) {
        this.studentid = studentid;
        this.studentname = studentname;
        this.age = age;
        this.department = department;
        this.marks = marks;
        studentCount++;
    }
    //getters - methods to access the instance variables
    public int getStudentid() {
        return studentid;
    }
    public String getStudentname() {
        return studentname;
    }
    public int getAge() {
        return age;
    }
    public String getDepartment() {
        return department;
    }
    public int[] getMarks() {
        return marks;
    }
    //setters - methods to modify the instance variables
    public void setStudentid(int studentid) {
        this.studentid = studentid;
    }
    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public void setMarks(int[] marks) {
        this.marks = marks;
    }

    //instance methods - belongs to object
    public void displayStudentInfo() {
        System.out.println("Student ID: " + studentid);
        System.out.println("Student Name: " + studentname);
        System.out.println("Age: " + age);
        System.out.println("Department: " + department);
    }

    public void displayStudentInfo(boolean showMarks) {
        displayStudentInfo();

        if (showMarks) {
            System.out.println("Marks: " + java.util.Arrays.toString(marks));
        }
    }
    
    //abstract method
    public abstract void studentType();

    //interface methods
    
    
    // static method-belongs to class, not to object
    public static void displayStudentCount() {
        System.out.println("Total number of students: " + studentCount);
    }
}