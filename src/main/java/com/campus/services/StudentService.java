package com.campus.services;

import com.campus.dao.DepartmentJPADAO;
import com.campus.dao.StudentJPADAO;
import com.campus.model.Department;
import com.campus.model.Student;

import java.util.List;

public class StudentService {

    private final StudentJPADAO studentDAO = new StudentJPADAO();
    private final DepartmentJPADAO departmentDAO = new DepartmentJPADAO();

    // ==========================================
    // Core Query Methods (Old & New)
    // ==========================================

    // New JPA standard name
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // Old method name retained for backward compatibility
    public List<Student> getStudents() {
        return studentDAO.getAllStudents();
    }

    // Fetch single student by id (works for both)
    public Student getStudentById(int id) {
        return studentDAO.getStudentById(id);
    }

    // ==========================================
    // Department Methods
    // ==========================================

    public List<Department> getAllDepartments() {
        return departmentDAO.getAllDepartments();
    }

    public Department getDepartmentById(int id) {
        return departmentDAO.getDepartmentById(id);
    }

    // ==========================================
    // Persistence Methods: Entity-based
    // ==========================================

    public void addStudent(Student student) {
        studentDAO.addStudent(student);
    }

    public void updateStudent(Student student) {
        studentDAO.updateStudent(student);
    }

    // ==========================================
    // Persistence Methods: Primitive-based
    // ==========================================

    public void addStudent(String name, String department, int age) {
        Department dept = departmentDAO.getDepartmentByName(department);
        if (dept == null) {
            dept = new Department(department);
            departmentDAO.addDepartment(dept);
        }
        studentDAO.addStudent(new Student(name, age, dept));
    }

    public void updateStudent(int id, String name, String department, int age) {
        Department dept = departmentDAO.getDepartmentByName(department);
        if (dept == null) {
            dept = new Department(department);
            departmentDAO.addDepartment(dept);
        }
        studentDAO.updateStudent(new Student(id, name, age, dept));
    }

    public void deleteStudent(int id) {
        studentDAO.deleteStudent(id);
    }

    // ==========================================
    // Advanced JPQL Queries
    // ==========================================

    public List<Student> findStudentsByDepartment(String departmentName) {
        return studentDAO.findStudentsByDepartment(departmentName);
    }

    public List<Student> searchByName(String name) {
        return studentDAO.searchByName(name);
    }

    public long getStudentCount() {
        return studentDAO.getStudentCount();
    }
}
