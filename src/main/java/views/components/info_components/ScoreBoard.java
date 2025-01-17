package views.components.info_components;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import model.Partie;
import model.players.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant graphique représentant le tableau des scores des joueurs.
 * Ce composant affiche les détails de chaque joueur et écoute les changements d'ordre des joueurs pour mettre à jour l'affichage.
 */
public class ScoreBoard extends VBox implements PropertyChangeListener {

    /** Liste contenant les détails de chaque joueur affiché dans le tableau des scores. */
    private final List<PlayerDetails> playerDetailsList = new ArrayList<>();

    /** Instance unique de la partie en cours. */
    private final Partie partie;

    /**
     * Constructeur de la classe ScoreBoard.
     * Initialise le tableau des scores avec la liste des joueurs fournie et configure l'écoute des événements de changement d'ordre des joueurs.
     *
     * @param players La liste des joueurs dont les détails seront affichés dans le tableau des scores.
     */
    public ScoreBoard(List<Player> players) {
        // Récupération de l'instance unique de la partie en cours
        this.partie = Partie.getInstance();

        // Ajout de ce composant en tant qu'écouteur des changements d'ordre des joueurs
        partie.addPropertyChangeListener("PLAYER_ORDER_CHANGE", this);

        // Application des styles CSS définis pour le tableau des scores
        getStyleClass().add("scoreboard");

        // Configuration de l'espacement entre les lignes des joueurs
        setSpacing(15);

        // Alignement central des éléments dans le VBox
        setAlignment(Pos.CENTER);

        // Initialisation des détails de chaque joueur et ajout au VBox
        for (Player player : players) {
            PlayerDetails playerDetails = new PlayerDetails(player);
            playerDetailsList.add(playerDetails);
            getChildren().add(playerDetails);
        }

        // Configuration des dimensions du VBox pour qu'elles s'ajustent à la taille préférée
        setMaxHeight(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
    }

    /**
     * Fait pivoter l'ordre des joueurs dans le tableau des scores.
     * Le dernier joueur de la liste est déplacé en première position.
     */
    private void rotatePlayerOrder() {
        // Suppression du dernier joueur de la liste des détails
        PlayerDetails lastPlayer = playerDetailsList.remove(playerDetailsList.size() - 1);

        // Ajout du joueur supprimé en première position de la liste
        playerDetailsList.add(0, lastPlayer);

        // Mise à jour des enfants du VBox pour refléter le nouvel ordre
        getChildren().remove(lastPlayer);
        getChildren().add(0, lastPlayer);
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Dans ce cas, elle écoute les changements d'ordre des joueurs et met à jour le tableau des scores en conséquence.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Vérifie si l'événement correspond à un changement d'ordre des joueurs
        if ("PLAYER_ORDER_CHANGE".equals(evt.getPropertyName())) {
            // Pivote l'ordre des joueurs dans le tableau des scores
            rotatePlayerOrder();
        }
    }
}
