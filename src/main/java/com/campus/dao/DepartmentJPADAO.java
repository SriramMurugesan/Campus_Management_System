package com.campus.dao;

import com.campus.model.Department;
import com.campus.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class DepartmentJPADAO {

    public List<Department> getAllDepartments() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Department d ORDER BY d.id", Department.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Department getDepartmentById(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    public Department getDepartmentByName(String name) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Department> list = em.createQuery("SELECT d FROM Department d WHERE LOWER(d.name) = LOWER(:name)", Department.class)
                    .setParameter("name", name != null ? name.trim() : "")
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    public void addDepartment(Department department) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(department);
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
}
