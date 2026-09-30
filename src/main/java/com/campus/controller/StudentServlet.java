package com.campus.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.campus.services.StudentService;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();
    //Read operation
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        var students = studentService.getStudents();
        request.setAttribute("students", students);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student.jsp");
        dispatcher.forward(request, response);
        
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {

        String name = request.getParameter("name");
        String department = request.getParameter("depart    ment");
        int age = Integer.parseInt(request.getParameter("age"));
        studentService.addStudent(name, department, age);
        response.sendRedirect("/students");
    }
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        int age = Integer.parseInt(request.getParameter("age"));
        studentService.updateStudent(id, name, department, age);
        response.sendRedirect("/students");
    }
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        studentService.deleteStudent(id);
        response.sendRedirect("/students");
    }
}
