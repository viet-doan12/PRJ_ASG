<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Chủ - Cửa Hàng Hoa</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
        }
        
        /* Top Header Styling (Mockup) */
        .top-header {
            background-color: #fff;
            padding: 15px 0;
            border-bottom: 1px solid #ebebeb;
        }
        .logo {
            font-size: 28px;
            font-weight: 700;
            color: #3bb77e;
            text-decoration: none;
        }
        
        /* Category Navigation Bar Styling (Replicating flowercorner.vn) */
        .category-nav-wrapper {
            background-color: #ffffff;
            position: sticky;
            top: 0;
            z-index: 1030;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
        }
        
        .category-list {
            display: flex;
            justify-content: center;
            list-style: none;
            margin: 0;
            padding: 0;
            flex-wrap: wrap;
        }
        
        .category-item {
            position: relative;
        }
        
        .category-link {
            display: block;
            padding: 16px 20px;
            color: #333333;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: color 0.3s ease;
        }
        
        .category-link:hover {
            color: #3bb77e;
        }
        
        /* Dropdown Icon effect */
        .category-link i {
            margin-left: 5px;
            font-size: 12px;
            transition: transform 0.3s;
        }
        
        .category-link:hover i {
            transform: rotate(180deg);
        }
        
        /* Promotion Highlight */
        .promo-link {
            color: #e53935 !important;
        }
        .promo-link:hover {
            color: #c62828 !important;
        }

        /* Banner mockup */
        .hero-banner {
            background: linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url('https://images.unsplash.com/photo-1563241527-3004b7be0ffd?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80') center/cover;
            height: 400px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-align: center;
            margin-bottom: 40px;
        }
        
        .hero-title {
            font-size: 3rem;
            font-weight: 700;
            margin-bottom: 15px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
        }
        
        .hero-subtitle {
            font-size: 1.2rem;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
        }
    </style>
</head>
<body>

    <!-- Mockup Header -->
    <header class="top-header">
        <div class="container d-flex justify-content-between align-items-center">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <i class="fa-solid fa-seedling"></i> FlowerCorner Clone
            </a>
            <div class="search-bar w-50 position-relative">
                <input type="text" class="form-control rounded-pill px-4" placeholder="Tìm kiếm hoa...">
                <button class="btn position-absolute top-50 end-0 translate-middle-y me-2 border-0 bg-transparent text-muted">
                    <i class="fa-solid fa-magnifying-glass"></i>
                </button>
            </div>
            <div class="user-actions">
                <a href="#" class="text-dark me-3 text-decoration-none"><i class="fa-regular fa-user"></i> Tài khoản</a>
                <a href="#" class="text-dark text-decoration-none"><i class="fa-solid fa-cart-shopping"></i> Giỏ hàng</a>
            </div>
        </div>
    </header>

    <!-- THIẾT KẾ PHẦN CATEGORY THEO YÊU CẦU CỦA NGƯỜI DÙNG -->
    <nav class="category-nav-wrapper">
        <div class="container">
            <ul class="category-list">
                <c:if test="${not empty categories}">
                    <c:forEach var="category" items="${categories}" varStatus="status">
                        <li class="category-item">
                            <!-- Nổi bật 1 category làm khuyến mãi để giống thiết kế của flowercorner -->
                            <a href="${pageContext.request.contextPath}/home?categoryId=${category.categoryID}" 
                               class="category-link ${status.index == categories.size() - 1 ? 'promo-link' : ''}">
                                ${category.categoryName}
                                <!-- Thêm icon caret-down giả lập dropdown nếu không phải mục cuối -->
                                <c:if test="${status.index != categories.size() - 1}">
                                    <i class="fa-solid fa-chevron-down"></i>
                                </c:if>
                            </a>
                        </li>
                    </c:forEach>
                </c:if>
                <c:if test="${empty categories}">
                    <li class="category-item"><a href="#" class="category-link">HOA SINH NHẬT <i class="fa-solid fa-chevron-down"></i></a></li>
                    <li class="category-item"><a href="#" class="category-link">HOA KHAI TRƯƠNG <i class="fa-solid fa-chevron-down"></i></a></li>
                    <li class="category-item"><a href="#" class="category-link">LAN HỒ ĐIỆP <i class="fa-solid fa-chevron-down"></i></a></li>
                    <li class="category-item"><a href="#" class="category-link">CHỦ ĐỀ <i class="fa-solid fa-chevron-down"></i></a></li>
                    <li class="category-item"><a href="#" class="category-link promo-link">GIẢM 30%</a></li>
                </c:if>
            </ul>
        </div>
    </nav>
    <!-- KẾT THÚC PHẦN CATEGORY -->

    <!-- Hero Banner Mockup -->
    <section class="hero-banner">
        <div>
            <h1 class="hero-title">Trao Gửi Yêu Thương</h1>
            <p class="hero-subtitle">Dịch vụ điện hoa tận nơi uy tín, chất lượng nhất</p>
        </div>
    </section>

    <!-- Main Content Area -->
    <main class="container mb-5">
        <div class="row">
            <div class="col-12 text-center">
                <h2 class="mb-4">Sản Phẩm Nổi Bật</h2>
                <p class="text-muted">Chọn một danh mục ở thanh menu phía trên để xem chi tiết hoa.</p>
            </div>
        </div>
    </main>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
