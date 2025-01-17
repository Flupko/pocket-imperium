package controllers.state_controllers;

import model.board.Board;
import model.board.Hex;
import model.board.Sector;
import model.players.Player;
import model.states.DeployState;
import model.states.AbstractGameState;
import views.BoardView;
import views.components.board_components.HexPolygon;
import views.components.board_components.SectorRegion;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôleur dédié à la phase de déploiement (Deploy) dans le jeu.
 * Gère les interactions utilisateur pour déployer des navires sur les hexagones valides.
 * Ce contrôleur écoute les événements de la partie concernant l'initialisation du déploiement
 * et met à jour la vue en conséquence. Il permet à l'utilisateur de sélectionner un hexagone
 * pour y déployer des navires et de confirmer l'action via le menu d'action.
 */
public class DeployController extends AbstractPhaseController {

    /**
     * Instance du plateau de jeu.
     */
    private final Board board;

    /**
     * Constructeur du contrôleur de déploiement.
     * Initialise les composants et configure les écouteurs d'événements nécessaires.
     *
     * @param boardView        La vue principale du plateau de jeu.
     * @param hexToPolygonMap  Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    public DeployController(BoardView boardView, HashMap<Hex, HexPolygon> hexToPolygonMap) {
        super(boardView, hexToPolygonMap);
        this.board = partie.getBoard();

        // Écouteur pour l'initialisation du déploiement
        partie.addPropertyChangeListener("DEPLOY_INIT", this);
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement à l'événement "DEPLOY_INIT" pour mettre à jour la vue de déploiement.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        switch (evt.getPropertyName()) {
            case "DEPLOY_INIT":
                if (newValue instanceof List<?>) {
                    boardDeploymentUpdate((List<Hex>) newValue);
                }
                break;
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de déploiement.
     * Met en évidence les hexagones valides pour le déploiement et configure les gestionnaires d'événements.
     *
     * @param validDeploymentHexes Liste des hexagones valides pour le déploiement des navires.
     */
    private void boardDeploymentUpdate(List<Hex> validDeploymentHexes) {
        DeployState deployState = getDeployState();

        if (deployState == null) {
            return;
        }

        Player player = partie.getCurrentPlayer();
        Set<Hex> validHexSet = new HashSet<>(validDeploymentHexes);

        // Met en évidence les hexagones valides pour le déploiement
        for (Hex hex : validDeploymentHexes) {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            boardView.highlightHex(hexPolygon, "blue");
            effectedHexPolygons.add(hexPolygon);
            addSpecialHexHandler(hexPolygon, event -> handleClickDeploymentHex(hexPolygon, hex));
        }

        // Configure les gestionnaires pour les hexagones non valides
        addNonSpecialHexHandler(validHexSet);

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", please choose a Hex to deploy your ships.");
    }

    /**
     * Gère le clic sur un hexagone valide pour le déploiement.
     * Sélectionne l'hexagone et configure le menu d'action pour déployer des navires.
     *
     * @param hexPolygon L'hexagone graphique cliqué.
     * @param hex        L'hexagone du modèle correspondant.
     */
    private void handleClickDeploymentHex(HexPolygon hexPolygon, Hex hex) {
        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickDeployment(hex));
            actionMenu.showActionMenu(false, "",
                    false, -1, -1, -1,
                    true, "Deploy");
        }
    }

    /**
     * Gère la logique de déploiement lorsque l'utilisateur confirme l'action via le menu d'action.
     * Réinitialise les modifications de phase et déploie les navires sur l'hexagone sélectionné.
     *
     * @param hex L'hexagone où déployer les navires.
     */
    private void handleClickDeployment(Hex hex) {
        resetPhaseModifications();

        DeployState deployState = getDeployState();
        if (deployState != null) {
            deployState.deployShips(hex);
        }
    }

    /**
     * Récupère le state actuel de déploiement si la phase de jeu est bien celle-ci.
     *
     * @return Le state de déploiement actuel ou {@code null} si ce n'est pas la phase de déploiement.
     */
    private DeployState getDeployState() {
        AbstractGameState state = partie.getCurrentGameState();
        if (state instanceof DeployState deployState) {
            return deployState;
        }
        return null;
    }
}
