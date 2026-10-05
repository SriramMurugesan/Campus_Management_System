package com.campus.controller;

import com.campus.model.Department;
import com.campus.model.Student;
import com.campus.services.StudentService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet({"/students", "/filter", "/reset"})
public class StudentServlet extends HttpServlet {

    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String servletPath = request.getServletPath();
        String action = request.getParameter("action");

        // Handle URL endpoints like /filter or /reset
        if ("/filter".equals(servletPath)) {
            action = "filter";
        } else if ("/reset".equals(servletPath)) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list": {
                List<Student> students = studentService.getAllStudents();
                List<Department> departments = studentService.getAllDepartments();
                request.setAttribute("students", students);
                request.setAttribute("departments", departments);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/student.jsp");
                dispatcher.forward(request, response);
                break;
            }
            case "edit": {
                int id = Integer.parseInt(request.getParameter("id"));
                Student student = studentService.getStudentById(id);
                List<Department> departments = studentService.getAllDepartments();
                request.setAttribute("student", student);
                request.setAttribute("departments", departments);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student.jsp");
                dispatcher.forward(request, response);
                break;
            }
            case "delete": {
                int id = Integer.parseInt(request.getParameter("id"));
                studentService.deleteStudent(id);
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
            case "filter":
            case "department": {
                String department = request.getParameter("department");
                List<Student> students;
                if (department == null || department.trim().isEmpty() || "-- All Departments --".equals(department.trim())) {
                    students = studentService.getAllStudents();
                } else {
                    students = studentService.findStudentsByDepartment(department.trim());
                }
                List<Department> departments = studentService.getAllDepartments();
                request.setAttribute("students", students);
                request.setAttribute("departments", departments);
                request.setAttribute("selectedDepartment", department);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/student.jsp");
                dispatcher.forward(request, response);
                break;
            }
            case "reset": {
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
            default: {
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "add";
        }

        switch (action) {
            case "add": {
                String name = request.getParameter("name");
                int age = Integer.parseInt(request.getParameter("age"));
                String departmentIdParam = request.getParameter("departmentId");

                if (departmentIdParam != null && !departmentIdParam.trim().isEmpty()) {
                    int departmentId = Integer.parseInt(departmentIdParam);
                    Department department = studentService.getDepartmentById(departmentId);
                    Student student = new Student(name, age, department);
                    studentService.addStudent(student);
                } else {
                    String departmentName = request.getParameter("department");
                    studentService.addStudent(name, departmentName, age);
                }
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
            case "update": {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                int age = Integer.parseInt(request.getParameter("age"));
                String departmentIdParam = request.getParameter("departmentId");

                if (departmentIdParam != null && !departmentIdParam.trim().isEmpty()) {
                    int departmentId = Integer.parseInt(departmentIdParam);
                    Department department = studentService.getDepartmentById(departmentId);
                    Student student = new Student(id, name, age, department);
                    studentService.updateStudent(student);
                } else {
                    String departmentName = request.getParameter("department");
                    studentService.updateStudent(id, name, departmentName, age);
                }
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
            case "delete": {
                int id = Integer.parseInt(request.getParameter("id"));
                studentService.deleteStudent(id);
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
            default: {
                response.sendRedirect(request.getContextPath() + "/students");
                break;
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        int age = Integer.parseInt(request.getParameter("age"));
        String departmentIdParam = request.getParameter("departmentId");

        if (departmentIdParam != null && !departmentIdParam.trim().isEmpty()) {
            int departmentId = Integer.parseInt(departmentIdParam);
            Department department = studentService.getDepartmentById(departmentId);
            Student student = new Student(id, name, age, department);
            studentService.updateStudent(student);
        } else {
            String departmentName = request.getParameter("department");
            studentService.updateStudent(id, name, departmentName, age);
        }
        response.sendRedirect(request.getContextPath() + "/students");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        studentService.deleteStudent(id);
        response.sendRedirect(request.getContextPath() + "/students");
    }
}
