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

import Models.Procedure;
import Models.Result;

/*
    This activity allow the user to view, edit, or delete a procedure
*/

public class ProcedureMenu extends AppCompatActivity {
    private TextView txt_procedure_label;
    private Button btn_edit_procedure, btn_delete_procedure;
    private Procedure procedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_menu);

        procedure = new Procedure();

        // This intent variable gets the data from the intent from the previous activity
        Intent intentProcedure = getIntent();
        procedure.setId(intentProcedure.getIntExtra("id", 0));
        procedure.setProcedure(intentProcedure.getStringExtra("procedure"));
        procedure.setRecipeId(intentProcedure.getIntExtra("recipeId", 0));

        txt_procedure_label = findViewById(R.id.txt_procedure_label);
        txt_procedure_label.setText(procedure.getProcedure());

        btn_edit_procedure = findViewById(R.id.btn_edit_procedure);
        btn_edit_procedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProcedureMenu.this, AddProcedure.class);

                // This intent gets the data from the procedure and puts it to the next activity
                intent.putExtra("id", procedure.getId());
                intent.putExtra("procedure", procedure.getProcedure());
                intent.putExtra("recipeId", procedure.getRecipeId());

                startActivity(intent);

                finish();
            }
        });

        btn_delete_procedure = findViewById(R.id.btn_delete_procedure);
        btn_delete_procedure.setOnClickListener(new View.OnClickListener() {
            // Alert dialog to confirm to delete a procedure
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProcedureMenu.this);

                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure to delete this procedure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProcedure(procedure.getId());
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

    // This method is called to delete a proceudre
    public void deleteProcedure(int procedureId) {
        RequestQueue requestQueue = Volley.newRequestQueue(ProcedureMenu.this);

        // API call for deleting a procedure
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, this.getString(R.string.api_url) + "procedure/delete/" + procedureId, null, response -> {
            Result result = new Result();

            try {
                result.setMessage(response.getString("message"));
                result.setSuccess(response.getBoolean("isSuccess"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(ProcedureMenu.this, result.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        },
        error -> {
            String error2 = "That didn't work!";
        });

        requestQueue.add(jsonObjectRequest);
    }
}