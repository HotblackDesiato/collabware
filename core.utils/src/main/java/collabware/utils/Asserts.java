package collabware.utils;

public class Asserts {
	public static void assertNotNull(String parameterName, Object parameter) {
		if (parameter == null) 
			throw new IllegalArgumentException("Parameter '"+parameterName+"' must not be null.");
	}

	public static void assertNotEmpty(String parameterName, Object[] array) {
		if (array == null || array.length == 0)
			throw new IllegalArgumentException("Parameter '"+parameterName+"' must not be null or empty.");
	}
}
