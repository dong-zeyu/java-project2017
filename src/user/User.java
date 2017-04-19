package user;

public abstract class User {
	
	public static int ID = 0; //Plus one when create a new user and current is set to userID
	protected int userID; //ID is unchangeable.
	protected String userName;
	protected String passHash; //using sha-256;
	
	protected void changePass(String newPass) { 
		// TODO password change should be written to file immediately
	}
}
