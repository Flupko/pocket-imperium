package controllers.page_controllers;

import app.Router;
import javafx.scene.Scene;
import model.Partie;
import views.SavesView;

import java.io.File;
import java.util.Arrays;

/**
 * Contrôleur de la vue des sauvegardes.
 * Permet de charger, supprimer ou rafraîchir la liste des sauvegardes disponibles.
 */
public class SavesController implements Controller {

    private final SavesView savesView;
    private final Router router;

    /**
     * Constructeur du contrôleur des sauvegardes.
     * Initialise la vue et configure les interactions avec les boutons.
     *
     * @param router L'instance de {@link Router} pour la navigation entre les pages.
     */
    public SavesController(Router router) {
        this.router = router;
        this.savesView = new SavesView();
        refreshSavesList();

        // Action du bouton "Retour"
        savesView.getBackButton().setOnAction(e -> navigateBack());
    }

    /**
     * Rafraîchit la liste des fichiers de sauvegarde affichés dans la vue.
     */
    public void refreshSavesList() {
        File savesFolder = new File(Partie.SAVES_FOLDER);
        File[] saveFiles = savesFolder.listFiles();

        savesView.getSavesListContainer().getChildren().clear();
        if (saveFiles != null) {
            Arrays.stream(saveFiles).forEach(file -> {
                savesView.createSaveItem(file.getName(),
                        () -> loadSave(file.getName()),
                        () -> deleteSave(file.getName()));
            });
        }
    }

    /**
     * Charge une partie à partir du nom de la sauvegarde.
     *
     * @param saveName Le nom du fichier de sauvegarde à charger.
     */
    private void loadSave(String saveName) {
        try {
            Partie.loadGame(saveName);
            router.resetScreen("Board");
            router.navigateTo("Board");
        } catch (Exception e) {
            savesView.showAlert("Load Error", "Failed to load the save: " + saveName);
            e.printStackTrace();
        }
    }

    /**
     * Supprime une sauvegarde spécifique et met à jour la vue.
     *
     * @param saveName Le nom du fichier de sauvegarde à supprimer.
     */
    private void deleteSave(String saveName) {
        File saveFile = new File(Partie.SAVES_FOLDER, saveName);
        if (saveFile.exists() && saveFile.delete()) {
            refreshSavesList();
            savesView.showAlert("Success", "Save file deleted successfully.");
        } else {
            savesView.showAlert("Delete Error", "Failed to delete the save: " + saveName);
        }
    }

    /**
     * Navigue vers la vue principale.
     */
    private void navigateBack() {
        router.navigateTo("MainMenu");
    }

    @Override
    public Scene getScene() {
        return savesView.getScene();
    }
}
