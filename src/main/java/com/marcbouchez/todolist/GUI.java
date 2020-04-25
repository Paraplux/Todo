package com.marcbouchez.todolist;

import java.util.ArrayList;
import java.util.List;


import io.jsondb.JsonDBTemplate;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
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
	static IntegerProperty completeTasksSize = new SimpleIntegerProperty();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox root = new VBox(10);
		primaryStage.setTitle("Todo List");

        titleLabel = new Label("Todo List");
        titleLabel.setId("titleLabel");
        
        infoLabel = new Label();
        infoLabel.setText("Il vous reste " + completeTasksSize.getValue() + " tâches à accomplir");
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
		
		
		//Button
		ToggleButton button = new ToggleButton();
		final SVGPath svgImage = new SVGPath();
		final String completePath = "M5.9,8.1 L4.5,9.5 L9,14 L19,4 L17.6,2.6 L9,11.2 L5.9,8.1 L5.9,8.1 Z M18,10 C18,14.4 14.4,18 10,18 C5.6,18 2,14.4 2,10 C2,5.6 5.6,2 10,2 C10.8,2 11.5,2.1 12.2,2.3 L13.8,0.7 C12.6,0.3 11.3,0 10,0 C4.5,0 0,4.5 0,10 C0,15.5 4.5,20 10,20 C15.5,20 20,15.5 20,10 L18,10 L18,10 Z";
		final String notCompletePath = "M10,0 C4.5,0 0,4.5 0,10 C0,15.5 4.5,20 10,20 C15.5,20 20,15.5 20,10 C20,4.5 15.5,0 10,0 L10,0 Z M10,18 C5.6,18 2,14.4 2,10 C2,5.6 5.6,2 10,2 C14.4,2 18,5.6 18,10 C18,14.4 14.4,18 10,18 L10,18 Z";
		BooleanProperty status = new SimpleBooleanProperty(item.isCompleted());
		Text task = new Text();
		
		svgImage.setContent(notCompletePath);
		button.setGraphic(svgImage);
		button.setBackground(null);
		if (status.getValue() == false) {
			svgImage.setContent(notCompletePath); 				
		} else {
			svgImage.setContent(completePath); 				    				
		}
		if(status.getValue() == true) button.setSelected(true);
		task.setText(item.getTitle() + " / state : " + status.getValue());
		task.setFont(Font.font("Baloo Bhaina 2", 14));
		refreshStyle(status, task);
		
		 // Add change listener
		status.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println("changed " + oldValue + "->" + newValue);
    			refreshStyle(status, task);
    			task.setText(item.getTitle() + " / state : " + status.getValue());
    			if (status.getValue() == false) {
    				svgImage.setContent(notCompletePath); 				
    			} else {
    				svgImage.setContent(completePath); 				    				
    			}
    			infoLabel.setText("Il vous reste " + getCompleteTasksSize().getValue() + " tâches à accomplir");
    		}
        });
		
		button.setOnAction((e) -> {
			toggleItem(item.getId(), status);
			status.setValue(!status.getValue());
			getCompleteTasksSize();
		});
		
		line.getChildren().addAll(button, task);
		line.setAlignment(Pos.CENTER_LEFT);;
		return line;
	}


    //Called before opening stage
    @Override
    public void init() throws Exception {

    	//OK
    	initializeDatabase("files", "com.marcbouchez.todolist");
    	
    	//OK
    	createTable();
    	
    	completeTasksSize = getCompleteTasksSize();
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
	private static void toggleItem (int itemID, BooleanProperty itemToToggleStatus) {
		
		String itemToToggleTitle = database.findById(itemID, TodoItem.class).getTitle();
		
		TodoItem itemToToggle = new TodoItem();
		
		itemToToggle.setId(itemID);
		itemToToggle.setTitle(itemToToggleTitle);
		itemToToggle.setCompleted(!itemToToggleStatus.getValue());
		System.out.println("Item Toggled");
	    database.upsert(itemToToggle);
	}
	
	private static void refreshStyle(BooleanProperty todoItemStatus, Text todoItemText) {
		if (todoItemStatus.getValue() == false) {
			todoItemText.setFill(Color.web("#202020"));
			todoItemText.getStyleClass().remove("taskCompleted");
			todoItemText.getStyleClass().add("taskToComplete");
		} else if (todoItemStatus.getValue() == true) {
			todoItemText.setFill(Color.web("#808080"));
			todoItemText.getStyleClass().remove("taskToComplete");		
			todoItemText.getStyleClass().add("taskCompleted");		
		}
	}
	
	private static IntegerProperty getCompleteTasksSize () {
		IntegerProperty size = new SimpleIntegerProperty(0);
		
		for (TodoItem task : database.findAll(TodoItem.class)) {
			System.out.println("Status de la tâche :" + task.isCompleted());
			if (task.isCompleted() == false) {
				System.out.println("Element non completé trouvé!");
				size.setValue(size.getValue() + 1);;
			}
		}
		System.out.println(size.getValue());
		
		return size;
	}
	
}
