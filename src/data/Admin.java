package data;

public class Admin extends User {
	
	public Admin(String userName, String password) {
		this.userName = userName;
		this.userID = User.ID;
		User.ID++;
		passHash = hashPass(password);
	}
	
}
