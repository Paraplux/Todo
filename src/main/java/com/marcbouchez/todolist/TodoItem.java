package com.marcbouchez.todolist;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

/**
 * A test Pojo representing a imaginary class TodoItem.
 *
 * @version 1.0 28-Sep-2016
 */
@Document(collection = "todoitems", schemaVersion = "1.0")
public class TodoItem {
    // This field will be used as a primary key, every POJO should have one
    @Id
    private int id;

    private String title;
    private boolean isCompleted;
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }


}