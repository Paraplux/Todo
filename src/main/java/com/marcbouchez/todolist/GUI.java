package com.marcbouchez.todolist;

import java.util.ArrayList;
import java.util.List;


import io.jsondb.JsonDBTemplate;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GUI extends Application {
	
	//GUI
	private Label titleLabel;
	private Label infoLabel;
	private Text task;
	//DATA
	static JsonDBTemplate database;
	static List<HBox> todoItemsArray;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox root = new VBox(10);
//		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
		primaryStage.setTitle("Todo List");

        titleLabel = new Label("Todo List");
        titleLabel.setId("titleLabel");
        
        infoLabel = new Label("Il vous reste " + database.findAll(TodoItem.class).size() + " tâches à accomplir");
        infoLabel.setId("infoLabel");
        
        VBox tasksContainer = new VBox(10);
        
        //getTasks();
        List<TodoItem> todoItems = database.findAll(TodoItem.class);
    	todoItemsArray = new ArrayList<>(database.findAll(TodoItem.class).size());
    	
        for (TodoItem todoItem : todoItems) {
        	todoItemsArray.add(renderLine(todoItem));
		}
        
        tasksContainer.getChildren().addAll(todoItemsArray);
		
        
        root.getChildren().addAll(titleLabel, infoLabel, tasksContainer);
        
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/css/Main.css").toString());
		
		primaryStage.setScene(scene);
		primaryStage.setWidth(400);
		primaryStage.setHeight(500);
		primaryStage.show();
		
	}
	
	
	private HBox renderLine(TodoItem item) {
		HBox line = new HBox();
		Button button = new Button("Toggle Completed");
		Text task = new Text();
		BooleanProperty status = new SimpleBooleanProperty(item.isCompleted());
		
		task.setText(item.getTitle());
		refreshStyle(status, task);
		
		button.setOnAction((e) -> {
			//Data change
			toggleItem(item.getId());
			
			//GUI change
			status.setValue(!status.getValue());
			refreshStyle(status, task);
        });
		
		line.getChildren().addAll(button, task);
		
		return line;
	}


    //Called before opening stage
    @Override
    public void init() throws Exception {

    	//OK
    	initializeDatabase("files", "com.marcbouchez.todolist");
    	
    	//OK
    	createTable();
    }
	
    public static void runApp(){ launch(); }
    
	/**
	 * @param collectionPath Location for the collections files
	 * @param collectionPackage Package where POJO Class are located
	 */
	private static void initializeDatabase(String collectionPath, String collectionPackage) {
		
		GUI.database = new JsonDBTemplate(collectionPath, collectionPackage);
        
	}
	
	/**
	 * @param table Table to create
	 */
	private static void createTable () {
		if(database.collectionExists(TodoItem.class)) {
        	System.out.println("Collection : " + TodoItem.class + " does already exists!");
        } else {        	
        	System.out.println("Collection : " + TodoItem.class + " does not exists!");
        	database.createCollection(TodoItem.class);
        }
	}
	
	/**
	 * @param title TodoItem title to insert
	 */
	private static void insertItem (String title) {
		TodoItem todoItem = new TodoItem();
		int dataLength = database.findAll(TodoItem.class).size();
		
		todoItem.setId(dataLength + 1);
		todoItem.setTitle(title);
		todoItem.setCompleted(false);
		
	    database.insert(todoItem);
	}
	
	/**
	 * @param itemID ID of the item to delete
	 */
	private static void removeItem (int itemID) {
		TodoItem itemToRemove = new TodoItem();
		
		itemToRemove.setId(itemID);
		
	    database.remove(itemToRemove, TodoItem.class);
	}
	
	/**
	 * @param itemID ID of the item to edit
	 */
	private static void editItem (String newTitle, int itemID) {
		TodoItem itemToEdit = new TodoItem();
		
		itemToEdit.setId(itemID);
		itemToEdit.setTitle(newTitle);
		
	    database.upsert(itemToEdit);
	}
	
	/**
	 * @param itemID to complete
	 */
	private static void toggleItem (int itemID) {
		
		boolean itemToToggleStatus = database.findById(itemID, TodoItem.class).isCompleted();
		String itemToToggleTitle = database.findById(itemID, TodoItem.class).getTitle();
		
		TodoItem itemToToggle = new TodoItem();
		
		itemToToggle.setId(itemID);
		itemToToggle.setTitle(itemToToggleTitle);
		itemToToggle.setCompleted(!itemToToggleStatus);
		
	    database.upsert(itemToToggle);
	}
	
	private static void refreshStyle(BooleanProperty todoItemStatus, Text todoItemText) {
		if (todoItemStatus.getValue() == false) {
			todoItemText.getStyleClass().remove("taskCompleted");
			todoItemText.getStyleClass().add("taskToComplete");
		} else if (todoItemStatus.getValue() == true) {
			todoItemText.getStyleClass().remove("taskToComplete");		
			todoItemText.getStyleClass().add("taskCompleted");		
		}
	}
	
}
