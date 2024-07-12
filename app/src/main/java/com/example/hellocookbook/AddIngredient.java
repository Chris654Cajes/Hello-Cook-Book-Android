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

import Models.Ingredient;
import Models.Result;

/*
    This activity lets the user to add or edit an ingredient to the recipe
 */

public class AddIngredient extends AppCompatActivity {
    private EditText txt_ingredient, txt_unit;
    private Button btn_add_ingredient;
    private TextView error_message;
    private Ingredient ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        error_message = findViewById(R.id.error_message);

        ingredient = new Ingredient();

        // This intent variable gets the data from the intent from the previous activity
        Intent intentRecipeId = getIntent();
        ingredient.setId(intentRecipeId.getIntExtra("id", 0));
        ingredient.setName(intentRecipeId.getStringExtra("name"));
        ingredient.setUnit(intentRecipeId.getStringExtra("unit"));
        ingredient.setRecipeId(intentRecipeId.getIntExtra("recipeId", 0));

        txt_ingredient = findViewById(R.id.txt_ingredient);
        txt_unit = findViewById(R.id.txt_unit);

        txt_ingredient.setText(ingredient.getName());
        txt_unit.setText(ingredient.getUnit());

        btn_add_ingredient = findViewById(R.id.btn_add_ingredient);
        btn_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIngredient();
            }
        });
    }

    // This method is called to add or update an ingredient when clicking the save ingredient button
    private void saveIngredient () {
        String ingredient_name = txt_ingredient.getText().toString();
        String unit = txt_unit.getText().toString();

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", ingredient.getId());
            requestBody.put("name", ingredient_name);
            requestBody.put("unit", unit);
            requestBody.put("recipeId", ingredient.getRecipeId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Checks whether add or edit an ingredient
        String requestURL = ingredient.getId() > 0 ? "http://192.168.1.253:80/ingredient/edit" : "http://192.168.1.253:80/ingredient/add";
        int requestMethod = ingredient.getId() > 0 ? Request.Method.PUT : Request.Method.POST;

        RequestQueue requestQueue = Volley.newRequestQueue(AddIngredient.this);

        // API call for adding or editing an ingredient
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