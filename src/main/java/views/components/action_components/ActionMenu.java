package views.components.action_components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;

/**
 * Représente un composant graphique (menu d'action) permettant de configurer
 * dynamiquement un label d’action, un spinner entier et un bouton, en fonction
 * des paramètres fournis. Le menu peut être affiché ou masqué, et permet
 * d'afficher un message d'erreur en cas d'entrée invalide.
 */
public class ActionMenu extends VBox {

    /**
     * Label affichant le texte d'action.
     * Par exemple : "Choisissez une valeur" ou "Entrez un nombre".
     */
    private final Label actionLabel;

    /**
     * Spinner permettant de sélectionner une valeur entière.
     * Par exemple : sélection d'un nombre d'unités à déployer.
     */
    private final Spinner<Integer> actionSpinner;

    /**
     * Label affichant un message d'entrée invalide, lorsque la saisie
     * de l'utilisateur ne respecte pas les contraintes.
     */
    private final Label invalidEntry;

    /**
     * Bouton permettant de déclencher l'action configurée.
     * Par exemple : "Valider" ou "Confirmer".
     */
    private final Button actionButton;

    /**
     * Constructeur de la classe ActionMenu.
     * Initialise les différents composants (label, spinner, bouton, label d'erreur)
     * et configure les styles, l'espacement et la visibilité.
     */
    public ActionMenu() {
        // Applique la classe CSS "action-menu" au conteneur
        getStyleClass().add("action-menu");

        // Label d'action
        actionLabel = new Label();
        actionLabel.getStyleClass().add("action-label");  // Classe CSS personnalisée

        // Spinner d'action
        actionSpinner = new Spinner<>();
        actionSpinner.getStyleClass().add("action-spinner");
        actionSpinner.setVisible(false); // Caché par défaut

        // Bouton d'action
        actionButton = new Button();
        actionButton.getStyleClass().add("action-button");

        // Label d'entrée invalide (masqué et vide au départ)
        invalidEntry = new Label("");
        invalidEntry.getStyleClass().add("invalid-entry-action-menu");

        // Configure l'espacement vertical entre les éléments
        setSpacing(10);

        // Le menu d'action est masqué par défaut
        setVisible(false);

        // Fixe les dimensions du VBox au niveau souhaité
        setMaxHeight(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
    }

    /**
     * Affiche le menu d'action, en configurant le label, le spinner et le bouton
     * selon les paramètres indiqués.
     *
     * @param showLabel        Indique si le label d'action doit être affiché.
     * @param labelText        Le texte à afficher dans le label.
     * @param showSpinner      Indique si le spinner doit être affiché.
     * @param spinnerMin       La valeur minimale du spinner (si affiché).
     * @param spinnerMax       La valeur maximale du spinner (si affiché).
     * @param spinnerStart     La valeur de départ du spinner (si affiché).
     * @param showActionButton Indique si le bouton d'action doit être affiché.
     * @param buttonText       Le texte à afficher sur le bouton d'action.
     */
    public void showActionMenu(boolean showLabel, String labelText,
                               boolean showSpinner, int spinnerMin, int spinnerMax, int spinnerStart,
                               boolean showActionButton, String buttonText) {

        // Nettoie les enfants actuels du VBox avant d'ajouter les nouveaux composants
        getChildren().clear();

        // Configuration du label d'action
        if (showLabel) {
            actionLabel.setText(labelText);
            getChildren().add(actionLabel);
        }

        // Configuration du spinner d'action
        if (showSpinner) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    spinnerMin, spinnerMax, spinnerStart
            );
            actionSpinner.setValueFactory(valueFactory);
            actionSpinner.setVisible(true);
            getChildren().add(actionSpinner);
        }

        // Configuration du bouton d'action
        if (showActionButton) {
            actionButton.setText(buttonText);
            getChildren().add(actionButton);
        }

        // Rend le menu visible
        setVisible(true);
    }

    /**
     * Masque entièrement le menu d'action et réinitialise ses composants (label, spinner,
     * bouton et message d'erreur).
     */
    public void hideActionMenu() {
        // Rétablit la visibilité globale à false
        setVisible(false);

        // Réinitialise le label d'action et le texte du bouton
        actionLabel.setText("");
        actionButton.setText("");

        // Masque le spinner et invalide l'entrée
        actionSpinner.setVisible(false);
        hideInvalidEntry();

        // Supprime tous les composants du VBox
        getChildren().clear();
    }

    /**
     * Retourne le label d'action (pour éventuellement le personnaliser ou y associer des événements).
     *
     * @return Le label d'action.
     */
    public Label getActionLabel() {
        return actionLabel;
    }

    /**
     * Retourne le bouton d'action (pour éventuellement y ajouter un EventHandler ou changer le texte).
     *
     * @return Le bouton d'action.
     */
    public Button getActionButton() {
        return actionButton;
    }

    /**
     * Retourne la valeur entière sélectionnée dans le spinner d'action.
     *
     * @return La valeur actuelle du spinner.
     */
    public int getActionSpinnerValue() {
        return actionSpinner.getValue();
    }

    /**
     * Affiche un message d'erreur ou d'entrée invalide, sous la forme d'un label supplémentaire.
     *
     * @param entryText Le message d'erreur à afficher.
     */
    public void showInvalidEntry(String entryText) {
        // Met à jour le label d'entrée invalide et le rend visible
        invalidEntry.setText(entryText);
        invalidEntry.setVisible(true);

        // Ajoute le label au VBox s'il n'est pas déjà présent
        if (!getChildren().contains(invalidEntry)) {
            getChildren().add(invalidEntry);
        }
    }

    /**
     * Masque le message d'entrée invalide (et retire le label associé).
     */
    public void hideInvalidEntry() {
        invalidEntry.setText("");
        invalidEntry.setVisible(false);
        getChildren().remove(invalidEntry);
    }
}
