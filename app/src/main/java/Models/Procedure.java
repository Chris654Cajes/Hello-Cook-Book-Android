package Models;

/*
    This class is the structure of a particular procedure
*/

public class Procedure {
    private int id;
    private String procedure1;
    private int recipeId;

    public Procedure() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcedure() {
        return procedure1;
    }

    public void setProcedure(String procedure) {
        this.procedure1 = procedure;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
