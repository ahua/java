import java.util.Properties;

public class GetClassPath {
    public static void main(String[] args) {
	String classPath = System.getProperty("java.class.path");
	System.out.println("java.class.path" + classPath);
    }
}
