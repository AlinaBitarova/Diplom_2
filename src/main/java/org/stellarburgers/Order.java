package org.stellarburgers;

import java.util.List;

public class Order {
    List<String> ingredients;

    public Order(List<String> ingredients){
        this.ingredients = ingredients;
    }

    public Order() {
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

}
