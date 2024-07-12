package Models;

/*
    This class is the structure of a particular userLoginDataInput to be used
    for login using the user input
*/

public class UserLoginDataInput {
    private String username;
    private String password;

    public UserLoginDataInput(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
