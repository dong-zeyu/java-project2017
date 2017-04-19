package user;

import java.util.Date;

import flight.Flight;

public class Order {
	
	private Passenger passager;
	private String seat;
	private Flight flight;
	private Date creatDate;
	private OrderStatus status;
	
	public Order(Passenger passager, Flight flight) {
		this.passager = passager;
		this.flight = flight;
		this.creatDate = new Date(); //now
		
		// TODO this should be generated randomly
		this.seat = "";		
	}
	
	public void printOrder() {
		// TODO printOrder
	}
}
