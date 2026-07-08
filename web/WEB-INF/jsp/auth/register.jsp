<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
</head>
<body>

<form action="${pageContext.request.contextPath}/register" method="post">

    Full Name
    <input type="text" name="fullName"><br><br>

    Email
    <input type="email" name="email"><br><br>

    Phone
    <input type="text" name="phone"><br><br>

    Address
    <input type="text" name="address"><br><br>

    Password
    <input type="password" name="password"><br><br>

    Confirm Password
    <input type="password" name="confirmPassword"><br><br>

    <button type="submit">
        Register
    </button>

</form>

${error}

</body>
</html>