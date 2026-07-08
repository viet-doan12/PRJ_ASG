<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="finalMessage" value="${requestScope.message}" />
<c:if test="${empty finalMessage}">
    <c:set var="finalMessage" value="${sessionScope.session_message}" />
</c:if>

<c:set var="finalError" value="${requestScope.error}" />
<c:if test="${empty finalError}">
    <c:set var="finalError" value="${sessionScope.session_error}" />
</c:if>

<c:if test="${not empty finalMessage}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-check-circle-fill me-2 fs-5"></i>
            <span>${finalMessage}</span>
        </div>
        <button type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"></button>
    </div>
    <c:remove var="session_message" scope="session"/>
</c:if>

<c:if test="${not empty finalError}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
            <span>${finalError}</span>
        </div>
        <button type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"></button>
    </div>
    <c:remove var="session_error" scope="session"/>
</c:if>

<c:if test="${not empty requestScope.warning}">
    <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-exclamation-circle-fill me-2 fs-5"></i>
            <span>${requestScope.warning}</span>
        </div>
        <button type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty requestScope.info}">
    <div class="alert alert-info alert-dismissible fade show shadow-sm" role="alert">
        <div class="d-flex align-items-center">
            <i class="bi bi-info-circle-fill me-2 fs-5"></i>
            <span>${requestScope.info}</span>
        </div>
        <button type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"></button>
    </div>
</c:if>

<script>
setTimeout(function () {
    document.querySelectorAll(".alert").forEach(function (alertElement) {
        bootstrap.Alert.getOrCreateInstance(alertElement).close();
    });
}, 5000);
</script>