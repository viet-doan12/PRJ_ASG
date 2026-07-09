<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<div class="row justify-content-center mb-5">

    <div class="col-lg-5 col-md-7 col-sm-10">

        <div class="card shadow border-0">

            <div class="card-body p-4">

                <h2 class="text-center text-success fw-bold mb-3">
                    <i class="bi bi-shield-lock-fill me-2"></i>
                    Đặt lại mật khẩu
                </h2>

                <p class="text-center text-muted mb-4">
                    Vui lòng nhập mật khẩu mới cho tài khoản của bạn.
                </p>

                <form action="${pageContext.request.contextPath}/reset-password"
                      method="post">

                    <!-- Mật khẩu mới -->
                    <div class="mb-3">

                        <label for="newPassword"
                               class="form-label fw-medium">
                            Mật khẩu mới
                        </label>

                        <div class="input-group">

                            <span class="input-group-text bg-light">
                                <i class="bi bi-lock-fill"></i>
                            </span>

                            <input
                                    type="password"
                                    class="form-control"
                                    id="newPassword"
                                    name="newPassword"
                                    placeholder="Nhập mật khẩu mới"
                                    required>

                            <button class="btn btn-outline-secondary"
                                    type="button"
                                    id="toggleNewPassword">
                                <i class="bi bi-eye" id="eyeNew"></i>
                            </button>

                        </div>

                    </div>

                    <!-- Xác nhận -->
                    <div class="mb-4">

                        <label for="confirmPassword"
                               class="form-label fw-medium">
                            Xác nhận mật khẩu
                        </label>

                        <div class="input-group">

                            <span class="input-group-text bg-light">
                                <i class="bi bi-lock-fill"></i>
                            </span>

                            <input
                                    type="password"
                                    class="form-control"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    placeholder="Nhập lại mật khẩu"
                                    required>

                            <button class="btn btn-outline-secondary"
                                    type="button"
                                    id="toggleConfirmPassword">
                                <i class="bi bi-eye"
                                   id="eyeConfirm"></i>
                            </button>

                        </div>

                    </div>

                    <button type="submit"
                            class="btn btn-success w-100 py-2 fw-bold">

                        <i class="bi bi-arrow-repeat me-2"></i>

                        Đặt lại mật khẩu

                    </button>

                </form>

                <hr class="my-4">

                <div class="text-center">

                    <a href="${pageContext.request.contextPath}/login"
                       class="text-decoration-none">

                        <i class="bi bi-arrow-left"></i>

                        Quay lại đăng nhập

                    </a>

                </div>

            </div>

        </div>

    </div>

</div>

<script>

function togglePassword(buttonId, inputId, iconId){

    const btn = document.getElementById(buttonId);
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);

    btn.addEventListener("click", function(){

        if(input.type === "password"){

            input.type = "text";

            icon.classList.remove("bi-eye");
            icon.classList.add("bi-eye-slash");

        }else{

            input.type = "password";

            icon.classList.remove("bi-eye-slash");
            icon.classList.add("bi-eye");

        }

    });

}

document.addEventListener("DOMContentLoaded", function(){

    togglePassword(
            "toggleNewPassword",
            "newPassword",
            "eyeNew");

    togglePassword(
            "toggleConfirmPassword",
            "confirmPassword",
            "eyeConfirm");

});

</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>