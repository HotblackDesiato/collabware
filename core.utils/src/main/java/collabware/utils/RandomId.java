package collabware.utils;

public class RandomId {
	private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	public static String createId(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(randomCharacter());
		}
		return sb.toString();
	}

	private static char randomCharacter() {
		int randomIndex = (int) Math.floor(Math.random() * alphabet.length());
		return alphabet.charAt(randomIndex);
	}
}
