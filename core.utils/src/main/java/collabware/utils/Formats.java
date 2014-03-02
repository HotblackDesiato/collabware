package collabware.utils;

public class Formats {
	private Formats(){}
	
	public static String format(String s, Object ...args) {
		for (int i = 0; i < args.length; i++) {
			s = s.replace("{"+i+"}", args[i].toString());
		}
		return s;
	}
}
