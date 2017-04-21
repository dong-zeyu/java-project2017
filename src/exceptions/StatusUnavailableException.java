package exceptions;

import flight.FlightStatus;

/**
 *  throws when status has already changed to not UNPUBLISH or TERMINATE but still trying to change something
 */
public class StatusUnavailableException extends Exception {

	private FlightStatus status;

	public StatusUnavailableException(FlightStatus status) {
		this.status = status;
	}
	
	public StatusUnavailableException() {
		status = null;
	}
	
	public FlightStatus getStatusMessage() {
		return status;
	}
}
