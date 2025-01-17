package controllers.state_controllers;

import javafx.scene.control.Button;
import model.board.Hex;
import model.commands.Explore;
import model.players.Player;
import model.states.AbstractGameState;
import model.states.PerformState;
import views.BoardView;
import views.components.board_components.HexPolygon;

import java.beans.PropertyChangeEvent;
import java.util.*;

/**
 * Contrôleur dédié à la phase d'exploration (Explore) dans le jeu.
 * Gère les interactions utilisateur pour explorer de nouveaux hexagones.
 * Ce contrôleur écoute les événements de la partie concernant les hexagones
 * possibles pour l'exploration et met à jour la vue en conséquence. Il permet à
 * l'utilisateur de sélectionner un hexagone de départ et des hexagones suivants
 * pour poursuivre l'exploration, ainsi que de confirmer ou arrêter l'exploration.
 */
public class ExploreController extends AbstractPhaseController {

    /**
     * Constructeur du contrôleur d'exploration.
     * Initialise les composants nécessaires et configure les écouteurs d'événements.
     *
     * @param boardView       La vue principale du plateau de jeu.
     * @param hexToPolygonMap Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    public ExploreController(BoardView boardView, HashMap<Hex, HexPolygon> hexToPolygonMap) {
        super(boardView, hexToPolygonMap);
        // Écouteurs pour l'initialisation de l'exploration et la sélection des hexagones suivants
        partie.addPropertyChangeListener("EXPLORE_START_HEXES", this);
        partie.addPropertyChangeListener("EXPLORE_NEXT_HEX", this);
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement aux événements "EXPLORE_START_HEXES" et "EXPLORE_NEXT_HEX"
     * pour mettre à jour la vue d'exploration.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof List<?>) {
            switch (evt.getPropertyName()) {
                case "EXPLORE_START_HEXES" -> boardExploreStartUpdate((List<Hex>) evt.getNewValue());
                case "EXPLORE_NEXT_HEX" -> boardExploreNextUpdate((List<Hex>) evt.getNewValue());
            }
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de départ de l'exploration.
     * Met en évidence les hexagones valides pour le départ et configure les gestionnaires d'événements.
     *
     * @param startHexes Liste des hexagones valides pour le départ de l'exploration.
     */
    private void boardExploreStartUpdate(List<Hex> startHexes) {

        Player player = partie.getCurrentPlayer();

        Set<Hex> setSpecialHexes = new HashSet<>(startHexes);

        startHexes.forEach(hex -> {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            effectedHexPolygons.add(hexPolygon);
            boardView.highlightHex(hexPolygon, "blue");
            addSpecialHexHandler(hexPolygon, event -> handleClickStartExploreHex(hexPolygon, hex));
        });


        // Configure le bouton "Finish" pour terminer la phase d'exploration
        finishButton.setOnMouseClicked(event -> {
            handleClickFinish();
        });
        finishButton.setVisible(true);

        // Configure les gestionnaires pour les hexagones non valides pour le départ
        addNonSpecialHexHandler(setSpecialHexes);
        instructions.setInstructions(player.getName() + ", choose a hex to start Exploring.");
    }

