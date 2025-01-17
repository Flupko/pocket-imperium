package controllers.page_controllers;

import app.Router;
import javafx.scene.Scene;
import model.Partie;
import views.MainMenuView;

/**
 * Contrôleur de la vue du menu principal.
 * Permet de gérer les actions principales telles que démarrer une partie ou charger une sauvegarde.
 */
public class MainMenuController implements Controller {

    private final Router router;
    private final MainMenuView mainMenuView;

    /**
     * Constructeur du contrôleur du menu principal.
     * Configure les actions des boutons de la vue.
     *
     * @param router L'instance de {@link Router} pour la navigation entre les pages.
     */
    public MainMenuController(Router router) {
        this.router = router;
        mainMenuView = new MainMenuView();

        // Actions des boutons du menu principal
        mainMenuView.getStartButton().setOnAction(e -> {
            if (Partie.getInstance().isGameOngoing()) {
                router.navigateTo("Board");
            } else {
                router.navigateTo("GameSettings");
            }
        });

        mainMenuView.getLoadButton().setOnAction(e -> {
            router.navigateTo("Saves"); // Affiche la vue des sauvegardes
        });
    }

    @Override
    public Scene getScene() {
        return mainMenuView.getScene();
    }
}
