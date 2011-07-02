package crashcollector;

import java.util.regex.Pattern;

public final class Patterns {
	private Patterns() {
	}
	
	public static final Pattern issues = Pattern.compile("-?\\d+");
}
