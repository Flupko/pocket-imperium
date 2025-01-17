package model;

import model.board.Board;
import model.players.Player;
import model.players.strategies.HumanStrategy;
import model.players.strategies.RobotAgressiveStrategy;
import model.players.strategies.RobotAmicalStrategy;
import model.states.DeployState;
import model.states.AbstractGameState;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;

/**
 * Classe centrale représentant une partie de jeu. Cette classe implémente le pattern Singleton pour assurer qu'il n'existe qu'une seule instance de Partie.
 * Elle gère le plateau de jeu, les joueurs, le state actuel du jeu, et les interactions entre eux.
 * La classe permet également de sauvegarder et charger l'état de la partie.
 * La classe sert de point d'entrée pour le pattern State. Les states du jeu émettent leurs événements via l'instance unique de {@link Partie}.
 */
public class Partie implements Serializable {

    /** Instance unique de la classe Partie (Singleton). */
    private static Partie instance;

    /** Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Dossier où les sauvegardes des parties sont stockées. */
    public static final String SAVES_FOLDER = "saves";

    /** Nombre maximum de joueurs dans une partie. */
    public static final int NB_PLAYERS = 3;

    /** Plateau de jeu associé à cette partie. */
    private final Board board;

    /** Support pour la gestion des Observers. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /** Liste des joueurs participant à la partie. */
    private final List<Player> players = new ArrayList<>();

    /** Numéro du tour actuel dans la partie. */
    private int turn;

    /** Joueur actuellement en train de jouer. */
    private Player currentPlayer;

    /** State actuel du jeu. */
    private AbstractGameState currentGameState;

    /**
     * Enumération représentant les couleurs attribuées aux joueurs.
     */
    public enum PlayerColor {
        BLUE,
        GREEN,
        RED
    }

    /**
     * Constructeur privé pour empêcher l'instanciation multiple (Singleton).
     * Initialise le tour, le plateau de jeu et le state initial du jeu.
     */
    private Partie() {
        this.turn = 1;
        this.board = new Board();
        this.currentGameState = new DeployState(this);
    }

    /**
     * Méthode permettant de récupérer l'instance unique de Partie.
     * Si l'instance n'existe pas encore, elle est créée.
     *
     * @return L'instance unique de Partie.
     */
    public static synchronized Partie getInstance() {
        if (instance == null) {
            instance = new Partie();
        }
        return instance;
    }

