import com.flowershop.util.DBContext;

public class TestMain extends DBContext {

    public static void main(String[] args) {

        TestMain db = new TestMain();

        if (db.connection != null) {
            System.out.println("Connected successfully!");
        } else {
            System.out.println("Connection failed!");
        }
    }
}