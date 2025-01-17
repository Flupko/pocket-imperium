package views.components.info_components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.board.Hex;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Composant graphique affichant les détails d'un hex spécifique sur le plateau de jeu.
 * Ce composant affiche les coordonnées, le niveau, le nombre de ships, le contrôleur,
 * ainsi que le nombre de ships non déplacés et non envahis dans l'hex sélectionné.
 * Il écoute les mises à jour de l'hex pour rafraîchir les informations affichées.
 */
public class HexDetails extends VBox implements PropertyChangeListener {

    /** Label affichant les coordonnées de l'hexagone. */
    private final Label coordsText = new Label();

    /** Label affichant le niveau de l'hexagone. */
    private final Label levelText = new Label();

    /** Label affichant le nombre de ships dans l'hexagone. */
    private final Label shipCountText = new Label();

    /** Label affichant le contrôleur de l'hexagone. */
    private final Label controllerText = new Label();

    /** Label affichant le nombre de ships non déplacés dans l'hexagone. */
    private final Label unmovedShipsText = new Label();

    /** Label affichant le nombre de ships non envahis dans l'hexagone. */
    private final Label uninvadedShipsText = new Label();

    /** Instance de l'hexagone actuellement sélectionné. */
    private Hex hex = null;

    /**
     * Constructeur de la classe HexDetails.
     * Initialise les composants graphiques, applique les styles CSS, et configure les paramètres de mise en page.
     */
    public HexDetails() {
        // Application de la classe CSS pour le thème spatial
        this.getStyleClass().add("hex-details-box");

        // Application des classes CSS aux labels de texte
        coordsText.getStyleClass().add("hex-details-text");
        levelText.getStyleClass().add("hex-details-text");
        shipCountText.getStyleClass().add("hex-details-text");
        controllerText.getStyleClass().add("hex-details-text");
        unmovedShipsText.getStyleClass().add("hex-details-text");
        uninvadedShipsText.getStyleClass().add("hex-details-text");

        // Ajout de tous les labels au VBox
        getChildren().addAll(coordsText, levelText, shipCountText, controllerText, unmovedShipsText, uninvadedShipsText);

        // Configuration initiale de la visibilité et de la mise en page
        setVisible(false); // Masque les détails par défaut
        setSpacing(5); // Espacement entre les lignes de texte
        setPadding(new Insets(10)); // Padding autour du VBox
        setMaxWidth(150); // Largeur maximale ajustée pour accueillir un texte plus long
        setMaxHeight(120); // Hauteur maximale ajustée pour un meilleur affichage
    }

    /**
     * Affiche les détails de l'hexagone spécifié.
     * Met à jour les informations affichées et configure les écouteurs d'événements.
     *
     * @param newHex L'hexagone dont les détails doivent être affichés.
     */
    public void showDetails(Hex newHex) {
        // Si un hexagone précédent était sélectionné, retire l'écouteur d'événements
        if (hex != null) {
            hex.removePropertyChangeListener(this);
        }

        // Assigne le nouvel hexagone
        this.hex = newHex;

        if (hex != null) {
            // Ajoute un écouteur pour les mises à jour de l'hexagone
            hex.addPropertyChangeListener("HEX_UPDATE", this);

            // Met à jour les détails affichés
            updateDetails();

            // Rend le composant visible
            setVisible(true);
        } else {
            // Si l'hexagone est nul, efface les détails affichés
            clearDetails();
        }
    }

    /**
     * Met à jour les informations affichées en fonction de l'hexagone sélectionné.
     */
    public void updateDetails() {
        // Mise à jour des coordonnées
        coordsText.setText("Coordinates: " + hex.getPosX() + ", " + hex.getPosY());

        // Mise à jour du niveau de l'hexagone
        levelText.setText(getHexLevelText(hex.getLevel()));

        // Mise à jour du nombre de ships
        shipCountText.setText("Number of Ships: " + hex.getShipsCount());

        // Mise à jour du contrôleur de l'hexagone
        controllerText.setText("Controller: " + (hex.getController() != null ? hex.getController().getName() : "None"));

        // Mise à jour du nombre de ships non déplacés
        unmovedShipsText.setText("Unmoved Ships: " + hex.getUnmovedShips().size());

        // Mise à jour du nombre de ships non envahis
        uninvadedShipsText.setText("Uninvaded Ships: " + hex.getUninvadedShipCount());
    }

    /**
     * Efface les détails affichés et masque le composant.
     * Retire également l'écouteur d'événements de l'hexagone précédent.
     */
    public void clearDetails() {
        // Efface tous les textes des labels
        coordsText.setText("");
        levelText.setText("");
        shipCountText.setText("");
        controllerText.setText("");
        unmovedShipsText.setText("");
        uninvadedShipsText.setText("");

        // Retire l'écouteur d'événements si un hexagone était sélectionné
        if (hex != null) {
            hex.removePropertyChangeListener(this);
            hex = null;
        }

        // Masque le composant
        setVisible(false);
    }

    /**
     * Retourne une représentation textuelle du niveau de l'hexagone.
     *
     * @param level Le niveau de l'hexagone.
     * @return Une chaîne de caractères représentant le niveau de l'hexagone.
     */
    private String getHexLevelText(int level) {
        return switch (level) {
            case 0 -> "Simple Hexagon";
            case 1 -> "System I";
            case 2 -> "System II";
            case 3 -> "Tri-Prime";
            default -> "Unknown Level";
        };
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Dans ce cas, elle écoute les mises à jour de l'hexagone et rafraîchit les détails affichés.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("HEX_UPDATE".equals(evt.getPropertyName())) {
            // Si l'hexagone a été mis à jour, rafraîchit les détails affichés
            updateDetails();
        }
    }
}
