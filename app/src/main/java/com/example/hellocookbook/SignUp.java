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

/*
    This activity allow the user register new account by signing up
*/

public class SignUp extends AppCompatActivity {
    private TextView txt_login, error_message;
    private EditText txt_username, txt_firstname, txt_lastname, txt_password, txt_confirm_password;
    private Button btn_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txt_username = findViewById(R.id.txt_username);
        txt_firstname = findViewById(R.id.txt_firstname);
        txt_lastname = findViewById(R.id.txt_lastname);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);

        error_message = findViewById(R.id.error_message);

        txt_login = findViewById(R.id.login_redirect);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    // This method is called to add new user to database after sign up
    private void registerNewUser() {
        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("Username", txt_username.getText().toString());
            requestBody.put("Firstname", txt_firstname.getText().toString());
            requestBody.put("Lastname", txt_lastname.getText().toString());
            requestBody.put("Password", txt_password.getText().toString());
            requestBody.put("ConfirmPassword", txt_confirm_password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(SignUp.this);

        // API call for adding the new user to database
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.253:80/user/add", requestBody, response -> {
            Result result = new Result();

            // Validation of forms that should be completed in order for function to succeed
            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                if (!result.isSuccess()) {
                    error_message.setText(result.getMessage());
                    error_message.setVisibility(View.VISIBLE);
                } else {
                    error_message.setText("");
                    error_message.setVisibility(View.GONE);

                    Intent intent = new Intent(SignUp.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(SignUp.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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