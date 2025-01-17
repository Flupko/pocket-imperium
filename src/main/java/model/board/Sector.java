package model.board;

import model.players.Player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un secteur (Sector) sur le plateau de jeu.
 * Chaque secteur contient plusieurs systèmes de niveau 1, 2 ou 3 (dans ce cas devient secteur central).
 * Un secteur peut être scoré pour attribuer des points aux joueurs.
 * La classe gère également les événements de changement de property pour notifier les autres classes
 */
public class Sector implements Serializable {

    /** Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Identifiant unique du secteur. */
    private final int sectorId;

    /** Liste des hexagones systèmes présents dans le secteur. */
    private final List<Hex> systemHexes = new ArrayList<>();

    /** Indique si le secteur a déjà été scoré. */
    private boolean isScored = false;

    /** Indique si le secteur est le secteur central (Tri-Prime). */
    private boolean isCentralSector = false;

    /** Support pour la gestion des écouteurs de changement de property. */
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructeur de la classe Sector.
     * Initialise le secteur avec un identifiant unique.
     *
     * @param sectorId Identifiant unique du secteur.
     */
    public Sector(int sectorId) {
        this.sectorId = sectorId;
    }

    /**
     * Ajoute un hexagone système à ce secteur.
     *
     * @param hex Hexagone système à ajouter.
     */
    public void addSystemHex(Hex hex) {
        systemHexes.add(hex);
    }

    /**
     * Retourne la liste des hexagones systèmes présents dans ce secteur.
     *
     * @return Liste des hexagones systèmes.
     */
    public List<Hex> getSystemHexes() {
        return systemHexes;
    }

    /**
     * Retourne l'identifiant unique du secteur.
     *
     * @return Identifiant du secteur.
     */
    public int getSectorId() {
        return sectorId;
    }

    /**
     * Définit si ce secteur est le secteur central (Tri-Prime).
     *
     * @param isCentralSector True si le secteur est central, false sinon.
     */
    public void setIsCentralSector(boolean isCentralSector) {
        this.isCentralSector = isCentralSector;
    }

    /**
     * Indique si ce secteur est le secteur central (Tri-Prime).
     *
     * @return True si le secteur est central, false sinon.
     */
    public boolean isCentralSector() {
        return isCentralSector;
    }

    /**
     * Émet un événement indiquant qu'un déploiement a eu lieu dans ce secteur.
     */
    public void deployEvent(){
        pcs.firePropertyChange("DEPLOYED", null, null);
    }

    /**
     * Score ce secteur en attribuant des points aux joueurs contrôlant les systèmes.
     *
     * @param endOfGame True si le scoré se fait en fin de jeu, ce qui double les points attribués.
     */
    public void scoreSector(boolean endOfGame) {
        int multiplier = endOfGame ? 2 : 1;
        for (Hex system : systemHexes) {
            if (system.isOccupied()) {
                Player controller = system.getController();
                controller.addToScore(system.getLevel() * multiplier);
            }
        }

        if(!endOfGame) setScored();
    }

    /**
     * Indique si ce secteur a déjà été scoré
     *
     * @return True si le secteur a été scoré, false sinon.
     */
    public boolean isScored(){
        return isScored;
    }

    /**
     * Marque ce secteur comme scoré et émet un événement de mise à jour.
     */
    public void setScored() {
        this.isScored = true;
        pcs.firePropertyChange("SCORED", null, null);
    }

    /**
     * Réinitialise l'état scoré de ce secteur et émet un événement de mise à jour.
     */
    public void resetScored(){
        pcs.firePropertyChange("UNSCORED", null, null);
        isScored = false;
    }

    /**
     * Indique si au moins un système dans ce secteur est contrôlé par un joueur.
     *
     * @return True si le secteur est occupé, false sinon.
     */
    public boolean isOccupied() {
        return systemHexes.stream().anyMatch(Hex::isOccupied);
    }

    /**
     * Calcule le score potentiel pour un joueur lors de l'exploitation de ce secteur.
     *
     * @param player Joueur dont on calcule le score.
     * @return Score potentiel pour le joueur.
     */
    public int getScorePlayerExploit(Player player) {
        int scorePlayerExploit = 0;
        for (Hex system : systemHexes) {
            if (system.isOccupied() && system.getController().equals(player)) {
                scorePlayerExploit += system.getLevel();
            }
        }
        return scorePlayerExploit;
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
}
