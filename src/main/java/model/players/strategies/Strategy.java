package model.players.strategies;

import model.Partie;
import model.board.Hex;
import model.board.Sector;
import model.commands.Expand;
import model.commands.Explore;
import model.commands.Exterminate;
import model.players.Player;
import model.states.DeployState;
import model.states.ExploitState;
import model.states.PlanState;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Classe abstraite définissant les méthodes de stratégie pour un joueur.
 * Les stratégies (humaine ou robots) déterminent comment un joueur
 * choisit ses actions lors des différentes phases du jeu : déploiement,
 * planification, expansion, exploration, extermination et exploitation.
 * Chaque méthode correspond à un moment précis de l'enchaînement des phases
 * du jeu. Les stratégies concrètes doivent implémenter ces méthodes en
 *émettant des événements ou en prenant des décisions automatiques.
 */
public abstract class Strategy implements Serializable {

    /**
     * Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation.
     * @serial
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Joueur propriétaire de cette stratégie.
     */
    protected Player player;

    /**
     * Instance unique de la partie actuelle, permettant d’accéder à l’état global du jeu.
     */
    protected Partie partie;

    /**
     * Constructeur principal de la classe Strategy.
     *
     * @param player Le joueur auquel cette stratégie est associée.
     * @param partie L'instance de la partie en cours.
     */
    public Strategy(Player player, Partie partie) {
        this.player = player;
        this.partie = partie;
    }

    /**
     * Gère la sélection d'un hexagone système valide pendant la phase de déploiement.
     *
     * @param deployState          Le state de déploiement dans lequel on se trouve.
     * @param validDeploymentHexes Liste des hexagones admissibles pour déployer des ships.
     */
    public abstract void handleDeployChooseSystem(DeployState deployState, List<Hex> validDeploymentHexes);

    /**
     * Gère la sélection et l'ordre des commandes (Expand, Explore, Exterminate) pendant la phase de planification.
     *
     * @param planState Le state de planification dans lequel on se trouve.
     */
    public abstract void handlePlanChooseCommands(PlanState planState);

    /**
     * Gère le choix d'un hexagone pour l'expansion (phase Expand).
     *
     * @param expand         La commande Expand courante.
     * @param hexesCanExpand Liste des hexagones admissibles pour l'expansion.
     */
    public abstract void handleExpandChooseHex(Expand expand, List<Hex> hexesCanExpand);

    /**
     * Gère le choix de l'hexagone de départ pour la phase d'exploration (Explore).
     *
     * @param explore    La commande Explore courante.
     * @param startHexes Liste des hexagones de départ admissibles pour l'exploration.
     */
    public abstract void handleExploreChooseStartHex(Explore explore, List<Hex> startHexes);

    /**
     * Gère le choix du prochain hexagone lors de la phase d'exploration.
     *
     * @param explore  La commande Explore courante.
     * @param nextHexes Liste des hexagones dans lesquels le joueur peut poursuivre l'exploration.
     */
    public abstract void handleExploreChooseNextHex(Explore explore, List<Hex> nextHexes);

    /**
     * Gère le choix de l'hexagone cible lors de la phase d'extermination (Exterminate).
     *
     * @param exterminate   La commande Exterminate courante.
     * @param invadableHexes Liste des hexagones pouvant être envahis.
     */
    public abstract void handleExterminateChooseHex(Exterminate exterminate, List<Hex> invadableHexes);

    /**
     * Gère le choix et la quantité de ships à impliquer lors d'une invasion (phase Exterminate).
     *
     * @param exterminate   La commande Exterminate courante.
     * @param invadingHexes Liste des hexagones d'où part l'invasion, nécessitant de fixer la quantité de ships.
     */
    public abstract void handleExterminateChooseShips(Exterminate exterminate, List<Hex> invadingHexes);

    /**
     * Gère le choix du secteur à exploiter (phase Exploit).
     *
     * @param exploitState   Le state d'exploitation actuel.
     * @param scorableSectors Liste des secteurs pouvant être scorés.
     */
    public abstract void handleExploitChooseSector(ExploitState exploitState, List<Sector> scorableSectors);
}
