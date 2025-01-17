package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import views.components.generic.SpaceButton;

/**
 * Vue principale du menu principal de l'application.
 * Cette classe permet d'afficher les options principales pour démarrer ou charger une partie.
 */
public class MainMenuView {

    /** Scène associée au menu principal. */
    private final Scene scene;

    /** Conteneur principal pour le menu principal. */
    private final VBox mainMenu;

    /** Bouton pour démarrer une nouvelle partie. */
    private final Button startButton;

    /** Bouton pour charger une partie existante. */
    private final Button loadButton;

    /**
     * Constructeur de la classe. Initialise les composants du menu principal et applique les styles nécessaires.
     */
    public MainMenuView() {
        // Menu principal
        mainMenu = new VBox(20);
        mainMenu.getStyleClass().add("main-menu"); // Applique la classe CSS associée

        // Logo
        Image logo = new Image("logo_pocket_imperium.png");
        ImageView logoView = new ImageView(logo);
        VBox.setMargin(logoView, new Insets(0, 0, 40, 0)); // Ajoute un espacement au bas du logo
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(450); // Ajuste la largeur du logo

        // Boutons
        startButton = new SpaceButton("Start Game", "#2a2a2a", "#ffffff", "#00c8ff"); // Bordure bleue futuriste
        loadButton = new SpaceButton("Load Game", "#2a2a2a", "#ffffff", "#00fa21"); // Bordure verte futuriste

        // Ajout des éléments au menu principal
        mainMenu.getChildren().addAll(logoView, startButton, loadButton);

        // Pane racine
        StackPane root = new StackPane();
        root.getStyleClass().add("space-background"); // Applique le style de fond spatial
        root.getChildren().addAll(mainMenu);

        // Création de la scène
        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("styles/style.css");
        scene.getStylesheets().add("styles/MainMenu.css");
    }

    /**
     * Retourne la scène associée à ce menu.
     *
     * @return La scène de la vue du menu principal.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Retourne le bouton permettant de démarrer une nouvelle partie.
     *
     * @return Le bouton pour démarrer une nouvelle partie.
     */
    public Button getStartButton() {
        return startButton;
    }

    /**
     * Retourne le bouton permettant de charger une partie existante.
     *
     * @return Le bouton pour charger une partie existante.
     */
    public Button getLoadButton() {
        return loadButton;
    }
}
