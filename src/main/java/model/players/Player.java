package model.players;

import model.Partie;
import model.ship.Ship;
import model.players.strategies.Strategy;

import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.beans.PropertyChangeSupport;

/**
 * Classe représentant un joueur dans la partie de jeu.
 * Cette classe gère les informations et les actions liées à un joueur, telles que les
 * ships possédés, les commandes choisies, le score, et la stratégie utilisée.
 * Elle permet également la notification des changements via le pattern Observer / Observable
 */
public class Player implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Nombre total fixe de vaisseaux que possède chaque joueur tout au long de la partie
     */
    private static final int NB_SHIPS = 15;

    /**
     * Liste des commandes choisies par le joueur.
     */
    protected List<Integer> choosenCommands;

    /**
     * Liste des ships possédés par le joueur.
     */
    private final List<Ship> ships = new ArrayList<>(NB_SHIPS);

    /**
     * Nom du joueur.
     */
    protected final String name;


    /**
     * Score actuel du joueur.
     */
    private int score;

    /**
     * Stratégie utilisée par le joueur.
     */
    private Strategy strategy;

    /**
     * Couleur attribuée au joueur.
     */
    private final Partie.PlayerColor playerColor;

    /**
     * Support pour la gestion des écouteurs de changement de property.
     */
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructeur de la classe Player.
     * Initialise le joueur avec son nom et sa couleur, et crée les ships associés.
     *
     * @param name        Le nom du joueur.
     * @param playerColor La couleur attribuée au joueur.
     */
    public Player(String name, Partie.PlayerColor playerColor) {
        this.name = name;
        this.playerColor = playerColor;

        // Création des ships du joueur
        for (int i = 0; i < 15; i++) {
            ships.add(new Ship(this));
        }
    }

    /**
     * Définit la stratégie utilisée par le joueur.
     *
     * @param strategy La stratégie à assigner au joueur.
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Retourne la stratégie actuelle du joueur.
     *
     * @return La stratégie du joueur.
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Ajoute un écouteur pour une property spécifique.
     *
     * @param property La property à écouter.
     * @param listener L'écouteur qui sera notifié des changements.
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    /**
     * Retourne le numéro de commande pour une phase spécifique.
     *
     * @param numPhase Le numéro de la phase.
     * @return Le numéro de commande associé à la phase.
     */
    public int getCommandNumberForPhase(int numPhase) {
        return choosenCommands.get(numPhase);
    }

    /**
     * Définit la liste des commandes choisies par le joueur.
     *
     * @param commands La liste des commandes choisies.
     */
    public void setChosenCommands(List<Integer> commands) {
        this.choosenCommands = commands;
    }

    /**
     * Retourne le nom du joueur.
     *
     * @return Le nom du joueur.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Ajoute des points au score du joueur et notifie les écouteurs du changement.
     *
     * @param scoreToAdd Le nombre de points à ajouter au score.
     */
    public void addToScore(int scoreToAdd) {
        score += scoreToAdd;
        pcs.firePropertyChange("SCORE_CHANGE", null, null);
    }

    /**
     * Retourne le score actuel du joueur.
     *
     * @return Le score du joueur.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Retourne la liste des ships possédés par le joueur.
     *
     * @return La liste des ships.
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Retourne la liste des ships non déplacés du joueur.
     *
     * @return La liste des ships non déplacés.
     */
    public List<Ship> getUnusedShips() {
        return ships.stream()
                .filter(ship -> !ship.isDeployed())
                .collect(Collectors.toList());
    }

    /**
     * Réinitialise les ships pour un nouveau tour en appelant leur méthode de réinitialisation.
     */
    public void resetShipsForNewTurn() {
        for (Ship ship : ships) {
            ship.resetForNewTurn();
        }
    }

    /**
     * Vérifie si le joueur est éliminé, c'est-à-dire s'il n'a plus de ships non déplacés.
     *
     * @return True si le joueur est éliminé, false sinon.
     */
    public boolean isEliminated() {
        return getUnusedShips().size() == NB_SHIPS;
    }

    /**
     * Retourne la couleur attribuée au joueur.
     *
     * @return La couleur du joueur.
     */
    public Partie.PlayerColor getColor() {
        return playerColor;
    }

    /**
     * Retourne une représentation textuelle du joueur, basée sur son nom.
     *
     * @return Le nom du joueur.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Méthode utilisée lors de la sérialisation de l'objet.
     * Écrit l'objet par défaut.
     *
     * @param oos Le flux de sortie utilisé pour la sérialisation.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    /**
     * Méthode utilisée lors de la désérialisation de l'objet.
     * Lit l'objet par défaut et réinitialise le support de changement de property.
     *
     * @param ois Le flux d'entrée utilisé pour la désérialisation.
     * @throws IOException            Si une erreur d'entrée/sortie survient.
     * @throws ClassNotFoundException Si la classe de l'objet sérialisé n'est pas trouvée.
     */
    @Serial
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        pcs = new PropertyChangeSupport(this); // Réinitialise le champ transient
    }
}
