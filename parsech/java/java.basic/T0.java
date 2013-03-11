import java.util.HashMap;


public class T0 {
	public static void main(String[] args){
		Integer i = new Integer(0);
		System.out.println(i);
		i = i + 3;
		System.out.println(i);
		
		HashMap hmap = new HashMap();
		hmap.put("hello", 0);
		
		int s = (Integer) hmap.get("hello");
		
		System.out.println(s);
		
		System.out.println(hmap.get("go") == null);
		
	}
}
