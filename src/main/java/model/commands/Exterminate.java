package model.commands;

import model.ship.Ship;
import model.commands.commands_data.ExterminateData;
import model.players.Player;
import model.board.Hex;
import model.board.Board;
import model.states.PerformState;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Commande représentant l'action "Exterminate" dans le jeu.
 * Cette commande permet à un joueur de lancer des invasions
 * sur des hexagones-systèmes occupés par d'autres joueurs.
 * Le joueur envahisseur envoie des ships depuis des hexagones qu'il contrôle
 * et déclenche un combat contre le défenseur occupant l'hex cible.
 * Les ships perdus sont retirés et marqués comme non déployés.
 * L'efficacité de la commande (4 - efficiency) détermine le nombre d'invasions
 * que le joueur peut effectuer pendant cette phase.
 */
public class Exterminate extends Command implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Couleur associée à la commande Exterminate, utilisée pour l'interface.
     */
    public static final String COLOR = "#ca4247";

    /**
     * Données spécifiques à l'action Exterminate :
     * - joueur déclencheur
     * - nombre d'invasions possibles
     * - hex envahi, hex envahisseurs
     * - suivi des ships utilisés lors de la bataille
     */
    private final ExterminateData exterminateData;

    /**
     * Constructeur de la classe Exterminate.
     *
     * @param board       Le plateau de jeu (Board).
     * @param peformState Le state Perform, qui gère la succession des commandes.
     * @param player      Le joueur qui exécute la commande Exterminate.
     * @param efficiency  L'efficacité (1 à 3), utilisée pour calculer le nombre
     *                    d'invasions autorisées (4 - efficiency).
     */
    public Exterminate(Board board, PerformState peformState, Player player, int efficiency) {
        super(board, peformState);
        exterminateData = new ExterminateData(player, 4 - efficiency);
    }

    /**
     * Méthode principale d'exécution de la commande.
     * Vérifie si l'extermination est terminée ou impossible,
     * puis initie l'invasion ou poursuit l'invasion en cours.
     */
    @Override
    public void execute() {

        // Vérifie si la limite d'invasions est atteinte ou si aucune invasion n'est faisable
        if (hasReachedInvasionLimit()
                || (exterminateData.getInvadingHexes().isEmpty() && getHexesCanInvade().isEmpty())) {
            finishCommand();
            return;
        }

        // Si aucun hex envahi n'est encore défini, on demande au joueur de choisir l'hex à envahir
        if (exterminateData.getInvadedHex() == null) {
            initiateExterminateStart();
            return;
        }

        // Si un hex est déjà envahi et qu'il existe des hex envahisseurs, on gère la sélection de ships
        if (!exterminateData.getInvadingHexes().isEmpty()) {
            processOngoingInvasion();
        } else {
            // S'il n'y a plus d'hex envahisseur, on finalise l'invasion en cours et on ré-exécute
            finalizeCurrentInvasion();
            execute();
        }
    }

    /**
     * Termine la commande Exterminate en finalisant l'invasion en cours,
     * puis en passant à la commande suivante via performState.
     */
    @Override
    public void finishCommand() {
        finalizeCurrentInvasion();
        finishExterminate();
    }

    /**
     * Vérifie si la limite d'invasions autorisées est atteinte
     * (nombre d'invasions réalisées >= nombre d'invasions possibles).
     *
     * @return true si la limite est atteinte, false sinon.
     */
    public boolean hasReachedInvasionLimit() {
        return exterminateData.getNbInvasionsMade() >= exterminateData.getNbInvasionsPossible();
    }

    /**
     * Lance la première étape de l'extermination en listant les hexagones
     * invadables et en demandant au joueur de choisir la cible.
     */
    public void initiateExterminateStart() {
        List<Hex> invadableHexes = getHexesCanInvade();
        Player player = exterminateData.getPlayer();
        player.getStrategy().handleExterminateChooseHex(this, invadableHexes);
    }

    /**
     * Gère l'invasion en cours, demandant au joueur de sélectionner
     * combien de ships envoyer depuis chaque hex envahisseur.
     */
    public void processOngoingInvasion() {
        List<Hex> invadingHexes = exterminateData.getInvadingHexes();
        Player player = exterminateData.getPlayer();
        player.getStrategy().handleExterminateChooseShips(this, invadingHexes);
    }

    /**
     * Retourne la liste des hex que le joueur peut envahir :
     * - non contrôlés par ce joueur
     * - occupés par un autre joueur
     * - adjacents à au moins un hex contrôlé par le joueur
     * possédant des ships non envahis.
     *
     * @return La liste des hex que le joueur peut envahir
     */
    public List<Hex> getHexesCanInvade() {
        if (exterminateData == null) {
            return new ArrayList<>();
        }
        Player player = exterminateData.getPlayer();
        return board.getSystemsNotControlledBy(player).stream()
                .filter(this::canInvadeHex)
                .collect(Collectors.toList());
    }

    /**
     * Détermine si l'hex donné est envahissable :
     * - non contrôlé par le joueur
     * - occupé (donc contrôlé par un adversaire)
     * - voisin d'un hex contrôlé par le joueur ayant des ships non envahis.
     *
     * @param hex L'hex cible potentielle.
     * @return true si envahissable, false sinon.
     */
    public boolean canInvadeHex(Hex hex) {
        if (exterminateData == null || hex == null) {
            return false;
        }
        Player player = exterminateData.getPlayer();
        return !hex.isControlledBy(player)
                && hex.isOccupied()
                && hex.getNeighbors().stream()
                .anyMatch(neighbor -> neighbor.isControlledBy(player)
                        && neighbor.getUninvadedShipCount() > 0);
    }

    /**
     * Retourne les hex depuis lesquels le joueur lancer une invasion
     * sur l'hex spécifié (voisins contrôlés ayant des ships non envahis).
     *
     * @param hex L'hex cible.
     * @return Liste d'hex envahisseurs potentiels.
     */
    public List<Hex> getHexesCanParticipateInvasion(Hex hex) {
        if (hex == null || !canInvadeHex(hex)) {
            return new ArrayList<>();
        }
        Player player = exterminateData.getPlayer();
        return hex.getNeighbors().stream()
                .filter(neighbor -> neighbor.isControlledBy(player)
                        && neighbor.getUninvadedShipCount() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Débute l'invasion d'un hex (cible) si celui-ci est envahissable.
     * On détermine alors quels hex peuvent envoyer des ships,
     * et on réinitialise le compteur de ships utilisés pour cette invasion.
     *
     * @param hex L'hex visé par l'invasion.
     */
    public void startInvadingHex(Hex hex) {
        if (!canInvadeHex(hex)) {
            return;
        }

        exterminateData.setInvadedHex(hex);

        List<Hex> participatingHexes = getHexesCanParticipateInvasion(hex);
        exterminateData.setInvadingHexes(participatingHexes);

        exterminateData.setNbShipsUsedCurrentInvasion(0);
        int maxShipsCanBeUsedCurrentInvasion = participatingHexes.stream()
                .mapToInt(Hex::getUninvadedShipCount)
                .sum();
        exterminateData.setMaxShipsCanBeUsedCurrentInvasion(maxShipsCanBeUsedCurrentInvasion);

        // Après la configuration, on relance la logique
        execute();
    }

    /**
     * Ajoute nbShips ships d'un hex envahisseur à l'hex envahi, en résolvant la bataille au besoin.
     *
     * @param hex     L'hex envahisseur.
     * @param nbShips Le nombre de ships à envoyer.
     */
    public void addShipsInvadingHex(Hex hex, int nbShips) {
        if (!canAddShipsInvadingHex(hex, nbShips)) {
            return;
        }

        Hex invadedHex = exterminateData.getInvadedHex();
        Player player = exterminateData.getPlayer();
        List<Ship> allUninvadedShips = hex.getUninvadedShips();

        // Vérifie qu'on ne dépasse pas le nombre de ships réellement présents
        if (nbShips > allUninvadedShips.size()) {
            nbShips = allUninvadedShips.size();
        }

        exterminateData.setNbShipsUsedCurrentInvasion(
                nbShips + exterminateData.getNbShipsUsedCurrentInvasion()
        );

        // Prépare la liste des ships attaquants
        List<Ship> invadingShips = new ArrayList<>(allUninvadedShips.subList(0, nbShips));
        int deletedShips = 0;

        // Résout le combat si l'hex cible est contrôlé par un adversaire
        if (!invadedHex.isControlledBy(player) && invadedHex.isOccupied()) {
            // Copie des ships défenseurs
            List<Ship> defendingShips = new ArrayList<>(invadedHex.getShips());
            deletedShips = Math.min(defendingShips.size(), invadingShips.size());

            // Supprime la portion min(défenseurs, attaquants) chez le défenseur
            List<Ship> shipsToRemoveDefending = new ArrayList<>(defendingShips.subList(0, deletedShips));
            invadedHex.removeShips(shipsToRemoveDefending);
            shipsToRemoveDefending.forEach(ship -> ship.setDeployed(false));

            // Supprime la portion correspondante chez l'attaquant
            List<Ship> shipsToRemoveInvading = new ArrayList<>(invadingShips.subList(0, deletedShips));
            hex.removeShips(shipsToRemoveInvading);
            shipsToRemoveInvading.forEach(ship -> ship.setDeployed(false));
        }


        // Les ships attaquants restants sont transférés à l'hex envahi
        if (nbShips > deletedShips) {
            List<Ship> remainingInvaders = new ArrayList<>(invadingShips.subList(deletedShips, nbShips));
            remainingInvaders.forEach(ship -> ship.setHasInvaded(true));

            // Retrait depuis l'hex attaquant
            hex.removeShips(remainingInvaders);

            // Ajout dans l'hex envahi
            invadedHex.addShips(remainingInvaders);

            // Mise à jour du contrôleur si l'attaquant a pris possession
            if (!invadedHex.isControlledBy(player) && invadedHex.getShipsCount() > 0) {
                invadedHex.setController(player);
            }
        }

        // Si l'hex envahisseur n'a plus de ships, on l'enlève de la liste des hex envahisseurs
        if (hex.getShipsCount() == 0) {
            exterminateData.removeInvadingHex(hex);
        }

        // Ré-exécute la logique pour la suite
        execute();
    }

    /**
     * Vérifie si le joueur peut envoyer nbShips ships d'un hex envahisseur
     * (hex figure dans la liste des hex envahisseurs et contient suffisamment
     * de ships non envahis).
     *
     * @param hex     L'hex envahisseur.
     * @param nbShips Le nombre de vaisseaux que le joueur souhaite envoyer.
     * @return True si le joueur ajouter ces vaisseaux, sinon False.
     */
    public boolean canAddShipsInvadingHex(Hex hex, int nbShips) {
        return !(exterminateData == null
                || exterminateData.getInvadedHex() == null
                || !exterminateData.getInvadingHexes().contains(hex)
                || hex.getUninvadedShipCount() < nbShips);
    }

    /**
     * Finalise l'invasion en cours, vide la liste des hex envahisseurs
     * et l'hex envahi, puis incrémente le compteur d'invasions accomplies.
     */
    public void finalizeCurrentInvasion() {
        exterminateData.clearInvasion();
        exterminateData.incrementNbInvasionsMade();
    }

    /**
     * Termine l'invasion en cours et relance la commande pour vérifier
     * si d'autres invasions restent possibles.
     */
    public void finishCurrentInvasion() {
        finalizeCurrentInvasion();
        execute();
    }

    /**
     * Met fin définitivement à la commande Exterminate,
     * en passant à la prochaine commande via performState.
     */
    public void finishExterminate() {
        performState.nextCommand();
    }

    /**
     * Retourne l'hexagone actuellement envahi, ou null si aucun n'est défini.
     *
     * @return L'hex ciblé par l'invasion en cours, ou null.
     */
    public Hex getInvadedHex() {
        if (exterminateData == null) {
            return null;
        }
        return exterminateData.getInvadedHex();
    }

    /**
     * Retourne le nombre de ships déjà utilisés dans l'invasion actuelle.
     *
     * @return Nombre de ships engagés dans le combat.
     */
    public int getNbShipsUsedCurrentInvasion() {
        return exterminateData.getNbShipsUsedCurrentInvasion();
    }

    /**
     * Retourne le nombre maximal de ships pouvant être encore engagés
     * dans l'invasion actuelle.
     *
     * @return Capacité maximale de ships pour cette invasion.
     */
    public int getMaxShipsCanBeUsedCurrentInvasion() {
        return exterminateData.getMaxShipsCanBeUsedCurrentInvasion();
    }
}