    /**
     * Sauvegarde l'état actuel de la partie dans un fichier.
     * La sauvegarde est effectuée dans le dossier spécifié par {@link #SAVES_FOLDER}.
     *
     * @param fileName Le nom du fichier de sauvegarde (sans extension).
     * @throws IOException Si une erreur survient lors de la création du dossier ou de l'écriture du fichier.
     */
    public static void saveGame(String fileName) throws IOException {
        Partie savedPartie = getInstance();

        // Vérifie si le nombre de joueurs est suffisant pour sauvegarder la partie
        if (savedPartie.getPlayers().size() < NB_PLAYERS) {
            return;
        }

        // Assure que le dossier de sauvegarde existe, sinon le crée
        File folder = new File(SAVES_FOLDER);
        if (!folder.exists()) {
            if (!folder.mkdirs()) { // Crée le dossier si nécessaire
                throw new IOException("Échec de la création du répertoire : " + SAVES_FOLDER);
            }
        }

        // Ajoute l'extension ".ser" au nom du fichier
        fileName += ".ser";

        // Combine le chemin du dossier et le nom du fichier
        String fullFilePath = SAVES_FOLDER + File.separator + fileName;

        // Sérialise l'instance unique de Partie dans le fichier spécifié
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fullFilePath))) {
            out.writeObject(savedPartie);
        }
    }

    /**
     * Charge une partie à partir d'un fichier de sauvegarde.
     * La sauvegarde est lue depuis le dossier spécifié par {@link #SAVES_FOLDER}.
     *
     * @param fileName Le nom du fichier de sauvegarde (avec extension).
     * @throws IOException            Si une erreur survient lors de la lecture du fichier.
     * @throws ClassNotFoundException Si la classe de l'objet sérialisé n'est pas trouvée.
     */
    public static void loadGame(String fileName) throws IOException, ClassNotFoundException {
        // Combine le chemin du dossier et le nom du fichier
        String fullFilePath = SAVES_FOLDER + File.separator + fileName;

        // Désérialise l'instance de Partie depuis le fichier spécifié
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fullFilePath))) {
            instance = (Partie) in.readObject();
        }
    }

    /**
     * Transite vers un nouveau state du jeu.
     * Met à jour l'état actuel et affiche une information sur la transition.
     *
     * @param newState Le nouveau state vers lequel transiter.
     */
    public void transitionTo(AbstractGameState newState) {
        this.currentGameState = newState;
    }

    /**
     * Exécute la logique du state actuel du jeu.
     */
    public void runCurrentState() {
        this.currentGameState.execute();
    }

    /**
     * Retourne le state actuel du jeu.
     *
     * @return Le state actuel du jeu.
     */
    public AbstractGameState getCurrentGameState() {
        return currentGameState;
    }

    /**
     * Ajoute un écouteur pour un changement de property spécifique.
     *
     * @param propertyName Le nom de la property à écouter.
     * @param listener     L'écouteur qui sera notifié des changements.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Supprime un écouteur pour un changement de property spécifique.
     *
     * @param propertyName Le nom de la property.
     * @param listener     L'écouteur à supprimer.
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Déclenche un événement de changement de property.
     *
     * @param propertyName Le nom de la property qui change.
     * @param oldValue     L'ancienne valeur de la property.
     * @param newValue     La nouvelle valeur de la property.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fait pivoter l'ordre des joueurs et déclenche un événement indiquant le changement d'ordre.
     */
    public void rotatePlayers() {
        Collections.rotate(players, 1);
        firePropertyChange("PLAYER_ORDER_CHANGE", null, players);
    }

    /**
     * Définit le joueur actuel par son index dans la liste des joueurs et déclenche un événement.
     *
     * @param index L'index du joueur à définir comme joueur actuel.
     */
    public void setCurrentPlayer(int index) {
        currentPlayer = players.get(index);
        firePropertyChange("CURRENT_PLAYER_CHANGE", null, currentPlayer);
    }

    /**
     * Retourne le plateau de jeu associé à cette partie.
     *
     * @return Le plateau de jeu.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Retourne le joueur actuellement actif.
     *
     * @return Le joueur actuel.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Retourne une liste non modifiable des joueurs participants à la partie.
     *
     * @return La liste des joueurs.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Ajoute un nouveau joueur avec le nom spécifié, le type (robot/humain) et la stratégie.
     *
     * @param playerName Le nom du joueur à ajouter.
     * @param isRobot    Indique si le joueur est un robot.
     * @param strategy   La stratégie du joueur (Aggressive, Amical, Human).
     */
    public void addPlayer(String playerName, boolean isRobot, String strategy) {
        // Vérifie si le nombre maximum de joueurs est déjà atteint
        if (players.size() == NB_PLAYERS) {
            return;
        }

        // Attribue une couleur au joueur en fonction de son ordre d'ajout
        PlayerColor[] playerColors = PlayerColor.values();
        PlayerColor playerColor = playerColors[players.size()];
        Player newPlayer = new Player(playerName, playerColor);

        // Si le joueur n'est pas un robot, définit la stratégie comme Human
        if (!isRobot) {
            strategy = "Human";
        }

        // Associe la stratégie appropriée au joueur
        switch (strategy) {
            case "Aggressive" -> newPlayer.setStrategy(new RobotAgressiveStrategy(newPlayer, this));
            case "Amical"     -> newPlayer.setStrategy(new RobotAmicalStrategy(newPlayer, this));
            case "Human"      -> newPlayer.setStrategy(new HumanStrategy(newPlayer, this));
            default -> throw new IllegalArgumentException("Stratégie inconnue : " + strategy);
        }

        // Ajoute le joueur à la liste des joueurs
        players.add(newPlayer);
    }

    /**
     * Définit le joueur actuel directement et déclenche un événement de changement de joueur.
     *
     * @param player Le joueur à définir comme joueur actuel.
     */
    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
        firePropertyChange("CURRENT_PLAYER_CHANGE", null, currentPlayer);
    }

    /**
     * Incrémente le compteur de tours et déclenche un événement indiquant le changement de tour.
     */
    public void incrementTurn() {
        turn++;
        pcs.firePropertyChange("TURN_CHANGE", null, turn);
    }

    /**
     * Retourne le numéro du tour actuel.
     *
     * @return Le numéro du tour.
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Vérifie si la partie est en cours en s'assurant que le nombre de joueurs est suffisant.
     *
     * @return True si la partie est en cours, false sinon.
     */
    public boolean isGameOngoing() {
        return players.size() == NB_PLAYERS;
    }


}
