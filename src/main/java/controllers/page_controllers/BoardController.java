package controllers.page_controllers;

import app.Router;
import controllers.state_controllers.*;
import javafx.scene.Scene;
import model.Partie;
import model.board.Hex;
import model.board.Sector;
import views.BoardView;
import views.components.action_components.SettingsMenu;
import views.components.info_components.HexDetails;
import views.components.board_components.HexPolygon;
import views.components.board_components.SectorRegion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contrôleur de la vue du plateau de jeu (Board).
 * Gère les interactions utilisateur et met à jour la vue en fonction des états de la partie.
 */
public class BoardController implements Controller {

    private final Router router;
    private final BoardView boardView;
    private final List<HexPolygon> highlightHexPolygons = new ArrayList<>();
    private final HexDetails hexDetails;

    private final HashMap<Hex, HexPolygon> hexToPolygonMap;
    private final HashMap<Sector, SectorRegion> sectorToRegionMap;

    private final Partie partie;
    private HexPolygon currentSelectedHexPolygon;

    private final DeployController deployController;
    private final PlanCommandsController planCommandsController;
    private final ExpandController expandController;
    private final ExterminateController exterminateController;
    private final ExploreController exploreController;
    private final ExploitController exploitController;
    private final EndGameController endGameController;

    /**
     * Constructeur du contrôleur du plateau de jeu.
     * Configure les interactions utilisateur et initialise les sous-contrôleurs.
     *
     * @param router L'instance de {@link Router} pour la navigation entre les pages.
     */
    public BoardController(Router router) {
        this.router = router;
        this.boardView = new BoardView();

        this.hexToPolygonMap = boardView.getHexToPolygonMap();
        this.sectorToRegionMap = boardView.getSectorToRegionMap();

        this.partie = Partie.getInstance();
        this.hexDetails = boardView.getHexDetails();

        // Initialisation des sous-contrôleurs
        deployController = new DeployController(boardView, hexToPolygonMap);
        planCommandsController = new PlanCommandsController(boardView);
        expandController = new ExpandController(boardView, hexToPolygonMap);
        exploreController = new ExploreController(boardView, hexToPolygonMap);
        exterminateController = new ExterminateController(boardView, hexToPolygonMap);
        exploitController = new ExploitController(boardView, sectorToRegionMap);
        endGameController = new EndGameController(boardView);

        // Configuration des interactions utilisateur sur les hexagones
        for (Hex hex : hexToPolygonMap.keySet()) {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            hexPolygon.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                if (currentSelectedHexPolygon == hexPolygon) {
                    hexDetails.clearDetails();
                    currentSelectedHexPolygon = null;
                } else {
                    currentSelectedHexPolygon = hexPolygon;
                    hexDetails.showDetails(hex);
                }
            });
        }

        // Bouton "Paramètres"
        boardView.getSettingsButton().setOnMouseClicked(event -> boardView.getSettingsMenu().showMenu());

        SettingsMenu settingsMenu = boardView.getSettingsMenu();

        // Bouton "Sauvegarder" dans le menu des paramètres
        settingsMenu.getSaveButton().setOnAction(e -> {
            String saveName = settingsMenu.getSaveNameField().getText().trim();
            if (saveName.isEmpty()) {
                settingsMenu.showErrorMessage("Please enter a valid save name!");
                return;
            }

            if (isSaveNameTaken(saveName)) {
                settingsMenu.showErrorMessage("A save with this name already exists!");
                return;
            }

            try {
                Partie.saveGame(saveName);
                settingsMenu.hideMenu();
            } catch (Exception ex) {
                ex.printStackTrace();
                settingsMenu.showErrorMessage("Failed to save the game. Try again.");
            }
        });

        // Boutons "Fermer" et "Retour" du menu des paramètres
        settingsMenu.getCloseButton().setOnAction(e -> settingsMenu.hideMenu());
        settingsMenu.getBackButton().setOnAction(e -> router.navigateTo("MainMenu"));

        // Lance la boucle de jeu
        partie.runCurrentState();
    }

    /**
     * Vérifie si un nom de sauvegarde existe déjà.
     *
     * @param saveName Le nom de la sauvegarde à vérifier.
     * @return {@code true} si une sauvegarde avec ce nom existe, sinon {@code false}.
     */
    private boolean isSaveNameTaken(String saveName) {
        File savesFolder = new File(Partie.SAVES_FOLDER);
        if (!savesFolder.exists()) {
            return false;
        }

        File[] saveFiles = savesFolder.listFiles((dir, name) -> name.endsWith(".ser"));
        if (saveFiles == null) {
            return false;
        }

        for (File file : saveFiles) {
            if (file.getName().equalsIgnoreCase(saveName + ".ser")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Scene getScene() {
        return boardView.getScene();
    }
}
