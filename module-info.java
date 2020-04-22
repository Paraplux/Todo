module com.marcbouchez.todolist {
	requires javafx.controls;
		requires transitive javafx.base;
		requires transitive javafx.graphics;
		
	requires java.net.http;
	requires jsondb.core;
		
	exports com.marcbouchez.todolist to javafx.graphics;
}