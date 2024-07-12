package com.example.hellocookbook;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Adapters.ImageAdapter;

import Models.Recipe;
import Models.Result;
import Models.User;

import Sessions.SessionManagement;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/*
    This is the main activity where the user was logged it and the user can view the
    recipes that he/she has created, and also can search for a particular recipe or
    add new recipe
 */

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private User user;
    private TextView label_firstname, label_lastname;
    private EditText txt_search_recipe;
    private CardView not_found;
    private Button btn_add_recipe, btn_add_recipe_if_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        not_found = findViewById(R.id.not_found);

        btn_add_recipe = findViewById(R.id.btn_add_recipe);
        btn_add_recipe_if_empty = findViewById(R.id.btn_add_recipe_if_empty);

        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddRecipe();
            }
        });

        btn_add_recipe_if_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddRecipe();
            }
        });

        txt_search_recipe = findViewById(R.id.txt_search_recipe);
        txt_search_recipe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayAllRecipes(user.getId(), txt_search_recipe.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        user = new User();

        displayUserData();
    }

    // This method gets user details and put the into navigation bar and uses the user Id for search recipes
    private void displayUserData() {
        SessionManagement sessionManagement = new SessionManagement(MainActivity.this); // User the session user details to get the entire user details

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("Username", sessionManagement.getSessionUsername());
            requestBody.put("Password", sessionManagement.getSessionPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // API call for check if the user exists in the database and store user data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://192.168.1.253:80/user/login", requestBody, response -> {
            Result result = new Result();

            try {
                result.setJsonResultObject(response.getJSONObject("jsonResultObject"));
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                try {
                    JSONObject object = result.getJsonResultObject();

                    user.setId(Integer.parseInt(object.get("id").toString()));
                    user.setUsername(object.get("username").toString());
                    user.setFirstname(object.get("firstname").toString());
                    user.setLastname(object.get("lastname").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // If the user exists, continue to Main, else logout and move to login page
                if (user.getId() != 0) {
                    displayMenus();
                    displayAllRecipes(user.getId(), txt_search_recipe.getText().toString());
                } else {
                    sessionManagement.removeSession();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
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

    // This method displays all menu items in the navigation bar
    private void displayMenus() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        label_firstname = findViewById(R.id.label_firstname);
        label_lastname = findViewById(R.id.label_lastname);

        label_firstname.setText(user.getFirstname());
        label_lastname.setText(user.getLastname());

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // Checks which menu item is selected
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case R.id.account_details: {
                        Intent intent = new Intent(MainActivity.this, AccountDetails.class);

                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("firstname", user.getFirstname());
                        intent.putExtra("lastname", user.getLastname());

                        startActivity(intent);
                        break;
                    }

                    case R.id.edit_account: {
                        Intent intent = new Intent(MainActivity.this, EditAccount.class);

                        intent.putExtra("id", user.getId());
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("firstname", user.getFirstname());
                        intent.putExtra("lastname", user.getLastname());
                        intent.putExtra("password", user.getPassword());

                        startActivity(intent);
                        break;
                    }

                    case R.id.change_password: {
                        Intent intent = new Intent(MainActivity.this, ChangePassword.class);

                        intent.putExtra("id", user.getId());

                        startActivity(intent);
                        break;
                    }

                    case R.id.logout: {
                        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                        sessionManagement.removeSession();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                        break;
                    }
                }

                return false;
            }
        });
    }

    // This method is called to display all recipes of the user
    private void displayAllRecipes(int userId, String recipe) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // API call for getting all the recipes of the user
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.1.253:80/recipe/user/" + userId + "/" + recipe,  null, response ->  {
            try {
                ArrayList<Recipe> recipes = new ArrayList<Recipe>();

                String jsonString = response.getJSONArray("jsonResultObject").toString();
                JSONArray jsonArray = response.getJSONArray("jsonResultObject");

                // This is where the json parsing happens
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                JsonElement mJson = parser.parse(jsonString);
                Gson gson = new Gson();
                recipes = gson.fromJson(mJson, new TypeToken<ArrayList<Recipe>>(){}.getType());

                RecyclerView recyclerView = findViewById(R.id.recipe_list);

                ImageAdapter imageAdapter = new ImageAdapter(this, recipes, user);

                recyclerView.setAdapter(imageAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                // The UI updates depending if there are at least 1 recipe
                if (recipes.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    btn_add_recipe.setVisibility(View.GONE);
                    btn_add_recipe_if_empty.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.VISIBLE);
                } else {
                    not_found.setVisibility(View.GONE);
                    btn_add_recipe.setVisibility(View.VISIBLE);
                    btn_add_recipe_if_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }

    // This method moves the user to add a new recipe, new activity
    private void navigateToAddRecipe() {
        Intent intent = new Intent(MainActivity.this, AddRecipe.class);

        intent.putExtra("recipeId", 0);
        intent.putExtra("userId", user.getId());

        startActivity(intent);
    }

    // This method is called if a menu button is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // If the back button is pressed to navigate back to this main activity, display all recipes again
    @Override
    public void onBackPressed() {
        displayAllRecipes(user.getId(), "");

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // If it navigates back to this activity again, execute this method
    @Override
    public void onRestart() {
        super.onRestart();
        displayUserData();
        displayAllRecipes(user.getId(), "");
    }
}