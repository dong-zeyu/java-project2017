package data;

import java.util.Date;

import exceptions.StatusUnavailableException;

public class Order {
	
	private Passenger passenger;
	private Flight flight;
	private Date createDate;
	private OrderStatus status;
	
	public Order(Passenger passenger, Flight flight, int seat) throws StatusUnavailableException {
		this.passenger = passenger;
		this.flight = flight;
		createDate = new Date(); //now
		status = OrderStatus.UNPAID;
		flight.addPassenger(passenger, seat, true);
	}
	
	public Order(Passenger passenger, Flight flight) throws StatusUnavailableException {
		this.passenger = passenger;
		this.flight = flight;
		createDate = new Date(); //now
		status = OrderStatus.UNPAID;
		flight.addPassenger(passenger);
	}

	@Override
	public int hashCode() {
        return status.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("Passenger: %s\n"
				+ "Flight: %s\n"
				+ "Seat: %d\n"
				+ "Date: %s\n"
				+ "Status: %s\n", 
				passenger.userName,
				flight.getFlightName(),
				getSeat(),
				createDate.toString(),
				status.name());
	}
	
	public Passenger getPassager() {
		return passenger;
	}

	public int getSeat() {
		return flight.passagers().get(passenger);
	}

	public Flight getFlight() {
		return flight;
	}

	public Date getCreatDate() {
		return createDate;
	}
	
	protected void setCreatDate(Date creatDate) {
		this.createDate = creatDate;
	}

	public OrderStatus getStatus() {
		return status;
	}
	
	protected void setStatus(OrderStatus status) {
		this.status = status;
		if (status == OrderStatus.CANCLE) {
			for (Passenger passenger : flight.getPassagers().keySet()) {
				if (passenger == this.passenger) {
					flight.getPassagers().remove(passenger);
				}
			}
		}
	}
	
	public void pay() throws StatusUnavailableException {
		if (status == OrderStatus.UNPAID) {
			status = OrderStatus.PAID;
		} else {
			throw new StatusUnavailableException(status);
		}
	}
	
	public void cancle() throws StatusUnavailableException {
		status = OrderStatus.CANCLE;
		flight.removePassenger(passenger);
	}
	
	public void printOrder() throws StatusUnavailableException {
		// DONE(Peng) printOrder
		if (status==OrderStatus.PAID) {
			System.out.println("Passager :"+getPassager());
			System.out.println("Your Seat :"+getSeat());
			System.out.println("Your Flight :"+getFlight());
			System.out.println("Create date :"+getCreatDate());
			System.out.println("OrderStatus :"+getStatus());
	    	
	    }
		else {
			 throw new StatusUnavailableException();
		}
	}

	public void remove() {
		for (Passenger passenger : flight.getPassagers().keySet()) {
			if (passenger == this.passenger) {
				flight.getPassagers().remove(passenger);
			}
		}
		passenger.getOrderList().remove(this);
		if (flight.getPassagers().size() < flight.getSeatCapacity()) {
			flight.setFlightStatus(FlightStatus.AVAILABLE);
		}
	}

}