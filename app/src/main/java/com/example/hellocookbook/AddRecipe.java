package com.example.hellocookbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import Models.Recipe;
import Models.Result;

/*
    This activity lets the user to add or edit a recipe to the list
 */

public class AddRecipe extends AppCompatActivity {
    private Recipe recipe;
    private ImageView current_image;
    private Button btn_take_photo, btn_upload, btn_add_recipe;
    private ActivityResultLauncher<Intent> resultLauncher; // This variable is used for navigating to camera or upload photo
    private EditText txt_recipe_name;
    private TextView error_message;
    private boolean isTakenByCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        current_image = findViewById(R.id.current_image);

        btn_take_photo = findViewById(R.id.btn_take_photo);
        btn_upload = findViewById(R.id.btn_upload);
        btn_add_recipe = findViewById(R.id.btn_add_recipe);

        txt_recipe_name = findViewById(R.id.txt_recipe_name);

        error_message =findViewById(R.id.error_message);

        recipe = new Recipe();

        recipe.setPicture("");

        // This intent variable gets the data from the intent from the previous activity
        Intent intentValue = getIntent();
        recipe.setId(intentValue.getIntExtra("recipeId", 0));
        recipe.setUsedId(intentValue.getIntExtra("userId", 0));

        // if the recipe exists, get the data for update
        if (recipe.getId() > 0) {
            getRecipeDetails(recipe.getId());
        }

        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakenByCamera = true;

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extra.quickCapture", true);
                startActivityForResult(cameraIntent, 123);
            }
        });

        registerResult();

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakenByCamera = false;
                pickImage();
            }
        });

        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });
    }

    // Get the image details from photo taken or upload and puts it on the ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get the image data from camera or upload
        Bitmap photo = (Bitmap)data.getExtras().get("data");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        if (isTakenByCamera) {
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        }

        // Convert from Byte Array to String
        byte[] byteArray = stream.toByteArray();
        String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "");

        recipe.setPicture(photoString);

        current_image.setImageBitmap(photo);
    }

    // This method allows the user to select and image from the phone to upload
    private void pickImage() {
        try {
            //Navigates to the camera
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method start with user selecting an image from phone and upload and put to the ImageView
    private void registerResult () {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    Uri imageUri = result.getData().getData();
                    current_image.setImageURI(imageUri);

                    Bitmap bitmap = ((BitmapDrawable) current_image.getDrawable()).getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    // Convert from Byte Array into String
                    byte[] byteArray = stream.toByteArray();
                    String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "");

                    recipe.setPicture(photoString);
                } catch (Exception e) {
                    e.printStackTrace();
                    String photoString = "2323";
                }
            }
        });
    }

    // This method gets recipe details by recipe Id
    private void getRecipeDetails(int recipeId) {
        RequestQueue requestQueue = Volley.newRequestQueue(AddRecipe.this);

        // API call for getting a recipe
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, this.getString(R.string.api_url) + "recipe/" + recipeId,  null, response ->  {
            Result result = new Result();

            try {
                result.setJsonResultObject(response.getJSONObject("jsonResultObject"));
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));

                JSONObject object = result.getJsonResultObject();

                recipe.setName(object.get("name").toString());
                recipe.setPicture(object.get("picture").toString());
                recipe.setUsedId(Integer.parseInt(object.get("userId").toString()));

                txt_recipe_name.setText(recipe.getName());

                // Convert from Byte Array to Bitmap and put that variable to ImageView
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

    // This method is called to add or update a recipe when clicking the save recipe button
    private void saveRecipe () {
        recipe.setName(txt_recipe_name.getText().toString());

        // Use the requestBody for input by user to be send to API
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("id", recipe.getId());
            requestBody.put("name", recipe.getName());
            requestBody.put("picture", recipe.getPicture());
            requestBody.put("userId", recipe.getUsedId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Checks whether add or edit a recipe
        String requestURL = recipe.getId() > 0 ? this.getString(R.string.api_url) + "recipe/edit" : this.getString(R.string.api_url) + "recipe/add";
        int requestMethod = recipe.getId() > 0 ? Request.Method.PUT : Request.Method.POST;

        RequestQueue requestQueue = Volley.newRequestQueue(AddRecipe.this);

        // API call for adding or editing a recipe
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