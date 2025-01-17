package app;

import controllers.page_controllers.GameSettingsController;
import controllers.page_controllers.SavesController;
import javafx.application.Application;
import javafx.stage.Stage;
import controllers.page_controllers.MainMenuController;
import controllers.page_controllers.BoardController;

/**
 * Classe principale de l'application, responsable de l'initialisation et du lancement de l'interface utilisateur.
 * Cette classe étend {@link Application}, le point d'entrée pour les applications JavaFX.
 * Elle configure le routeur pour gérer la navigation entre les différentes scènes de l'application.
 */
public class Main extends Application {

    /**
     * Point d'entrée principal de l'application JavaFX.
     * Initialise le {@link Router}, enregistre les écrans, et démarre l'application avec le menu principal.
     *
     * @param primaryStage La scène principale de l'application, fournie par JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialisation du Router pour gérer la navigation entre les écrans
        Router router = new Router(primaryStage);

        // Enregistrement des écrans avec leurs contrôleurs respectifs
        router.addScreen("MainMenu", MainMenuController.class); // Écran du menu principal
        router.addScreen("Board", BoardController.class);       // Écran du plateau de jeu
        router.addScreen("GameSettings", GameSettingsController.class); // Écran des paramètres du jeu
        router.addScreen("Saves", SavesController.class);       // Écran des sauvegardes

        // Définir l'écran des sauvegardes comme non mis en cache
        router.notCacheScene("Saves");

        // Démarrer l'application avec le menu principal
        router.startWith("MainMenu");
    }

    /**
     * Point d'entrée de l'application.
     * La méthode `main` utilise {@link Application#launch(String...)} pour démarrer l'application JavaFX.
     *
     * @param args Les arguments de ligne de commande (non utilisés dans cette application).
     */
    public static void main(String[] args) {
        launch(args); // Démarre l'application JavaFX
    }
}
