package com.marcbouchez.todolist;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class CheckBox extends ButtonBase implements Toggle{

	@Override
	public ToggleGroup getToggleGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BooleanProperty selectedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelected(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setToggleGroup(ToggleGroup arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectProperty<ToggleGroup> toggleGroupProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fire() {
		// TODO Auto-generated method stub
		
	}

	

}
