package model.commands;

import model.ship.Ship;
import model.commands.commands_data.ExpandData;
import model.players.Player;
import model.board.Hex;
import model.board.Board;
import model.states.PerformState;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Commande concrète représentant l'action "Expand" dans le jeu.
 * Cette commande permet à un joueur de placer un nombre déterminé de ses ships (non déployés)
 * sur des hexagones-systèmes déjà contrôlés. L'efficacité de la commande (4 - efficiency) détermine le
 * nombre de ships maximum que le joueur peut ajouter.
 */
public class Expand extends Command implements Serializable {

    /** Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Couleur associée à la commande Expand, utilisée par l'interface.
     */
    public static final String COLOR = "#d3a602";

    /**
     * Objet contenant les données spécifiques à l'action Expand,
     * notamment le joueur, le nombre total de ships pouvant être ajoutés,
     * et le nombre déjà ajoutés.
     */
    private final ExpandData expandData;

    /**
     * Constructeur de la classe Expand.
     * @param board        Le plateau de jeu.
     * @param peformState  Le state Perform pour l'enchaînement des commandes.
     * @param player       Le joueur qui exécute la commande Expand.
     * @param efficiency   L'efficacité (1 à 3) détermine le nombre de ships ajoutés (4 - efficiency).
     */
    public Expand(Board board, PerformState peformState, Player player, int efficiency) {
        super(board, peformState);

        // Nombre maximum de ships que le joueur peut encore déployer
        int nbUnusedShipsPlayer = player.getUnusedShips().size();
        int shipsCanAdd = Math.min(4 - efficiency, nbUnusedShipsPlayer);

        expandData = new ExpandData(player, shipsCanAdd);

        // Si le joueur ne possède plus de ships non déployés, la commande termine immédiatement
        if (nbUnusedShipsPlayer == 0) {
            finishExpand();
        }
    }

    /**
     * Méthode principale d'exécution de la commande.
     * Vérifie si la limite d'ajout de ships est atteinte ou si aucun hexagone
     * ne peut plus recevoir de ships. Sinon, sollicite la stratégie du joueur
     * pour choisir où placer les ships.
     */
    @Override
    public void execute() {

        // Si tous les ships que le joueur peut ajouter sont déjà placés ou si aucun hexagone n'est éligible
        if (expandData.getShipsAdded() == expandData.getTotShipsCanAdd() || getHexesCanExpand().isEmpty()) {
            finishCommand();
            return;
        }

        // Appelle la stratégie du joueur pour sélectionner un hexagone-système où déployer
        Player currentPlayer = expandData.getPlayer();
        currentPlayer.getStrategy().handleExpandChooseHex(this, getHexesCanExpand());
    }

    /**
     * Termine la commande Expand en appelant finishExpand().
     */
    @Override
    public void finishCommand() {
        finishExpand();
    }

    /**
     * Ajoute un certain nombre de ships sur un hexagone-système contrôlé par le joueur.
     * @param systemHex L'hexagone-système ciblé pour l'ajout de ships.
     * @param nbShips   Le nombre de ships à déployer sur cet hexagone.
     */
    public void addShips(Hex systemHex, int nbShips) {
        if (!canExpandFleet(systemHex, nbShips)) {
            return;
        }

        List<Ship> unusedShipsPlayer = expandData.getPlayer().getUnusedShips();

        // Déploiement des ships
        for (int i = 0; i < nbShips; i++) {
            Ship shipAdded = unusedShipsPlayer.get(i);
            shipAdded.setDeployed(true);
            systemHex.addShip(shipAdded);
        }

        // Met à jour le nombre de ships ajoutés
        expandData.addShipsAdded(nbShips);

        // Relance la logique d'exécution
        execute();
    }

    /**
     * Méthode interne pour terminer la logique Expand.
     * Appelle la commande suivante via PerformState.
     */
    public void finishExpand() {
        performState.nextCommand();
    }

    /**
     * Vérifie qu'le joueur peut déployer nbShips ships sur un hexagone donné.
     * @param targetHex Hexagone visé (système).
     * @param nbShips   Nombre de ships à déployer.
     * @return True si l'action est possible, false sinon.
     */
    public boolean canExpandFleet(Hex targetHex, int nbShips) {
        return targetHex != null
                && targetHex.isSystem()
                && targetHex.isOccupied()
                && targetHex.getController() == expandData.getPlayer()
                && nbShips >= 1
                && nbShips <= getShipsCanAdd();
    }

    /**
     * Donne la liste des hexagones-systèmes que le joueur contrôle et
     * sur lesquels il peut déployer des ships.
     * @return Liste d'hexagones-systèmes contrôlés.
     */
    public List<Hex> getHexesCanExpand() {
        return board.getSystemsControlledBy(expandData.getPlayer());
    }

    /**
     * Retourne le nombre total de ships pouvant être ajoutés pendant la commande.
     * @return Nombre total de ships déployables.
     */
    public int getTotShipsCanAdd() {
        return expandData.getTotShipsCanAdd();
    }

    /**
     * Retourne le nombre de ships déjà ajoutés via cette commande.
     * @return Nombre de ships déjà placés.
     */
    public int getShipsAdded() {
        return expandData.getShipsAdded();
    }

    /**
     * Indique combien de ships peuvent encore être ajoutés lors de cette commande.
     * @return Différence entre total possible et nombre déjà ajoutés.
     */
    public int getShipsCanAdd() {
        return expandData.getTotShipsCanAdd() - expandData.getShipsAdded();
    }
}