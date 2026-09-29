<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="UTF-8">
        <title>List of Students</title>
    </head>

    <body>
        <h1>Campus Management System</h1>
        <ul>
           <% for(String student : students){ %>

            <li><%= student %></li>

           <% } %>
        </ul>
        <br/>
        <a href="/student.html">Add Student</a>
    </body>

    </html>