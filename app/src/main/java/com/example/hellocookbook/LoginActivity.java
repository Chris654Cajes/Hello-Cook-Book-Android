package com.example.hellocookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Models.Result;
import Models.UserLoginDataInput;
import Sessions.SessionManagement;

/*
    The user uses his/her username and password to login to the app
*/

public class LoginActivity extends AppCompatActivity {
    private EditText txt_username, txt_password;
    private TextView error_message, txt_login;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);

        error_message = findViewById(R.id.error_message);

        txt_login = findViewById(R.id.sign_up_redirect);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        checkSession();
    }

    // This method checks if the user has already logged in, if yes, automatically move to main activity
    private void checkSession() {
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);

        String username = sessionManagement.getSessionUsername();

        if (!username.isEmpty()) {
            moveToMainActivity();
        }
    }

    // This method logs in with correct form validations
    private void loginUser() {
        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("Username", txt_username.getText().toString());
            requestBody.put("Password", txt_password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        // API call for check if the user exists in the database and logged in to main if exists
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.253:80/user/login", requestBody, response -> {
            Result result = new Result();

            // Validation of forms that should be completed in order for function to succeed
            try {
                if (response.has("jsonResultObject") && !response.isNull("jsonResultObject")) {
                    result.setJsonResultObject(response.getJSONObject("jsonResultObject"));
                }

                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                if (!result.isSuccess()) {
                    error_message.setText(result.getMessage());
                    error_message.setVisibility(View.VISIBLE);
                } else {
                    error_message.setText("");
                    error_message.setVisibility(View.GONE);

                    UserLoginDataInput userLoginDataInput = new UserLoginDataInput(result.getJsonResultObject().getString("username"), result.getJsonResultObject().getString("password"));

                    // Saves the session in the app
                    SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                    sessionManagement.saveSession(userLoginDataInput);

                    moveToMainActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }

    // This method moves the user to main activity if logged in
    private void moveToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}