package Adapters;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hellocookbook.ProcedureMenu;
import com.example.hellocookbook.R;

import java.util.ArrayList;

import Models.Procedure;

/*
    This class displays all the procedures and add the to the list of procedures UI
*/

public class ProcedureAdapter extends RecyclerView.Adapter<ProcedureAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Procedure> procedures;

    // Use this constructor to create an instance of ProcedureAdapter
    public ProcedureAdapter(Context context, ArrayList<Procedure> procedures) {
        this.context = context;
        this.procedures = procedures;
    }

    // Create a row of TextViews and icon
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.procedure_row, parent, false);
        return new MyViewHolder(view);
    }

    // Put every procedure to every row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        int index = position;

        holder.txt_procedure_item_number.setText(index + 1 + "");
        holder.txt_procedure.setText(procedures.get(index).getProcedure());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProcedureMenu.class);
                intent.putExtra("id", procedures.get(index).getId());
                intent.putExtra("procedure", procedures.get(index).getProcedure());
                intent.putExtra("recipeId", procedures.get(index).getRecipeId());
                context.startActivity(intent);
            }
        });
    }

    // Get the number of rows in the procedures array
    @Override
    public int getItemCount() {
        return procedures.size();
    }

    // Blueprints of the row structure
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_procedure_item_number, txt_procedure;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_procedure = itemView.findViewById(R.id.txt_procedure);
            txt_procedure_item_number = itemView.findViewById(R.id.txt_procedure_item_number);

            mainLayout = itemView.findViewById(R.id.procedure_row);
        }
    }
}
