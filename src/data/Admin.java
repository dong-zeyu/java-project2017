package data;

public class Admin extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 799118982657424310L;

	public Admin(String userName, String password) {
		this.userName = userName;
		this.userID = User.ID;
		User.ID++;
		passHash = hashPass(password);
	}
	
}
