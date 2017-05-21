package exceptions;

import data.FlightStatus;
import data.OrderStatus;

/**
 *  throws when status has already changed to not UNPUBLISH or TERMINATE but still trying to change something
 *  or the order status unavailable
 */
@SuppressWarnings("serial")
public class StatusUnavailableException extends Exception {

	public StatusUnavailableException(FlightStatus status) {
		super("flight has status " + status.name());
	}
	
	public StatusUnavailableException(OrderStatus status) {
		super("order has status: " + status.name());
	}
	
	public StatusUnavailableException() {
		super();
	}

	public StatusUnavailableException(String string) {
		super(string);
	}
	
}
