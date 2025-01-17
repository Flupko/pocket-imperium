package views.components.info_components;

import javafx.scene.control.Label;
import model.Partie;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Composant graphique affichant le numéro du tour actuel dans le jeu.
 * Ce composant écoute les changements de tour et met à jour son affichage en conséquence.
 */
public class TurnText extends Label implements PropertyChangeListener {

    /**
     * Constructeur de la classe TurnText.
     * Initialise le composant, applique les styles CSS et configure l'écoute des événements de changement de tour.
     */
    public TurnText() {
        // Récupération de l'instance unique de la partie en cours
        Partie partie = Partie.getInstance();

        // Ajout de ce composant en tant qu'écouteur des changements de tour
        partie.addPropertyChangeListener("TURN_CHANGE", this);

        // Application des styles CSS définis pour ce composant
        getStyleClass().add("turn-text");

        // Mise à jour du texte initial pour refléter le tour actuel
        updateTurnText(partie.getTurn());
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Dans ce cas, elle écoute les changements de tour et met à jour le texte en conséquence.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Vérifie si l'événement correspond à un changement de tour
        if (evt.getPropertyName().equals("TURN_CHANGE")) {
            // Récupère la nouvelle valeur du tour depuis l'événement
            int newTurn = (int) evt.getNewValue();

            // Met à jour le texte affiché avec le nouveau numéro de tour
            updateTurnText(newTurn);
        }
    }

    /**
     * Met à jour le texte du composant pour afficher le numéro du tour actuel.
     *
     * @param turn Le numéro du tour à afficher.
     */
    private void updateTurnText(int turn) {
        // Mise à jour du texte avec le format "Round: X / 9"
        setText("Round: " + turn + " / 9");
    }
}
