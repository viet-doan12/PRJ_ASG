<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<div class="row justify-content-center mb-5">

    <div class="col-lg-6 col-md-8 col-sm-10">

        <div class="card shadow border-0">

            <div class="card-body text-center p-5">

                <i class="bi bi-shield-lock-fill text-danger"
                   style="font-size:90px;"></i>

                <h1 class="display-3 fw-bold text-danger mt-3">
                    403
                </h1>

                <h3 class="fw-bold mb-3">
                    Truy cập bị từ chối
                </h3>

                <p class="text-muted mb-4">
                    Bạn không có quyền truy cập vào trang này.
                    Nếu bạn cho rằng đây là lỗi, vui lòng liên hệ quản trị viên.
                </p>

                <div class="d-flex justify-content-center gap-3">

                    <a href="${pageContext.request.contextPath}/home"
                       class="btn btn-success">

                        <i class="bi bi-house-door-fill me-2"></i>
                        Trang chủ

                    </a>

                    <button class="btn btn-outline-secondary"
                            onclick="history.back()">

                        <i class="bi bi-arrow-left me-2"></i>
                        Quay lại

                    </button>

                </div>

            </div>

        </div>

    </div>

</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>