package data;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1641926112430680231L;
	public static int ID = 0; //Plus one when create a new user and current is set to userID
	protected int userID; //ID is unchangeable.
	protected String userName;
	protected String passHash; //using sha-1;
	
	public void changePass(String newPass) { 
		passHash = hashPass(newPass);
	}
	
	public int getID() {
		return userID;
	}
	
	public String getPassHash() {
		return passHash;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	public static String hashPass(String password) {
	    if (password  ==  null || password.length() == 0){
	        return null;
	    }
		try {
			MessageDigest m = MessageDigest.getInstance("SHA-1");
			m.update(password.getBytes());
			byte messageDigest[] = m.digest();
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) { /* impossible! */ }
		return null;
	}
	
}
