<%-- 
    Document   : flower-form
    Author     : ADMIN
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="/WEB-INF/jsp/common/header.jsp" />

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8">

            <div class="mb-3">
                <a href="${pageContext.request.contextPath}/admin/flowers" class="text-success text-decoration-none fw-bold">
                    <i class="bi bi-arrow-left"></i> Quay lại danh sách
                </a>
            </div>

            <div class="card shadow-sm border-0">
                <div class="card-header bg-success text-white py-3">
                    <h5 class="mb-0 fw-bold text-center">
                        <c:choose>
                            <c:when test="${not empty flower}">CẬP NHẬT THÔNG TIN HOA</c:when>
                            <c:otherwise>THÊM SẢN PHẨM HOA MỚI</c:otherwise>
                        </c:choose>
                    </h5>
                </div>

                <div class="card-body p-4">
                    <form action="${pageContext.request.contextPath}/admin/flowers" method="post" enctype="multipart/form-data">

                        <c:if test="${not empty flower}">
                            <input type="hidden" name="flowerID" value="${flower.flowerID}">
                            <input type="hidden" name="action" value="update">
                        </c:if>
                        <c:if test="${empty flower}">
                            <input type="hidden" name="action" value="insert">
                        </c:if>

                        <div class="row g-3">
                            <div class="col-12">
                                <label class="form-label fw-bold">Tên bó hoa <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="flowerName" value="${flower.flowerName}" required>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold">Danh mục <span class="text-danger">*</span></label>
                                <select class="form-select" name="categoryID" required>
                                    <option value="" disabled ${empty flower ? 'selected' : ''}>-- Chọn danh mục --</option>
                                    <c:forEach var="cat" items="${categoryList}">
                                        <option value="${cat.categoryID}" ${flower.categoryID == cat.categoryID ? 'selected' : ''}>
                                            ${cat.categoryName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold">Trạng thái hiển thị</label>
                                <select class="form-select" name="status">
                                    <option value="true" ${flower == null || flower.status ? 'selected' : ''}>Đang bán</option>
                                    <option value="false" ${flower != null && !flower.status ? 'selected' : ''}>Ngừng bán</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold">Giá bán (VNĐ) <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="price" min="0" step="1000" value="${flower != null ? flower.price : ''}" required>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold">Số lượng trong kho <span class="text-danger">*</span></label>
                                <input type="number" class="form-control" name="stockQuantity" min="0" value="${flower != null ? flower.stockQuantity : ''}" required>
                            </div>

                            <div class="col-12">
                                <label class="form-label fw-bold">Hình ảnh sản phẩm</label>
                                <input type="file" class="form-control mb-2" name="imageFile" id="imgUpload" accept="image/*">

                                <div class="text-center bg-light border rounded p-2" style="min-height: 120px;">
                                    <c:choose>
                                        <c:when test="${not empty flower.image}">
                                            <img id="imgPreview" src="${pageContext.request.contextPath}/images/flowers/${flower.image}" style="max-height: 150px; object-fit: contain;">
                                        </c:when>
                                        <c:otherwise>
                                            <img id="imgPreview" src="" style="max-height: 150px; object-fit: contain; display: none;">
                                            <span id="imgPlaceholder" class="text-muted" style="${not empty flower.image ? 'display:none;' : ''}">Chưa có ảnh</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <div class="col-12">
                                <label class="form-label fw-bold">Mô tả chi tiết</label>
                                <textarea class="form-control" name="description" rows="4">${flower.description}</textarea>
                            </div>
                        </div>

                        <div class="d-flex justify-content-end gap-2 mt-4">
                            <a href="${pageContext.request.contextPath}/admin/flowers" class="btn btn-secondary px-4">Hủy</a>
                            <button type="submit" class="btn btn-success px-4 fw-bold">Lưu Dữ Liệu</button>
                        </div>

                    </form>
                </div>
            </div>

        </div>
    </div>
</div>

<script>
    document.getElementById('imgUpload').addEventListener('change', function(event) {
        const file = event.target.files[0];
        const preview = document.getElementById('imgPreview');
        const placeholder = document.getElementById('imgPlaceholder');

        if (file) {
            preview.src = URL.createObjectURL(file);
            preview.style.display = 'inline-block';
            if (placeholder) placeholder.style.display = 'none';
        }
    });
</script>

<jsp:include page="/WEB-INF/jsp/common/footer.jsp" />