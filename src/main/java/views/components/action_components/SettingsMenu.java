package views.components.action_components;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import views.components.generic.SpaceButton;

/**
 * Composant graphique (SettingsMenu) affichant un menu de sauvegarde et
 * des boutons pour fermer ou revenir à l'écran principal. Inclut un champ
 * de texte pour nommer la sauvegarde, et un message d'erreur optionnel.
 * Par défaut, ce menu est masqué et recouvre l'écran (overlay semi-transparent).
 */
public class SettingsMenu extends StackPane {

    /**
     * Champ de texte où l'utilisateur entre le nom de la sauvegarde.
     */
    private final TextField saveNameField;

    /**
     * Bouton permettant de sauvegarder la partie.
     */
    private final SpaceButton saveButton;

    /**
     * Bouton permettant de fermer le menu.
     */
    private final SpaceButton closeButton;

    /**
     * Bouton permettant de revenir à l'écran principal.
     */
    private final SpaceButton backButton;

    /**
     * Champ de texte affichant un message d'erreur éventuel.
     */
    private final Text errorMessage;

    /**
     * Constructeur du menu de paramètres (SettingsMenu).
     * Configure l'apparence (overlay, conteneur) et
     * initialise les composants : champ de saisie, message d'erreur,
     * boutons de sauvegarde, fermeture, et retour.
     */
    public SettingsMenu() {
        // Applique la classe CSS pour l'overlay semi-transparent
        this.getStyleClass().add("settings-menu-overlay");
        this.setPrefSize(1000, 750); // Taille en plein écran (approximatif)

        // VBox centré pour le menu
        VBox menuContainer = new VBox(15);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.getStyleClass().add("settings-menu-container");

        // Titre du menu
        Text title = new Text("Save Game");
        title.getStyleClass().add("settings-menu-title");

        // Champ de saisie du nom de sauvegarde
        saveNameField = new TextField();
        saveNameField.setPromptText("Enter save name...");
        saveNameField.getStyleClass().add("settings-menu-textfield");

        // Message d'erreur (initialement masqué)
        errorMessage = new Text();
        errorMessage.getStyleClass().add("settings-menu-error");
        errorMessage.setVisible(false);

        // HBox contenant les deux boutons (sauvegarde, fermer)
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        // Initialise les boutons
        saveButton = new SpaceButton("Save", "#2a2a2a", "#ffffff", "#00c8ff");  // Thème bleu
        closeButton = new SpaceButton("Close", "#2a2a2a", "#ffffff", "#ff5722"); // Thème orange

        // Bouton supplémentaire pour retourner à l'écran principal
        backButton = new SpaceButton("Back to Main Screen", "#2a2a2a", "#ffffff", "#007bff");

        buttons.getChildren().addAll(saveButton, closeButton);

        // Ajoute tous les composants au conteneur vertical
        menuContainer.getChildren().addAll(title, saveNameField, errorMessage, buttons, backButton);

        // Ajoute le conteneur au StackPane
        this.getChildren().add(menuContainer);

        // Par défaut, ce menu est masqué
        this.setVisible(false);
    }

    /**
     * Retourne le champ de saisie du nom de sauvegarde.
     *
     * @return Le TextField utilisé pour saisir le nom de sauvegarde.
     */
    public TextField getSaveNameField() {
        return saveNameField;
    }

    /**
     * Retourne le bouton de sauvegarde.
     *
     * @return Le SpaceButton "Save".
     */
    public SpaceButton getSaveButton() {
        return saveButton;
    }

    /**
     * Retourne le bouton de fermeture (Close).
     *
     * @return Le SpaceButton "Close".
     */
    public SpaceButton getCloseButton() {
        return closeButton;
    }

    /**
     * Retourne le bouton permettant de revenir à l'écran principal.
     *
     * @return Le SpaceButton "Back to Main Screen".
     */
    public SpaceButton getBackButton() {
        return backButton;
    }

    /**
     * Rendre le menu (SettingsMenu) visible à l'écran.
     * (Overlay recouvrant tout l'affichage.)
     */
    public void showMenu() {
        this.setVisible(true);
    }

    /**
     * Masque le menu (SettingsMenu) et efface le message d'erreur éventuel.
     */
    public void hideMenu() {
        this.setVisible(false);
        clearErrorMessage();
    }

    /**
     * Affiche un message d'erreur dans le menu, en le rendant visible.
     *
     * @param message Le texte de l'erreur à afficher.
     */
    public void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }

    /**
     * Efface et masque le message d'erreur.
     */
    public void clearErrorMessage() {
        errorMessage.setVisible(false);
        errorMessage.setText("");
    }
}
