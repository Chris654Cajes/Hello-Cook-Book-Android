package Models;

/*
    This class is the structure of a particular ingredient
*/

public class Ingredient {
    private int id;
    private String name, unit;
    private int recipeId;

    public Ingredient() {}

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public int getRecipeId() { return recipeId; }

    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
}
