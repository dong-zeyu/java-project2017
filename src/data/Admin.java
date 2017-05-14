package data;

public class Admin extends User {
	
	@Override
	public int hashCode() {
        int hashCode = 1;
        hashCode = 31*hashCode + userName.hashCode();
        hashCode = 31*hashCode + passHash.hashCode();
        return hashCode;
	}
	
	public Admin(String userName, String password) {
		this.userName = userName;
		this.userID = User.ID;
		User.ID++;
		passHash = hashPass(password);
	}
	
}
