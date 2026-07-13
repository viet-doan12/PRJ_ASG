package com.flowershop.servlet.customer;

import com.flowershop.dao.CartDAO;
import com.flowershop.dao.FlowerDAO;
import com.flowershop.model.CartItem;
import com.flowershop.model.Flower;
import com.flowershop.model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Controller xử lý luồng nghiệp vụ giỏ hàng của Customer mua hoa
 * Các action hỗ trợ: view (mặc định), add, update, delete, clear
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        // 1. Kiểm tra quyền đăng nhập của Khách hàng từ Session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("redirectAfterLogin", request.getContextPath() + "/cart");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "view";
        }

        CartDAO cartDAO = new CartDAO();
        FlowerDAO flowerDAO = new FlowerDAO();

        try {
            int cartID = cartDAO.getCartIDByUserID(user.getUserID());

            if (cartID == -1) {
                session.setAttribute("errorMessage", "Không thể khởi tạo giỏ hàng. Vui lòng thử lại.");
                response.sendRedirect(request.getContextPath() + "/cart?action=view");
                return;
            }

            switch (action) {
                case "add":
                    handleAdd(request, response, cartDAO, flowerDAO, session, cartID);
                    break;

                case "update":
                    handleUpdate(request, response, cartDAO, flowerDAO, session, cartID);
                    break;

                case "delete":
                    handleDelete(request, response, cartDAO, session, cartID);
                    break;

                case "clear":
                    cartDAO.clearCart(cartID);
                    session.setAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng.");
                    response.sendRedirect(request.getContextPath() + "/cart?action=view");
                    break;

                case "view":
                default:
                    showCart(request, response, cartDAO, session, cartID);
break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/jsp/500.jsp");
        } finally {
            cartDAO.closeConnection();
            flowerDAO.closeConnection();
        }
    }

    // Thêm sản phẩm vào giỏ hàng (có kiểm tra tồn kho)
    private void handleAdd(HttpServletRequest request, HttpServletResponse response,
            CartDAO cartDAO, FlowerDAO flowerDAO, HttpSession session, int cartID)
            throws IOException {

        String flowerIDParam = request.getParameter("flowerID");
        if (flowerIDParam == null || flowerIDParam.isBlank()) {
            session.setAttribute("errorMessage", "Thiếu thông tin sản phẩm.");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
            return;
        }

        int flowerID = Integer.parseInt(flowerIDParam);
        int quantityToAdd = 1;
        String qtyParam = request.getParameter("quantity");
        if (qtyParam != null && !qtyParam.isBlank()) {
            quantityToAdd = Integer.parseInt(qtyParam);
        }

        if (quantityToAdd <= 0) {
            session.setAttribute("errorMessage", "Số lượng phải lớn hơn 0.");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
            return;
        }

        Flower flower = flowerDAO.getFlowerById(flowerID);
        if (flower == null || !flower.isStatus()) {
            session.setAttribute("errorMessage", "Sản phẩm không tồn tại hoặc đã ngừng bán.");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
            return;
        }

        List<CartItem> currentCart = cartDAO.getCartItemsFromDB(cartID);
        int currentQtyInCart = 0;
        for (CartItem item : currentCart) {
            if (item.getFlower().getFlowerID() == flowerID) {
                currentQtyInCart = item.getQuantity();
                break;
            }
        }

        if (currentQtyInCart + quantityToAdd > flower.getStockQuantity()) {
            session.setAttribute("errorMessage",
                    "Số lượng vượt quá tồn kho. Chỉ còn " + flower.getStockQuantity() + " sản phẩm.");
            response.sendRedirect(request.getContextPath() + "/cart?action=view");
            return;
        }

        cartDAO.addItemToCart(cartID, flowerID, quantityToAdd);
        session.setAttribute("successMessage", "Đã thêm \"" + flower.getFlowerName() + "\" vào giỏ hàng.");
        response.sendRedirect(request.getContextPath() + "/cart?action=view");
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
private void handleUpdate(HttpServletRequest request, HttpServletResponse response,
            CartDAO cartDAO, FlowerDAO flowerDAO, HttpSession session, int cartID)
            throws IOException {

        int updateID = Integer.parseInt(request.getParameter("flowerID"));
        int newQuantity = Integer.parseInt(request.getParameter("quantity"));

        if (newQuantity <= 0) {
            cartDAO.deleteCartItem(cartID, updateID);
            session.setAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } else {
            Flower flower = flowerDAO.getFlowerById(updateID);
            if (flower != null && newQuantity > flower.getStockQuantity()) {
                session.setAttribute("errorMessage",
                        "Số lượng vượt quá tồn kho. Chỉ còn " + flower.getStockQuantity() + " sản phẩm.");
            } else {
                cartDAO.updateCartItemQuantity(cartID, updateID, newQuantity);
                session.setAttribute("successMessage", "Đã cập nhật giỏ hàng.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/cart?action=view");
    }

    // Xóa một sản phẩm khỏi giỏ hàng
    private void handleDelete(HttpServletRequest request, HttpServletResponse response,
            CartDAO cartDAO, HttpSession session, int cartID)
            throws IOException {

        int deleteID = Integer.parseInt(request.getParameter("flowerID"));
        cartDAO.deleteCartItem(cartID, deleteID);
        session.setAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        response.sendRedirect(request.getContextPath() + "/cart?action=view");
    }

    // Hiển thị giỏ hàng
    private void showCart(HttpServletRequest request, HttpServletResponse response,
            CartDAO cartDAO, HttpSession session, int cartID)
            throws ServletException, IOException {

        List<CartItem> cartList = cartDAO.getCartItemsFromDB(cartID);

        session.setAttribute("cart", cartList);
        session.setAttribute("cartTotal", calculateTotal(cartList));

        request.getRequestDispatcher("/WEB-INF/jsp/customer/cart.jsp").forward(request, response);
    }

    private BigDecimal calculateTotal(List<CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart != null) {
            for (CartItem item : cart) {
                if (item.getFlower() != null) {
                    BigDecimal price = item.getFlower().getPrice();
                    BigDecimal qty = new BigDecimal(item.getQuantity());
                    total = total.add(price.multiply(qty));
                }
            }
        }
        return total;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}