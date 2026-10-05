package com.campus.dao;

import com.campus.model.Student;
import com.campus.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class StudentJPADAO {

    public void addStudent(Student student) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Student> getAllStudents() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s ORDER BY s.id", Student.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Student getStudentById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public void updateStudent(Student student) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(student);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void deleteStudent(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Student student = em.find(Student.class, id);
            if (student != null) {
                em.remove(student);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Student> findStudentsByDepartment(String departmentName) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.department.name = :dept ORDER BY s.id", Student.class)
                    .setParameter("dept", departmentName)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> findStudentsByAge(int age) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.age = :age ORDER BY s.id", Student.class)
                    .setParameter("age", age)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Student> searchByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(:name) ORDER BY s.id", Student.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long getStudentCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM Student s", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
