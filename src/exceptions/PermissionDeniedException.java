package exceptions;

/**
 * throws when permission denied
 */
public class PermissionDeniedException extends Exception {

	public PermissionDeniedException() {
		super();
	}
	
	public PermissionDeniedException(String msg) {
		super(msg);
	}
	
}
