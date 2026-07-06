package com.flowershop.servlet.customer;
 
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.CartItem;
import com.flowershop.model.Flower;
 
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
 
/**
 * CartServlet - quản lý giỏ hàng dựa trên HttpSession (không lưu DB).
 * Cart được lưu dưới dạng Map<FlowerID, CartItem> để dễ update/remove theo id.
 *
 * Các action hỗ trợ (param "action"):
 *   - add     : thêm sản phẩm vào giỏ (hoặc cộng dồn số lượng nếu đã có)
 *   - update  : cập nhật số lượng 1 dòng trong giỏ
 *   - remove  : xóa 1 dòng khỏi giỏ
 *   - clear   : xóa toàn bộ giỏ
 *   - (không truyền action) : chỉ xem giỏ hàng -> forward sang cart.jsp
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
 
    public static final String CART_SESSION_KEY = "CART";
 
    private FlowerDAO flowerDAO;
 
    @Override
    public void init() throws ServletException {
        flowerDAO = new FlowerDAO();
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }
 
    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
 
        try {
            if ("add".equalsIgnoreCase(action)) {
                addToCart(request, session);
            } else if ("update".equalsIgnoreCase(action)) {
                updateQuantity(request, session);
            } else if ("remove".equalsIgnoreCase(action)) {
                removeFromCart(request, session);
            } else if ("clear".equalsIgnoreCase(action)) {
                clearCart(session);
            }
            // action == null -> chỉ view giỏ hàng, không làm gì thêm
        } catch (IllegalArgumentException ex) {
            request.setAttribute("cartError", ex.getMessage());
        }
 
        // Tính tổng tiền để cart.jsp hiển thị (tránh phải tính lại trong JSP)
        Map<Integer, CartItem> cart = getCart(session);
        request.setAttribute("cartTotal", calculateTotal(cart));
 
        // Sau mọi thao tác, forward sang trang cart.jsp để hiển thị kết quả mới nhất
        RequestDispatcher rd = request.getRequestDispatcher("cart.jsp");
        rd.forward(request, response);
    }
 
    // ================= Helper methods =================
 
    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new LinkedHashMap<>(); // giữ thứ tự thêm vào giỏ
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }
 
    private void addToCart(HttpServletRequest request, HttpSession session) {
        int flowerId = parseIntParam(request, "flowerId");
        int quantity = parseIntParam(request, "quantity", 1);
 
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
 
        // Lấy thông tin hoa mới nhất từ DB để đảm bảo giá & tồn kho chính xác
        Flower flower = flowerDAO.getFlowerById(flowerId);
        if (flower == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }
 
        Map<Integer, CartItem> cart = getCart(session);
        CartItem existing = cart.get(flowerId);
 
        int newQuantity = quantity + (existing != null ? existing.getQuantity() : 0);
 
        if (newQuantity > flower.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Chỉ còn " + flower.getStockQuantity() + " sản phẩm trong kho.");
        }
 
        if (existing != null) {
            existing.setQuantity(newQuantity);
        } else {
            CartItem item = new CartItem(
                    flower.getFlowerId(),
                    flower.getFlowerName(),
                    flower.getImage(),
                    flower.getPrice(),
                    newQuantity,
                    flower.getStockQuantity()
            );
            cart.put(flowerId, item);
        }
    }
 
    private void updateQuantity(HttpServletRequest request, HttpSession session) {
        int flowerId = parseIntParam(request, "flowerId");
        int quantity = parseIntParam(request, "quantity", 1);
 
        Map<Integer, CartItem> cart = getCart(session);
        CartItem item = cart.get(flowerId);
        if (item == null) {
            throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng.");
        }
 
        if (quantity <= 0) {
            cart.remove(flowerId);
            return;
        }
 
        if (quantity > item.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Chỉ còn " + item.getStockQuantity() + " sản phẩm trong kho.");
        }
 
        item.setQuantity(quantity);
    }
 
    private void removeFromCart(HttpServletRequest request, HttpSession session) {
        int flowerId = parseIntParam(request, "flowerId");
        getCart(session).remove(flowerId);
    }
 
    private void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
 
    private int parseIntParam(HttpServletRequest request, String name) {
        return parseIntParam(request, name, -1);
    }
 
    private int parseIntParam(HttpServletRequest request, String name, int defaultValue) {
        String raw = request.getParameter(name);
        if (raw == null || raw.trim().isEmpty()) {
            if (defaultValue == -1) {
                throw new IllegalArgumentException("Thiếu tham số " + name);
            }
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Tham số " + name + " không hợp lệ.");
        }
    }
 
    /**
     * Utility tĩnh để CheckoutServlet có thể lấy giỏ hàng hiện tại và tính tổng tiền.
     */
    public static BigDecimal calculateTotal(Map<Integer, CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.values()) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}