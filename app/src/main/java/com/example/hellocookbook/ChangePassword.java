package com.example.hellocookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Models.Result;

import Sessions.SessionManagement;

/*
    This activity allow the user to change password
*/

public class ChangePassword extends AppCompatActivity {
    private EditText txt_password, txt_confirm_password;
    private Button btn_change_password;
    private TextView error_message;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // This intent variable gets the data from the intent from the previous activity
        Intent intentUserId = getIntent();
        userId = intentUserId.getIntExtra("id", 0);

        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);

        error_message = findViewById(R.id.error_message);

        btn_change_password = findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    // This method is called to change password
    private void changePassword () {
        String password = txt_password.getText().toString();
        String confirmPassword = txt_confirm_password.getText().toString();

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", userId);
            requestBody.put("password", password);
            requestBody.put("confirmpassword", confirmPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(ChangePassword.this);

        // API call for changing the password
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, this.getString(R.string.api_url) + "user/changePassword", requestBody, response -> {
            Result result = new Result();

            // Validation of forms that should be completed in order for function to succeed
            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                if (result.isSuccess()) {
                    error_message.setText("");
                    error_message.setVisibility(View.GONE);

                    // Remove session for logout
                    SessionManagement sessionManagement = new SessionManagement(ChangePassword.this);
                    sessionManagement.removeSession();

                    // Logout the user after changing the password
                    Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    error_message.setText(result.getMessage());
                    error_message.setVisibility(View.VISIBLE);
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
}