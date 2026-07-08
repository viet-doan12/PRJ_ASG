<%@ page contentType="text/html;charset=UTF-8"
         language="java"
         isErrorPage="true" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>

<div class="row justify-content-center mb-5">

    <div class="col-lg-6 col-md-8 col-sm-10">

        <div class="card shadow border-0">

            <div class="card-body text-center p-5">

                <i class="bi bi-exclamation-octagon-fill text-danger"
                   style="font-size:90px;"></i>

                <h1 class="display-3 fw-bold text-danger mt-3">
                    500
                </h1>

                <h3 class="fw-bold mb-3">
                    Đã xảy ra lỗi hệ thống
                </h3>

                <p class="text-muted mb-4">
                    Rất tiếc! Máy chủ đang gặp sự cố khi xử lý yêu cầu của bạn.
                    Vui lòng thử lại sau hoặc liên hệ quản trị viên nếu lỗi vẫn tiếp diễn.
                </p>

                <div class="d-flex justify-content-center gap-3">

                    <a href="${pageContext.request.contextPath}/home"
                       class="btn btn-success">

                        <i class="bi bi-house-door-fill me-2"></i>
                        Trang chủ

                    </a>

                    <button type="button"
                            class="btn btn-outline-secondary"
                            onclick="location.reload()">

                        <i class="bi bi-arrow-clockwise me-2"></i>
                        Thử lại

                    </button>

                </div>

                <%-- Chỉ hiển thị Exception khi đang chạy ở localhost --%>
                <%
                    String server = request.getServerName();
                    if ("localhost".equals(server) && exception != null) {
                %>

                <div class="alert alert-light text-start mt-4">

                    <strong>Developer Debug</strong>

                    <hr>

                    <p class="mb-1">
                        <strong>Exception:</strong>
                    </p>

                    <pre class="small"><%= exception.toString() %></pre>

                </div>

                <%
                    }
                %>

            </div>

        </div>

    </div>

</div>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>