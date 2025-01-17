package views.components.info_components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.Partie;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Composant graphique affichant les instructions du jeu.
 * Ce composant écoute les événements de réflexion du robot et met à jour son affichage en conséquence.
 * Il est également responsable d'afficher les instructions de jeu aux joueurs humains
 */
public class Instructions extends HBox implements PropertyChangeListener {

    /** Label affichant les instructions ou les messages du robot. */
    private final Label instructionsLabel;

    /**
     * Constructeur de la classe Instructions.
     * Initialise le composant, applique les styles CSS et configure l'écoute des événements de réflexion du robot.
     *
     * @param maxWidth La largeur maximale du composant pour le texte des instructions.
     */
    public Instructions(double maxWidth) {
        // Création du label pour les instructions
        instructionsLabel = new Label();
        instructionsLabel.getStyleClass().add("instructions-label"); // Ajout de la classe CSS
        instructionsLabel.setWrapText(true); // Permet le retour à la ligne automatique
        instructionsLabel.setMaxWidth(maxWidth); // Définit la largeur maximale du label
        instructionsLabel.setAlignment(Pos.CENTER); // Centre le texte dans le label

        // Ajout du label à l'HBox
        getChildren().add(instructionsLabel);

        // Application des styles CSS définis pour l'HBox
        this.getStyleClass().add("instructions-box");
        setAlignment(Pos.CENTER); // Centre les éléments dans l'HBox
        setMaxWidth(maxWidth); // Définit la largeur maximale de l'HBox

        // Ajout de ce composant en tant qu'écouteur des événements "ROBOT_THINKING"
        Partie.getInstance().addPropertyChangeListener("ROBOT_THINKING", this);
        Partie.getInstance().addPropertyChangeListener("ROBOT_DONE_THINKING", this); // Ajout de l'écouteur pour la fin de la réflexion
    }

    /**
     * Définit le texte des instructions affichées.
     *
     * @param instructions Le texte des instructions à afficher.
     */
    public void setInstructions(String instructions) {
        instructionsLabel.setText(instructions);
    }

    /**
     * Affiche le composant des instructions.
     */
    public void showInstructions() {
        setVisible(true);
    }

    /**
     * Masque le composant des instructions.
     */
    public void hideInstructions() {
        setVisible(false);
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Cette méthode gère les événements liés à la réflexion du robot et met à jour les instructions en conséquence.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        switch (evt.getPropertyName()) {
            case "ROBOT_THINKING":
                // Si le robot est en train de réfléchir, afficher le texte d'action du robot
                if (newValue instanceof String robotTextAction) {
                    setInstructions(robotTextAction);
                }
                break;
            case "ROBOT_DONE_THINKING":
                // Si le robot a terminé de réfléchir, effacer le texte des instructions
                setInstructions("");
                break;
            default:
                // Ignorer les autres événements
                break;
        }
    }
}
