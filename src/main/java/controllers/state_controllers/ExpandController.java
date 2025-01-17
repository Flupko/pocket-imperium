package controllers.state_controllers;

import model.board.Hex;
import model.commands.Expand;
import model.players.Player;
import model.states.AbstractGameState;
import model.states.PerformState;
import views.BoardView;
import views.components.board_components.HexPolygon;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contrôleur dédié à la phase d'expansion (Expand) dans le jeu.
 * Gère les interactions utilisateur pour déployer des navires sur les hexagones valides.
 * Ce contrôleur écoute les événements de la partie concernant les hexagones
 * possibles pour l'expansion et met à jour la vue en conséquence. Il permet à
 * l'utilisateur de sélectionner un hexagone pour y déployer des navires et
 * de confirmer l'action via le menu d'action.
 */
public class ExpandController extends AbstractPhaseController {

    /**
     * Constructeur du contrôleur d'expansion.
     * Initialise les composants nécessaires et configure les écouteurs d'événements.
     *
     * @param boardView       La vue principale du plateau de jeu.
     * @param hexToPolygonMap Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    public ExpandController(BoardView boardView, HashMap<Hex, HexPolygon> hexToPolygonMap) {
        super(boardView, hexToPolygonMap);
        // Écouteur pour l'initialisation de l'expansion
        partie.addPropertyChangeListener("EXPAND_POSSIBLE_HEXES", this);
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement à l'événement "EXPAND_POSSIBLE_HEXES" pour mettre à jour la vue d'expansion.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        switch (evt.getPropertyName()) {
            case "EXPAND_POSSIBLE_HEXES":
                if (newValue instanceof List<?>) {
                    boardExpandUpdate((List<Hex>) newValue);
                } else {
                    throw new IllegalArgumentException("New value not list of hexes");
                }
                break;
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase d'expansion.
     * Met en évidence les hexagones valides pour l'expansion et configure les gestionnaires d'événements.
     *
     * @param expandableHexes Liste des hexagones valides pour l'expansion des navires.
     */
    public void boardExpandUpdate(List<Hex> expandableHexes) {

        Expand expand = getExpandCommand();

        if(expand == null) return;

        Player player = partie.getCurrentPlayer();
        Set<Hex> setExpandableHexes = new HashSet<>(expandableHexes);

        // Met en évidence les hexagones valides pour l'expansion
        for (Hex hex : expandableHexes) {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            boardView.highlightHex(hexPolygon, "yellow");
            effectedHexPolygons.add(hexPolygon);
            addSpecialHexHandler(hexPolygon, event -> handleClickExpandHex(hexPolygon, hex));
        }

        // Configure le bouton "Finish" pour terminer la phase d'expansion
        finishButton.setOnMouseClicked(event -> {
            handleClickFinish();
        });
        finishButton.setVisible(true);

        // Configure les gestionnaires pour les hexagones non valides pour l'expansion
        addNonSpecialHexHandler(setExpandableHexes);

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", choose a hex to expand.");
    }

    /**
     * Gère le clic sur un hexagone valide pour l'expansion.
     * Sélectionne l'hexagone et configure le menu d'action pour déployer des navires.
     *
     * @param hexPolygon L'hexagone graphique cliqué.
     * @param hex        L'hexagone du modèle correspondant.
     */
    private void handleClickExpandHex(HexPolygon hexPolygon, Hex hex) {
        Expand expand = getExpandCommand();
        if (expand == null) return;

        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickExpand(hexPolygon, hex));
            actionMenu.showActionMenu(
                    true, expand.getShipsCanAdd() + " ships available.",
                    true, 1, expand.getShipsCanAdd(), 1,
                    true, "Expand");
        }
    }

    /**
     * Gère l'action de clic sur le bouton "Finish" pour terminer la phase d'expansion.
     * Réinitialise les modifications de phase et termine la commande d'expansion.
     */
    private void handleClickFinish(){
        Expand expand = getExpandCommand();
        if (expand == null) {
            return;
        }
        resetPhaseModifications();
        expand.finishCommand();
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour déployer des navires sur un hexagone sélectionné.
     *
     * @param hexPolygon L'hexagone graphique sélectionné.
     * @param hex        L'hexagone du modèle correspondant.
     */
    private void handleClickExpand(HexPolygon hexPolygon, Hex hex) {
        Expand expand = getExpandCommand();
        if (expand == null) {
            return;
        }

        int nbShipsAdded = actionMenu.getActionSpinnerValue();
        if (expand.canExpandFleet(hex, nbShipsAdded)) {
            resetPhaseModifications();
            expand.addShips(hex, nbShipsAdded);
        } else {
            actionMenu.showInvalidEntry("Invalid entry.");
        }
    }

    /**
     * Récupère la commande d'expansion actuelle si la phase de jeu est bien celle-ci.
     *
     * @return La commande d'expansion courante ou {@code null} si ce n'est pas la phase d'expansion.
     */
    private Expand getExpandCommand() {
        AbstractGameState state = partie.getCurrentGameState();
        if (state instanceof PerformState performState && performState.getCurrentCommand() instanceof Expand expand) {
            return expand;
        }
        return null;
    }
}
