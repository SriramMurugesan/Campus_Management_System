package com.campus.services;

import java.util.List;
import java.util.ArrayList;

public class StudentService {
    private static final List<String> students = new ArrayList<>();

    public  StudentService(){
        students.add("101 - Bill - Java");
        students.add("102 - Steve - Python");
        students.add("103 - John - C++");
    }
    //get student
    public List<String> getStudents() {
        return students;
    }

    //add student
    public void addStudent(String name,String course){
        students.add(String.valueOf(students.size() + 101) + " - " +name + " - " +course);
    }    
}
