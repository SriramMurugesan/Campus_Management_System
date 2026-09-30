package com.campus.services;

import java.util.List;
import com.campus.dao.StudentDAO;
import com.campus.model.Student;


public class StudentService {

    private final StudentDAO studentDAO;

    public  StudentService(){
        studentDAO = new StudentDAO();
    }
    //get student
    public List<Student> getStudents() {
        return studentDAO.getAllStudents();
    }
    //get student by id
    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }

    //add student
    public void addStudent(String name,String department,int age){
        studentDAO.addStudent(new Student(name,department,age));
    }
    
    //update student
    public void updateStudent(int id,String name,String department,int age){
        studentDAO.updateStudent(new Student(id,name,department,age));
    }
    
    //delete student
    public void deleteStudent(int id){
        studentDAO.deleteStudent(id);
    }
}
