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
        String course = request.getParameter("course");
        studentService.addStudent(name, course);
        response.sendRedirect("/students");
    }
}
