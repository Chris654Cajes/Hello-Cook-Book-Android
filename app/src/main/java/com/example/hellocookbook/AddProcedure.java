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

import Models.Procedure;
import Models.Result;

/*
    This activity lets the user to add or edit a procedure to the recipe
 */

public class AddProcedure extends AppCompatActivity {
    private EditText txt_procedure;
    private Button btn_add_procedure;
    private TextView error_message;
    private Procedure procedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_procedure);

        error_message = findViewById(R.id.error_message);

        procedure = new Procedure();

        // This intent variable gets the data from the intent from the previous activity
        Intent intentRecipeId = getIntent();
        procedure.setId(intentRecipeId.getIntExtra("id", 0));
        procedure.setProcedure(intentRecipeId.getStringExtra("procedure"));
        procedure.setRecipeId(intentRecipeId.getIntExtra("recipeId", 0));

        txt_procedure = findViewById(R.id.txt_procedure);
        txt_procedure.setText(procedure.getProcedure());

        btn_add_procedure = findViewById(R.id.btn_add_procedure);
        btn_add_procedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProcedure();
            }
        });
    }

    // This method is called to add or update a procedure when clicking the save procedure button
    private void saveProcedure () {
        String procedure_name = txt_procedure.getText().toString();

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", procedure.getId());
            requestBody.put("procedure", procedure_name);
            requestBody.put("recipeId", procedure.getRecipeId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Checks whether add or edit a procedure
        String requestURL = procedure.getId() > 0 ? "http://192.168.1.253:80/procedure/edit" : "http://192.168.1.253:80/procedure/add";
        int requestMethod = procedure.getId() > 0 ? Request.Method.PUT : Request.Method.POST;

        RequestQueue requestQueue = Volley.newRequestQueue(AddProcedure.this);

        // API call for adding or editing a procedure
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, requestURL, requestBody, response -> {
            Result result = new Result();

            // Validation of forms that should be completed in order for function to succeed
            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                if (result.isSuccess()) {
                    error_message.setText("");
                    error_message.setVisibility(View.GONE);

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