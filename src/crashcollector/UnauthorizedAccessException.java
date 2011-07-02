package crashcollector;

public class UnauthorizedAccessException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnauthorizedAccessException() {
		super("Unauthorized access");
	}
}
