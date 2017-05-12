package user;

import java.util.Date;

import exceptions.StatusUnavailableException;
import flight.Flight;

public class Order {
	
	private Passenger passenger;
	private int seat;
	private Flight flight;
	private Date createDate;
	private OrderStatus status;
	
	public Order(Passenger passenger, Flight flight, int seat) {
		this.passenger = passenger;
		this.flight = flight;
		this.seat = seat;
		createDate = new Date(); //now
		status = OrderStatus.UNPAID;
	}
	
	@Override
	public int hashCode() {
        return status.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("Passenger: %s\n"
				+ "Flight: %s\n"
				+ "Seat: %d"
				+ "Date: %s"
				+ "Status: %s", 
				passenger.userName,
				flight.getFlightName(),
				seat,
				createDate.toString(),
				status.name());
	}
	
	public Passenger getPassager() {
		return passenger;
	}

	public int getSeat() {
		return seat;
	}

	public Flight getFlight() {
		return flight;
	}

	public Date getCreatDate() {
		return createDate;
	}
	
	public void setCreatDate(Date creatDate) {
		this.createDate = creatDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
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

}
