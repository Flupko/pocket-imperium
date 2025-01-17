package model.board;

import model.ship.Ship;
import model.players.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Classe représentant un hexagone (qui peut être également un système, et le Tri-Prime) sur le plateau de jeu.
 * Chaque hexagone possède des coordonnées, peut être contrôlé par un joueur, et peut contenir plusieurs ships.
 * La classe gère également les relations de voisinage entre les hexagones et les changements d'état via des événements.
 */
public class Hex implements Serializable {

    /** Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Coordonnée X de l'hexagone. */
    private final int posX;

    /** Coordonnée Y de l'hexagone. */
    private final int posY;

    /** Indique si l'hexagone est le Tri-Prime (niveau 3). */
    private boolean isTriprime = false;

    /** Ensemble des hexagones voisins. */
    private final Set<Hex> neighbors = new HashSet<>();

    /** Joueur contrôlant actuellement cet hexagone, null si non contrôlé. */
    private Player controller = null;

    /** Liste des ships présents sur cet hexagone. */
    private final ArrayList<Ship> ships = new ArrayList<>();

    /** Niveau de l'hexagone (0 par défaut).
     * Si > 0, l'hexagone est alors un système, et si le niveau est 3, c'est le Tri-Prime
     * */
    private int level = 0;

    /** Support pour la gestion des écouteurs de changement de property. */
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructeur de la classe Hex.
     * Initialise les coordonnées de l'hexagone.
     *
     * @param posX Coordonnée X de l'hexagone.
     * @param posY Coordonnée Y de l'hexagone.
     */
    public Hex(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Retourne la coordonnée X de l'hexagone.
     *
     * @return Coordonnée X.
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Retourne la coordonnée Y de l'hexagone.
     *
     * @return Coordonnée Y.
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Retourne les coordonnées de l'hexagone sous forme de tableau.
     *
     * @return Tableau contenant les coordonnées [posX, posY].
     */
    public int[] getCoords() {
        return new int[]{posX, posY};
    }

    /**
     * Retourne l'identifiant unique de l'hexagone sous la forme "posX,posY".
     *
     * @return Identifiant de l'hexagone.
     */
    public String getId() {
        return posX + "," + posY;
    }

    /**
     * Ajoute un voisin à cet hexagone.
     *
     * @param neighbor Hexagone voisin à ajouter.
     */
    public void addNeighbor(Hex neighbor) {
        this.neighbors.add(neighbor);
    }

    /**
     * Retourne l'ensemble des hexagones voisins.
     *
     * @return Ensemble des voisins.
     */
    public Set<Hex> getNeighbors() {
        return this.neighbors;
    }

    /**
     * Indique si l'hexagone est actuellement contrôlé par un joueur.
     *
     * @return ships si l'hexagone est occupé, false sinon.
     */
    public boolean isOccupied() {
        return this.controller != null;
    }

    /**
     * Retourne le joueur contrôlant cet hexagone.
     *
     * @return Joueur contrôlant l'hexagone, null si non contrôlé.
     */
    public Player getController() {
        return this.controller;
    }

    /**
     * Définit le joueur contrôlant cet hexagone et émet un événement de mise à jour.
     *
     * @param controller Joueur à définir comme contrôleur.
     */
    public void setController(Player controller) {
        Player oldController = this.controller;
        this.controller = controller;
        pcs.firePropertyChange("HEX_UPDATE", oldController, controller);
    }

    /**
     * Définit un système sur l'hexagone
     * Si le niveau est 3, l'hexagone devient le Tri-Prime.
     *
     * @param level Nouveau niveau de l'hexagone.
     */
    public void setSystem(int level) {
        this.level = level;
        this.isTriprime = (level == 3);
        pcs.firePropertyChange("HEX_LEVEL_CHANGE", null, level);
    }

    /**
     * Indique si l'hexagone est un système (niveau > 0)
     *
     * @return True si l'hexagone est un système, false sinon.
     */
    public boolean isSystem() {
        return this.level > 0;
    }

    /**
     * Retourne le niveau de l'hexagone.
     *
     * @return Niveau de l'hexagone.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Retourne le nombre total de ships présents sur l'hexagone.
     *
     * @return Nombre de ships.
     */
    public int getShipsCount() {
        return this.ships.size();
    }

    /**
     * Retourne le nombre de ships non envahis sur l'hexagone.
     *
     * @return Nombre de ships non envahis.
     */
    public int getUninvadedShipCount() {
        return (int) this.ships.stream().filter(ship -> !ship.hasInvaded()).count();
    }

    /**
     * Retourne le nombre de ships non déplacés sur l'hexagone.
     *
     * @return Nombre de ships non déplacés.
     */
    public int getUnmovedShipsCount() {
        return (int) this.ships.stream().filter(ship -> !ship.hasMoved()).count();
    }

    /**
     * Supprime les ships non durables pour respecter la capacité de l'hexagone à la fin du tour.
     * Émet un événement de mise à jour après la suppression.
     */
    public void removeUnsustainableShips() {
        // Supprime les ships jusqu'à ce que la capacité soit respectée
        while (this.ships.size() > getEndRoundCapacityShips()) {
            Ship ship = this.ships.remove(0);
            ship.setDeployed(false);
        }
        pcs.firePropertyChange("HEX_UPDATE", null, null);
    }

    /**
     * Retourne la capacité maximale de ships à la fin du tour en fonction du niveau de l'hexagone.
     *
     * @return Capacité maximale de ships.
     */
    public int getEndRoundCapacityShips(){
        return this.level + 1;
    }

    /**
     * Ajoute un ship à l'hexagone.
     * Si l'hexagone n'est pas occupé, définit le contrôleur comme le propriétaire du ship.
     * Émet un événement de mise à jour.
     *
     * @param ship Ship à ajouter.
     */
    public void addShip(Ship ship) {
        this.ships.add(ship);
        if (!isOccupied()) {
            setController(ship.getOwner());
        }
        pcs.firePropertyChange("HEX_UPDATE", null, null);
    }

    /**
     * Ajoute une liste de ships à l'hexagone.
     * Si l'hexagone n'est pas occupé, définit le contrôleur comme le propriétaire du premier ship ajouté.
     * Émet un événement de mise à jour.
     *
     * @param shipsToAdd Liste de ships à ajouter.
     */
    public void addShips(List<Ship> shipsToAdd) {
        if (shipsToAdd == null || shipsToAdd.isEmpty()) {
            return;
        }
        this.ships.addAll(shipsToAdd);
        if (!isOccupied()) {
            setController(shipsToAdd.get(0).getOwner());
        }
        pcs.firePropertyChange("HEX_UPDATE", null, null);
    }

    /**
     * Retire un ship de l'hexagone.
     * Si aucun ship ne reste, le contrôleur est défini sur null.
     *
     * @param ship Ship à retirer.
     */
    public void removeShip(Ship ship) {
        this.ships.remove(ship);
        if (this.ships.isEmpty()) {
            setController(null);
        }
        pcs.firePropertyChange("HEX_UPDATE", null, null);
    }

    /**
     * Retire une liste de ships de l'hexagone.
     * Si aucun ship ne reste, le contrôleur est défini sur null.
     * Émet un événement de mise à jour.
     *
     * @param shipsToRemove Liste de ships à retirer.
     */
    public void removeShips(List<Ship> shipsToRemove) {
        if (shipsToRemove == null || shipsToRemove.isEmpty()) {
            return;
        }
        this.ships.removeAll(shipsToRemove);
        if (this.ships.isEmpty()) {
            setController(null);
        }
        pcs.firePropertyChange("HEX_UPDATE", null, null);
    }

    /**
     * Retourne une liste des ships présents sur l'hexagone.
     *
     * @return Liste des ships.
     */
    public List<Ship> getShips() {
        return new ArrayList<>(this.ships);
    }

    /**
     * Retourne une liste des ships non déplacés sur l'hexagone.
     *
     * @return Liste des ships non déplacés.
     */
    public List<Ship> getUnmovedShips() {
        return this.ships.stream()
                .filter(ship -> !ship.hasMoved())
                .collect(Collectors.toList());
    }

    /**
     * Retourne une liste des ships non envahis sur l'hexagone.
     *
     * @return Liste des ships non envahis.
     */
    public List<Ship> getUninvadedShips() {
        return this.ships.stream()
                .filter(ship -> !ship.hasInvaded())
                .collect(Collectors.toList());
    }

    /**
     * Indique si l'hexagone est le Tri-Prime (niveau 3).
     *
     * @return True si l'hexagone est le Tri-Prime, false sinon.
     */
    public boolean isTriPrime() {
        return this.isTriprime;
    }

    /**
     * Indique si l'hexagone est contrôlé par un joueur spécifique.
     *
     * @param player Joueur à vérifier.
     * @return True si l'hexagone est contrôlé par le joueur, false sinon.
     */
    public boolean isControlledBy(Player player) {
        return this.controller == player;
    }

    /**
     * Ajoute un écouteur pour un changement de property spécifique.
     *
     * @param property  Nom de la property à écouter.
     * @param listener  Écouteur à ajouter.
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    /**
     * Supprime un écouteur de changement de property.
     *
     * @param listener Écouteur à supprimer.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
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
        pcs = new PropertyChangeSupport(this); // Réinitialisation du support transitoire
    }

    /**
     * Calcule la direction du voisinage entre deux hexagones en fonction de leurs coordonnées.
     *
     * @param coord1 Coordonnées du premier hexagone [x1, y1].
     * @param coord2 Coordonnées du second hexagone [x2, y2].
     * @return Direction du voisinage (0 à 5), ou -1 si non voisins.
     */
    public static int getNeighborDirection(int[] coord1, int[] coord2) {

        int x1 = coord1[0];
        int y1 = coord1[1];
        int x2 = coord2[0];
        int y2 = coord2[1];

        // Calcul des différences
        int dx = x2 - x1;
        int dy = y2 - y1;

        // Détermine si la colonne du premier hexagone est paire
        boolean isEven = (x1 % 2 == 0);

        // Définition des deltas pour les colonnes paires et impaires
        // Chaque sous-tableau représente [Δx, Δy] pour les directions 0 à 5
        final int[][] evenDeltas = {
                {1, -1},
                {0, -1},
                {-1, -1},
                {-1, 0},
                {0, 1},
                {1, 0},
        };

        final int[][] oddDeltas = {
                {1, 0},
                {0, -1},
                {-1, 0},
                {-1, 1},
                {0, 1},
                {1, 1},
        };

        // Sélectionne les deltas appropriés en fonction de la parité de la colonne
        int[][] deltas = isEven ? evenDeltas : oddDeltas;

        // Parcourt les deltas pour trouver la direction correspondante
        for (int direction = 0; direction < deltas.length; direction++) {
            if (dx == deltas[direction][0] && dy == deltas[direction][1]) {
                return direction;
            }
        }

        // Les hexagones étant supposés être des voisins directs, ce point ne devrait pas être atteint
        // Retourne -1 comme code d'erreur si non voisins
        return -1;
    }
}
