package controllers.state_controllers;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import model.Partie;
import model.board.Hex;
import views.BoardView;
import views.components.action_components.ActionMenu;
import views.components.board_components.HexPolygon;
import views.components.info_components.Instructions;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Classe abstraite représentant un contrôleur de phase dans le jeu.
 * Elle fournit des fonctionnalités communes pour gérer les différentes phases du jeu
 * telles que la sélection des hexagones, la gestion des événements utilisateur,
 * et la manipulation des composants visuels associés.
 * Cette classe implémente {@link PropertyChangeListener} pour écouter les changements de state
 * de la partie et réagir en conséquence.
 */
public abstract class AbstractPhaseController implements PropertyChangeListener {

    /**
     * Vue principale du plateau de jeu.
     */
    protected final BoardView boardView;

    /**
     * Liste des hexagones affectés visuellement durant la phase.
     */
    protected final List<HexPolygon> effectedHexPolygons = new ArrayList<>();

    /**
     * Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    protected final HashMap<Hex, HexPolygon> hexToPolygonMap;

    /**
     * Mappage entre les hexagones graphiques et leurs gestionnaires d'événements.
     */
    protected final HashMap<HexPolygon, EventHandler<MouseEvent>> hexesEventMap = new HashMap<>();

    /**
     * Hexagone actuellement sélectionné par l'utilisateur.
     */
    protected HexPolygon currentSelectedHex;

    /**
     * Instance de la partie en cours.
     */
    protected final Partie partie;

    /**
     * Composant affichant les instructions pour l'utilisateur.
     */
    protected final Instructions instructions;

    /**
     * Menu d'action affiché pour effectuer des actions spécifiques.
     */
    protected final ActionMenu actionMenu;

    /**
     * Bouton permettant de terminer la phase en cours.
     */
    protected final Button finishButton;

    /**
     * Constructeur de la classe AbstractPhaseController.
     * Initialise les composants nécessaires et associe la vue et le mappage des hexagones.
     *
     * @param boardView      La vue principale du plateau de jeu.
     * @param hexToPolygonMap Mappage entre les hexagones du modèle et leurs représentations graphiques.
     */
    public AbstractPhaseController(BoardView boardView, HashMap<Hex, HexPolygon> hexToPolygonMap) {
        this.boardView = boardView;
        this.hexToPolygonMap = hexToPolygonMap;
        this.instructions = boardView.getInstructions();
        this.actionMenu = boardView.getActionMenu();
        this.finishButton = boardView.getFinishButton();
        this.partie = Partie.getInstance();
    }

    /**
     * Sélectionne l'hexagone graphique spécifié et met à jour l'état visuel.
     *
     * @param hexPolygon L'hexagone graphique à sélectionner.
     */
    protected void selectHex(HexPolygon hexPolygon) {
        if (currentSelectedHex != null) {
            currentSelectedHex.unselectHex();
        }

        currentSelectedHex = hexPolygon;
        currentSelectedHex.selectHex();
    }

    /**
     * Réinitialise les modifications apportées durant la phase.
     * Cela inclut la réinitialisation des instructions, la désélection de l'hexagone actuel,
     * le nettoyage des gestionnaires d'événements et des effets visuels,
     * ainsi que la suppression du bouton de fin de phase.
     */
    protected void resetPhaseModifications(){
        instructions.setInstructions("");
        deselectCurrentHex();
        clearEventHandlers();
        clearVisualEffects();
        removeFinishButton();
    }

    /**
     * Désélectionne l'hexagone actuellement sélectionné et masque le menu d'action.
     * Si aucun hexagone n'est sélectionné, cette méthode n'a aucun effet.
     */
    protected void deselectCurrentHex() {
        if (currentSelectedHex != null) {
            currentSelectedHex.unselectHex();
            currentSelectedHex = null;
        }
        actionMenu.hideActionMenu();
        actionMenu.getActionButton().setOnMouseClicked(null);
    }

    /**
     * Ajoute un gestionnaire d'événements de clic à un hexagone "spécial".
     * Un hexagone spécial est généralement lié à une action spécifique durant la phase.
     *
     * @param hexPolygon L'hexagone graphique auquel ajouter le gestionnaire.
     * @param handler    Le gestionnaire d'événements à associer.
     */
    protected void addSpecialHexHandler(HexPolygon hexPolygon, EventHandler<MouseEvent> handler) {
        hexesEventMap.put(hexPolygon, handler);
        hexPolygon.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
    }

    /**
     * Ajoute des gestionnaires d'événements de clic aux hexagones non spéciaux.
     * Ces hexagones n'ont pas d'action spécifique associée et réagissent simplement en désélectionnant l'hexagone actuel.
     *
     * @param specialHexes Ensemble des hexagones spéciaux à exclure de l'ajout de gestionnaires.
     */
    protected void addNonSpecialHexHandler(Set<Hex> specialHexes) {
        for (Hex hex : hexToPolygonMap.keySet()) {
            if(specialHexes.contains(hex)) {
                continue;
            }
            HexPolygon hexPolygon = hexToPolygonMap.get(hex);
            EventHandler<MouseEvent> handler = event -> {
                actionMenu.getActionButton().setOnMouseClicked(null);
                actionMenu.hideActionMenu();
                deselectCurrentHex();
            };
            hexesEventMap.put(hexPolygon, handler);
            hexPolygon.addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        }
    }

    /**
     * Supprime tous les gestionnaires d'événements associés aux hexagones.
     * Cette méthode est utile pour réinitialiser l'état avant de configurer une nouvelle phase.
     */
    protected void clearEventHandlers() {
        for (Map.Entry<HexPolygon, EventHandler<MouseEvent>> entry : hexesEventMap.entrySet()) {
            HexPolygon polygon = entry.getKey();
            EventHandler<MouseEvent> handler = entry.getValue();
            polygon.removeEventHandler(MouseEvent.MOUSE_CLICKED, handler);
        }
        hexesEventMap.clear();
    }

    /**
     * Réinitialise les effets visuels appliqués aux hexagones durant la phase.
     * Cette méthode nettoie les modifications visuelles pour préparer une nouvelle phase.
     */
    protected void clearVisualEffects() {
        for(HexPolygon hexPolygon : effectedHexPolygons) {
            hexPolygon.resetEffets();
        }
    }

    /**
     * Masque et désactive le bouton de fin de phase.
     * Cette action est généralement effectuée à la fin d'une phase pour empêcher des interactions supplémentaires.
     */
    protected void removeFinishButton() {
        finishButton.setVisible(false);
        finishButton.setOnMouseClicked(null);
    }
}
