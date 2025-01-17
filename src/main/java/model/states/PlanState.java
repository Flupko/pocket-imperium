package model.states;

import model.Partie;
import model.players.Player;

import java.io.Serial;
import java.util.List;

/**
 * State gérant la phase de planification (Plan) du jeu.
 * Chaque joueur doit choisir l'ordre de ses commandes (Expand, Explore, Exterminate)
 * pour la phase Perform. Une fois tous les joueurs servis, on passe à PerformState.
 */
public class PlanState extends AbstractGameState {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Compteur indiquant combien de joueurs ont déjà planifié leurs commandes.
     */
    private int planPhaseCounter = 0;

    /**
     * Constructeur de la classe PlanState.
     * @param partie Instance de la partie en cours.
     */
    public PlanState(Partie partie) {
        super(partie);
    }

    /**
     * Méthode principale de la phase Plan.
     * Choisit le joueur à tour de rôle (en fonction de planPhaseCounter),
     * puis demande à sa stratégie de définir l'ordre des 3 commandes.
     * Lorsque tout le monde a planifié, on passe à PerformState.
     */
    @Override
    public void execute() {
        if (planPhaseCounter < players.size()) {
            partie.setCurrentPlayer(planPhaseCounter);
            Player currentPlayer = partie.getCurrentPlayer();
            currentPlayer.getStrategy().handlePlanChooseCommands(this);
        } else {
            partie.transitionTo(new PerformState(partie));
            partie.runCurrentState();
        }
    }

    /**
     * Méthode appelée lorsque le joueur a fixé l'ordre de ses commandes.
     * @param chosenCommands Liste des commandes, par exemple [3,2,1].
     */
    public void planCommands(List<Integer> chosenCommands) {
        Player currentPlayer = partie.getCurrentPlayer();
        currentPlayer.setChosenCommands(chosenCommands);
        planPhaseCounter++;
        execute();
    }
}
