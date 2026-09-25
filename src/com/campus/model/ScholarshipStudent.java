package com.campus.model;

public class ScholarshipStudent extends Student{

    private double scholarshipPercentage;

    public ScholarshipStudent(int studentid, String studentname, int age, String department, int[] marks,double scholarshipPercentage) {
        super(studentid, studentname, age, department, marks);
        this.scholarshipPercentage = scholarshipPercentage;
    }
    // getters and setters
    public double getScholarshipPercentage() {
        return scholarshipPercentage;
    }
    public void setScholarshipPercentage(double scholarshipPercentage) {
        this.scholarshipPercentage = scholarshipPercentage;
    }

    
    

    
}
