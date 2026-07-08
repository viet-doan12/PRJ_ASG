package com.flowershop.util;

/**
 * Utility class hỗ trợ phân trang cho toàn bộ hệ thống.
 *
 * Sử dụng cho: - User Management - Flower Management - Category Management -
 * Order Management - Review Management
 */
public final class PaginationUtil {

    /**
     * Số bản ghi mặc định trên mỗi trang.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Không cho phép tạo đối tượng.
     */
    private PaginationUtil() {
    }

    /**
     * Tính tổng số trang.
     *
     * @param totalRecords tổng số bản ghi
     * @param pageSize số bản ghi mỗi trang
     * @return tổng số trang (trả về 0 nếu không có bản ghi nào)
     */
    public static int getTotalPages(int totalRecords, int pageSize) {

        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (totalRecords <= 0) {
            return 0; // Trả về 0 trang nếu DB trống để ẩn thanh phân trang ở JSP
        }

        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    /**
     * Tính OFFSET cho SQL Server.
     *
     * SQL: OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
     *
     * @param page số trang hiện tại (bắt đầu từ 1)
     * @param pageSize số bản ghi mỗi trang
     * @return offset
     */
    public static int getOffset(int page, int pageSize) {

        if (page < 1) {
            page = 1;
        }

        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return (page - 1) * pageSize;
    }

    /**
     * Parse tham số page từ request.
     *
     * Ví dụ: ?page=2
     */
    public static int parsePage(String pageParam) {

        try {
            int page = Integer.parseInt(pageParam);
            return Math.max(page, 1);
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Parse pageSize từ request.
     *
     * Ví dụ: ?pageSize=20
     */
    public static int parsePageSize(String sizeParam) {

        try {
            int size = Integer.parseInt(sizeParam);
            return size > 0 ? size : DEFAULT_PAGE_SIZE;
        } catch (Exception e) {
            return DEFAULT_PAGE_SIZE;
        }
    }

    /**
     * Chuẩn hóa số trang.
     *
     * Nếu page < 1 -> trả về 1 Nếu page > totalPages -> trả về totalPages
     */
    public static int normalizePage(int page, int totalPages) {

        if (totalPages <= 0) {
            return 1;
        }

        if (page < 1) {
            return 1;
        }

        if (page > totalPages) {
            return totalPages;
        }

        return page;
    }

    /**
     * Kiểm tra còn trang trước hay không.
     */
    public static boolean hasPrevious(int page) {
        return page > 1;
    }

    /**
     * Kiểm tra còn trang sau hay không.
     */
    public static boolean hasNext(int page, int totalPages) {
        return page < totalPages;
    }

    /**
     * Lấy số trang trước.
     */
    public static int previousPage(int page) {
        return Math.max(page - 1, 1);
    }

    /**
     * Lấy số trang tiếp theo.
     */
    public static int nextPage(int page, int totalPages) {
        if (totalPages <= 0) {
            return 1;
        }
        return Math.min(page + 1, totalPages);
    }
}
