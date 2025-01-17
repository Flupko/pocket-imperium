package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import views.components.generic.SpaceButton;

/**
 * Vue permettant de configurer les paramètres des joueurs avant le début d'une partie.
 * Cette classe offre une interface pour entrer les noms des joueurs, choisir des stratégies
 * et configurer si un joueur est un robot.
 */
public class GameSettingsView {

    /** Scène associée à la vue des paramètres de jeu. */
    private final Scene scene;

    /** Champs de texte pour les noms des joueurs. */
    private final TextField[] playerNameFields = new TextField[3];

    /** Cases à cocher pour définir si un joueur est un robot. */
    private final CheckBox[] robotCheckboxes = new CheckBox[3];

    /** Sélecteurs de stratégie pour les joueurs. */
    private final ComboBox<String>[] strategySelectors = new ComboBox[3];

    /** Colonnes correspondant aux informations de chaque joueur. */
    private final VBox[] playerColumns = new VBox[3];

    /** Étiquette affichée en cas de saisie invalide. */
    private final Label invalidEntry = new Label("Please complete all the fields.");

    /** Conteneur principal pour la vue des paramètres de jeu. */
    private final VBox root = new VBox(20);

    /** Bouton pour lancer la partie. */
    private final Button startGameButton;

    /**
     * Constructeur de la classe. Initialise les composants de la vue et applique les styles nécessaires.
     */
    public GameSettingsView() {
        // Style pour le conteneur principal
        root.getStyleClass().add("space-background");
        root.getStyleClass().add("game-settings-root");

        // Titre de la vue
        Label titleLabel = new Label("Player Settings");
        titleLabel.getStyleClass().add("game-settings-title");

        // Conteneur pour les colonnes des joueurs
        HBox columns = new HBox(20);
        columns.setAlignment(Pos.CENTER);

        // Création des colonnes pour chaque joueur
        for (int i = 0; i < 3; i++) {
            VBox playerColumn = createPlayerColumn(i + 1);
            columns.getChildren().add(playerColumn);
        }

        // Style pour l'étiquette des saisies invalides
        invalidEntry.getStyleClass().add("game-settings-invalid-entry");

        // Bouton pour démarrer le jeu
        startGameButton = new SpaceButton("Start Game", "#2a2a2a", "#ffffff", "#00c8ff");
        VBox.setMargin(startGameButton, new Insets(20, 0, 0, 0)); // Espacement sous les colonnes

        // Ajout des composants au conteneur principal
        root.getChildren().addAll(titleLabel, columns, startGameButton);

        // Création de la scène
        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("styles/GameSettings.css");
        scene.getStylesheets().add("styles/style.css");
    }

    /**
     * Crée une colonne pour un joueur spécifique.
     *
     * @param playerNumber Numéro du joueur (1, 2 ou 3).
     * @return Une VBox contenant les composants pour le joueur donné.
     */
    private VBox createPlayerColumn(int playerNumber) {
        VBox playerColumn = new VBox(10);
        playerColumn.getStyleClass().add("player-column");
        playerColumns[playerNumber - 1] = playerColumn;

        // Étiquette du joueur
        Label playerLabel = new Label("Player " + playerNumber);
        playerLabel.getStyleClass().add("player-label");

        // Champ de saisie pour le nom du joueur
        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.getStyleClass().add("player-name-field");
        playerNameFields[playerNumber - 1] = nameField;

        // Case à cocher pour définir si le joueur est un robot
        CheckBox robotCheckBox = new CheckBox("Is Robot?");
        robotCheckBox.getStyleClass().add("player-robot-checkbox");
        robotCheckboxes[playerNumber - 1] = robotCheckBox;

        // Sélecteur de stratégie
        ComboBox<String> strategySelector = new ComboBox<>();
        strategySelector.getItems().addAll("Aggressive", "Amical");
        strategySelector.setPromptText("Select Strategy");
        strategySelector.getStyleClass().add("player-strategy-selector");
        strategySelectors[playerNumber - 1] = strategySelector;

        // Ajout des composants à la colonne du joueur
        playerColumn.getChildren().addAll(playerLabel, nameField, robotCheckBox);

        return playerColumn;
    }

    /**
     * Ajoute le sélecteur de stratégie à la colonne d'un joueur.
     *
     * @param playerNumberOffByOne Numéro du joueur (indexé à 0).
     */
    public void addStrategySelector(int playerNumberOffByOne) {
        playerColumns[playerNumberOffByOne].getChildren().addAll(strategySelectors[playerNumberOffByOne]);
    }

    /**
     * Supprime le sélecteur de stratégie de la colonne d'un joueur.
     *
     * @param playerNumberOffByOne Numéro du joueur (indexé à 0).
     */
    public void removeStrategySelector(int playerNumberOffByOne) {
        playerColumns[playerNumberOffByOne].getChildren().removeAll(strategySelectors[playerNumberOffByOne]);
    }

    /**
     * Ajoute l'étiquette signalant une saisie invalide au conteneur principal.
     */
    public void addInvalidEntry() {
        if (!root.getChildren().contains(invalidEntry)) {
            root.getChildren().add(2, invalidEntry);
        }
    }

    /**
     * Retourne la scène associée à cette vue.
     *
     * @return La scène de la vue des paramètres de jeu.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Retourne les champs de saisie pour les noms des joueurs.
     *
     * @return Un tableau contenant les champs de texte pour les noms des joueurs.
     */
    public TextField[] getPlayerNameFields() {
        return playerNameFields;
    }

    /**
     * Retourne les cases à cocher indiquant si un joueur est un robot.
     *
     * @return Un tableau contenant les cases à cocher pour chaque joueur.
     */
    public CheckBox[] getRobotCheckboxes() {
        return robotCheckboxes;
    }

    /**
     * Retourne les sélecteurs de stratégie pour les joueurs.
     *
     * @return Un tableau contenant les ComboBox pour sélectionner une stratégie.
     */
    public ComboBox<String>[] getStrategySelectors() {
        return strategySelectors;
    }

    /**
     * Retourne le bouton permettant de démarrer la partie.
     *
     * @return Le bouton "Start Game".
     */
    public Button getStartGameButton() {
        return startGameButton;
    }

    /**
     * Retourne les colonnes des joueurs contenant leurs informations.
     *
     * @return Un tableau contenant les VBox des joueurs.
     */
    public VBox[] getPlayerColumns() {
        return playerColumns;
    }
}
