package model.players.strategies;

import javafx.application.Platform;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Stratégie concrète représentant un comportement agressif pour un robot joueur.
 * Cette classe implémente les méthodes définies dans la classe abstraite {@link Strategy}
 * pour gérer les actions d'un joueur robot lors des différentes phases du jeu
 * (déploiement, planification, expansion, exploration, extermination et exploitation).
 * Le robot agressif cherche systématiquement à optimiser son choix
 * en sélectionnant les hexagones et secteurs les plus avantageux.
 */
public class RobotAgressiveStrategy extends Strategy implements Serializable {

    /**
     * Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation.
     *
     * @serial
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Délai minimum de réflexion (en millisecondes).
     */
    private static final int MIN_THINKING_DELAY = 1800;

    /**
     * Délai maximum de réflexion (en millisecondes).
     */
    private static final int MAX_THINKING_DELAY = 2000;

    /**
     * Générateur de nombres aléatoires pour simuler la réflexion du robot.
     */
    private final Random random = new Random();

    /**
     * Constructeur de la classe RobotAgressiveStrategy.
     * Initialise la stratégie en associant le joueur et la partie.
     *
     * @param player Le joueur-robot auquel on assigne cette stratégie.
     * @param partie L'instance unique de la partie en cours.
     */
    public RobotAgressiveStrategy(Player player, Partie partie) {
        super(player, partie);
    }

