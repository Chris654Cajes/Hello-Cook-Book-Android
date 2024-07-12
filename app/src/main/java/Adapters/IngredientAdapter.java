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

import com.example.hellocookbook.IngredientMenu;
import com.example.hellocookbook.R;

import java.util.ArrayList;

import Models.Ingredient;

/*
    This class displays all the ingredients and add the to the list of ingredients UI
*/

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Ingredient> ingredients;

    // Use this constructor to create an instance of IngredientAdapter
    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    // Create a row of TextViews and icon
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_row, parent, false);
        return new MyViewHolder(view);
    }

    // Put every ingredient to every row
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        int index = position;

        holder.txt_ingredient_item_number.setText((index + 1) + "");
        holder.txt_ingredient.setText(ingredients.get(index).getName());
        holder.txt_unit.setText(ingredients.get(index).getUnit());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IngredientMenu.class);
                intent.putExtra("id", ingredients.get(index).getId());
                intent.putExtra("name", ingredients.get(index).getName());
                intent.putExtra("unit", ingredients.get(index).getUnit());
                intent.putExtra("recipeId", ingredients.get(index).getRecipeId());
                context.startActivity(intent);
            }
        });
    }

    // Get the number of rows in the ingredients array
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Blueprints of the row structure
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_ingredient_item_number, txt_ingredient, txt_unit;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_ingredient_item_number = itemView.findViewById(R.id.txt_ingredient_item_number);
            txt_ingredient = itemView.findViewById(R.id.txt_ingredient);
            txt_unit = itemView.findViewById(R.id.txt_unit);

            mainLayout = itemView.findViewById(R.id.ingredient_row);
        }
    }
}
