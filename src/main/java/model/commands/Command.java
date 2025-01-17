package model.commands;

import model.Partie;
import model.board.Board;
import model.players.Player;
import model.states.PerformState;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serial;
import java.io.Serializable;

/**
 * Représente une commande abstraite dans le jeu.
 * Chaque commande (comme Expand, Explore, Exterminate) hérite de cette classe
 * et fournit sa propre logique d'exécution et de fin.
 * Les commandes accèdent au plateau (Board), à la partie (Partie) et au state
 * PerformState pour enchaîner avec d'autres commandes.
 */
public abstract class Command implements Serializable {

    /** Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Référence vers le plateau (Board) sur lequel la commande agit
     * (accès aux hexagones, secteurs, etc.).
     */
    protected final Board board;

    /**
     * Référence vers l'instance unique de la partie (Partie).
     * Permet d'accéder à l'état global (joueurs, tours, etc.).
     */
    protected final Partie partie;

    /**
     * State Perform (PerformState) pour gérer le déroulement des différentes commandes
     * lors de la phase Perform du jeu.
     */
    protected final PerformState performState;

    /**
     * Constructeur de la classe abstraite Command.
     * @param board         Le plateau de jeu.
     * @param performState  Le state Perform qui coordonne l'exécution des commandes.
     */
    public Command(Board board, PerformState performState) {
        this.board = board;
        this.partie = Partie.getInstance();
        this.performState = performState;
    }

    /**
     * Méthode principale pour exécuter la logique de la commande.
     * Chaque classe concrète doit implémenter son propre comportement.
     */
    public abstract void execute();

    /**
     * Termine la commande en cours. Chaque commande concrète décide
     * de la manière dont elle finalise son exécution.
     */
    public abstract void finishCommand();
}