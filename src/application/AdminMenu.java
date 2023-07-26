package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class AdminMenu extends Application {

    private Connection connection;
    private String address;
    private String password;
    public AdminMenu(String address, String password) {
		
    	this.address=address;
    	this.password=password;
	}

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        connectToDatabase();

        Label titleLabel = new Label("Admin Menu");
        titleLabel.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");
        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        
        Button addRestaurantButton = new Button("Add New Restaurant");
        Button addHotelButton = new Button("Add New Hotel");
        Button addEventButton = new Button("Add New Event");
        Button addDocumentButton = new Button("Add New Document");
        addDocumentButton.setOnAction(e -> showAddDocumentDialog());
        addRestaurantButton.setOnAction(e -> showAddRestaurantDialog());
        addHotelButton.setOnAction(e -> showAddHotelDialog());
        addEventButton.setOnAction(e -> showAddEventDialog());

        
        layout.getChildren().addAll(titleLabel,addDocumentButton,addRestaurantButton, addHotelButton, addEventButton);

        
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Menu");
        primaryStage.show();
    }

    private void connectToDatabase() {

        try {
            
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bienvenu_en_tunisie", "root", "wahchywahchy12");
            System.out.println("Connected to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAddDocumentDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Document");

        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));

        TextField titleField = new TextField();
        TextField contentField = new TextField();
        TextField filePathField = new TextField();

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Text", "Video", "Image");

        ComboBox<String> regionComboBox = new ComboBox<>();
        populateRegionComboBox(regionComboBox);

        Button addButton = new Button("Add Document");
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String content = contentField.getText();
            String filePath = filePathField.getText();
            String type = typeComboBox.getValue();
            String regionName = regionComboBox.getValue();
            int regionId = getRegionId(regionName);

            addDocument(title, content, filePath, type, regionId);
            dialogStage.close();
        });
        
        layout.addRow(0, new Label("Title:"), titleField);
        layout.addRow(1, new Label("Content:"), contentField);
        layout.addRow(2, new Label("File Path:"), filePathField);
        layout.addRow(3, new Label("Type:"), typeComboBox);
        layout.addRow(4, new Label("Region:"), regionComboBox);
        layout.addRow(5, addButton);

        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    private void addDocument(String title, String content, String filePath, String type, int regionId) {
        try {
            String query = "INSERT INTO document (title, content, file_path, type, id_region) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, content);
            statement.setString(3, filePath);
            statement.setString(4, type);
            statement.setInt(5, regionId);
            statement.executeUpdate();
            System.out.println("Document added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void showAddRestaurantDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Restaurant");

        
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));

        
        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField categoryField = new TextField();

        
        ComboBox<String> regionComboBox = new ComboBox<>();
        populateRegionComboBox(regionComboBox);

        
        Button addButton = new Button("Add Restaurant");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String address = addressField.getText();
            String category = categoryField.getText();
            String regionName = regionComboBox.getValue();
            int regionId = getRegionId(regionName);

            addRestaurant(name, address, category, regionId);
            dialogStage.close();
        });

        
        layout.addRow(0, new Label("Name:"), nameField);
        layout.addRow(1, new Label("Address:"), addressField);
        layout.addRow(2, new Label("Category:"), categoryField);
        layout.addRow(3, new Label("Region:"), regionComboBox);
        layout.addRow(4, addButton);

        
        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void populateRegionComboBox(ComboBox<String> regionComboBox) {
        try {
            String query = "SELECT nom FROM region";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String regionName = resultSet.getString("nom");
                regionComboBox.getItems().add(regionName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getRegionId(String regionName) {
        try {
            String query = "SELECT id_region FROM region WHERE nom = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, regionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id_region");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void addRestaurant(String name, String address, String category, int regionId) {
        try {
            String query = "INSERT INTO restaurant (nom_restaurant, adresse_restaurant, categorie, id_region) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, address);
            statement.setString(3, category);
            statement.setInt(4, regionId);
            statement.executeUpdate();
            System.out.println("Restaurant added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAddHotelDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Hotel");

        
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));

        
        TextField nameField = new TextField();
        TextField addressField = new TextField();
        TextField starField = new TextField();

        
        ComboBox<String> regionComboBox = new ComboBox<>();
        populateRegionComboBox(regionComboBox);

        
        Button addButton = new Button("Add Hotel");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String address = addressField.getText();
            int starRating = Integer.parseInt(starField.getText());
            String regionName = regionComboBox.getValue();
            int regionId = getRegionId(regionName);

            addHotel(name, address, starRating, regionId);
            dialogStage.close();
        });

        
        layout.addRow(0, new Label("Name:"), nameField);
        layout.addRow(1, new Label("Address:"), addressField);
        layout.addRow(2, new Label("Star Rating:"), starField);
        layout.addRow(3, new Label("Region:"), regionComboBox);
        layout.addRow(4, addButton);

        
        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private void addHotel(String name, String address, int starRating, int regionId) {
        try {
            String query = "INSERT INTO hotel (nom_hotel, adresse_hotel, nb_etoile, id_region) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, address);
            statement.setInt(3, starRating);
            statement.setInt(4, regionId);
            statement.executeUpdate();
            System.out.println("Hotel added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddEventDialog() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Event");

        
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(10));

        
        TextField nameField = new TextField();
        DatePicker dateField = new DatePicker();

        
        ComboBox<String> monumentComboBox = new ComboBox<>();
        populateMonumentComboBox(monumentComboBox);

        
        Button addButton = new Button("Add Event");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            LocalDate date = dateField.getValue();
            int monumentId = getMonumentId(monumentComboBox.getValue());

            addEvent(name, date, monumentId);
            dialogStage.close();
        });

        
        layout.addRow(0, new Label("Name:"), nameField);
        layout.addRow(1, new Label("Date:"), dateField);
        layout.addRow(2, new Label("Monument:"), monumentComboBox);
        layout.addRow(3, addButton);

        // Create the scene and set it to the dialog stage
        Scene scene = new Scene(layout);
        dialogStage.setScene(scene);
        dialogStage.show();
    }
    private void populateMonumentComboBox(ComboBox<String> comboBox) {
        try {
            String query = "SELECT nom_monument FROM monument";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String monumentName = resultSet.getString("nom_monument");
                comboBox.getItems().add(monumentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getMonumentId(String monumentName) {
        try {
            String query = "SELECT id_monument FROM monument WHERE nom_monument = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, monumentName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_monument");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; 
    }
    private void addEvent(String name, LocalDate date, int monumentId) {
        try {
            String query = "INSERT INTO evenement (nom_evenement, date_evenement, id_monument) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setDate(2, java.sql.Date.valueOf(date));
            statement.setInt(3, monumentId);
            statement.executeUpdate();
            System.out.println("Event added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}