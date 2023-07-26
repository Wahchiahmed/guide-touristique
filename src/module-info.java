module guide_touristique {
	requires javafx.controls;
	requires java.sql;
	requires javafx.media;
	requires javafx.base;
	requires javafx.graphics;
	opens application to javafx.graphics, javafx.fxml;
}
