package model.states;

import model.Partie;
import model.board.Sector;
import model.players.Player;

import java.io.Serial;
import java.util.Comparator;

/**
 * State marquant la fin de la partie (EndGame).
 * Au passage dans cet état, on effectue un scoring final de tous les secteurs,
 * puis on détermine le joueur ayant le score le plus élevé comme gagnant.
 * Ensuite, on notifie l'issue de la partie par un événement.
 */
public class EndGameState extends AbstractGameState {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Le joueur vainqueur de la partie. Peut être null tant que non déterminé.
     */
    private Player winner = null;

    /**
     * Constructeur de la classe EndGameState.
     * @param partie Instance de la partie en cours.
     */
    public EndGameState(Partie partie) {
        super(partie);
    }

    /**
     * Méthode principale de la fin de partie.
     * 1) Score tous les secteurs en fin de partie (double la valeur).
     * 2) Détermine le joueur gagnant selon les scores.
     * 3) Notifie l'issue (END_GAME) avec le vainqueur.
     */
    @Override
    public void execute() {
        if (winner == null) {
            scoreAllSectorsEndOfGame();
            winner = determinePlayerWithHighestScore();
        }
        notifyWinner();
    }

    /**
     * Score tous les secteurs à la fin de la partie,
     * en leur appliquant un mode "fin de partie" (valeur doublée).
     */
    private void scoreAllSectorsEndOfGame() {
        for (Sector sector : board.getSectors()) {
            sector.scoreSector(true); // true indique fin de partie
        }
    }

    /**
     * Détermine le joueur ayant le score le plus élevé.
     * @return Le joueur vainqueur.
     * @throws IllegalStateException si aucun joueur n'est disponible
     */
    private Player determinePlayerWithHighestScore() {
        return players.stream()
                .max(Comparator.comparingInt(Player::getScore))
                .orElseThrow(() -> new IllegalStateException("Aucun joueur disponible pour déterminer un gagnant."));
    }

    /**
     * Notifie les écouteurs de la fin de partie, en transmettant le gagnant.
     * Puis logge le résultat dans la console.
     */
    private void notifyWinner() {
        partie.firePropertyChange("END_GAME", null, winner);
    }
}
