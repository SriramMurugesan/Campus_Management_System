<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Student</title>
</head>
<body>

<h1>Edit Student</h1>

<form action="${pageContext.request.contextPath}/students" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${student.id}">

    <label>ID:</label>
    <span>${student.id}</span>
    <br><br>

    <label>Name:</label>
    <input type="text" name="name" value="${student.name}" required>
    <br><br>

    <label>Age:</label>
    <input type="number" name="age" value="${student.age}" required>
    <br><br>

    <label>Department:</label>
    <select name="departmentId" required>
        <c:forEach var="department" items="${departments}">
            <option value="${department.id}" ${student.department != null && department.id == student.department.id ? 'selected' : ''}>
                ${department.name}
            </option>
        </c:forEach>
    </select>
    <br><br>

    <button type="submit">Update Student</button>
</form>

<br>
<a href="${pageContext.request.contextPath}/students">Back to Students</a>

</body>
</html>
