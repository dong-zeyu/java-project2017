package user;

public class Admin extends User {
	
	public Admin(String userName, String password) {
		this(userName, password, false);
	}
	
	public Admin(String userName, String password, boolean isPassHash) {
		this.userName = userName;
		this.userID = User.ID;
		User.ID++;
		if (isPassHash) {
			passHash = password;
		} else {
			passHash = hashPass(password);			
		}
	}
	
}
