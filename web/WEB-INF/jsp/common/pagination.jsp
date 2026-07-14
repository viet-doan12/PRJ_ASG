<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${totalPages > 1}">

    <c:set var="pageSeparator" value="?" />
    <c:if test="${baseUrl.contains('?')}">
        <c:set var="pageSeparator" value="&" />
    </c:if>

    <c:set var="maxStep" value="2"/>
    <c:set var="beginPage" value="${currentPage - maxStep}" />
    <c:set var="endPage" value="${currentPage + maxStep}" />

    <c:if test="${beginPage < 1}">
        <c:set var="beginPage" value="1"/>
        <c:set var="endPage" value="${1 + (maxStep * 2)}"/>
    </c:if>

    <c:if test="${endPage > totalPages}">
        <c:set var="endPage" value="${totalPages}"/>
        <c:set var="beginPage" value="${totalPages - (maxStep * 2)}"/>
        <c:if test="${beginPage < 1}">
            <c:set var="beginPage" value="1"/>
        </c:if>
    </c:if>

    <nav class="mt-4" aria-label="Pagination">
        <ul class="pagination justify-content-center shadow-sm d-inline-flex border rounded">

            <c:if test="${currentPage > 1}">
                <li class="page-item">
                    <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=1" aria-label="Trang đầu">
                        <i class="bi bi-chevron-double-left"></i>
                    </a>
                </li>
            </c:if>

            <c:choose>
                <c:when test="${currentPage == 1}">
                    <li class="page-item disabled">
                        <span class="page-link text-muted">&laquo; Trước</span>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item">
                        <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=${currentPage - 1}" aria-label="Trang trước">
                            &laquo; Trước
                        </a>
                    </li>
                </c:otherwise>
            </c:choose>

            <c:if test="${beginPage > 1}">
                <li class="page-item">
                    <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=1">1</a>
                </li>
                <%-- Chỉ hiện ba chấm nếu khoảng cách từ trang 1 đến beginPage lớn hơn 1 đơn vị --%>
                <c:if test="${beginPage > 2}">
                    <li class="page-item disabled"><span class="page-link text-muted">...</span></li>
                </c:if>
            </c:if>

            <c:forEach begin="${beginPage}" end="${endPage}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <li class="page-item active">
                            <span class="page-link bg-success border-success text-white px-3 fw-bold">${i}</span>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="page-item">
                            <a class="page-link text-success px-3" href="${baseUrl}${pageSeparator}page=${i}">${i}</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${endPage < totalPages}">
                <%-- Chỉ hiện ba chấm nếu khoảng cách từ endPage đến trang cuối lớn hơn 1 đơn vị --%>
                <c:if test="${endPage < totalPages - 1}">
                    <li class="page-item disabled"><span class="page-link text-muted">...</span></li>
                </c:if>
                <li class="page-item">
                    <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=${totalPages}">${totalPages}</a>
                </li>
            </c:if>

            <c:choose>
                <c:when test="${currentPage == totalPages}">
                    <li class="page-item disabled">
                        <span class="page-link text-muted">Sau &raquo;</span>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item">
                        <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=${currentPage + 1}" aria-label="Trang sau">
                            Sau &raquo;
                        </a>
                    </li>
                </c:otherwise>
            </c:choose>

            <c:if test="${currentPage < totalPages}">
                <li class="page-item">
                    <a class="page-link text-success" href="${baseUrl}${pageSeparator}page=${totalPages}" aria-label="Trang cuối">
                        <i class="bi bi-chevron-double-right"></i>
                    </a>
                </li>
            </c:if>

        </ul>
    </nav>
</c:if>
