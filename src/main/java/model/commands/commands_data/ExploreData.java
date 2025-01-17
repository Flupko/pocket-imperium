package model.commands.commands_data;

import model.ship.Ship;
import model.board.Hex;
import model.players.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente les données spécifiques à la commande "Explore" dans le jeu.
 * Cette classe enregistre :
 * - Le joueur qui effectue l'action
 * - Le nombre maximum de mouvements de flotte autorisés (dépendant de l'efficacité)
 * - Le nombre de mouvements déjà effectués
 * - Le chemin actuel d'exploration (liste de hex)
 * - La flotte actuelle (liste de ships) en déplacement
 */
public class ExploreData implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Nombre maximum de mouvements de flotte autorisés (4 - efficiency).
     * Chaque mouvement peut couvrir plusieurs hex successifs, dans la limite
     * imposée par les règles (distance, occupation, etc.).
     */
    private final int nbFleetMovementsAllowed;

    /**
     * Nombre de mouvements de flotte déjà effectués par ce joueur
     * lors de la commande Explore actuelle.
     */
    private int nbFleetMovementsMade;

    /**
     * Le joueur qui exécute la commande Explore. Doit disposer
     * de ships non déplacés et contrôler certains hex pour initier l'exploration.
     */
    private final Player player;

    /**
     * Liste de hex constituant le chemin actuel d'exploration.
     * Le premier élément est l'hex de départ, le dernier est
     * l'hex sur lequel la flotte se trouve actuellement.
     */
    private final List<Hex> curMovementPath = new ArrayList<>();

    /**
     * Flotte actuelle en déplacement, représentée sous forme d'une liste de ships.
     * Les ships sont ajoutés ou retirés selon les déplacements et ajustements de flotte.
     */
    private final List<Ship> curFleet = new ArrayList<>();

    /**
     * Constructeur de ExploreData.
     * @param player Le joueur effectuant la commande.
     * @param nbFleetMovementsAllowed Nombre maximum de mouvements de flotte autorisés.
     */
    public ExploreData(Player player, int nbFleetMovementsAllowed) {
        this.player = player;
        this.nbFleetMovementsAllowed = nbFleetMovementsAllowed;
        this.nbFleetMovementsMade = 0;
    }

    /**
     * Incrémente le compteur de mouvements de flotte déjà réalisés.
     * Chaque déplacement (jusqu'à deux hex successifs) correspond
     * à un mouvement de flotte consommé.
     */
    public void incrementNbFleetMovementsMade() {
        nbFleetMovementsMade++;
    }

    /**
     * Retourne le nombre de mouvements de flotte déjà effectués.
     * @return Nombre de mouvements consommés jusqu'à présent.
     */
    public int getNbFleetMovementsMade() {
        return nbFleetMovementsMade;
    }

    /**
     * Retourne le nombre total de mouvements de flotte autorisés
     * (dépend de l'efficacité : 4 - efficiency).
     * @return Nombre maximum de mouvements de flotte.
     */
    public int getNbFleetMovementsAllowed() {
        return nbFleetMovementsAllowed;
    }

    /**
     * Retourne le joueur qui effectue l'action Explore.
     * @return Joueur concerné.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retourne la liste de hex formant le chemin actuel d'exploration.
     * @return Liste d'hex représentant la route parcourue.
     */
    public List<Hex> getCurMovementPath() {
        return curMovementPath;
    }

    /**
     * Retourne l'hex actuel dans le chemin (dernier élément),
     * ou null si le chemin est vide.
     * @return L'hex actuel ou null si chemin vide.
     */
    public Hex getCurHex() {
        if (curMovementPath.isEmpty()) {
            return null;
        }
        return curMovementPath.get(curMovementPath.size() - 1);
    }

    /**
     * Ajoute un hex au chemin d'exploration, représentant
     * le prochain hex atteint lors du déplacement.
     * @param hex L'hex à ajouter au chemin.
     */
    public void addHexCurMovementPath(Hex hex) {
        curMovementPath.add(hex);
    }

    /**
     * Crée un nouveau chemin d'exploration en partant d'un hex de départ,
     * effaçant tout chemin précédent.
     * @param hex Hex de départ pour l'exploration.
     */
    public void newMovementPath(Hex hex) {
        curMovementPath.clear();
        curMovementPath.add(hex);
    }

    /**
     * Réinitialise complètement le chemin d'exploration,
     * le laissant vide.
     */
    public void clearMovementPath() {
        curMovementPath.clear();
    }

    /**
     * Réinitialise la flotte actuelle, en vidant la liste de ships.
     */
    public void clearFleet() {
        curFleet.clear();
    }

    /**
     * Retourne la flotte actuelle, sous forme de liste de ships.
     * @return Liste de ships composant la flotte.
     */
    public List<Ship> getCurFleet() {
        return curFleet;
    }
}
