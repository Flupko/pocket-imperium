package controllers.state_controllers;

import model.board.Hex;
import model.commands.Exterminate;
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
 * Contrôleur dédié à la phase d'extermination (Exterminate) dans le jeu.
 * Gère les interactions utilisateur pour envahir et exterminer des hexagones adverses.
 * Ce contrôleur écoute les événements de la partie concernant les hexagones
 * possibles à envahir et à choisir les navires pour l'extermination. Il permet à
 * l'utilisateur de sélectionner un hexagone à envahir, de choisir le nombre de navires
 * à engager et de confirmer ou arrêter l'extermination.
 */
public class ExterminateController extends AbstractPhaseController {

    /**
     * Constructeur du contrôleur d'extermination.
     * Initialise les composants nécessaires et configure les écouteurs d'événements.
     *
     * @param boardView       La vue principale du plateau de jeu.
     * @param hexToPolygonMap Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    public ExterminateController(BoardView boardView, HashMap<Hex, HexPolygon> hexToPolygonMap) {
        super(boardView, hexToPolygonMap);
        // Écouteurs pour l'initialisation de l'extermination, le choix des navires et la fin de l'invasion
        partie.addPropertyChangeListener("EXTERMINATE_START", this);
        partie.addPropertyChangeListener("EXTERMINATE_CHOOSE_SHIPS", this);
        partie.addPropertyChangeListener("INVASION_FINISHED", this);
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement aux événements "EXTERMINATE_START" et "EXTERMINATE_CHOOSE_SHIPS"
     * pour mettre à jour la vue d'extermination.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        switch (evt.getPropertyName()) {
            case "EXTERMINATE_START":
                if (newValue instanceof List<?>) {
                    boardExterminateStartUpdate((List<Hex>) newValue);
                }
                break;
            case "EXTERMINATE_CHOOSE_SHIPS":
                if (newValue instanceof List<?>) {
                    boardInvadeChooseShipsUpdate((List<Hex>) newValue);
                }
                break;
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de départ de l'extermination.
     * Met en évidence les hexagones valides pour l'extermination et configure les gestionnaires d'événements.
     *
     * @param invadablesHex Liste des hexagones valides pour l'extermination.
     */
    public void boardExterminateStartUpdate(List<Hex> invadablesHex) {
        Player player = partie.getCurrentPlayer();
        for (Hex hex : invadablesHex) {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            effectedHexPolygons.add(hexPolygon);
            boardView.highlightHex(hexPolygon, "red");
            addSpecialHexHandler(hexPolygon, event -> handleClickInvadeHex(hexPolygon, hex));
        }

        Set<Hex> setInvadableHexes = new HashSet<>(invadablesHex);
        addNonSpecialHexHandler(setInvadableHexes);

        // Configure le bouton "Finish" pour terminer la phase d'extermination
        finishButton.setOnMouseClicked(event -> {
            handleClickFinish();
        });
        finishButton.setVisible(true);

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", choose a hex to invade.");
    }

    /**
     * Gère le clic sur un hexagone valide pour l'extermination.
     * Sélectionne l'hexagone et configure le menu d'action pour lancer l'invasion.
     *
     * @param hexPolygon L'hexagone graphique cliqué.
     * @param hex        L'hexagone du modèle correspondant.
     */
    public void handleClickInvadeHex(HexPolygon hexPolygon, Hex hex) {
        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickInvade(hexPolygon, hex));
            actionMenu.showActionMenu(false, "",
                    false, -1, -1, -1,
                    true, "Invade");
        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour lancer l'invasion d'un hexagone sélectionné.
     *
     * @param hexPolygon L'hexagone graphique sélectionné pour l'invasion.
     * @param hex        L'hexagone du modèle correspondant pour l'invasion.
     */
    public void handleClickInvade(HexPolygon hexPolygon, Hex hex) {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) {
            return;
        }

        if (exterminate.canInvadeHex(hex)) {
            resetPhaseModifications();
            exterminate.startInvadingHex(hex);
        } else {
            actionMenu.showInvalidEntry("Invalid entry.");
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de choix des navires lors de l'extermination.
     * Met en évidence les hexagones déjà envahis et configure les gestionnaires d'événements.
     *
     * @param invadingHexes Liste des hexagones déjà envahis nécessitant une extermination.
     */
    public void boardInvadeChooseShipsUpdate(List<Hex> invadingHexes) {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) {
            return;
        }

        Hex invadedHex = exterminate.getInvadedHex();

        Player player = partie.getCurrentPlayer();

        HexPolygon invadedHexPolygon = hexToPolygonMap.get(invadedHex);
        invadedHexPolygon.showInvadeSparks();
        addSpecialHexHandler(invadedHexPolygon, event -> {
            handleClickCurInvadedHex(invadedHexPolygon);
        });
        effectedHexPolygons.add(invadedHexPolygon);
        boardView.highlightHex(invadedHexPolygon, "red");


        for (Hex hex : invadingHexes) {
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            effectedHexPolygons.add(hexPolygon);
            boardView.highlightHex(hexPolygon, "red");
            addSpecialHexHandler(hexPolygon, event -> {
                handleClickChooseShipsHex(hexPolygon, hex);
            });
        }

        Set<Hex> setSpecialHExes = new HashSet<>(invadingHexes);
        setSpecialHExes.add(invadedHex);
        addNonSpecialHexHandler(setSpecialHExes);


        // Configure le bouton "Finish" pour terminer la phase d'extermination
        finishButton.setOnMouseClicked(event -> {
            handleClickFinish();
        });
        finishButton.setVisible(true);

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", choose ships to Invade.");
    }

    /**
     * Gère le clic sur l'hexagone actuellement envahi.
     * Sélectionne l'hexagone et configure le menu d'action pour arrêter l'invasion.
     *
     * @param invadedHexPolygon L'hexagone graphique actuellement envahi.
     */
    public void handleClickCurInvadedHex(HexPolygon invadedHexPolygon) {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) return;
        if(currentSelectedHex == invadedHexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(invadedHexPolygon);
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickStopEarlyInvasion());
            actionMenu.showActionMenu(
                    false, "",
                    false, -1, -1, -1,
                    true, "Stop Invading");
        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour arrêter prématurément l'invasion.
     * Réinitialise les modifications de phase et termine l'invasion en cours.
     */
    public void handleClickStopEarlyInvasion() {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) return;
        resetPhaseModifications();
        exterminate.finishCurrentInvasion();
    }

    /**
     * Gère le clic sur un hexagone déjà envahi pour choisir le nombre de navires à exterminer.
     * Sélectionne l'hexagone et configure le menu d'action pour choisir les navires.
     *
     * @param hexPolygon L'hexagone graphique cliqué pour choisir les navires à exterminer.
     * @param hex        L'hexagone du modèle correspondant pour l'extermination.
     */
    public void handleClickChooseShipsHex(HexPolygon hexPolygon, Hex hex) {
        if (currentSelectedHex == hexPolygon) {
            deselectCurrentHex();
        } else {
            selectHex(hexPolygon);
            int nbShipsAvailaible = hex.getUninvadedShipCount();
            actionMenu.getActionButton().setOnMouseClicked(event -> handleClickChooseShips(hexPolygon, hex));
            actionMenu.showActionMenu(true, nbShipsAvailaible + " ship(s) available to invade.",
                    true, 1, nbShipsAvailaible, 1,
                    true, "Invade");

        }
    }

    /**
     * Gère l'action de clic sur le bouton d'action pour choisir le nombre de navires à exterminer.
     *
     * @param hexPolygon L'hexagone graphique sélectionné pour choisir les navires.
     * @param hex        L'hexagone du modèle correspondant pour l'extermination.
     */
    public void handleClickChooseShips(HexPolygon hexPolygon, Hex hex) {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) {
            return;
        }

        int nbShipsInvading = actionMenu.getActionSpinnerValue();
        if (exterminate.canAddShipsInvadingHex(hex, nbShipsInvading)) {
            resetPhaseModifications();
            Hex invadedHex = exterminate.getInvadedHex();
            HexPolygon invadedHexPolygon = hexToPolygonMap.get(invadedHex);
            invadedHexPolygon.hideInvadeSparks();
            exterminate.addShipsInvadingHex(hex, nbShipsInvading);
        } else {
            actionMenu.showInvalidEntry("Invalid entry.");
        }

    }

    /**
     * Gère le clic sur le bouton "Finish" pour terminer la phase d'extermination.
     * Réinitialise les modifications de phase et termine la commande d'extermination.
     */
    private void handleClickFinish() {
        Exterminate exterminate = getExterminateCommand();
        if (exterminate == null) {
            return;
        }
        resetPhaseModifications();
        exterminate.finishCommand();
    }

    /**
     * Récupère la commande d'extermination actuelle si la phase de jeu est bien celle-ci.
     *
     * @return La commande d'extermination courante ou {@code null} si ce n'est pas la phase d'extermination.
     */
    private Exterminate getExterminateCommand() {
        AbstractGameState state = partie.getCurrentGameState();
        if (state instanceof PerformState performState && performState.getCurrentCommand() instanceof Exterminate exterminate) {
            return exterminate;
        }
        return null;
    }
}
