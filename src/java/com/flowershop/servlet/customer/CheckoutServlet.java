package com.flowershop.servlet.customer;

import com.flowershop.dao.CartDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.dao.OrderDAO;
import com.flowershop.dao.PaymentDAO;
import com.flowershop.model.CartItem;
import com.flowershop.model.Flower;
import com.flowershop.model.Order;
import com.flowershop.model.OrderDetail;
import com.flowershop.model.Payment;
import com.flowershop.model.User;
import com.flowershop.util.ValidationUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller xử lý luồng thanh toán (Checkout) của Customer.
 * GET  /checkout -> hiển thị trang xác nhận đơn hàng (thông tin giỏ hàng + form nhận hàng).
 * POST /checkout -> tạo Order + OrderDetails (qua OrderDAO, có transaction + trừ tồn kho),
 *                    lưu Payment, xóa giỏ hàng, rồi chuyển hướng sang lịch sử đơn hàng.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    // Phải khớp với CHECK constraint của cột PaymentMethod trong bảng Payments
    private static final String[] VALID_PAYMENT_METHODS = {"COD", "Bank Transfer"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showCheckoutPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        placeOrder(request, response);
    }

    // ==========================================================
    // GET: Hiển thị trang xác nhận đơn hàng
    // ==========================================================
    private void showCheckoutPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/checkout");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CartDAO cartDAO = new CartDAO();
        int cartID = cartDAO.getCartIDByUserID(user.getUserID());
        List<CartItem> cartItems = cartDAO.getCartItemsFromDB(cartID);

        if (cartItems.isEmpty()) {
            session.setAttribute("session_error", "Giỏ hàng của bạn đang trống, không thể thanh toán.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Kiểm tra lại tồn kho trước khi cho vào trang thanh toán
        // (đề phòng sản phẩm vừa hết hàng sau khi khách đã thêm vào giỏ)
        List<String> outOfStockNames = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            Flower flower = item.getFlower();
            if (flower == null || !flower.isStatus() || item.getQuantity() > flower.getStockQuantity()) {
                outOfStockNames.add(flower != null ? flower.getFlowerName() : "Sản phẩm không xác định");
            } else {
                total = total.add(flower.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        if (!outOfStockNames.isEmpty()) {
            session.setAttribute("session_error",
                    "Một số sản phẩm không đủ số lượng tồn kho: " + String.join(", ", outOfStockNames)
                    + ". Vui lòng cập nhật lại giỏ hàng.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", total);
        request.getRequestDispatcher("/WEB-INF/jsp/customer/checkout.jsp").forward(request, response);
    }

    // ==========================================================
    // POST: Xử lý đặt hàng
    // ==========================================================
    private void placeOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String receiverName = request.getParameter("receiverName");
        String receiverPhone = request.getParameter("receiverPhone");
        String shippingAddress = request.getParameter("shippingAddress");
        String paymentMethod = request.getParameter("paymentMethod");

        // 1. VALIDATE dữ liệu người nhận (dùng chung ValidationUtil của Leader)
        if (!ValidationUtil.isValidFullName(receiverName)) {
            session.setAttribute("session_error", "Họ tên người nhận không hợp lệ (2-100 ký tự).");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        if (!ValidationUtil.isValidPhone(receiverPhone)) {
            session.setAttribute("session_error", "Số điện thoại không hợp lệ. Định dạng: 10 số, bắt đầu bằng 0.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        if (ValidationUtil.isEmpty(shippingAddress)) {
            session.setAttribute("session_error", "Vui lòng nhập địa chỉ giao hàng.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }
        if (!isValidPaymentMethod(paymentMethod)) {
            session.setAttribute("session_error", "Vui lòng chọn phương thức thanh toán hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        // 2. LẤY GIỎ HÀNG TỪ DATABASE (không tin số lượng/giá gửi lên từ client)
        CartDAO cartDAO = new CartDAO();
        FlowerDAO flowerDAO = new FlowerDAO();
        int cartID = cartDAO.getCartIDByUserID(user.getUserID());
        List<CartItem> cartItems = cartDAO.getCartItemsFromDB(cartID);

        if (cartItems.isEmpty()) {
            session.setAttribute("session_error", "Giỏ hàng của bạn đang trống, không thể thanh toán.");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // 3. KIỂM TRA LẠI TỒN KHO LẦN CUỐI + XÂY DANH SÁCH OrderDetail
        //    (giá lấy từ Flowers tại thời điểm đặt hàng, không lấy từ form)
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            Flower flower = flowerDAO.getFlowerById(item.getFlower().getFlowerID());

            if (flower == null || !flower.isStatus() || item.getQuantity() > flower.getStockQuantity()) {
                session.setAttribute("session_error",
                        "Sản phẩm \"" + (flower != null ? flower.getFlowerName() : "")
                        + "\" không đủ số lượng tồn kho. Vui lòng cập nhật lại giỏ hàng.");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            OrderDetail detail = new OrderDetail();
            detail.setFlowerID(flower.getFlowerID());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(flower.getPrice());
            orderDetails.add(detail);

            totalAmount = totalAmount.add(flower.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 4. TẠO ĐƠN HÀNG (OrderDAO.createOrder tự lo transaction + trừ tồn kho)
        Order order = new Order();
        order.setUserID(user.getUserID());
        order.setReceiverName(receiverName.trim());
        order.setReceiverPhone(receiverPhone.trim());
        order.setShippingAddress(shippingAddress.trim());
        order.setTotalAmount(totalAmount);
        order.setStatus("Pending");

        OrderDAO orderDAO = new OrderDAO();
        boolean orderCreated = orderDAO.createOrder(order, orderDetails);

        if (!orderCreated) {
            session.setAttribute("session_error", "Đặt hàng thất bại. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        // 5. LƯU THÔNG TIN THANH TOÁN CHO ĐƠN VỪA TẠO
        int newOrderID = orderDAO.getLastInsertedOrderID();
        if (newOrderID != -1) {
            Payment payment = new Payment();
            payment.setOrderID(newOrderID);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus("Pending");
            payment.setTransactionCode(null);

            PaymentDAO paymentDAO = new PaymentDAO();
            paymentDAO.insertPayment(payment);
        }

        // 6. XÓA GIỎ HÀNG SAU KHI ĐẶT HÀNG THÀNH CÔNG
        cartDAO.clearCart(cartID);

        // 7. THÔNG BÁO & CHUYỂN HƯỚNG SANG LỊCH SỬ ĐƠN HÀNG (/orders)
        session.setAttribute("session_message", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại Flower Shop.");
        response.sendRedirect(request.getContextPath() + "/orders");
    }

    private boolean isValidPaymentMethod(String method) {
        if (method == null) {
            return false;
        }
        for (String valid : VALID_PAYMENT_METHODS) {
            if (valid.equals(method)) {
                return true;
            }
        }
        return false;
    }
}