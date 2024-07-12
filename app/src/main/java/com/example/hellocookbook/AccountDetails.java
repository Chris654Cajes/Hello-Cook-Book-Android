package com.example.hellocookbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/*
    This activity gets the user details and puts them into labels
*/

public class AccountDetails extends AppCompatActivity {
    private TextView txt_username, txt_firstname, txt_lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        txt_username = findViewById(R.id.txt_username);
        txt_firstname = findViewById(R.id.txt_firstname);
        txt_lastname = findViewById(R.id.txt_lastname);

        // This intent variable gets the data from the intent from the previous activity
        Intent intentUser = getIntent();
        txt_username.setText(intentUser.getStringExtra("username"));
        txt_firstname.setText(intentUser.getStringExtra("firstname"));
        txt_lastname.setText(intentUser.getStringExtra("lastname"));
    }
}