    /**
     * Gère le clic sur un hexagone valide pour le départ de l'exploration.
     * Sélectionne l'hexagone et configure le menu d'action pour démarrer l'exploration.
     *
     * @param hexPolygon L'hexagone graphique cliqué.
     * @param hex        L'hexagone du modèle correspondant.
     */
    private void handleClickStartExploreHex(HexPolygon hexPolygon, Hex hex) {
        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickStartExplore(hex));
            actionMenu.showActionMenu(
                    true, hex.getUnmovedShips().size() + " ship(s) available.",
                    false, -1, -1, -1,
                    true, "Explore");
        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour démarrer l'exploration depuis un hexagone sélectionné.
     *
     * @param hex L'hexagone sélectionné pour démarrer l'exploration.
     */
    private void handleClickStartExplore(Hex hex) {
        Explore explore = getExploreCommand();
        if (explore != null && explore.canStartExplore(hex)) {
            resetPhaseModifications();
            explore.startExplore(hex);
        } else {
            actionMenu.showInvalidEntry("Invalid entry.");
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de sélection des hexagones suivants lors de l'exploration.
     * Met en évidence les hexagones valides pour poursuivre l'exploration et configure les gestionnaires d'événements.
     *
     * @param nextHexes Liste des hexagones valides pour poursuivre l'exploration.
     */
    private void boardExploreNextUpdate(List<Hex> nextHexes) {
        Explore explore = getExploreCommand();
        if (explore == null) return;

        Player player = partie.getCurrentPlayer();
        Hex curHex = explore.getCurHex();
        HexPolygon curHexPolygon = hexToPolygonMap.get(curHex);

        Set<Hex> setSpecialHexes = new HashSet<>(new HashSet<>(nextHexes));
        setSpecialHexes.add(curHex);
        addNonSpecialHexHandler(setSpecialHexes);

        if (explore.getSizeCurMovementPath() > 1) {
            addFlyingShipsEffect(explore, player, curHex, curHexPolygon);
            addSpecialHexHandler(curHexPolygon, event -> {
                handleClickCurHex(curHexPolygon);
            });
            effectedHexPolygons.add(curHexPolygon);
            boardView.highlightHex(curHexPolygon, "blue");
        }

        nextHexes.forEach(nextHex -> {
            HexPolygon nextHexPolygon = hexToPolygonMap.get(nextHex);
            effectedHexPolygons.add(nextHexPolygon);
            boardView.highlightHex(nextHexPolygon, "blue");
            addSpecialHexHandler(nextHexPolygon, event -> {
                handleClickExploreNextHex(nextHexPolygon, nextHex, curHex);
            });
        });

        // Configure le bouton "Finish" pour terminer la phase d'exploration
        Button finishButton = boardView.getFinishButton();
        finishButton.setOnMouseClicked(event -> {
            handleClickFinish();
        });
        finishButton.setVisible(true);

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", choose a Hex to Explore next.");
    }

    /**
     * Ajoute un effet visuel représentant des navires en vol lors de l'exploration.
     *
     * @param explore       L'objet {@link Explore} gérant le state de l'exploration.
     * @param player        Le joueur actuel.
     * @param curHex        L'hexagone courant où l'exploration se déroule.
     * @param curHexPolygon La représentation graphique de l'hexagone courant.
     */
    private void addFlyingShipsEffect(Explore explore, Player player, Hex curHex, HexPolygon curHexPolygon) {
        Hex hexBefore = explore.getHexBefore();
        double angle = 30 + 60 * Hex.getNeighborDirection(curHex.getCoords(), hexBefore.getCoords());
        curHexPolygon.addFlyingShips(explore.getCurFleetSize(), player.getColor().toString(), angle);
    }

    /**
     * Gère le clic sur un hexagone valide pour poursuivre l'exploration.
     * Sélectionne l'hexagone et configure le menu d'action pour ajuster la flotte.
     *
     * @param hexPolygon L'hexagone graphique cliqué.
     * @param nextHex    L'hexagone du modèle correspondant pour l'exploration suivante.
     * @param curHex     L'hexagone du modèle actuellement exploré.
     */
    private void handleClickExploreNextHex(HexPolygon hexPolygon, Hex nextHex, Hex curHex) {
        Explore explore = getExploreCommand();
        if (explore == null) return;

        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickExploreNext(nextHex));
            actionMenu.showActionMenu(
                    true, "Choose fleet adjustment.",
                    true, -explore.getCurFleetSize() + 1, curHex.getUnmovedShips().size(), 0,
                    true, "Move");
        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour ajuster la flotte lors de l'exploration.
     *
     * @param hex L'hexagone sélectionné pour poursuivre l'exploration.
     */
    private void handleClickExploreNext(Hex hex) {
        Explore explore = getExploreCommand();
        if (explore == null) return;

        int fleetAdjustment = actionMenu.getActionSpinnerValue();
        if (explore.canExploreNext(hex, fleetAdjustment)) {
            resetPhaseModifications();
            explore.exploreNext(hex, fleetAdjustment);
        } else {
            actionMenu.showInvalidEntry("Invalid entry.");
        }
    }

    /**
     * Gère le clic sur l'hexagone courant pour arrêter prématurément l'exploration.
     *
     * @param curHexPolygon L'hexagone graphique courant.
     */
    private void handleClickCurHex(HexPolygon curHexPolygon) {
        Explore explore = getExploreCommand();
        if (explore == null) return;
        if(currentSelectedHex == curHexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(curHexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickStopEarlyMovement());
            actionMenu.showActionMenu(
                    false, "",
                    false, -1, -1, -1,
                    true, "Stop");
        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour arrêter prématurément l'exploration.
     * Réinitialise les modifications de phase et termine l'exploration en cours.
     */
    private void handleClickStopEarlyMovement(){
        Explore explore = getExploreCommand();
        if (explore == null) return;
        resetPhaseModifications();
        explore.finishCurrentMovement();
    }

    /**
     * Récupère la commande d'exploration actuelle si la phase de jeu est bien celle-ci.
     *
     * @return La commande d'exploration courante ou {@code null} si ce n'est pas la phase d'exploration.
     */
    private Explore getExploreCommand() {
        AbstractGameState state = partie.getCurrentGameState();
        return (state instanceof PerformState performState && performState.getCurrentCommand() instanceof Explore explore)
                ? explore : null;
    }

    /**
     * Gère le clic sur le bouton "Finish" pour terminer la phase d'exploration.
     * Réinitialise les modifications de phase et termine la commande d'exploration.
     */
    private void handleClickFinish() {
        Explore explore = getExploreCommand();
        if (explore == null) {
            return;
        }
        resetPhaseModifications();
        explore.finishCommand();
    }
}
