package data;

public class Admin extends User {
	
	public Admin(String userName, String password) {
		this(userName, password, false);
	}
	
	@Override
	public int hashCode() {
        int hashCode = 1;
        hashCode = 31*hashCode + userName.hashCode();
        hashCode = 31*hashCode + passHash.hashCode();
        return hashCode;
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
