package model.states;

import model.Partie;
import model.board.Board;
import model.players.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Représente un State générique du jeu (GameState) dans le pattern State.
 * Les classes dérivées implémentent les différentes phases du jeu
 * (ex.: DeployState, PlanState, PerformState, etc.).
 * Cette classe stocke les références essentielles à la partie,
 * aux joueurs, et au plateau.
 */
public abstract class AbstractGameState implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Référence vers l'instance unique de la partie.
     */
    protected final Partie partie;

    /**
     * Liste des joueurs participant à la partie.
     */
    protected final List<Player> players;

    /**
     * Plateau de jeu (Board) associé à la partie.
     */
    protected final Board board;

    /**
     * Constructeur de la classe abstraite AbstractGameState.
     * @param partie L'instance unique de la partie en cours.
     */
    public AbstractGameState(Partie partie) {
        this.partie = partie;
        this.players = partie.getPlayers();
        this.board = partie.getBoard();
    }

    /**
     * Méthode d'exécution principale pour l'état actuel.
     * Les sous-classes doivent fournir leur propre logique
     * selon la phase du jeu (Deploy, Plan, Perform, etc.).
     */
    public abstract void execute();
}
