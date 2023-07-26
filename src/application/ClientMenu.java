package application;

import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ClientMenu extends Application {
    private Connection connection;
    private String address;
    private String password;
    private ObservableList<String> selectedOptions = FXCollections.observableArrayList();

    public ObservableList<String> getSelectedOptions() {
        return selectedOptions;
    }
 

    public ClientMenu(String address,String password) {
		
    	this.address=address;
    	this.password=password;
	}

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bienvenu_en_tunisie", "root", "wahchywahchy12");
            System.out.println("Connected to database");

            
            Label titleLabel = new Label("Client Menu");
            titleLabel.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

            Button reserveButton = new Button("Reserve a Room");
            reserveButton.setOnAction(e -> showRoomReservationOptions());

            Button cancelButton = new Button("Cancel Reservation");
            cancelButton.setOnAction(e -> showReservationCancellationOptions());

            
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            vbox.getChildren().addAll(titleLabel, reserveButton, cancelButton);

            
            Scene scene = new Scene(vbox, 300, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Client Menu");
            primaryStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorAlert("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void showRoomReservationOptions() {
        try {
            
            String query = "SELECT c.num_chambre, h.nom_hotel, h.nb_etoile, c.nbre_lits FROM chambre c " +
                    "JOIN hotel h ON c.id_hotel = h.id_hotel";

            Statement statement = connection.createStatement();

            
            ResultSet resultSet = statement.executeQuery(query);

            ObservableList<String> roomOptions = FXCollections.observableArrayList();

            
            if (resultSet.next()) {
                do {
                    String roomNumber = resultSet.getString("num_chambre");
                    String hotelName = resultSet.getString("nom_hotel");
                    int starRating = resultSet.getInt("nb_etoile");
                    int nbre_lits = resultSet.getInt("nbre_lits");
                    String option = "Room Number: " + roomNumber +
                            ", Hotel Name: " + hotelName +
                            ", Star Rating: " + starRating+
                            ", nombre de lits: "+nbre_lits;
                    roomOptions.add(option);
                } while (resultSet.next());

                showRoomOptions(roomOptions);
            } else {
                displayErrorAlert("No available room", "No rooms are available for reservation.");
            }

            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorAlert("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void showRoomOptions(ObservableList<String> roomOptions) {
        Stage optionsStage = new Stage();
        optionsStage.setTitle("Room Reservation");

        ComboBox<String> roomComboBox = new ComboBox<>(roomOptions);
        roomComboBox.setPromptText("Select a room");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        Button reserveButton = new Button("Reserve");
        reserveButton.setOnAction(e -> {
            String selectedRoom = roomComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (selectedRoom != null && startDate != null && endDate != null) {
                
                String roomNumber = selectedRoom.split(",")[0].split(":")[1].trim();
                processSelectedRoom(roomNumber, startDate, endDate);
                optionsStage.close();
            } else {
                displayErrorAlert("Invalid Selection", "Please select a room and enter the start and end dates.");
            }
        });

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(roomComboBox, startDatePicker, endDatePicker, reserveButton);

        Scene scene = new Scene(vbox, 300, 200);
        optionsStage.setScene(scene);
        optionsStage.show();
    }

    private void processSelectedRoom(String roomNumber, LocalDate startDate, LocalDate endDate) {
        int clientId = getCurrentClientId(); 

        
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bienvenu_en_tunisie", "root", "wahchywahchy12");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO reservation (date_debut, date_fin, nb_lits, id_client, id_chambre) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            
            String query = "SELECT nbre_lits, id_chambre FROM chambre WHERE num_chambre = ?";
            PreparedStatement bedsStatement = connection.prepareStatement(query);
            bedsStatement.setString(1, roomNumber);
            ResultSet bedsResultSet = bedsStatement.executeQuery();

            int availableBeds = 0;
            int roomId = 0;
            if (bedsResultSet.next()) {
                availableBeds = bedsResultSet.getInt("nbre_lits");
                roomId = bedsResultSet.getInt("id_chambre");
            }

            
            if (availableBeds > 0) {
                
                statement.setDate(1, Date.valueOf(startDate));
                statement.setDate(2, Date.valueOf(endDate));
                statement.setInt(3, availableBeds);
                statement.setInt(4, clientId);
                statement.setInt(5, roomId);
                statement.executeUpdate();

                
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int reservationId = generatedKeys.getInt(1);
                    displayInformationAlert("Reservation Successful", "Your reservation has been added successfully. Reservation ID: " + reservationId);
                } else {
                    
                    displayErrorAlert("Reservation Error", "Failed to retrieve the reservation ID.");
                }
            } else {
                
                displayErrorAlert("No Beds Available", "There are no beds available in the selected room.");
            }
        } catch (SQLException e) {
            
            displayErrorAlert("Reservation Error", "An error occurred while adding the reservation to the database.");
            e.printStackTrace();
        }
    }

    int getCurrentClientId() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bienvenu_en_tunisie", "root", "wahchywahchy12");
             PreparedStatement statement = connection.prepareStatement("SELECT id_client FROM client WHERE email_client = ? AND mot_de_passe = ?")) {
            statement.setString(1, address);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_client");
            } else {
               
                displayErrorAlert("Login Error", "Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            
            displayErrorAlert("Database Error", "An error occurred while accessing the database.");
            e.printStackTrace();
        }

        return 0; 
    }



	private void displayInformationAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showReservationCancellationOptions() {
        try {
            
            String query = "SELECT r.id_reservation, r.date_debut, r.date_fin, c.nom_client, c.prenom_client " +
                    "FROM reservation r " +
                    "JOIN client c ON r.id_client = c.id_client " +
                    "WHERE r.id_client = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 1); 

            
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<String> reservations = FXCollections.observableArrayList();

            
            if (resultSet.next()) {
                do {
                    int reservationId = resultSet.getInt("id_reservation");
                    Date startDate = resultSet.getDate("date_debut");
                    Date endDate = resultSet.getDate("date_fin");
                    String clientName = resultSet.getString("nom_client") + " " + resultSet.getString("prenom_client");

                    String reservationInfo = "Reservation ID: " + reservationId +
                            ", Start Date: " + startDate +
                            ", End Date: " + endDate +
                            ", Client Name: " + clientName;

                    reservations.add(reservationInfo);
                } while (resultSet.next());

                showReservationCancellationForm(reservations);
            } else {
                displayInformationAlert("No Reservations", "You have no reservations to cancel.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorAlert("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void showReservationCancellationForm(ObservableList<String> reservations) {
        Stage formStage = new Stage();
        formStage.setTitle("Reservation Cancellation");

        Label titleLabel = new Label("Select a Reservation to Cancel:");

        ListView<String> reservationListView = new ListView<>(reservations);
        reservationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button cancelButton = new Button("Cancel Reservation");
        cancelButton.setOnAction(e -> {
            String selectedReservation = reservationListView.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                cancelReservation(selectedReservation);
                formStage.close();
            } else {
                displayErrorAlert("No Reservation Selected", "Please select a reservation to cancel.");
            }
        });

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(titleLabel, reservationListView, cancelButton);

        Scene scene = new Scene(vbox, 400, 300);
        formStage.setScene(scene);
        formStage.show();
    }

    private void cancelReservation(String selectedReservation) {
        try {
            
            String[] reservationDetails = selectedReservation.split(",");
            int reservationId = Integer.parseInt(reservationDetails[0].split(":")[1].trim());

            
            String deleteQuery = "DELETE FROM reservation WHERE id_reservation = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, reservationId);

            
            int rowsAffected = preparedStatement.executeUpdate();

            
            if (rowsAffected > 0) {
                displayInformationAlert("Reservation Canceled", "The selected reservation has been canceled.");
            } else {
                displayErrorAlert("Cancellation Error", "An error occurred while canceling the reservation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorAlert("Database Error", "An error occurred while accessing the database.");
        }
    }

    public void show() {
        Stage stage = new Stage();
        start(stage);
    }
}