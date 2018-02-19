package message;

public class LogoutMessage extends Message {
    private int userID;

    public LogoutMessage(){super("LogoutMessage");}


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
