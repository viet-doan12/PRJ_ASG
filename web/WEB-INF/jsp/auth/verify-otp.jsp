<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>

    <title>Verify OTP</title>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

</head>

<body class="bg-light">

<div class="container mt-5">

    <div class="row justify-content-center">

        <div class="col-md-5">

            <div class="card shadow">

                <div class="card-header text-center">

                    <h3>Email Verification</h3>

                </div>

                <div class="card-body">

                    <p class="text-center">

                        A verification code has been sent to

                        <br>

                        <strong>

                            ${sessionScope.tempUser.email}

                        </strong>

                    </p>

                    <!-- Error -->

                    <c:if test="${not empty error}">

                        <div class="alert alert-danger">

                            ${error}

                        </div>

                    </c:if>

                    <!-- Success -->

                    <c:if test="${not empty message}">

                        <div class="alert alert-success">

                            ${message}

                        </div>

                    </c:if>

                    <form action="${pageContext.request.contextPath}/verify-otp"

                          method="post">

                        <div class="mb-3">

                            <label class="form-label">

                                Verification Code

                            </label>

                            <input

                                type="text"

                                name="otp"

                                maxlength="6"

                                class="form-control"

                                placeholder="Enter 6-digit code"

                                required>

                        </div>

                        <button

                                class="btn btn-primary w-100"

                                type="submit">

                            Verify

                        </button>

                    </form>

                    <hr>

                    <div class="text-center">

                        Didn't receive the code?

                        <br><br>

                        <a class="btn btn-outline-secondary"

                           href="${pageContext.request.contextPath}/resend-otp">

                            Resend OTP

                        </a>

                    </div>

                </div>

            </div>

        </div>

    </div>

</div>

</body>
</html>