package com.flowershop.servlet.auth;

import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import com.flowershop.util.RoleConstants;
import com.flowershop.util.ValidationUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final String LOGIN_VIEW = "/WEB-INF/jsp/auth/login.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // Đã đăng nhập -> Điều hướng thẳng về khu vực tương ứng, không cho xem lại trang Login
                redirectToDashboard(user, request, response);
                return;
            }
        }

        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");

        // Trim email
        if (email != null) {
            email = email.trim();
        }

        // Giữ lại dữ liệu gửi ngược về JSP nếu đăng nhập lỗi
        request.setAttribute("email", email);
        request.setAttribute("returnUrl", returnUrl);

        // Validate đầu vào trống
        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
            forwardToLogin(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);

        // Sai email hoặc mật khẩu
        if (user == null || !ValidationUtil.matchesPassword(password, user.getPassword())) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            forwardToLogin(request, response);
            return;
        }

        // Tài khoản bị khóa
        if (!user.isStatus()) {
            request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
            forwardToLogin(request, response);
            return;
        }

        // ===============================
        // Đăng nhập thành công
        // ===============================
        
        // Chống lỗ hổng Session Fixation bằng cách làm mới Session hoàn toàn
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Tạo Session mới bảo mật
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60); // Đặt hết hạn sau 30 phút

        // Chống lỗ hổng bảo mật Open Redirect: returnUrl hợp lệ bắt buộc phải bắt đầu bằng "/" 
        // nhưng KHÔNG ĐƯỢC bắt đầu bằng "//" hoặc "/\" để tránh đánh lừa trình duyệt chuyển hướng ra ngoài domain.
        if (returnUrl != null 
                && !returnUrl.isBlank() 
                && returnUrl.startsWith("/") 
                && !returnUrl.startsWith("//") 
                && !returnUrl.startsWith("/\\")) {

            response.sendRedirect(request.getContextPath() + returnUrl);
            return;
        }

        // Nếu không có link cũ cần quay lại, thực hiện điều hướng mặc định theo Role
        redirectToDashboard(user, request, response);
    }

    /**
     * Hàm phụ trợ điều hướng theo quyền hạn (Role) của User để tái sử dụng code sạch hơn
     */
    private void redirectToDashboard(User user, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String roleName = user.getRoleName();

        if (RoleConstants.ADMIN.equalsIgnoreCase(roleName)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (RoleConstants.STAFF.equalsIgnoreCase(roleName)) {
            response.sendRedirect(request.getContextPath() + "/staff/orders");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher(LOGIN_VIEW);
        rd.forward(request, response);
    }
}