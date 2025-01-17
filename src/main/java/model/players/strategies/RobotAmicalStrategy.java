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
 * Classe concrète représentant la stratégie amicale d'un robot joueur dans le jeu.
 * Cette classe implémente les méthodes définies dans la classe abstraite {@link Strategy}
 * pour gérer les actions spécifiques d'un joueur robot amical lors des différentes phases du jeu.
 * Contrairement à la stratégie agressive, cette stratégie simule une réflexion plus modérée
 * et effectue des actions partielles ou minimales, évitant ainsi d'exploiter pleinement toutes les possibilités.
 * Cela produit un style de jeu de robot moins agressif et plus décontracté.
 */
public class RobotAmicalStrategy extends Strategy implements Serializable {

    /**
     * Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation.
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
     * Générateur de nombres aléatoires utilisé pour simuler la "réflexion" du robot.
     */
    private final Random random = new Random();

    /**
     * Constructeur de la classe RobotAmicalStrategy.
     * Initialise la stratégie avec le joueur et la partie associés.
     *
     * @param player Le joueur auquel cette stratégie est associée.
     * @param partie L'instance unique de la partie en cours.
     */
    public RobotAmicalStrategy(Player player, Partie partie) {
        super(player, partie);
    }

    /**
     * Simule la réflexion en ajoutant un délai avant d'exécuter la logique sur le thread JavaFX.
     * Émet un événement indiquant que le robot est en train de réfléchir, puis un autre
     * lorsque le robot a terminé sa réflexion.
     *
     * @param action       La logique à exécuter après le délai de réflexion.
     * @param thinkingText Le texte décrivant l'action de réflexion du robot (pour l'interface).
     */
    private void simulateThinking(Runnable action, String thinkingText) {
        // Commentaire supplémentaire (hors javadoc) :
        // Émet un événement indiquant que le robot commence à réfléchir
        partie.firePropertyChange("ROBOT_THINKING", null, thinkingText);

        // Démarre un nouveau thread pour simuler la réflexion
        new Thread(() -> {
            try {
                // Génère un délai aléatoire entre MIN_THINKING_DELAY et MAX_THINKING_DELAY
                int randomDelay = MIN_THINKING_DELAY + random.nextInt(MAX_THINKING_DELAY - MIN_THINKING_DELAY + 1);
                Thread.sleep(randomDelay); // Simule la réflexion
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Exécute l'action sur le thread JavaFX après le délai
            Platform.runLater(() -> {
                // Émet un événement indiquant que le robot a terminé de réfléchir
                partie.firePropertyChange("ROBOT_DONE_THINKING", null, player.getName());
                action.run(); // Exécute la logique
            });
        }).start();
    }

    /**
     * Gère le choix du système lors de la phase de déploiement pour un joueur robot amical.
     * Simule la réflexion avant de déployer les ships sur le premier hexagone valide.
     *
     * @param deployState          Le state actuel de déploiement.
     * @param validDeploymentHexes Liste des hexagones valides pour le déploiement.
     */
    @Override
    public void handleDeployChooseSystem(DeployState deployState, List<Hex> validDeploymentHexes) {
        simulateThinking(() -> {
            // Commentaire supplémentaire (hors javadoc) :
            // Choix simple : sélectionne le premier hexagone valide
            Hex chosenHex = validDeploymentHexes.get(0);
            deployState.deployShips(chosenHex);
        }, player.getName() + " is choosing a Hex to deploy his ships.");
    }

    /**
     * Gère le choix des commandes lors de la phase de planification pour un joueur robot amical.
     * Simule la réflexion avant de planifier les commandes dans l'ordre (2=Explore, 1=Expand, 3=Exterminate).
     *
     * @param planState Le state actuel de planification.
     */
    @Override
    public void handlePlanChooseCommands(PlanState planState) {
        simulateThinking(() -> {
            planState.planCommands(Arrays.asList(2, 1, 3));
        }, player.getName() + " is planning his Commands.");
    }

    /**
     * Gère le choix de l'hexagone lors de la phase d'expansion (Expand).
     * Sélectionne l'hexagone ayant le cumul (ships + niveau) le plus faible,
     * puis n'ajoute qu'environ la moitié des ships disponibles.
     *
     * @param expand         La commande Expand en cours.
     * @param hexesCanExpand Liste des hexagones où l'expansion est possible.
     */
    @Override
    public void handleExpandChooseHex(Expand expand, List<Hex> hexesCanExpand) {
        simulateThinking(() -> {
            // Choisit le hex avec le plus faible (shipsCount + level)
            Hex chosenHex = hexesCanExpand.stream()
                    .min(Comparator.comparingInt(h -> h.getShipsCount() + h.getLevel()))
                    .orElse(hexesCanExpand.get(0));

            int currentShipsAdded = expand.getShipsAdded();
            int totalShipsAvailable = expand.getTotShipsCanAdd();
            int halfShips = (totalShipsAvailable + 1) / 2;

            // Si on a déjà déployé la moitié, terminer
            if (currentShipsAdded >= halfShips) {
                expand.finishCommand();
                return;
            }

            int shipsToAdd = halfShips - currentShipsAdded;
            expand.addShips(chosenHex, shipsToAdd);
        }, player.getName() + " is choosing a hex to Expand.");
    }

    /**
     * Gère le choix de l'hexagone de départ pour l'exploration (Explore).
     * 50% de chances de ne pas explorer, sinon choisit le hex avec
     * le ratio ships/voisins le plus faible.
     *
     * @param explore    La commande Explore en cours.
     * @param startHexes Liste des hex de départ admissibles.
     */
    @Override
    public void handleExploreChooseStartHex(Explore explore, List<Hex> startHexes) {
        simulateThinking(() -> {
            // 50% de chances de skip
            if (random.nextBoolean()) {
                explore.finishCommand();
                return;
            }
            // Si on a déjà fait 1 déplacement, finir la commande
            int movementsMade = explore.getNbFleetMovementsMade();
            if (movementsMade >= 1) {
                explore.finishCommand();
                return;
            }
            // Choisit le hex avec le plus petit ratio ships / neighbors
            Hex chosenHex = startHexes.stream()
                    .min(Comparator.comparingDouble(h ->
                            (double) h.getShipsCount() / Math.max(1, h.getNeighbors().size())))
                    .orElse(startHexes.get(0));
            explore.startExplore(chosenHex);
        }, player.getName() + " is choosing a Hex to start Explore.");
    }

    /**
     * Gère le choix du prochain hexagone lors de la phase d'exploration pour un joueur robot amical.
     * Simule la réflexion avant de continuer l'exploration.
     *
     * @param explore  La commande Explore en cours.
     * @param nextHexes Liste des hex que le joueur peut explorer ensuite.
     */
    @Override
    public void handleExploreChooseNextHex(Explore explore, List<Hex> nextHexes) {
        simulateThinking(() -> {
            int pathSize = explore.getSizeCurMovementPath();
            // Si on a déjà fait 2 déplacements, on arrête
            if (pathSize >= 2) {
                explore.finishCommand();
                return;
            }
            // Choisit l'hex avec le moins de ships
            Hex chosenNext = nextHexes.stream()
                    .min(Comparator.comparingInt(Hex::getShipsCount))
                    .orElse(nextHexes.get(0));

            // Ajustement éventuel de la flotte
            int fleetAdjustment = (pathSize == 1)
                    ? (explore.getCurHex().getUnmovedShipsCount() + 1) / 2
                    : 0;

            explore.exploreNext(chosenNext, fleetAdjustment);
        }, player.getName() + " is choosing a hex to Explore next.");
    }

    /**
     * Gère le choix de l'hexagone à exterminer (Exterminate) pour un joueur robot amical.
     * Simule la réflexion avant de lancer éventuellement l'attaque (75% de chances de skip).
     *
     * @param exterminate   La commande Exterminate en cours.
     * @param invadableHexes Liste des hex que le joueur peut envahir.
     */
    @Override
    public void handleExterminateChooseHex(Exterminate exterminate, List<Hex> invadableHexes) {
        simulateThinking(() -> {
            // 75% de chances de ne pas attaquer
            if (random.nextInt(4) != 0) {
                exterminate.finishCommand();
                return;
            }
            // Choisit le hex le moins défendu (shipsCount) puis plus bas level
            Hex chosenHex = invadableHexes.stream()
                    .min(Comparator.comparingInt(Hex::getShipsCount)
                            .thenComparingInt(Hex::getLevel))
                    .orElse(invadableHexes.get(0));
            exterminate.startInvadingHex(chosenHex);
        }, player.getName() + " is choosing a Hex to start Invade.");
    }

    /**
     * Gère le choix des ships lors de la phase d'extermination pour un joueur robot amical.
     * N'envoie qu'environ la moitié des ships restants, puis termine la commande.
     *
     * @param exterminate   La commande Exterminate en cours.
     * @param invadingHexes Liste des hex déjà envahis, où l'on doit décider du nombre de ships à envoyer.
     */
    @Override
    public void handleExterminateChooseShips(Exterminate exterminate, List<Hex> invadingHexes) {
        simulateThinking(() -> {
            int usedSoFar = exterminate.getNbShipsUsedCurrentInvasion();
            int maxPossible = exterminate.getMaxShipsCanBeUsedCurrentInvasion();
            int halfShips = (maxPossible + 1) / 2;

            if (usedSoFar >= halfShips) {
                exterminate.finishCommand();
                return;
            }

            Hex invadingHex = invadingHexes.get(0);
            int uninvaded = invadingHex.getUninvadedShipCount();
            // On envoie le min(ce qui reste pour arriver à la moitié, ships dispos)
            int toSend = Math.min(halfShips - usedSoFar, uninvaded);
            exterminate.addShipsInvadingHex(invadingHex, toSend);
        }, player.getName() + " is choosing ships to Invade.");
    }

    /**
     * Gère le choix du secteur à exploiter (Exploit) pour un joueur robot amical.
     * Simule la réflexion avant de cibler le secteur au plus bas score potentiel.
     *
     * @param exploitState    State Exploit en cours.
     * @param scorableSectors Liste des secteurs pouvant être scorés.
     */
    @Override
    public void handleExploitChooseSector(ExploitState exploitState, List<Sector> scorableSectors) {
        simulateThinking(() -> {
            Sector chosenSector = scorableSectors.stream()
                    .min(Comparator.comparingInt(s -> s.getScorePlayerExploit(player)))
                    .orElse(scorableSectors.get(0));
            exploitState.chooseSectorToScore(chosenSector);
        }, player.getName() + " is choosing a Sector to score.");
    }
}
