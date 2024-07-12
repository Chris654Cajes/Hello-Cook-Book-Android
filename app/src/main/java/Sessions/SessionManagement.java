package Sessions;

import android.content.Context;
import android.content.SharedPreferences;

import Models.UserLoginDataInput;

/*
    This class is used to add session during login and gets the current session for
    other pages to access, after logout the session will be cleared
*/

public class SessionManagement {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String SESSION = "session", SESSION_USERNAME = "session_username", SESSION_PASSWORD = "session_password";

    // Instantiate this class to object for getting the session
    public SessionManagement(Context context) {
        sharedPreferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Add session during login
    public void saveSession(UserLoginDataInput userLoginDataInput) {
        editor.putString(SESSION_USERNAME, userLoginDataInput.getUsername()).commit();
        editor.putString(SESSION_PASSWORD, userLoginDataInput.getPassword()).commit();
    }

    // Get session username
    public String getSessionUsername () { return sharedPreferences.getString(SESSION_USERNAME, ""); }

    // Get session password
    public String getSessionPassword () { return sharedPreferences.getString(SESSION_PASSWORD, ""); }

    // Delete session after logout
    public void removeSession() {
        editor.putString(SESSION_USERNAME, "").commit();
        editor.putString(SESSION_PASSWORD, "").commit();
    }
}
