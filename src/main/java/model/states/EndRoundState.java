package model.states;

import model.Partie;
import model.board.Sector;
import model.players.Player;
import java.io.Serial;

/**
 * State gérant la fin d'un round (EndRound).
 * On :
 * 1) Réinitialise l'état scoré des secteurs
 * 2) Vérifie si la partie se termine (limite de tours ou élimination de joueurs)
 * 3) Sinon, on incrémente le tour, on réinitialise les ships pour le nouveau tour,
 *    on réorganise les joueurs, puis on revient à PlanState.
 */
public class EndRoundState extends AbstractGameState {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Limite maximale de tours autorisés.
     */
    private static final int MAX_TURNS = 9;

    /**
     * Constructeur de la classe EndRoundState.
     * @param partie Instance de la partie en cours.
     */
    public EndRoundState(Partie partie) {
        super(partie);
    }

    /**
     * Méthode principale de la fin de round.
     * Réinitialise les secteurs, vérifie la condition de fin de partie,
     * sinon incrémente le tour et retourne en PlanState pour entamer
     * un nouveau cycle.
     */
    @Override
    public void execute() {
        resetSectorsForNewTurn();
        if (partie.getTurn() == MAX_TURNS || areAnyPlayersEliminated()) {
            partie.transitionTo(new EndGameState(partie));
            partie.runCurrentState();
        } else {
            partie.rotatePlayers();
            resetShipsForNewTurn();
            partie.incrementTurn();
            partie.transitionTo(new PlanState(partie));
            partie.runCurrentState();
        }
    }

    /**
     * Réinitialise l'état "scored" de tous les secteurs pour le nouveau tour.
     */
    private void resetSectorsForNewTurn() {
        for (Sector sector : board.getSectors()) {
            sector.resetScored();
        }
    }

    /**
     * Vérifie si un joueur est éliminé (n'a plus de ships),
     * ce qui peut entraîner la fin anticipée de la partie.
     * @return true si au moins un joueur est éliminé, false sinon.
     */
    private boolean areAnyPlayersEliminated() {
        return players.stream().anyMatch(Player::isEliminated);
    }

    /**
     * Réinitialise les ships de chaque joueur (marque hasMoved=false, hasInvaded=false, etc.)
     * pour le nouveau tour.
     */
    private void resetShipsForNewTurn() {
        players.forEach(Player::resetShipsForNewTurn);
    }
}
