package model.states;

import model.Partie;
import model.ship.Ship;
import model.board.Hex;
import model.board.Sector;
import model.players.Player;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * State gérant la phase de déploiement (Deploy) du jeu.
 * Chaque joueur place 2 ships à deux reprises sur des hexagones de niveau I
 * inoccupés (hors secteur central). Une fois tous les déploiements terminés,
 * on passe à la phase PlanState.
 */
public class DeployState extends AbstractGameState {

    /**
     * Identifiant de sérialisation pour garantir la compatibilité lors de la désérialisation.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Compteur indiquant combien de déploiements ont déjà eu lieu.
     * Puisque chaque joueur effectue 2 déploiements, la limite
     * est players.size() * 2.
     */
    private int deployPhaseCounter = 0;

    /**
     * Liste de secteurs effectivement occupés lors de cette phase de déploiement.
     */
    private final List<Sector> occupiedSectors;

    /**
     * Constructeur de la classe DeployState.
     * @param partie L'instance de la partie courante.
     */
    public DeployState(Partie partie) {
        super(partie);
        occupiedSectors = new ArrayList<>();
    }

    /**
     * Méthode principale de la phase Deploy.
     * Sélectionne le joueur devant déployer 2 ships, puis lui propose
     * de choisir un hex valide parmi ceux disponibles (niveau I non occupés).
     * Lorsque tous les joueurs ont effectué 2 déploiements, on déclenche
     * l'événement de fin de déploiement et on passe à la phase PlanState.
     */
    @Override
    public void execute() {
        // Vérifie si on n'a pas dépassé le nombre total de déploiements
        if (deployPhaseCounter < players.size() * 2) {
            // Calcule un index de joueur, permettant un ordre 0..n-1, n-1..0...
            int playerIndex = Math.min(deployPhaseCounter, 2 * players.size() - 1 - deployPhaseCounter);
            partie.setCurrentPlayer(playerIndex);
            Player currentPlayer = partie.getCurrentPlayer();
            currentPlayer.getStrategy().handleDeployChooseSystem(this, getValidInitialDeploymentHexes());
        } else {
            // Une fois tous les déploiements effectués
            partie.firePropertyChange("FINISH_DEPLOY", null, null);
            partie.transitionTo(new PlanState(partie));
            partie.runCurrentState();
        }
    }

    /**
     * Déploie 2 ships sur un hex donné, si c'est valide, puis incrémente
     * le compteur de phase et relance la logique.
     * @param chosenHex L'hex système de niveau I choisi pour le déploiement.
     */
    public void deployShips(Hex chosenHex) {
        if (!canInitDeploy(chosenHex)) {
            return;
        }

        // Retient le secteur correspondant
        Sector systemSector = board.getSectorContainingHex(chosenHex);
        occupiedSectors.add(systemSector);

        // Déploie 2 ships du joueur actuel
        Player currentPlayer = partie.getCurrentPlayer();
        List<Ship> unusedShips = currentPlayer.getUnusedShips();
        for (int i = 0; i < 2; i++) {
            Ship ship = unusedShips.get(i);
            ship.setDeployed(true);
            chosenHex.addShip(ship);
        }
        deployPhaseCounter++;

        // Déclenche un événement indiquant un déploiement
        systemSector.deployEvent();

        // Poursuit la phase
        execute();
    }

    /**
     * Retourne la liste des hex éligibles pour le déploiement initial :
     * - Hors secteur central
     * - Non occupés
     * - Niveau I
     * @return Liste d'hex candidats pour le déploiement.
     */
    private List<Hex> getValidInitialDeploymentHexes() {
        return Arrays.stream(board.getSectors())
                .filter(sector -> !sector.isCentralSector())
                .filter(sector -> sector.getSystemHexes().stream().noneMatch(Hex::isOccupied))
                .flatMap(sector -> sector.getSystemHexes().stream())
                .filter(hex -> hex.getLevel() == 1)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si le joueur peut déployer 2 ships sur un hex de niveau I.
     * @param targetHex L'hex visé pour le déploiement.
     * @return true si c'est un hex système de niveau I inoccupé et
     *         que le secteur n'a pas d'autre hex occupé, false sinon.
     */
    public boolean canInitDeploy(Hex targetHex) {
        if (targetHex == null || targetHex.getLevel() != 1 || targetHex.isOccupied()) {
            return false;
        }
        Sector sector = board.getSectorContainingHex(targetHex);
        return (sector != null) && sector.getSystemHexes().stream().noneMatch(Hex::isOccupied);
    }

    /**
     * Retourne la liste des secteurs occupés lors de la phase de déploiement.
     * @return Liste de secteurs occupés.
     */
    public List<Sector> getOccupiedSectors() {
        return occupiedSectors;
    }
}
