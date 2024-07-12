package Adapters;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;
import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hellocookbook.R;
import com.example.hellocookbook.RecipeMenu;

import Models.Recipe;
import Models.User;

/*
    This class displays all the recipes and add the to the list of recipe images with
    recipe name and user who creates the recipe
*/

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.myViewHolder> {
    private Context mContext;
    private ArrayList<Recipe> recipes;
    private User user;

    // Use this constructor to create an instance of ImageAdapter
    public ImageAdapter(Context mContext, ArrayList<Recipe> recipes, User user) {
        this.mContext = mContext;
        this.recipes = recipes;
        this.user = user;
    }

    // Create a row of image, TextViews and icon
    @Override
    public myViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.recipe_card_item, parent, false);
        return new myViewHolder(v);
    }

    // Put every recipe to every row
    @Override
    public void onBindViewHolder (myViewHolder holder, int position) {
        int index = position;

        // convey picture string to byte to bitmap to put the converted image in the ImageView
        byte[] stringToByte = Base64.decode(recipes.get(position).getPicture(), 0);
        Bitmap convertedPhoto = BitmapFactory.decodeByteArray(stringToByte, 0, stringToByte.length);

        holder.recipe_image.setImageBitmap(convertedPhoto);
        holder.txt_recipe.setText(recipes.get(position).getName());
        holder.txt_author.setText("Created by " + user.getFirstname() + " " + user.getLastname());

        holder.recipe_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeMenu.class);
                intent.putExtra("recipeId", recipes.get(index).getId());
                mContext.startActivity(intent);
            }
        });
    }

    // Get the number of rows in the recipes array
    @Override
    public int getItemCount () {
        return recipes.size();
    }

    // Blueprints of the row structure
    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView recipe_image;
        TextView txt_recipe, txt_author;

        public myViewHolder (View itemview) {
            super(itemview);

            recipe_image = itemview.findViewById(R.id.recipe_image);
            txt_recipe = itemview.findViewById(R.id.txt_recipe);
            txt_author = itemview.findViewById(R.id.txt_author);
        }
    }
}
