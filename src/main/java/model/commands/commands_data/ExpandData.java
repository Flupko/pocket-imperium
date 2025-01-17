package model.commands.commands_data;

import model.players.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * Données spécifiques à la commande "Expand".
 * Cette classe conserve :
 * - le joueur effectuant la commande
 * - le nombre total de ships pouvant être déployés
 * - le nombre de ships déjà déployés
 */
public class ExpandData implements Serializable {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Nombre total de ships que le joueur peut déployer (4 - efficiency),
     * limité également par le nombre de ships non déployés à sa disposition.
     */
    private final int totShipsCanAdd;

    /**
     * Nombre de ships déjà déployés via cette commande.
     */
    private int shipsAdded;

    /**
     * Le joueur qui exécute la commande Expand.
     */
    private final Player player;

    /**
     * Constructeur de la classe ExpandData.
     * @param player         Le joueur effectuant la commande Expand.
     * @param shipsCanAdd    Nombre de ships que le joueur peut encore déployer
     *                       (déterminé par l'efficacité et ses ships restants).
     */
    public ExpandData(Player player, int shipsCanAdd) {
        this.player = player;
        this.totShipsCanAdd = shipsCanAdd;
    }

    /**
     * Retourne le nombre total de ships pouvant être déployés dans cette commande.
     * @return Limite de ships à déployer.
     */
    public int getTotShipsCanAdd() {
        return totShipsCanAdd;
    }

    /**
     * Retourne le nombre de ships déjà déployés.
     * @return Nombre de ships placés jusqu'à présent.
     */
    public int getShipsAdded() {
        return shipsAdded;
    }

    /**
     * Ajoute un certain nombre de ships déployés (typiquement lors d'un appel à addShips).
     * @param nbNewShipsAdded Nombre supplémentaire de ships placés.
     */
    public void addShipsAdded(int nbNewShipsAdded) {
        shipsAdded += nbNewShipsAdded;
    }

    /**
     * Retourne le joueur effectuant la commande.
     * @return Joueur concerné.
     */
    public Player getPlayer() {
        return player;
    }
}
