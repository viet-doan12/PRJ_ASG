import com.flowershop.dao.UserDAO;
import com.flowershop.model.User;
import com.flowershop.util.DBContext;


public class TestMain extends DBContext {

    public static void main(String[] args) {

         UserDAO dao = new UserDAO();

        for (User u : dao.getAllUsers()) {
            System.out.println("-------------------------");
            System.out.println("ID: " + u.getUserID());
            System.out.println("Name: " + u.getFullName());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Phone: " + u.getPhone());
            System.out.println("RoleID: " + u.getRoleID());
            System.out.println("Status: " + u.isStatus());
        }
    }
}