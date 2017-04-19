package exceptions;

public class StatusUnavailableException extends Exception {

	/**
	 * default
	 */
	private static final long serialVersionUID = 1L;
	private String cause = null;

	public StatusUnavailableException(String cause) {
		// TODO Auto-generated constructor stub
		this.cause = cause;
	}
	
	public StatusUnavailableException() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCauseMessage() {
		return cause;
	}
}
