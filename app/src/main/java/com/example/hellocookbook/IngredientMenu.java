package com.example.hellocookbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import Models.Ingredient;
import Models.Result;

/*
    This activity allow the user to view, edit, or delete an ingredient
*/

public class IngredientMenu extends AppCompatActivity {
    private TextView txt_ingredient_label, txt_unit_label;
    private Button btn_edit_ingredient, btn_delete_ingredient;
    private Ingredient ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_menu);

        ingredient = new Ingredient();

        // This intent variable gets the data from the intent from the previous activity
        Intent intentIngredient = getIntent();
        ingredient.setId(intentIngredient.getIntExtra("id", 0));
        ingredient.setName(intentIngredient.getStringExtra("name"));
        ingredient.setUnit(intentIngredient.getStringExtra("unit"));
        ingredient.setRecipeId(intentIngredient.getIntExtra("recipeId", 0));

        txt_ingredient_label = findViewById(R.id.txt_ingredient_label);
        txt_unit_label = findViewById(R.id.txt_unit_label);

        txt_ingredient_label.setText(ingredient.getName());
        txt_unit_label.setText(ingredient.getUnit());

        btn_edit_ingredient = findViewById(R.id.btn_edit_ingredient);
        btn_edit_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IngredientMenu.this, AddIngredient.class);

                // This intent gets the data from the ingredient and puts it to the next activity
                intent.putExtra("id", ingredient.getId());
                intent.putExtra("name", ingredient.getName());
                intent.putExtra("unit", ingredient.getUnit());
                intent.putExtra("recipeId", ingredient.getRecipeId());

                startActivity(intent);

                finish();
            }
        });

        btn_delete_ingredient = findViewById(R.id.btn_delete_ingredient);
        btn_delete_ingredient.setOnClickListener(new View.OnClickListener() {
            // Alert dialog to confirm to delete an ingredient
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IngredientMenu.this);

                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure to delete this ingredient?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteIngredient(ingredient.getId());
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    // This method is called to delete an ingredient
    private void deleteIngredient(int ingredientId) {
        RequestQueue requestQueue = Volley.newRequestQueue(IngredientMenu.this);

        // API call for deleting an ingredient
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, this.getString(R.string.api_url) + "ingredient/delete/" + ingredientId, null, response -> {
            Result result = new Result();

            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(IngredientMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }
}