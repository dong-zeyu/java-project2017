package exceptions;

import flight.FlightStatus;

public class StatusUnavailableException extends Exception {

	/**
	 *  throws when status has already changed to not UNPUBLISH or TERMINATE but still trying to change something
	 */
	private static final long serialVersionUID = 4179354336026016114L;
	private FlightStatus status;

	public StatusUnavailableException(FlightStatus status) {
		this.status = status;
	}
	
	public StatusUnavailableException() {
	}
	
	public FlightStatus getStatusMessage() {
		return status;
	}
}
