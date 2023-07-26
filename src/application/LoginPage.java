package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage extends Application {
    private RadioButton clientRadioButton;
    private RadioButton visiteurRadioButton;
    private RadioButton adminRadioButton;
    private Button loginButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bienvenue en Tunisie");

        // Création des composants
        clientRadioButton = new RadioButton("Client");
        visiteurRadioButton = new RadioButton("Visiteur");
        adminRadioButton = new RadioButton("Admin");

        ToggleGroup toggleGroup = new ToggleGroup();
        clientRadioButton.setToggleGroup(toggleGroup);
        visiteurRadioButton.setToggleGroup(toggleGroup);
        adminRadioButton.setToggleGroup(toggleGroup);

        loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> {
            if (clientRadioButton.isSelected()) {
                // Afficher la page d'authentification pour le client
            	ClientLoginPage clientLoginPage = new ClientLoginPage();
                clientLoginPage.show();
            } else if (visiteurRadioButton.isSelected()) {
            	VisitorMenu visiteur = new VisitorMenu();
            	visiteur.start(primaryStage);
                // Afficher la page pour les visiteurs
            } else if (adminRadioButton.isSelected()) {
                // Afficher la page d'authentification pour l'admin
            	AdminLoginPage AdminLoginPage = new AdminLoginPage();
                AdminLoginPage.show();
            }
        });

        // Création du conteneur principal et ajout des composants
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(clientRadioButton, visiteurRadioButton, adminRadioButton, loginButton);

        // Création de la scène et affichage de la fenêtre
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
