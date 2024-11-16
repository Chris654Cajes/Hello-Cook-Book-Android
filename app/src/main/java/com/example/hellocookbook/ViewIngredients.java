package com.example.hellocookbook;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

import Adapters.IngredientAdapter;

import Models.Ingredient;

/*
    This activity allows the user to view ingredients in the selected recipe
    and can add a new recipe or select the existing one
*/

public class ViewIngredients extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btn_add_ingredient;
    private TextView txt_recipe_label;
    private ArrayList<Ingredient> ingredients;
    private int recipeId;
    private String recipeName;
    private IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ingredients);

        // This intent variable gets the data from the intent from the previous activity
        Intent intentRecipeId = getIntent();
        recipeId = intentRecipeId.getIntExtra("recipeId", 0);
        recipeName = intentRecipeId.getStringExtra("recipeName");

        txt_recipe_label = findViewById(R.id.txt_recipe_label);
        txt_recipe_label.setText(recipeName);

        displayAllIngredients(recipeId);

        btn_add_ingredient = findViewById(R.id.btn_add_ingredient);
        btn_add_ingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewIngredients.this, AddIngredient.class);

                // This intent gets the data from the ingredient and puts it to the next activity
                intent.putExtra("id", 0);
                intent.putExtra("name", "");
                intent.putExtra("unit", "");
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });
    }

    // This method is called to get all the ingredients in the selected recipe
    private void displayAllIngredients(int recipeId) {
        RequestQueue requestQueue = Volley.newRequestQueue(ViewIngredients.this);

        // API call for getting all the ingredients from the recipe
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, this.getString(R.string.api_url) + "ingredient/recipe/" + recipeId,  null, response ->  {
            try {
                ingredients = new ArrayList<Ingredient>();

                // Convert JSON String to JSONArray
                String jsonString = response.getJSONArray("jsonResultObject").toString();
                JSONArray jsonArray = response.getJSONArray("jsonResultObject");

                // Automatically convert the JSON String into JSONElement
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                JsonElement mJson = parser.parse(jsonString);

                Gson gson = new Gson();
                ingredients = gson.fromJson(mJson, new TypeToken<ArrayList<Ingredient>>(){}.getType());

                recyclerView = findViewById(R.id.recyclerView);
                adapter = new IngredientAdapter(ViewIngredients.this, ingredients);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewIngredients.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }

        // If it navigates back to this activity again, execute this method
    @Override
    public void onRestart() {
        super.onRestart();
        displayAllIngredients(recipeId);
    }
}