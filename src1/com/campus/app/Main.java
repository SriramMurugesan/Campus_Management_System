package com.campus.app;

import java.util.Scanner;

import com.campus.model.ScholarshipStudent;
import com.campus.model.Student;
import com.campus.service.StudentService;



public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        //inputs from users
        System.out.println("enter the student id");
        int studentid=sc.nextInt();
        System.out.println("enter the student name");
        String studentname=sc.nextLine();
        System.out.println("enter the student age");
        int age=sc.nextInt();
        System.out.println("enter the student department");
        String department=sc.nextLine();
        System.out.println("number of subjects"); 
        int n=sc.nextInt();
        int[] marks=new int[n];
        System.out.println("enter the marks of "+n+" subjects");
        for(int i=0;i<n;i++) {
            System.out.println("enter the mark of subject "+(i+1));
            marks[i]=sc.nextInt();
            sc.nextLine();
        }
        System.out.println("enter the scholarship percentage");
        double scholarshipPercentage=sc.nextDouble();
        sc.nextLine();
        Student student = new ScholarshipStudent(studentid, studentname, age, department, marks,scholarshipPercentage);
        student.displayStudentInfo(true);
        Student.displayStudentCount();
        StudentService studentService = new StudentService();
        studentService.displayReportCard(student);
        student.eligbleForScholarship();
        sc.close(); 
    }

}