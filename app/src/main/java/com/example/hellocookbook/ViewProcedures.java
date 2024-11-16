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

import Adapters.ProcedureAdapter;

import Models.Procedure;

/*
    This activity allows the user to view procedures in the selected recipe
    and can add a new procedure or select the existing one
*/

public class ViewProcedures extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btn_add_procedure;
    private TextView txt_recipe_label;
    private ArrayList<Procedure> procedures;
    private int recipeId;
    private String recipeName;
    private ProcedureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_procedures);

        // This intent variable gets the data from the intent from the previous activity
        Intent intentRecipeId = getIntent();
        recipeId = intentRecipeId.getIntExtra("recipeId", 0);
        recipeName = intentRecipeId.getStringExtra("recipeName");

        txt_recipe_label = findViewById(R.id.txt_recipe_label);
        txt_recipe_label.setText(recipeName);

        displayAllProcedures(recipeId);

        btn_add_procedure = findViewById(R.id.btn_add_procedure);
        btn_add_procedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProcedures.this, AddProcedure.class);

                // This intent gets the data from the procedure and puts it to the next activity
                intent.putExtra("id", 0);
                intent.putExtra("procedure", "");
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });
    }

    // This method is called to get all the procedures in the selected recipe
    private void displayAllProcedures(int recipeId) {
        RequestQueue requestQueue = Volley.newRequestQueue(ViewProcedures.this);

        // API call for getting all the procedures from the recipe
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, this.getString(R.string.api_url) + "procedure/recipe/" + recipeId,  null, response ->  {
            try {
                procedures = new ArrayList<Procedure>();

                // Convert JSON String to JSONArray
                String jsonString = response.getJSONArray("jsonResultObject").toString();
                JSONArray jsonArray = response.getJSONArray("jsonResultObject");

                // Automatically convert the JSON String into JSONElement
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                JsonElement mJson = parser.parse(jsonString);

                Gson gson = new Gson();
                procedures = gson.fromJson(mJson, new TypeToken<ArrayList<Procedure>>(){}.getType());

                recyclerView = findViewById(R.id.recyclerView);
                adapter = new ProcedureAdapter(ViewProcedures.this, procedures);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewProcedures.this));
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
        displayAllProcedures(recipeId);
    }
}