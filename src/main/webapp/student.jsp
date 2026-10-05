<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Campus Student Management</title>
</head>
<body>

<h1>Campus Student Management</h1>

<!-- 1. Add Student Form -->
<h2>Add Student</h2>
<form action="${pageContext.request.contextPath}/students" method="post">
    <input type="hidden" name="action" value="add">

    <label>Name:</label>
    <input type="text" name="name" required>
    <br><br>

    <label>Age:</label>
    <input type="number" name="age" required>
    <br><br>

    <label>Department:</label>
    <select name="departmentId" required>
        <option value="">-- Select Department --</option>
        <c:forEach var="department" items="${departments}">
            <option value="${department.id}">${department.name}</option>
        </c:forEach>
    </select>
    <br><br>

    <button type="submit">Add Student</button>
</form>

<hr>

<!-- 2. Filter by Department Form -->
<h2>Filter by Department</h2>
<form action="${pageContext.request.contextPath}/students" method="get">
    <input type="hidden" name="action" value="filter">

    <label>Select Department:</label>
    <select name="department">
        <option value="">-- All Departments --</option>
        <c:forEach var="department" items="${departments}">
            <option value="${department.name}" ${department.name == selectedDepartment ? 'selected' : ''}>
                ${department.name}
            </option>
        </c:forEach>
    </select>

    <button type="submit">Filter</button>
    <a href="${pageContext.request.contextPath}/students" style="text-decoration:none;">
        <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/students';">Reset</button>
    </a>
</form>

<hr>

<!-- 3. Student List Table -->
<h2>Student List</h2>
<table border="1" cellpadding="10">
    <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Age</th>
            <th>Department</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="student" items="${students}">
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.age}</td>
                <td>${student.department != null ? student.department.name : 'N/A'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/students?action=edit&id=${student.id}">Edit</a>
                    &nbsp;|&nbsp;
                    <a href="${pageContext.request.contextPath}/students?action=delete&id=${student.id}"
                       onclick="return confirm('Delete this student?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<br>
<a href="${pageContext.request.contextPath}/index.html">Home</a>

</body>
</html>