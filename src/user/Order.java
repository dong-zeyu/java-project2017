package user;

import java.util.Date;

import exceptions.StatusUnavailableException;
import flight.Flight;

public class Order {
	
	private Passenger passenger;
	private int seat;
	private Flight flight;
	private Date creatDate;
	private OrderStatus status;
	
	public Order(Passenger passenger, Flight flight, int seat) {
		this.passenger = passenger;
		this.flight = flight;
		this.seat = seat;
		creatDate = new Date(); //now
		status = OrderStatus.UNPAID;
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
		return creatDate;
	}
	
	public void setCreatDate(Date creatDate) {
		this.creatDate = creatDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	public void printOrder() throws StatusUnavailableException {
		// TODO(Peng) printOrder
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
	
	public void displayOrder() throws StatusUnavailableException {
		
	}
}
