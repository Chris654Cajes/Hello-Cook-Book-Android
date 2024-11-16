package com.example.hellocookbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Models.Recipe;
import Models.Result;

/*
    This activity allow the user to view, edit, or delete a recipe
    and also can view ingredients and procedured from that recipe
*/

public class RecipeMenu extends AppCompatActivity {
    private Recipe recipe;
    private ImageView current_image;
    private TextView txt_recipe_label;
    private Button btn_view_ingredients, btn_view_procedures, btn_view_edit_recipe, btn_delete_recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_menu);

        recipe = new Recipe();

        // This intent variable gets the data from the intent from the previous activity
        Intent intentUserId = getIntent();
        recipe.setId(intentUserId.getIntExtra("recipeId", 0));

        current_image = findViewById(R.id.current_image);
        txt_recipe_label = findViewById(R.id.txt_recipe_label);

        getRecipeDetails(recipe.getId());

        btn_view_ingredients = findViewById(R.id.btn_view_ingredients);
        btn_view_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeMenu.this, ViewIngredients.class);

                // This intent gets the data from the recipe and puts it to the next activity
                intent.putExtra("recipeId", recipe.getId());
                intent.putExtra("recipeName", recipe.getName());
                startActivity(intent);
            }
        });

        btn_view_procedures = findViewById(R.id.btn_view_procedures);
        btn_view_procedures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeMenu.this, ViewProcedures.class);

                // This intent gets the data from the recipe and puts it to the next activity
                intent.putExtra("recipeId", recipe.getId());
                intent.putExtra("recipeName", recipe.getName());
                startActivity(intent);
            }
        });

        btn_view_edit_recipe = findViewById(R.id.btn_view_edit_recipe);
        btn_view_edit_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeMenu.this, AddRecipe.class);

                // This intent gets the data from the recipe and puts it to the next activity
                intent.putExtra("recipeId", recipe.getId());
                intent.putExtra("userId", recipe.getUsedId());
                startActivity(intent);
                finish();
            }
        });

        btn_delete_recipe = findViewById(R.id.btn_delete_recipe);
        btn_delete_recipe.setOnClickListener(new View.OnClickListener() {
            // Alert dialog to confirm to delete a recipe
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeMenu.this);

                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure to delete this recipe? All ingredients and procedures in this recipe will also be deleted");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecipe(recipe.getId());
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

    // This method gets a recipe by recipe Id
    private void getRecipeDetails(int recipeId) {
        RequestQueue requestQueue = Volley.newRequestQueue(RecipeMenu.this);

        // API call for getting a recipe
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, this.getString(R.string.api_url) + "recipe/" + recipeId,  null, response ->  {
            try {
                Result result = new Result();

                result.setJsonResultObject(response.getJSONObject("jsonResultObject"));
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                JSONObject object = result.getJsonResultObject();

                recipe.setName(object.get("name").toString());
                recipe.setPicture(object.get("picture").toString());
                recipe.setUsedId(Integer.parseInt(object.get("userId").toString()));

                txt_recipe_label.setText(recipe.getName());

                // Convert from Byte array to Bitmap and put it on ImageView
                byte[] stringToByte = Base64.decode(recipe.getPicture(), 0);
                Bitmap convertedPhoto = BitmapFactory.decodeByteArray(stringToByte, 0, stringToByte.length);
                current_image.setImageBitmap(convertedPhoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }

    // This method is called to delete a recipe
    private void deleteRecipe(int recipeId) {
        RequestQueue requestQueue = Volley.newRequestQueue(RecipeMenu.this);

        // API call for deleting a recipe
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, this.getString(R.string.api_url) + "recipe/delete/" + recipeId, null, response -> {
            Result result = new Result();

            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(RecipeMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }
}