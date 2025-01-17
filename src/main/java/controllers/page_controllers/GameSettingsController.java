package controllers.page_controllers;

import app.Router;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import model.Partie;
import views.GameSettingsView;

/**
 * Contrôleur dédié à la gestion des paramètres du jeu (Game Settings).
 * Gère les interactions utilisateur pour configurer les joueurs, définir les stratégies des robots,
 * et démarrer la partie.
 * Ce contrôleur écoute les événements de l'interface utilisateur liés aux paramètres du jeu,
 * valide les entrées, configure les joueurs (humains ou robots) avec leurs stratégies respectives,
 * et initie la navigation vers le plateau de jeu une fois les paramètres validés.
 */
public class GameSettingsController implements Controller {
    /**
     * Instance du routeur pour la navigation entre les écrans.
     */
    private final Router router;

    /**
     * Vue associée aux paramètres du jeu.
     */
    private final GameSettingsView gameSettingsView;

    /**
     * Constructeur du contrôleur des paramètres du jeu.
     * Initialise la vue, configure les actions des boutons et des cases à cocher.
     *
     * @param router L'instance de {@link Router} utilisée pour la navigation entre les écrans.
     */
    public GameSettingsController(Router router) {
        this.router = router;
        this.gameSettingsView = new GameSettingsView();

        // Configuration de l'action du bouton "Start Game"
        gameSettingsView.getStartGameButton().setOnAction(e -> {
            handleStartGame();
        });

        // Configuration des actions des cases à cocher pour les robots
        CheckBox[] robotCheckBoxes = gameSettingsView.getRobotCheckboxes();
        for (int i = 0; i < 3; i++) {
            CheckBox checkBox = robotCheckBoxes[i];
            int finalI = i;
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    gameSettingsView.addStrategySelector(finalI);
                } else {
                    gameSettingsView.removeStrategySelector(finalI);
                }
            });
        }
    }

    /**
     * Gère l'action de démarrage du jeu lorsque le bouton "Start Game" est cliqué.
     * Valide les entrées utilisateur, configure les joueurs (humains ou robots) avec leurs stratégies,
     * et navigue vers l'écran du plateau de jeu si les paramètres sont valides.
     */
    private void handleStartGame() {
        TextField[] nameFields = gameSettingsView.getPlayerNameFields();
        CheckBox[] robotCheckboxes = gameSettingsView.getRobotCheckboxes();
        ComboBox<String>[] strategySelectors = gameSettingsView.getStrategySelectors();

        // Validation des entrées utilisateur
        for (int i = 0; i < 3; i++) {
            String playerName = nameFields[i].getText();

            if (playerName.trim().isEmpty()) {
                gameSettingsView.addInvalidEntry();
                return;
            }

            ComboBox<String> strategySelector = strategySelectors[i];
            CheckBox checkBox = robotCheckboxes[i];
            if (checkBox.isSelected() && strategySelector.getSelectionModel().isEmpty()) {
                gameSettingsView.addInvalidEntry();
                return;
            }
        }

        // Configuration des joueurs dans la partie
        Partie partie = Partie.getInstance();
        for (int i = 0; i < 3; i++) {
            String playerName = nameFields[i].getText();
            if(playerName.length() > 7) {
                playerName = playerName.substring(0, 7);
            }
            boolean isRobot = robotCheckboxes[i].isSelected();
            String strategy = isRobot ? strategySelectors[i].getValue() : "Human";
            partie.addPlayer(playerName, isRobot, strategy);
        }

        // Navigation vers le plateau de jeu
        router.navigateTo("Board");
    }

    /**
     * Retourne la scène associée au contrôleur.
     *
     * @return La scène correspondant à la vue des paramètres du jeu.
     */
    @Override
    public Scene getScene() {
        return gameSettingsView.getScene();
    }
}