    /**
     * Simule un temps de réflexion, puis exécute l'action fournie sur le thread JavaFX.
     * Émet également des événements pour signaler le début et la fin de la "réflexion".
     *
     * @param action       Action (logique) à exécuter après la réflexion.
     * @param thinkingText Message décrivant l'action pensée par le robot (pour l'affichage).
     */
    private void simulateThinking(Runnable action, String thinkingText) {
        // Émission de l'événement indiquant que le robot commence à réfléchir
        partie.firePropertyChange("ROBOT_THINKING", null, thinkingText);

        // Création et démarrage d'un thread séparé pour simuler la réflexion
        new Thread(() -> {
            try {
                // Délai aléatoire entre MIN_THINKING_DELAY et MAX_THINKING_DELAY
                int randomDelay = MIN_THINKING_DELAY + random.nextInt(MAX_THINKING_DELAY - MIN_THINKING_DELAY + 1);
                Thread.sleep(randomDelay); // Simule la réflexion
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Après ce délai, on repasse sur le thread JavaFX
            Platform.runLater(() -> {
                // Émission de l'événement indiquant que le robot a fini de "réfléchir"
                partie.firePropertyChange("ROBOT_DONE_THINKING", null, player.getName());
                action.run(); // Exécute la logique (action)
            });
        }).start();
    }

    /**
     * Gère le choix du système lors de la phase de déploiement (Deploy).
     * Simule la réflexion avant de déployer les ships sur le premier hexagone valide.
     *
     * @param deployState          Le state actuel de déploiement.
     * @param validDeploymentHexes Liste des hexagones valides pour le déploiement.
     */
    @Override
    public void handleDeployChooseSystem(DeployState deployState, List<Hex> validDeploymentHexes) {
        // Simule la réflexion avant de déployer les ships
        simulateThinking(
                () -> deployState.deployShips(validDeploymentHexes.get(0)),
                player.getName() + " is choosing a Hex to deploy his ships."
        );
    }

    /**
     * Gère la planification des commandes lors de la phase Plan.
     * Le robot agressif privilégie l’ordre Exterminate(3), Explore(2), Expand(1).
     *
     * @param planState Le state de planification actuel.
     */
    @Override
    public void handlePlanChooseCommands(PlanState planState) {
        // Simule la réflexion avant de planifier les commandes
        simulateThinking(
                () -> planState.planCommands(Arrays.asList(3, 2, 1)),
                player.getName() + " is planning his Commands."
        );
    }

    /**
     * Gère le choix d'un hex pour l'expansion (phase Expand).
     * Sélectionne l'hex ayant le plus grand total (ships + level),
     * puis y déploie l'intégralité des ships restants à ajouter.
     *
     * @param expand         Commande Expand en cours.
     * @param hexesCanExpand Liste d'hex admissibles pour l'expansion.
     */
    @Override
    public void handleExpandChooseHex(Expand expand, List<Hex> hexesCanExpand) {
        simulateThinking(() -> {
            // Sélectionne l'hex le plus "rentable"
            Hex bestHex = hexesCanExpand.stream()
                    .max(Comparator.comparingInt(h -> h.getShipsCount() + h.getLevel()))
                    .orElse(hexesCanExpand.get(0));

            int shipsToAdd = expand.getTotShipsCanAdd() - expand.getShipsAdded();
            expand.addShips(bestHex, shipsToAdd);
        }, player.getName() + " is choosing a hex to Expand.");
    }

    /**
     * Gère le choix de l'hexagone de départ lors de la phase d'exploration (Explore).
     * Sélectionne celui qui présente le meilleur ratio ships / voisins.
     *
     * @param explore    Commande Explore en cours.
     * @param startHexes Liste des hex éligibles pour commencer l'exploration.
     */
    @Override
    public void handleExploreChooseStartHex(Explore explore, List<Hex> startHexes) {
        simulateThinking(() -> {
            Hex bestHex = startHexes.stream()
                    .max(Comparator.comparingDouble(h ->
                            (double) h.getShipsCount() / Math.max(1, h.getNeighbors().size())))
                    .orElse(startHexes.get(0));
            explore.startExplore(bestHex);
        }, player.getName() + " is choosing a Hex to start Explore.");
    }

    /**
     * Gère le choix du prochain hexagone pour l'exploration.
     * Le robot agressif prend l'hex contenant le plus de ships.
     *
     * @param explore   Commande Explore en cours.
     * @param nextHexes Liste des hex où le joueur peut poursuivre l'exploration.
     */
    @Override
    public void handleExploreChooseNextHex(Explore explore, List<Hex> nextHexes) {
        simulateThinking(() -> {
            Hex bestNextHex = nextHexes.stream()
                    .max(Comparator.comparingInt(Hex::getShipsCount))
                    .orElse(nextHexes.get(0));

            // Ajustement de la flotte selon les ships non encore déplacés
            int fleetAdjustment = explore.getCurHex().getUnmovedShips().size();
            explore.exploreNext(bestNextHex, fleetAdjustment);
        }, player.getName() + " is choosing a hex to Explore next.");
    }

    /**
     * Gère le choix de l'hexagone à exterminer (phase Exterminate).
     * Le robot agressif choisit aléatoirement entre cibler l'hex le plus faible ou le plus fort.
     *
     * @param exterminate    Commande Exterminate en cours.
     * @param invadableHexes Liste des hex pouvant être envahis.
     */
    @Override
    public void handleExterminateChooseHex(Exterminate exterminate, List<Hex> invadableHexes) {
        simulateThinking(() -> {
            // Décision aléatoire : attaquer l'hex le plus faible ou le plus fort
            boolean pickWeakest = random.nextBoolean();
            Hex chosenHex = pickWeakest
                    ? invadableHexes.stream()
                    .filter(Hex::isOccupied)
                    .min(Comparator.comparingInt(Hex::getShipsCount))
                    .orElse(invadableHexes.get(0))
                    : invadableHexes.stream()
                    .filter(Hex::isOccupied)
                    .max(Comparator.comparingInt(Hex::getShipsCount))
                    .orElse(invadableHexes.get(0));

            exterminate.startInvadingHex(chosenHex);
        }, player.getName() + " is choosing a Hex to start Invade.");
    }

    /**
     * Gère le choix des ships (combien en envoyer) lors de la phase Exterminate.
     * Le robot agressif envoie tous les ships non encore envahis disponibles
     * depuis le premier hex envahisseur.
     *
     * @param exterminate   Commande Exterminate en cours.
     * @param invadingHexes Liste des hex déjà envahis où le joueur peut ajouter des ships.
     */
    @Override
    public void handleExterminateChooseShips(Exterminate exterminate, List<Hex> invadingHexes) {
        simulateThinking(() -> {
            // Le robot agressif choisit le premier hex et y envoie un maximum de ships
            Hex invadingHex = invadingHexes.get(0);
            int maxShips = invadingHex.getUninvadedShipCount();
            exterminate.addShipsInvadingHex(invadingHex, maxShips);
        }, player.getName() + " is choosing ships to Invade.");
    }

    /**
     * Gère le choix du secteur à exploiter (phase Exploit).
     * Le robot agressif prend le secteur offrant le plus haut score.
     *
     * @param exploitState    State exploit en cours.
     * @param scorableSectors Liste des secteurs éligibles pour le scoring.
     */
    @Override
    public void handleExploitChooseSector(ExploitState exploitState, List<Sector> scorableSectors) {
        simulateThinking(() -> {
            Sector bestSector = scorableSectors.stream()
                    .max(Comparator.comparingInt(s -> s.getScorePlayerExploit(player)))
                    .orElse(scorableSectors.get(0));
            exploitState.chooseSectorToScore(bestSector);
        }, player.getName() + " is choosing a Sector to score.");
    }
}
