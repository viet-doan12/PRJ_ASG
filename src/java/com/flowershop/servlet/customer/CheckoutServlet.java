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

        try {
            int cartID = cartDAO.getCartIDByUserID(user.getUserID());
            List<CartItem> cartItems = cartDAO.getCartItemsFromDB(cartID);

            if (cartItems.isEmpty()) {
                session.setAttribute("session_error", "Giỏ hàng của bạn đang trống, không thể thanh toán.");
                response.sendRedirect(request.getContextPath() + "/cart");
return;
            }

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
        } finally {
            cartDAO.closeConnection();
        }
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

        CartDAO cartDAO = new CartDAO();
        FlowerDAO flowerDAO = new FlowerDAO();
        OrderDAO orderDAO = new OrderDAO();
        PaymentDAO paymentDAO = new PaymentDAO();

        try {
            int cartID = cartDAO.getCartIDByUserID(user.getUserID());
            List<CartItem> cartItems = cartDAO.getCartItemsFromDB(cartID);

            if (cartItems.isEmpty()) {
                session.setAttribute("session_error", "Giỏ hàng của bạn đang trống, không thể thanh toán.");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

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

            Order order = new Order();
            order.setUserID(user.getUserID());
            order.setReceiverName(receiverName.trim());
            order.setReceiverPhone(receiverPhone.trim());
            order.setShippingAddress(shippingAddress.trim());
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending");

            int newOrderID = orderDAO.createOrder(order, orderDetails);

            if (newOrderID <= 0) {
                session.setAttribute("session_error",
                        "Đặt hàng thất bại (có thể do một sản phẩm vừa hết hàng). Vui lòng kiểm tra lại giỏ hàng.");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            Payment payment = new Payment();
            payment.setOrderID(newOrderID);
payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus("Pending");
            payment.setTransactionCode(null);

            boolean paymentSaved = paymentDAO.insertPayment(payment);

            if (!paymentSaved) {
                System.out.println("[CANH BAO] Da tao Order #" + newOrderID
                        + " nhung KHONG luu duoc Payment. Can kiem tra thu cong.");
            }

            cartDAO.clearCart(cartID);

            session.setAttribute("session_message", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại Flower Shop.");
            response.sendRedirect(request.getContextPath() + "/orders");
        } finally {
            cartDAO.closeConnection();
            flowerDAO.closeConnection();
            orderDAO.closeConnection();
            paymentDAO.closeConnection();
        }
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