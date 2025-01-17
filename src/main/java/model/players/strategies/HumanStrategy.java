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
 * Classe concrète représentant la stratégie d'un joueur humain dans le jeu.
 * Implémente les méthodes définies dans {@link Strategy} pour gérer
 * les choix d'un joueur humain lors des différentes phases :
 * déploiement, planification, expansion, exploration, extermination et exploitation.
 * Les appels à ces méthodes se traduisent ici par l'émission d'événements
 * (firePropertyChange) qui sont ensuite captés par la Vue, permettant au joueur
 * humain de faire ses choix via l'interface.
 */
public class HumanStrategy extends Strategy implements Serializable {

    /**
     * Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation.
     *
     * @serial
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de la classe HumanStrategy.
     * Initialise la stratégie en associant le joueur et la partie courante.
     *
     * @param player Le joueur humain auquel est attribuée cette stratégie.
     * @param partie L'instance unique de la partie en cours.
     */
    public HumanStrategy(Player player, Partie partie) {
        super(player, partie);
    }

    /**
     * Gère le choix du système (hex) lors de la phase de déploiement.
     * Émet un événement listant les hex valides où déployer des ships.
     *
     * @param deployState          Le state actuel de déploiement.
     * @param validDeploymentHexes Liste des hexagones admissibles pour le déploiement.
     */
    @Override
    public void handleDeployChooseSystem(DeployState deployState, List<Hex> validDeploymentHexes) {
        // Émet un événement pour initier le déploiement avec les hexagones valides
        partie.firePropertyChange("DEPLOY_INIT", null, validDeploymentHexes);
    }

    /**
     * Gère le choix et l'ordre des commandes lors de la phase de planification (Plan).
     * Émet un événement signalant que la phase de planification doit commencer.
     *
     * @param planState Le state actuel de la phase Plan.
     */
    @Override
    public void handlePlanChooseCommands(PlanState planState) {
        // Émet un événement pour démarrer la planification des commandes
        partie.firePropertyChange("PLAN", null, null);
    }

    /**
     * Gère le choix de l'hexagone (niveau I ou II, etc.) où déployer
     * davantage de ships lors de la phase d'expansion (Expand).
     * Émet un événement listant les hex disponibles.
     *
     * @param expand         La commande Expand en cours.
     * @param hexesCanExpand Liste des hex admissibles à l'expansion.
     */
    @Override
    public void handleExpandChooseHex(Expand expand, List<Hex> hexesCanExpand) {
        // Émet un événement avec les hex disponibles pour l'expansion
        partie.firePropertyChange("EXPAND_POSSIBLE_HEXES", null, hexesCanExpand);
    }

    /**
     * Gère le choix de l'hexagone de départ lors de la phase d'exploration (Explore).
     * Émet un événement listant les hex de départ admissibles.
     *
     * @param explore    La commande Explore en cours.
     * @param startHexes Liste des hex possibles pour initier l'exploration.
     */
    @Override
    public void handleExploreChooseStartHex(Explore explore, List<Hex> startHexes) {
        // Émet un événement avec les hex de départ pour l'exploration
        partie.firePropertyChange("EXPLORE_START_HEXES", null, startHexes);
    }

    /**
     * Gère le choix du prochain hexagone lors de la phase d'exploration.
     * Émet un événement listant les hex suivants admissibles.
     *
     * @param explore  La commande Explore en cours.
     * @param nextHexes Liste des hex où le joueur peut poursuivre l'exploration.
     */
    @Override
    public void handleExploreChooseNextHex(Explore explore, List<Hex> nextHexes) {
        // Émet un événement avec les hex suivants pour l'exploration
        partie.firePropertyChange("EXPLORE_NEXT_HEX", null, nextHexes);
    }

    /**
     * Gère le choix de l'hexagone à envahir lors de la phase d'extermination (Exterminate).
     * Émet un événement listant les hex occupant un adversaire pouvant être envahis.
     *
     * @param exterminate    La commande Exterminate en cours.
     * @param invadableHexes Liste des hex pouvant être envahis.
     */
    @Override
    public void handleExterminateChooseHex(Exterminate exterminate, List<Hex> invadableHexes) {
        // Émet un événement avec les hex à exterminer
        partie.firePropertyChange("EXTERMINATE_START", null, invadableHexes);
    }

    /**
     * Gère le choix des ships à envoyer pour une invasion
     * lors de la phase d'extermination (Exterminate).
     * Émet un événement listant les hex déjà envahis où le joueur
     * peut sélectionner le nombre de ships à engager.
     *
     * @param exterminate   La commande Exterminate en cours.
     * @param invadingHexes Liste des hex envahis où il faut décider
     *                      du nombre de ships supplémentaires à envoyer.
     */
    @Override
    public void handleExterminateChooseShips(Exterminate exterminate, List<Hex> invadingHexes) {
        // Émet un événement avec les hexagones envahis pour choisir les ships à exterminer
        partie.firePropertyChange("EXTERMINATE_CHOOSE_SHIPS", null, invadingHexes);
    }

    /**
     * Gère le choix d'un secteur à exploiter lors de la phase Exploit.
     * Émet un événement listant les secteurs occupés par ce joueur
     * et pouvant être scorés.
     *
     * @param exploitState    State exploit en cours.
     * @param scorableSectors Liste des secteurs éligibles pour le scoring.
     */
    @Override
    public void handleExploitChooseSector(ExploitState exploitState, List<Sector> scorableSectors) {
        // Émet un événement avec les secteurs à scorer pour l'exploitation
        partie.firePropertyChange("CHOOSE_SECTOR_SCORE", null, scorableSectors);
    }
}
