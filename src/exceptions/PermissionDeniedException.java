package exceptions;

/**
 * throws when permission denied
 */
public class PermissionDeniedException extends Exception {

	public PermissionDeniedException() {
		super("Permission denied!");
	}
	
	public PermissionDeniedException(String msg) {
		super("Permission denied: " + msg);
	}
	
}
