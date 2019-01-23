package com.bxtore.dev.bxt.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 28-09-2018.
 */

public class CategoryItem implements Serializable{

    String name;
    int id;

    public CategoryItem(){}

    public CategoryItem(int id,String name){
        this.id = id;
        this.name = name;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
