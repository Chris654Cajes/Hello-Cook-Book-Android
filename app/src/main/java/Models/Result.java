package Models;

import org.json.JSONObject;

/*
    This class is the structure of a particular result to be used by rendering UI
*/

public class Result {
    private JSONObject jsonResultObject;
    private String message;
    private boolean isSuccess;

    public Result() {}

    public JSONObject getJsonResultObject() { return jsonResultObject; }

    public void setJsonResultObject(JSONObject jsonResultObject) { this.jsonResultObject = jsonResultObject; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
