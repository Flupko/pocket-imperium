package model.ship;

import model.board.Hex;
import model.players.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * Représente un ship appartenant à un joueur.
 * Chaque ship peut être "déployé" ou non, et possède divers drapeaux d'état
 * indiquant s'il a déjà bougé ou participé à une invasion lors du tour actuel.
 */
public class Ship implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Indique si le ship est actuellement déployé sur le plateau.
     */
    private boolean deployed;

    /**
     * Joueur propriétaire de ce ship.
     */
    private final Player owner;

    /**
     * Indique si le ship a déjà été déplacé (hasMoved) pendant ce tour.
     */
    private boolean hasMoved;

    /**
     * Indique si le ship a déjà participé à une invasion (hasInvaded) pendant ce tour.
     */
    private boolean hasInvaded;

    /**
     * Constructeur de la classe Ship.
     * @param player Joueur à qui appartient ce ship.
     */
    public Ship(Player player) {
        this.owner = player;
        this.hasMoved = false;
        this.deployed = false;
        this.hasInvaded = false;
    }

    /**
     * Retourne le joueur propriétaire de ce ship.
     * @return Joueur détenteur du ship.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Indique si le ship est déployé sur le plateau.
     * @return true si le ship est déployé, false sinon.
     */
    public boolean isDeployed() {
        return this.deployed;
    }

    /**
     * Définit l'état de déploiement du ship.
     * @param deployed true pour marquer le ship comme déployé, false sinon.
     */
    public void setDeployed(boolean deployed) {
        this.deployed = deployed;
    }

    /**
     * Indique si le ship a déjà été déplacé (hasMoved) pendant ce tour.
     * @return true si le ship a bougé, false sinon.
     */
    public boolean hasMoved() {
        return this.hasMoved;
    }

    /**
     * Indique si le ship a déjà participé à une invasion (hasInvaded) pendant ce tour.
     * @return true si le ship a envahi, false sinon.
     */
    public boolean hasInvaded() {
        return this.hasInvaded;
    }

    /**
     * Définit le drapeau hasMoved.
     * @param hasMoved true si le ship a été déplacé ce tour, false sinon.
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Définit le drapeau hasInvaded.
     * @param hasInvaded true si le ship a participé à une invasion, false sinon.
     */
    public void setHasInvaded(boolean hasInvaded) {
        this.hasInvaded = hasInvaded;
    }

    /**
     * Réinitialise les drapeaux de déplacement et d'invasion pour un nouveau tour.
     */
    public void resetForNewTurn() {
        this.hasMoved = false;
        this.hasInvaded = false;
    }
}
