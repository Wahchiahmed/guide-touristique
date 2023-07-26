package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

public class ClientLoginPage extends Application {
    private TextField addressTextField;
    private PasswordField passwordField;
    private Button loginButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Page de connexion du client");

        
        Label addressLabel = new Label("Adresse :");
        addressTextField = new TextField();
        Label passwordLabel = new Label("Mot de passe :");
        passwordField = new PasswordField();

        loginButton = new Button("Connexion");
        	loginButton.setOnAction(e -> {
        	    String address = addressTextField.getText();
        	    String password = passwordField.getText();

        	    
        	    DatabaseConnector db = new DatabaseConnector();
        	    if (db.verifyClientCredentials(address, password)) {
        	        
        	        showClientPage(address, password);
        	    } else {
        	        
        	        System.out.println("Identifiants de connexion invalides.");
        	    }
        	});


        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(addressLabel, addressTextField, passwordLabel, passwordField, loginButton);

        
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showClientPage(String address, String password) {
        
        ClientMenu clientMenu = new ClientMenu(address, password);

        
        Stage clientStage = new Stage();
        clientMenu.start(clientStage);
    }

    public void show() {
        Stage stage = new Stage();
        start(stage);
    }
}
