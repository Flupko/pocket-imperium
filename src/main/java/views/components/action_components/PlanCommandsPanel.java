package views.components.action_components;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import views.components.generic.SpaceButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un panel dédié au choix et à l'ordre des commandes lors de la phase
 * Plan. Il affiche trois images (correspondant aux commandes, par exemple) que
 * l'utilisateur peut cliquer pour définir l'ordre. Contient également deux boutons
 * (Reset et Validate) et un message d'erreur (invalidEntry).
 */
public class PlanCommandsPanel extends VBox {

    /**
     * Liste des ImageView gérant les images de commandes (ex. Expand, Explore, Exterminate).
     */
    private final List<ImageView> imageViews = new ArrayList<>();

    /**
     * Liste d'entiers mémorisant l'ordre dans lequel les images ont été cliquées.
     */
    private final List<Integer> clickOrder = new ArrayList<>();

    /**
     * Conteneur horizontal affichant les images.
     */
    private final HBox imageBox;

    /**
     * Bouton pour réinitialiser l'état (remettre les images en place, vider clickOrder).
     */
    private final SpaceButton resetButton;

    /**
     * Bouton pour valider l'ordre choisi.
     */
    private final SpaceButton submitButton;

    /**
     * Label affichant un message d'erreur si l'utilisateur n'a pas cliqué toutes les images.
     */
    private final Label invalidEntry;

    /**
     * Constructeur du PlanCommandsPanel.
     *
     * @param image1 Chemin d'accès ou ressource pour la première image (commande).
     * @param image2 Chemin d'accès ou ressource pour la deuxième image (commande).
     * @param image3 Chemin d'accès ou ressource pour la troisième image (commande).
     */
    public PlanCommandsPanel(String image1, String image2, String image3) {
        // Applique la classe CSS "plan-commands-panel" à ce conteneur principal (VBox)
        getStyleClass().add("plan-commands-panel");

        // HBox pour afficher les trois images
        imageBox = new HBox(20);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getStyleClass().add("image-box");

        // Charge et crée les ImageView
        loadImage(image1, 1);
        loadImage(image2, 2);
        loadImage(image3, 3);

        // Ajoute le HBox au VBox principal
        getChildren().add(imageBox);

        // Label d'erreur (invalid entry)
        invalidEntry = new Label("Please select all the fields.");
        invalidEntry.getStyleClass().add("invalid-entry-plan-panel");
        hideInvalidEntry(); // Masqué par défaut
        getChildren().add(invalidEntry);

        // Crée les boutons (SpaceButton) : Reset et Validate
        resetButton = new SpaceButton("Reset", "#1a1a1a", "#ffffff", "#00c8ff");
        submitButton = new SpaceButton("Validate", "#1a1a1a", "#ffffff", "#00c8ff");

        // Conteneur horizontal pour les deux boutons
        HBox buttonBox = new HBox(20, resetButton, submitButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Ajoute le conteneur de boutons au VBox principal
        getChildren().add(buttonBox);

        // Dimensionne le PlanCommandsPanel (optionnel, basé sur imageBox + buttonBox)
        setMaxWidth(imageBox.getWidth());
        setMaxHeight(imageBox.getHeight() + buttonBox.getHeight());

        // Gère l'action du bouton "Reset"
        resetButton.setOnAction(e -> resetState());

        // Petite transition d'apparition (fondu entrant)
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    /**
     * Charge une image et crée son ImageView, puis l'ajoute au HBox imageBox.
     *
     * @param imagePath Chemin ou ressource de l'image à charger.
     * @param index     Numéro identifiant l'image (1,2,3), utilisé pour l'ordre de clic.
     */
    private void loadImage(String imagePath, int index) {
        // Crée un ImageView à partir du chemin de l'image
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(400);   // Largeur d'affichage
        imageView.setFitHeight(280);  // Hauteur d'affichage
        imageView.setPreserveRatio(true);

        // Ajout d'une classe CSS pour gérer les effets (hover, etc.)
        imageView.getStyleClass().add("command-image");

        // Gère le clic sur l'image
        imageView.setOnMouseClicked(e -> handleImageClick(index));

        // Stocke l'ImageView dans la liste, puis l'ajoute au HBox
        imageViews.add(imageView);
        imageBox.getChildren().add(imageView);
    }

    /**
     * Gère le clic sur l'image correspondante (index).
     * - Rend l'image invisible (imageView.setVisible(false))
     * - Ajoute l'index dans la liste de clics (clickOrder)
     *
     * @param index Index de l'image cliquée (1,2,3).
     */
    private void handleImageClick(int index) {
        ImageView imageView = imageViews.get(index - 1); // Off-by-one car index commence à 1
        imageView.setVisible(false);
        clickOrder.add(index);
    }

    /**
     * Réinitialise l'état du PlanCommandsPanel :
     * - Rend toutes les images à nouveau visibles
     * - Vide la liste clickOrder
     * - Cache le message d'erreur éventuel.
     */
    public void resetState() {
        // Rendre toutes les images visibles
        for (ImageView imageView : imageViews) {
            imageView.setVisible(true);
        }
        // Nettoie l'ordre de clic
        clickOrder.clear();
        // Masque le message d'erreur
        hideInvalidEntry();
    }

    /**
     * Retourne le bouton "Reset".
     * @return Le SpaceButton permettant de réinitialiser l'état du panel.
     */
    public SpaceButton getResetButton() {
        return resetButton;
    }

    /**
     * Retourne le bouton "Validate".
     * @return Le SpaceButton permettant de valider l'ordre choisi.
     */
    public SpaceButton getSubmitButton() {
        return submitButton;
    }

    /**
     * Retourne une copie de la liste des index (1..3) représentant l'ordre
     * dans lequel les images ont été cliquées.
     * @return Une nouvelle liste contenant l'ordre de clic.
     */
    public List<Integer> getClickOrder() {
        return new ArrayList<>(clickOrder);
    }

    /**
     * Affiche le message d'entrée invalide (invalidEntry).
     */
    public void showInvalidEntry() {
        invalidEntry.setVisible(true);
    }

    /**
     * Masque le message d'entrée invalide (invalidEntry).
     */
    public void hideInvalidEntry() {
        invalidEntry.setVisible(false);
    }
}
