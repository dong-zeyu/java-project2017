package exceptions;

/**
 * throws when permission denied
 */
@SuppressWarnings("serial")
public class PermissionDeniedException extends Exception {

	public PermissionDeniedException() {
		super("Permission denied!");
	}
	
	public PermissionDeniedException(String msg) {
		super("Permission denied: " + msg);
	}
	
}
