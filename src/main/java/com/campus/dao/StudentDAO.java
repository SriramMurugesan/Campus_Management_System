package com.campus.dao;

import com.campus.model.Department;
import com.campus.model.Student;
import com.campus.util.DBConnection;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

/**
 * JDBC StudentDAO implementation.
 */
public class StudentDAO {

    public void addStudent(Student student) {
        String sql = "INSERT INTO students (name, department_id, age) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            if (student.getDepartment() != null) {
                pstmt.setInt(2, student.getDepartment().getId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setInt(3, student.getAge());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("department_id"));
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), dept));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentById(int id) {
        Student student = null;
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("department_id"));
                student = new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), dept);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, department_id = ?, age = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            if (student.getDepartment() != null) {
                pstmt.setInt(2, student.getDepartment().getId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setInt(3, student.getAge());
            pstmt.setInt(4, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
