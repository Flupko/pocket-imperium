package model.commands.commands_data;

import model.board.Hex;
import model.players.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Données spécifiques à la commande Exterminate.
 * Cette classe gère les informations liées aux invasions, notamment :
 * - l'hex cible (invadedHex)
 * - les hex depuis lesquels le joueur lance son attaque (invadingHexes)
 * - le joueur initiant l'action
 * - le nombre total d'invasions autorisées et réalisées
 * - le nombre de ships engagés dans l'invasion en cours
 */
public class ExterminateData implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Hex visé par l'invasion en cours (peut être null si aucune invasion n'est en cours).
     */
    private Hex invadedHex = null;

    /**
     * Liste des hex depuis lesquels le joueur attaque (envahit).
     * Chaque hex doit être contrôlé par le joueur et contenir des ships non envahis.
     */
    private List<Hex> invadingHexes;

    /**
     * Nombre total d'invasions que le joueur peut réaliser (4 - efficiency).
     */
    private final int nbInvasionsPossible;

    /**
     * Joueur effectuant la commande Exterminate.
     */
    private final Player player;

    /**
     * Nombre d'invasions déjà accomplies.
     */
    private int nbInvasionsMade;

    /**
     * Nombre de ships déjà engagés dans l'invasion en cours.
     */
    private int nbShipsUsedCurrentInvasion;

    /**
     * Nombre maximum de ships pouvant être engagés pour l'invasion actuelle,
     * basé sur la somme des ships disponibles dans les hex attaquants.
     */
    private int maxShipsCanBeUsedCurrentInvasion;

    /**
     * Constructeur de la classe ExterminateData.
     * @param player             Joueur qui lance la commande Exterminate.
     * @param nbInvasionsPossible Nombre total d'invasions autorisées.
     */
    public ExterminateData(Player player, int nbInvasionsPossible) {
        this.nbInvasionsPossible = nbInvasionsPossible;
        this.player = player;
        this.nbInvasionsMade = 0;
        this.invadingHexes = new ArrayList<>();
    }

    /**
     * Retourne l'hex actuellement envahi.
     * @return L'hex visé par l'invasion, ou null si aucune invasion n'est définie.
     */
    public Hex getInvadedHex() {
        return invadedHex;
    }

    /**
     * Définit l'hex cible de l'invasion en cours.
     * @param invadedHex L'hex à envahir.
     */
    public void setInvadedHex(Hex invadedHex) {
        this.invadedHex = invadedHex;
    }

    /**
     * Retourne la liste des hex depuis lesquels le joueur attaque.
     * @return Liste d'hex envahisseurs.
     */
    public List<Hex> getInvadingHexes() {
        return invadingHexes;
    }

    /**
     * Définit le nombre de ships déjà engagés dans l'invasion actuelle.
     * @param nbShipsUsedCurrentInvasion Nombre de ships déjà utilisés.
     */
    public void setNbShipsUsedCurrentInvasion(int nbShipsUsedCurrentInvasion) {
        this.nbShipsUsedCurrentInvasion = nbShipsUsedCurrentInvasion;
    }

    /**
     * Retourne le nombre de ships déjà engagés dans l'invasion actuelle.
     * @return Nombre de ships déjà utilisés.
     */
    public int getNbShipsUsedCurrentInvasion() {
        return nbShipsUsedCurrentInvasion;
    }

    /**
     * Définit la liste des hex attaquants pour l'invasion.
     * @param invadingHexes Liste d'hex envahisseurs.
     */
    public void setInvadingHexes(List<Hex> invadingHexes) {
        this.invadingHexes = invadingHexes;
    }

    /**
     * Retourne le nombre d'invasions déjà réalisées par le joueur.
     * @return Nombre d'invasions accomplies.
     */
    public int getNbInvasionsMade() {
        return nbInvasionsMade;
    }

    /**
     * Incrémente le compteur d'invasions réalisées.
     */
    public void incrementNbInvasionsMade() {
        nbInvasionsMade += 1;
    }

    /**
     * Retourne le nombre d'invasions autorisées pour ce joueur.
     * @return Limite d'invasions (4 - efficiency).
     */
    public int getNbInvasionsPossible() {
        return nbInvasionsPossible;
    }

    /**
     * Retourne le joueur exécutant la commande Exterminate.
     * @return Joueur concerné.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retire un hex envahisseur de la liste, généralement si ses ships sont épuisés.
     * @param hex L'hex à retirer de la liste des envahisseurs.
     */
    public void removeInvadingHex(Hex hex) {
        invadingHexes.remove(hex);
    }

    /**
     * Réinitialise l'invasion en cours, en vidant la liste des hex attaquants
     * et en annulant l'hex envahi.
     */
    public void clearInvasion() {
        invadingHexes.clear();
        invadedHex = null;
    }

    /**
     * Vérifie si un hex donné se trouve dans la liste des hex envahisseurs.
     * @param hex L'hex à vérifier.
     * @return true si l'hex est un envahisseur, false sinon.
     */
    public boolean isHexInvading(Hex hex) {
        return invadingHexes.contains(hex);
    }

    /**
     * Retourne la capacité maximale de ships pouvant être utilisés
     * lors de l'invasion actuelle (somme des ships disponibles dans les hex attaquants).
     * @return Nombre maximum de ships mobilisables pour l'invasion.
     */
    public int getMaxShipsCanBeUsedCurrentInvasion() {
        return maxShipsCanBeUsedCurrentInvasion;
    }

    /**
     * Définit la capacité maximale de ships mobilisables pour l'invasion actuelle.
     * @param maxShipsCanBeUsedCurrentInvasion Nouvelle valeur pour le maximum mobilisable.
     */
    public void setMaxShipsCanBeUsedCurrentInvasion(int maxShipsCanBeUsedCurrentInvasion) {
        this.maxShipsCanBeUsedCurrentInvasion = maxShipsCanBeUsedCurrentInvasion;
    }
}
