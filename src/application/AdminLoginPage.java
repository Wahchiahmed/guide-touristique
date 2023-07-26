package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminLoginPage extends Application {
    private TextField addressTextField;
    private PasswordField passwordField;
    private Button loginButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Page de connexion d'Admin");

        
        Label addressLabel = new Label("Adresse :");
        addressTextField = new TextField();
        Label passwordLabel = new Label("Mot de passe :");
        passwordField = new PasswordField();

        loginButton = new Button("Connexion");
        loginButton.setOnAction(e -> {
            String address = addressTextField.getText();
            String password = passwordField.getText();

            
            DatabaseConnector db = new DatabaseConnector();
            if (db.verifyAdminCredentials(address, password)) {
                
                showAdminPage(address,password);
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

    private void showAdminPage(String address,String password) {
    	AdminMenu adminMenu = new AdminMenu(address, password);

        
        Stage adminStage = new Stage();
        adminMenu.start(adminStage);
    }
    public void show() {
        Stage stage = new Stage();
        start(stage);
    }
}
