package controllers.state_controllers;

import model.Partie;
import model.players.Player;
import model.states.AbstractGameState;
import model.states.PlanState;
import views.BoardView;
import views.components.info_components.Instructions;
import views.components.action_components.PlanCommandsPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Contrôleur dédié à la phase de planification des commandes (Plan) dans le jeu.
 * Gère les interactions utilisateur pour sélectionner et valider l'ordre des commandes.
 * Ce contrôleur écoute l'événement "PLAN" de la partie et, lorsqu'il est déclenché,
 * il affiche le panneau de planification des commandes, permet à l'utilisateur de
 * sélectionner les commandes dans l'ordre souhaité, et valide cette sélection.
 */
public class PlanCommandsController implements PropertyChangeListener {

    /**
     * Vue principale du plateau de jeu.
     */
    private final BoardView boardView;

    /**
     * Instance unique de la partie en cours.
     */
    private final Partie partie;

    /**
     * Panneau de planification des commandes affiché à l'utilisateur.
     */
    private final PlanCommandsPanel planCommandsPanel;

    /**
     * Composant affichant les instructions pour l'utilisateur.
     */
    private final Instructions instructions;

    /**
     * Constructeur du contrôleur de planification des commandes.
     * Initialise les composants nécessaires et configure les écouteurs d'événements.
     *
     * @param boardView La vue principale du plateau de jeu.
     */
    public PlanCommandsController(BoardView boardView) {
        this.boardView = boardView;
        this.partie = Partie.getInstance();
        // Écouteur pour l'événement de planification des commandes
        partie.addPropertyChangeListener("PLAN", this);
        this.planCommandsPanel = boardView.getPlanCommandsPanel();
        this.instructions = boardView.getInstructions();
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement à l'événement "PLAN" pour mettre à jour la vue de planification.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "PLAN":
                boardPlanUpdate();
                break;
        }
    }

    /**
     * Met à jour l'interface utilisateur pour la phase de planification des commandes.
     * Affiche le panneau de planification, réinitialise son state, et configure les interactions.
     */
    private void boardPlanUpdate() {
        Player player = partie.getCurrentPlayer();
        // Réinitialise l'état du panneau de planification
        planCommandsPanel.resetState();
        // Affiche le panneau de planification dans la vue principale
        boardView.showPlanCommandsPanel();

        // Affiche les instructions pour le joueur
        instructions.setInstructions(player.getName() + ", please click on your commands in the order you want to execute them.");
        // Configure l'action du bouton "Validate" pour soumettre l'ordre des commandes
        planCommandsPanel.getSubmitButton().setOnMouseClicked(e -> handleClickPlan(player));
    }

    /**
     * Gère la soumission de l'ordre des commandes planifiées par l'utilisateur.
     * Vérifie que toutes les commandes ont été sélectionnées et valide l'ordre.
     *
     * @param player Le joueur actuel effectuant la planification.
     */
    private void handleClickPlan(Player player) {
        PlanState planState = getPlanState();
        if (planState == null) {
            return;
        }

        List<Integer> clickOrder = planCommandsPanel.getClickOrder();
        if (clickOrder.size() < 3) {
            // Affiche un message d'erreur si toutes les commandes n'ont pas été sélectionnées
            planCommandsPanel.showInvalidEntry();
        } else {
            // Masque le panneau de planification et réinitialise les instructions
            boardView.hidePlanCommandsPanel();
            instructions.setInstructions("");
            // Valide l'ordre des commandes dans l'état de planification
            planState.planCommands(clickOrder);
        }
    }

    /**
     * Récupère le state actuel de planification des commandes si la phase de jeu est bien celle-ci.
     *
     * @return Le state de planification actuel ou {@code null} si ce n'est pas la phase de planification.
     */
    private PlanState getPlanState() {
        AbstractGameState state = partie.getCurrentGameState();
        if (state instanceof PlanState planState) {
            return planState;
        }
        return null;
    }
}
