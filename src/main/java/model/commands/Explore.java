package model.commands;

import model.ship.Ship;
import model.commands.commands_data.ExploreData;
import model.players.Player;
import model.board.Hex;
import model.board.Board;
import model.states.PerformState;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Commande représentant l'action "Explore" dans le jeu.
 * Cette commande permet à un joueur de déplacer ses ships (qui n'ont pas encore bougé)
 * par petits déplacements successifs, avec une distance maximale autorisée (deux hex).
 * Elle permet aussi de prendre le contrôle d'hex non occupés ou déjà contrôlés par
 * le même joueur. Le nombre de mouvements de flotte autorisés dépend de l'efficacité
 * (4 - efficiency).
 */
public class Explore extends Command implements Serializable {

    /** Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Couleur associée à la commande Explore, utilisée pour l'interface.
     */
    public static final String COLOR = "#0878b1";

    /**
     * Distance maximale qu'un seul mouvement de flotte peut parcourir : 2 hex.
     * Si le chemin dépasse cette limite, on finalise le déplacement actuel.
     */
    private static final int MAX_RANGE_EXPLORE = 2;

    /**
     * Données spécifiques à l'action Explore, dont :
     * - le joueur
     * - le nombre total de mouvements autorisés
     * - le chemin d'exploration actuel
     * - la flotte actuelle
     */
    private final ExploreData exploreData;

    /**
     * Constructeur de la classe Explore.
     * @param board       Le plateau de jeu (Board).
     * @param peformState Le state Perform gérant la succession des commandes.
     * @param player      Le joueur effectuant la commande.
     * @param efficiency  Valeur d'efficacité (1, 2 ou 3) déterminant le nombre
     *                    de mouvements de flotte autorisés (4 - efficiency).
     */
    public Explore(Board board, PerformState peformState, Player player, int efficiency) {
        super(board, peformState);
        exploreData = new ExploreData(player, 4 - efficiency);
    }

    /**
     * Méthode principale d'exécution de la commande.
     * Vérifie si l'exploration est finie ou impossible, puis initie un
     * nouveau déplacement ou poursuit le chemin d'exploration actuel.
     */
    @Override
    public void execute() {
        // Vérifie si l'exploration est terminée ou impossible
        if (isExploreFinished()) {
            finishCommand();
            return;
        }

        // S'il n'y a pas encore de chemin (liste vide), on initie l'exploration
        if (exploreData.getCurMovementPath().isEmpty()) {
            initiateExploreStart();
        } else {
            // Sinon, on gère la poursuite du chemin d'exploration
            processMovementPath();
        }
    }

    /**
     * Termine la commande Explore en finalisant le déplacement actuel
     * et en passant à la commande suivante.
     */
    @Override
    public void finishCommand() {
        finalizeMovement();
        finishExplore();
    }

    /**
     * Indique si l'exploration est déjà terminée parce que :
     * - le nombre maximal de mouvements est atteint
     * - il n'y a plus d'hex éligible pour commencer ou poursuivre un déplacement
     */
    private boolean isExploreFinished() {
        return hasReachedMovementLimit()
                || (exploreData.getCurMovementPath().isEmpty() && getHexesCanStartExplore().isEmpty());
    }

    /**
     * Lance l'initiation de l'exploration en demandant au joueur
     * de choisir un hex de départ parmi ceux où il a des ships non déplacés.
     */
    private void initiateExploreStart() {
        Player player = exploreData.getPlayer();
        player.getStrategy().handleExploreChooseStartHex(this, getHexesCanStartExplore());
    }

    /**
     * Gère la poursuite du chemin d'exploration : soit on le finalise,
     * soit on demande le prochain hex à explorer.
     */
    private void processMovementPath() {
        List<Hex> nextHexes = getHexesCanExploreNext();
        if (shouldFinalizeMovement(nextHexes)) {
            finalizeMovement();
            execute();
        } else {
            Player player = exploreData.getPlayer();
            player.getStrategy().handleExploreChooseNextHex(this, nextHexes);
        }
    }

    /**
     * Détermine si le déplacement actuel doit être finalisé, notamment si :
     * - la distance maximale est atteinte
     * - Tri-Prime est atteint
     * - aucun hex voisin n'est valide
     * @param nextHexes Liste de hex voisins éligibles
     * @return Vrai si on doit finaliser, faux sinon
     */
    private boolean shouldFinalizeMovement(List<Hex> nextHexes) {
        return isMaxRangeReached()
                || (exploreData.getCurMovementPath().size() > 1 && getLastHexInMovementPath().isTriPrime())
                || nextHexes.isEmpty();
    }

    /**
     * Termine le déplacement actuel et relance l'exécution pour
     * savoir si un nouveau déplacement est possible.
     */
    public void finishCurrentMovement() {
        finalizeMovement();
        execute();
    }

    /**
     * Finalise le déplacement actuel : on place la flotte dans l'hex final,
     * on incrémente le compteur de mouvements réalisés, puis on efface le chemin.
     */
    private void finalizeMovement() {
        resolveFleetMovement();
        exploreData.incrementNbFleetMovementsMade();
        exploreData.clearMovementPath();
    }

    /**
     * Retourne tous les hex où le joueur peut initier une exploration :
     * - Contrôlés par le joueur
     * - Possédant au moins un ship non déplacé
     * - Au moins un voisin est inoccupé ou contrôlé par le joueur
     * @return Les hexagones que le joueur peut commencer à explorer explorer
     */
    public List<Hex> getHexesCanStartExplore() {
        Player player = exploreData.getPlayer();
        return board.getHexesPlayerOn(player).stream()
                .filter(hex -> hex.getShips().stream().anyMatch(ship -> !ship.hasMoved()))
                .filter(hex -> hex.getNeighbors().stream()
                        .anyMatch(neighbor -> !neighbor.isOccupied() || neighbor.isControlledBy(player)))
                .collect(Collectors.toList());
    }

    /**
     * Retourn tout les hex voisins que le joueur peut explorer depuis l'hex actuel
     * @return Les hexagones que le joueur peut explorer ensuite
     */
    public List<Hex> getHexesCanExploreNext() {
        Hex currentHex = exploreData.getCurHex();
        if (currentHex == null) {
            return null;
        }

        Player player = exploreData.getPlayer();
        return currentHex.getNeighbors().stream()
                .filter(neighbor -> !neighbor.isOccupied() || neighbor.isControlledBy(player))
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un nextHex donné peut être exploré, compte tenu
     * d'un fleetAdjustment (positif => on prend des ships, négatif => on en laisse).
     * @param nextHex         Hex à explorer
     * @param fleetAdjustment Ajustement de flotte (peut être 0)
     * @return Vrai si c'est faisable, faux sinon
     */
    public boolean canExploreNext(Hex nextHex, int fleetAdjustment) {
        if (nextHex == null) {
            return false;
        }

        boolean isAccessible = isHexAccessible(nextHex);
        boolean fleetCondition = evaluateFleetCondition(fleetAdjustment, exploreData.getCurHex());

        return isAccessible && fleetCondition;
    }

    /**
     * Vérifie si un hex est accessible : soit inoccupé, soit déjà contrôlé par le joueur,
     * et adjacent à l'hex actuel.
     */
    private boolean isHexAccessible(Hex hex) {
        Player player = exploreData.getPlayer();
        Hex currentHex = exploreData.getCurHex();
        return (!hex.isOccupied() || hex.isControlledBy(player))
                && currentHex.getNeighbors().contains(hex);
    }

    /**
     * Évalue les conditions de flotte liées à un fleetAdjustment.
     * fleetAdjustment > 0 : on prend des ships supplémentaires
     * fleetAdjustment < 0 : on en laisse sur l'hex actuel
     * fleetAdjustment = 0 : la flotte reste inchangée
     */
    private boolean evaluateFleetCondition(int fleetAdjustment, Hex currentHex) {
        List<Ship> currentFleet = exploreData.getCurFleet();
        int currentFleetSize = currentFleet.size();
        int unmovedShipsAtCurrent = currentHex.getUnmovedShips().size();

        if (fleetAdjustment < 0) {
            // On laisse des ships
            return currentFleetSize > -fleetAdjustment;
        } else if (fleetAdjustment > 0) {
            // On prend des ships
            return unmovedShipsAtCurrent >= fleetAdjustment;
        } else {
            // Aucun changement
            return currentFleetSize > 0;
        }
    }

    /**
     * Déplace la flotte vers nextHex en ajustant le nombre de ships (positif ou négatif).
     * @param nextHex         Cible de l'exploration
     * @param fleetAdjustment Ajustement de flotte
     */
    public void exploreNext(Hex nextHex, int fleetAdjustment) {
        if (!canExploreNext(nextHex, fleetAdjustment)) {
            return;
        }

        adjustFleet(exploreData.getCurHex(), exploreData.getCurFleet(), fleetAdjustment);
        exploreData.addHexCurMovementPath(nextHex);
        execute();
    }

    /**
     * Lance l'exploration en créant un nouveau chemin partant d'un hex de départ.
     * @param startHex L'hex de départ
     */
    public void startExplore(Hex startHex) {
        if (!canStartExplore(startHex)) {
            return;
        }

        exploreData.newMovementPath(startHex);
        execute();
    }

    /**
     * Vérifie si le joueur peut initier un déplacement depuis un hex de départ :
     * - Le joueur le contrôle
     * - Il y a des ships non déplacés
     * - Au moins un voisin est inoccupé
     * @param startHex L'hex de départ
     * @return True si le joueur peut initier un déplacement depuis l'hex de départ sinon False
     */
    public boolean canStartExplore(Hex startHex) {
        if (startHex == null) {
            return false;
        }

        Player player = exploreData.getPlayer();
        boolean isControlled = startHex.isControlledBy(player);
        boolean hasUnmovedShips = !startHex.getUnmovedShips().isEmpty();
        boolean hasShipsThatCanMove = startHex.getShips().stream().anyMatch(ship -> !ship.hasMoved());
        boolean hasAvailableNeighbors = startHex.getNeighbors().stream().anyMatch(neighbor -> !neighbor.isOccupied());

        return isControlled && hasUnmovedShips && hasShipsThatCanMove && hasAvailableNeighbors;
    }

    /**
     * Termine la commande Explore et passe la main à la commande suivante.
     */
    private void finishExplore() {
        performState.nextCommand();
    }

    /**
     * Ajuste la flotte dans l'hex actuel en fonction de shipsToAdjust
     * (positif => on prend des ships, négatif => on en laisse).
     * @param hex          Hex source
     * @param fleet        Flotte actuelle
     * @param shipsToAdjust Nombre de ships à ajouter ou retirer
     */
    private void adjustFleet(Hex hex, List<Ship> fleet, int shipsToAdjust) {
        if (shipsToAdjust > 0) {
            addShipsToFleet(hex, fleet, shipsToAdjust);
        } else if (shipsToAdjust < 0) {
            removeShipsFromFleet(hex, fleet, -shipsToAdjust);
        }
    }

    /**
     * Ajoute shipsToAdjust ships depuis hex à la flotte, en les marquant comme "moved".
     */
    private void addShipsToFleet(Hex hex, List<Ship> fleet, int numberOfShips) {
        List<Ship> additionalShips = takeShipsFromHex(hex, numberOfShips);
        if (additionalShips != null) {
            fleet.addAll(additionalShips);
        }
    }

    /**
     * Retire shipsToAdjust ships de la flotte pour les laisser sur l'hex (cible).
     */
    private void removeShipsFromFleet(Hex hex, List<Ship> fleet, int numberOfShips) {
        int shipsToRemove = Math.min(numberOfShips, fleet.size());
        List<Ship> shipsToLeave = new ArrayList<>(fleet.subList(0, shipsToRemove));
        fleet.removeAll(shipsToLeave);
        hex.addShips(shipsToLeave);
    }

    /**
     * Sélectionne et retire numberOfShips ships non déplacés depuis hex,
     * puis les renvoie dans une liste. Les ships sont marqués "moved".
     * @param hex           Hex source
     * @param numberOfShips Nombre de ships à prendre
     * @return Liste de ships pris, ou null si insuffisant
     */
    private List<Ship> takeShipsFromHex(Hex hex, int numberOfShips) {
        List<Ship> unmovedShips = hex.getUnmovedShips();
        if (unmovedShips.size() < numberOfShips) {
            return null;
        }

        List<Ship> shipsToTake = new ArrayList<>();
        for (int i = 0; i < numberOfShips; i++) {
            Ship ship = unmovedShips.get(i);
            ship.setHasMoved(true);
            shipsToTake.add(ship);
        }

        hex.removeShips(shipsToTake);
        return shipsToTake;
    }

    /**
     * Place la flotte actuelle sur l'hex final, puis vide la flotte.
     */
    private void resolveFleetMovement() {
        Hex finalHex = exploreData.getCurHex();
        if (finalHex == null) {
            return;
        }

        List<Ship> finalFleet = new ArrayList<>(exploreData.getCurFleet());
        finalHex.addShips(finalFleet);
        exploreData.clearFleet();
    }

    /**
     * Vérifie si la distance maximale (2) est dépassée en comparant
     * la taille du chemin d'exploration.
     */
    private boolean isMaxRangeReached() {
        return exploreData.getCurMovementPath().size() > MAX_RANGE_EXPLORE;
    }

    /**
     * Vérifie si le joueur a atteint son quota de mouvements (4 - efficiency).
     */
    private boolean hasReachedMovementLimit() {
        return exploreData.getNbFleetMovementsMade() >= exploreData.getNbFleetMovementsAllowed();
    }

    /**
     * Retourne le dernier hex du chemin (destination actuelle).
     * @return Le dernier hex, ou null si le chemin est vide.
     */
    private Hex getLastHexInMovementPath() {
        List<Hex> path = exploreData.getCurMovementPath();
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }

    /**
     * Retourne l'hex actuel dans le chemin d'exploration,
     * ou null si aucun chemin n'est défini.
     * @return L'hex actuel dans le chemin d'exploration
     */
    public Hex getCurHex() {
        return (exploreData != null) ? exploreData.getCurHex() : null;
    }

    /**
     * Indique la taille de la flotte actuelle (nombre de ships).
     * @return Nombre de ships dans la flotte, ou 0 si inexistant.
     */
    public int getCurFleetSize() {
        return (exploreData != null) ? exploreData.getCurFleet().size() : 0;
    }

    /**
     * Retourne le nombre de mouvements déjà effectués par cette commande Explore.
     * @return Nombre de mouvements accomplis.
     */
    public int getNbFleetMovementsMade() {
        return exploreData.getNbFleetMovementsMade();
    }

    /**
     * Retourne la taille du chemin d'exploration (nombre d'hex) pour l'exploration actuelle.
     * @return Nombre d'hex dans le chemin.
     */
    public int getSizeCurMovementPath() {
        return exploreData.getCurMovementPath().size();
    }

    /**
     * Retourne l'hex précédent dans le chemin d'exploration,
     * ou null si la taille du chemin est trop faible (<= 1).
     * @return L'hex avant-dernier, ou null si indisponible.
     */
    public Hex getHexBefore() {
        List<Hex> movementPath = exploreData.getCurMovementPath();
        if (movementPath.size() <= 1) {
            return null;
        }
        return movementPath.get(movementPath.size() - 2);
    }
}
