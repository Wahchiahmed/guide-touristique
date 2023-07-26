package application;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import java.sql.*;

public class VisitorMenu extends Application {

    private ComboBox<String> regionComboBox;
    private ListView<String> dataListView;
    private ListView<Object> listView = new ListView<>();
    private ListView<MediaItem> documentsListView;


    private Connection connection;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        Button hotelsButton = new Button("Hotels");
        Button restaurantsButton = new Button("Restaurants");
        Button documentsButton = new Button("Documents");

        hotelsButton.setOnAction(e -> showHotels());
        restaurantsButton.setOnAction(e -> showRestaurants());
        documentsButton.setOnAction(e -> showDocuments());

        regionComboBox = new ComboBox<>();
        try {
            connectToDatabase();
            Statement statement = connection.createStatement();
            String query = "SELECT nom FROM region";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String regionName = resultSet.getString("nom");
                regionComboBox.getItems().add(regionName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataListView = new ListView<>();
        documentsListView = new ListView<>(); // Initialize the documentsListView

        // Set the cell factory for the documentsListView
        documentsListView.setCellFactory(listView -> new MediaItemCell());

        layout.getChildren().addAll(hotelsButton, restaurantsButton, documentsButton, new Label("Select Region:"), regionComboBox, dataListView, documentsListView);

        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Visitor Menu");
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

    private void showHotels() {
        String selectedRegion = regionComboBox.getValue();
        if (selectedRegion != null) {
            // Retrieve hotels for the selected region from the database
            System.out.println("Hotels in " + selectedRegion + ":");

            // Assuming you have a database connection object named 'connection'
            try {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM hotel WHERE id_region = (SELECT id_region FROM region WHERE nom = '" + selectedRegion + "')";
                ResultSet resultSet = statement.executeQuery(query);

                ObservableList<String> data = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    int hotelId = resultSet.getInt("id_hotel");
                    String hotelName = resultSet.getString("nom_hotel");
                    String hotelAddress = resultSet.getString("adresse_hotel");
                    int starRating = resultSet.getInt("nb_etoile");

                    String hotelInfo = "Hotel ID: " + hotelId + "\n" +
                            "Name: " + hotelName + "\n" +
                            "Address: " + hotelAddress + "\n" +
                            "Star Rating: " + starRating + "\n" +
                            "--------------------------";

                    data.add(hotelInfo);
                }

                dataListView.setItems(data);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please select a region");
        }
    }

    private void showRestaurants() {
        String selectedRegion = regionComboBox.getValue();
        if (selectedRegion != null) {
            // Retrieve restaurants for the selected region from the database
            System.out.println("Restaurants in " + selectedRegion + ":");

            // Assuming you have a database connection object named 'connection'
            try {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM restaurant WHERE id_region = (SELECT id_region FROM region WHERE nom = '" + selectedRegion + "')";
                ResultSet resultSet = statement.executeQuery(query);

                ObservableList<String> data = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    int restaurantId = resultSet.getInt("id_restaurant");
                    String restaurantName = resultSet.getString("nom_restaurant");
                    String restaurantAddress = resultSet.getString("adresse_restaurant");
                    String restaurantCategory = resultSet.getString("categorie");

                    String restaurantInfo = "Restaurant ID: " + restaurantId + "\n" +
                            "Name: " + restaurantName + "\n" +
                            "Address: " + restaurantAddress + "\n" +
                            "Category: " + restaurantCategory + "\n" +
                            "--------------------------";

                    data.add(restaurantInfo);
                }

                dataListView.setItems(data);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please select a region");
        }
    }
    public void showDocuments() {
        ObservableList<MediaItem> mediaList = FXCollections.observableArrayList();

        try (Statement statement = connection.createStatement()) {
            String query = "SELECT file_path, type FROM document";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String filePath = resultSet.getString("file_path");
                String fileType = resultSet.getString("type");

                File file = new File(filePath);
                if (file.exists()) {
                    if (fileType.equals("image")) {
                        Image image = new Image(file.toURI().toString());
                        mediaList.add(new MediaItem(image));
                    } else if (fileType.equals("video")) {
                        Media media = new Media(file.toURI().toString());
                        mediaList.add(new MediaItem(media));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        documentsListView.setItems(mediaList);
    }

    public static class MediaItem {
        private Image image;
        private Media video;
        private StringProperty type;
        private StringProperty path;

        public MediaItem(Image image, Media video, String path) {
            this.image = image;
            this.video = video;
            this.type = new SimpleStringProperty(video != null ? "video" : "image");
            this.path = new SimpleStringProperty(path);
        }
        public MediaItem(Image image) {
        	this.image=image;
        }
        public MediaItem(Media video) {
        	this.video=video;
        }

        public Image getImage() {
            return image;
        }

        public Media getVideo() {
            return video;
        }

        public String getType() {
            return type.get();
        }

        public String getPath() {
            return path.get();
        }

        public StringProperty typeProperty() {
            return type;
        }

        public StringProperty pathProperty() {
            return path;
        }
    }



    public static class MediaItemCell extends ListCell<MediaItem> {
        private ImageView imageView;
        private MediaView mediaView;

        public MediaItemCell() {
            imageView = new ImageView();
            mediaView = new MediaView();

            setPrefWidth(300);
        }

        @Override
        protected void updateItem(MediaItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                if (item.getType().equals("image")) {
                    imageView.setImage(item.getImage());
                    setGraphic(imageView);
                } else if (item.getType().equals("video")) {
                    mediaView.setMediaPlayer(new MediaPlayer(item.getVideo()));
                    setGraphic(mediaView);
                }
                setText(item.getPath());
            }
        }
    }



}
