package model.states;

import model.Partie;
import model.commands.*;
import model.players.Player;

import java.io.Serial;
import java.util.Comparator;

/**
 * State gérant la phase Perform. Dans cette phase, les commandes planifiées
 * (Expand, Explore, Exterminate) sont exécutées selon l'ordre et l'efficacité
 * déterminés. Chaque joueur exécute sa commande pour la phase en cours,
 * puis on passe à la phase suivante. Au total, il y a 3 phases (pour 3 commandes).
 */
public class PerformState extends AbstractGameState {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Matrice d'efficacité : pour chaque phase (0..2) et chaque commande (1..3),
     * indique combien de joueurs ont choisi cette commande, influençant le gameplay.
     */
    private Integer[][] commandEfficiencies;

    /**
     * Tableau indiquant, pour chaque phase, l'ordre des joueurs
     * triés par numéro de commande (Expand=1, Explore=2, Exterminate=3).
     */
    private Player[][] playerOrderCommands;

    /**
     * Indice du joueur actuel dans la phase en cours.
     */
    private int currentPerformCommandIndex = 0;

    /**
     * Numéro de la phase Perform en cours (0..2).
     */
    private int performPhaseNum = 0;

    /**
     * Commande en cours d'exécution (Expand, Explore ou Exterminate).
     */
    private Command currentCommand;

    /**
     * Nombre total de phases Perform (3 commandes).
     */
    private static final int NUM_PERFORM_PHASES = 3;

    /**
     * Nombre total de commandes distinctes (Expand, Explore, Exterminate).
     */
    private static final int NUM_COMMANDS = 3;

    /**
     * Constructeur de la classe PerformState.
     * @param partie Partie en cours.
     */
    public PerformState(Partie partie) {
        super(partie);
    }

    /**
     * Méthode principale de la phase Perform.
     * Gère la progression à travers les 3 phases (chacune correspondant
     * à une commande planifiée). Si on termine la 3e phase, on passe à ExploitState.
     */
    @Override
    public void execute() {
        // Quand tous les joueurs ont exécuté la commande de la phase, on passe à la phase suivante
        if (currentPerformCommandIndex == players.size()) {
            performPhaseNum++;
            currentPerformCommandIndex = 0;
        }

        // Si on a déjà fait les 3 phases (0..2), on finit Perform et passe à Exploit
        if (performPhaseNum == NUM_PERFORM_PHASES) {
            partie.firePropertyChange("END_PERFORM", null, null);
            partie.transitionTo(new ExploitState(partie));
            partie.runCurrentState();
            return;
        }

        // Au tout début de la phase 0, on calcule l'efficacité des commandes
        if (performPhaseNum == 0 && currentPerformCommandIndex == 0) {
            calculateCommandEfficiencies();
        }

        // Au début de chaque phase, ffiche les commandes choisies pour information
        if (currentPerformCommandIndex == 0) {
            partie.firePropertyChange("DISPLAY_CHOSEN_COMMAND", null, performPhaseNum);
        }

        // Si aucune commande n'est en cours, on initialise celle du joueur actuel
        if (currentCommand == null) {
            initiateCommandPerformPhase();
        }
        // Exécute la commande actuelle
        currentCommand.execute();
    }

    /**
     * Calcule commandEfficiencies pour chaque phase et commande,
     * et détermine l'ordre (playerOrderCommands) dans lequel les joueurs
     * exécuteront leurs commandes.
     */
    private void calculateCommandEfficiencies() {
        commandEfficiencies = new Integer[NUM_PERFORM_PHASES][NUM_COMMANDS + 1];
        for (int phase = 0; phase < NUM_PERFORM_PHASES; phase++) {
            for (int command = 1; command <= NUM_COMMANDS; command++) {
                final int currentPhase = phase;
                final int currentCommand = command;
                commandEfficiencies[phase][command] = (int) players.stream()
                        .filter(p -> p.getCommandNumberForPhase(currentPhase) == currentCommand)
                        .count();
            }
        }

        playerOrderCommands = new Player[NUM_PERFORM_PHASES][];
        for (int phase = 0; phase < NUM_PERFORM_PHASES; phase++) {
            playerOrderCommands[phase] = sortPlayersByCommand(phase);
        }
    }

    /**
     * Initialise la commande pour le joueur actuel (en fonction de son choix),
     * en tenant compte de l'efficacité (nombre de joueurs ayant fait le même choix).
     */
    private void initiateCommandPerformPhase() {
        Player currentPlayer = playerOrderCommands[performPhaseNum][currentPerformCommandIndex];
        partie.setCurrentPlayer(currentPlayer);

        int commandNumber = currentPlayer.getCommandNumberForPhase(performPhaseNum);
        int efficiency = commandEfficiencies[performPhaseNum][commandNumber];

        switch (commandNumber) {
            case 1 -> currentCommand = new Expand(board, this, currentPlayer, efficiency);
            case 2 -> currentCommand = new Explore(board, this, currentPlayer, efficiency);
            case 3 -> currentCommand = new Exterminate(board, this, currentPlayer, efficiency);
            default -> throw new IllegalStateException("Commande inconnue : " + commandNumber);
        }
    }

    /**
     * Trie les joueurs selon la commande choisie (1=Expand, 2=Explore, 3=Exterminate)
     * pour la phase donnée, afin de déterminer l'ordre d'exécution.
     * @param phase Indice de phase (0..2).
     * @return Tableau de Player trié par numéro de commande.
     */
    private Player[] sortPlayersByCommand(int phase) {
        return players.stream()
                .sorted(Comparator.comparingInt(p -> p.getCommandNumberForPhase(phase)))
                .toArray(Player[]::new);
    }

    /**
     * Indique à la phase Perform qu'on passe au prochain joueur.
     * Réinitialise la commande en cours pour forcer un recalcul dans execute().
     */
    public void nextCommand() {
        currentPerformCommandIndex++;
        currentCommand = null;
        execute();
    }

    /**
     * Retourne la commande (Expand, Explore ou Exterminate) actuellement en cours d'exécution.
     * @return L'objet Command en cours, ou null si aucune commande n'est en exécution.
     */
    public Command getCurrentCommand() {
        return currentCommand;
    }

    /**
     * Retourne le numéro de la phase Perform actuelle (0,1,2).
     * @return Indice de la phase en cours.
     */
    public int getPerformPhaseNum() {
        return performPhaseNum;
    }

    /**
     * Retourne l'efficacité (nombre de joueurs ayant choisi cette commande)
     * pour la commande spécifiée lors de la phase actuelle.
     * @param commandNumber Numéro de la commande (1=Expand, 2=Explore, 3=Exterminate).
     * @return Nombre de joueurs ayant choisi cette commande à la phase en cours.
     */
    public int getCurPhaseCommandEfficiency(int commandNumber) {
        return commandEfficiencies[performPhaseNum][commandNumber];
    }
}
