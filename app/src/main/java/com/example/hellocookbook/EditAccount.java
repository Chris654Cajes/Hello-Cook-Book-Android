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
    This activity allow the user to edit his/her account
*/

public class EditAccount extends AppCompatActivity {
    private EditText txt_username, txt_firstname, txt_lastname;
    private Button btn_update_account;
    private TextView error_message;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        // This intent variable gets the data from the intent from the previous activity
        Intent intentUser = getIntent();
        userId = intentUser.getIntExtra("id", 0);

        txt_username = findViewById(R.id.txt_username);
        txt_firstname = findViewById(R.id.txt_firstname);
        txt_lastname = findViewById(R.id.txt_lastname);

        txt_username.setText(intentUser.getStringExtra("username"));
        txt_firstname.setText(intentUser.getStringExtra("firstname"));
        txt_lastname.setText(intentUser.getStringExtra("lastname"));

        error_message = findViewById(R.id.error_message);

        btn_update_account = findViewById(R.id.btn_update_account);
        btn_update_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });
    }

    // This method is called to update an account
    private void updateAccount () {
        String username = txt_username.getText().toString();
        String firstname = txt_firstname.getText().toString();
        String lastname = txt_lastname.getText().toString();

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", userId);
            requestBody.put("username", username);
            requestBody.put("firstname", firstname);
            requestBody.put("lastname", lastname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(EditAccount.this);

        // API call for updating an account
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, this.getString(R.string.api_url) + "user/edit", requestBody, response -> {
            Result result = new Result();

            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                // Validation of forms that should be completed in order for function to succeed
                if (result.isSuccess()) {
                    error_message.setText("");
                    error_message.setVisibility(View.GONE);

                    SessionManagement sessionManagement = new SessionManagement(EditAccount.this);
                    sessionManagement.removeSession();

                    Intent intent = new Intent(EditAccount.this, LoginActivity.class);
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