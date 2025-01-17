package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import views.components.generic.SpaceButton;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

/**
 * Vue permettant de gérer l'écran des sauvegardes dans l'application.
 * Cette classe offre une interface pour afficher, charger et supprimer des sauvegardes.
 */
public class SavesView {

    /** Conteneur principal de la vue des sauvegardes. */
    private final StackPane rootPane;

    /** Conteneur principal de l'écran des sauvegardes. */
    private final VBox savesScreen;

    /** Scène associée à la vue des sauvegardes. */
    private final Scene scene;

    /** Conteneur pour la liste des sauvegardes. */
    private final VBox savesListContainer;

    /** Bouton pour revenir à l'écran précédent. */
    private final Button backButton;

    /**
     * Constructeur de la classe. Initialise tous les composants de l'écran des sauvegardes
     * et applique les styles nécessaires.
     */
    public SavesView() {
        // Initialisation du conteneur principal
        rootPane = new StackPane();
        rootPane.getStyleClass().add("space-background"); // Applique le style de fond spatial

        // Mise en page de l'écran des sauvegardes
        savesScreen = new VBox();
        savesScreen.getStyleClass().add("saves-screen"); // Classe CSS associée

        // Conteneur pour la liste des sauvegardes
        savesListContainer = new VBox();
        savesListContainer.getStyleClass().add("saves-list-container"); // Classe CSS associée

        // ScrollPane pour rendre la liste des sauvegardes défilable
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(savesListContainer);
        scrollPane.setFitToWidth(true); // Assure que le contenu s'ajuste à la largeur
        scrollPane.setMaxHeight(300); // Limite la hauteur de la zone défilable
        scrollPane.getStyleClass().add("saves-scroll-pane");

        // Bouton de retour
        backButton = new SpaceButton("Back", "#2a2a2a", "#ffffff", "#00c8ff");
        backButton.getStyleClass().add("back-button"); // Classe CSS pour le bouton
        VBox.setMargin(backButton, new Insets(20, 0, 0, 0)); // Espacement entre le bouton et la liste

        // Ajout des composants à l'écran des sauvegardes
        savesScreen.getChildren().addAll(scrollPane, backButton);

        // Ajout de l'écran au conteneur principal
        rootPane.getChildren().add(savesScreen);

        // Création de la scène
        scene = new Scene(rootPane, 800, 600);

        // Chargement des fichiers CSS externes
        scene.getStylesheets().add("styles/style.css");
        scene.getStylesheets().add("styles/Saves.css");
    }

    /**
     * Retourne la scène associée à cette vue.
     *
     * @return La scène de l'écran des sauvegardes.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Retourne le conteneur de la liste des sauvegardes.
     *
     * @return Le conteneur VBox contenant la liste des sauvegardes.
     */
    public VBox getSavesListContainer() {
        return savesListContainer;
    }

    /**
     * Retourne le bouton permettant de revenir à l'écran précédent.
     *
     * @return Le bouton de retour.
     */
    public Button getBackButton() {
        return backButton;
    }

    /**
     * Retourne le conteneur principal de cette vue.
     *
     * @return Le StackPane racine de l'écran des sauvegardes.
     */
    public StackPane getRootPane() {
        return rootPane;
    }

    /**
     * Affiche une alerte avec un titre et un message spécifiés.
     *
     * @param title   Le titre de l'alerte.
     * @param message Le message de l'alerte.
     */
    public void showAlert(String title, String message) {
        VBox alertBox = new VBox(20);
        alertBox.getStyleClass().add("alert-box"); // Applique une classe CSS

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("alert-message");

        Button closeButton = new SpaceButton("OK", "#2a2a2a", "#ffffff", "#00c8ff");
        closeButton.setOnAction(e -> rootPane.getChildren().remove(alertBox)); // Ferme l'alerte

        alertBox.getChildren().addAll(titleLabel, messageLabel, closeButton);
        alertBox.setAlignment(Pos.CENTER);

        alertBox.setMaxHeight(USE_PREF_SIZE);
        alertBox.setMinHeight(USE_PREF_SIZE);
        alertBox.setMinWidth(USE_PREF_SIZE);
        alertBox.setMaxWidth(USE_PREF_SIZE);

        rootPane.getChildren().add(alertBox);
        StackPane.setAlignment(alertBox, Pos.CENTER); // Centre l'alerte
    }

    /**
     * Crée un élément représentant une sauvegarde dans la liste.
     *
     * @param saveName    Le nom de la sauvegarde.
     * @param loadAction  Action à effectuer lorsqu'on clique sur "Load".
     * @param deleteAction Action à effectuer lorsqu'on clique sur "Delete".
     */
    public void createSaveItem(String saveName, Runnable loadAction, Runnable deleteAction) {
        HBox saveItem = new HBox(10);
        saveItem.getStyleClass().add("save-item");

        Label saveNameLabel = new Label(saveName);
        saveNameLabel.getStyleClass().add("save-item-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SpaceButton loadButton = new SpaceButton("Load", "#2a2a2a", "#ffffff", "#00c8ff");
        loadButton.getStyleClass().add("load-button");
        loadButton.setOnAction(e -> loadAction.run());

        SpaceButton deleteButton = new SpaceButton("Delete", "#2a2a2a", "#ffffff", "#ff0000");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteAction.run());

        saveItem.getChildren().addAll(saveNameLabel, spacer, loadButton, deleteButton);
        savesListContainer.getChildren().add(saveItem);
    }
}
