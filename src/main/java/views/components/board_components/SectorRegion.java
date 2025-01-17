package views.components.board_components;

import javafx.scene.layout.Region;
import model.Partie;
import model.board.Sector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Composant graphique représentant un secteur sur le plateau de jeu.
 * Cette classe gère l'affichage visuel des secteurs, y compris les bordures et les couleurs de fond
 * en fonction de l'état du secteur (scoré, déployé, etc.). Elle écoute les événements de changement
 * de propriété pour mettre à jour l'affichage en conséquence.
 */
public class SectorRegion extends Region implements PropertyChangeListener {

    /** Secteur associé à cette région graphique. */
    private Sector sector;

    /** Ligne (row) du secteur sur le plateau. */
    private int row;

    /** Colonne (col) du secteur sur le plateau. */
    private int col;

    /** Style actuel de la bordure de la région. */
    private String currentBorderStyle = "";

    /** Style actuel de l'arrière-plan de la région. */
    private String currentBackgroundStyle = "";

    /**
     * Constructeur de la classe SectorRegion.
     * Initialise la région sectorielle avec sa position et le secteur lié.
     * Applique les styles par défaut et configure les écouteurs d'événements.
     *
     * @param row         La ligne du secteur sur le plateau.
     * @param col         La colonne du secteur sur le plateau.
     * @param linkedSector Le secteur lié à cette région graphique.
     */
    public SectorRegion(int row, int col, Sector linkedSector) {
        this.row = row;
        this.col = col;
        this.sector = linkedSector;

        // Réinitialiser les bordures au style par défaut
        resetBorderToDefault();

        // Ajouter des écouteurs pour les changements de propriété du secteur
        sector.addPropertyChangeListener("SCORED", this);
        sector.addPropertyChangeListener("UNSCORED", this);
        sector.addPropertyChangeListener("DEPLOYED", this);

        // Ajouter un écouteur pour la fin du déploiement dans la partie
        Partie.getInstance().addPropertyChangeListener("FINISH_DEPLOY", this);

        // Si le secteur est déjà scoré, appliquer la mise en évidence correspondante
        if (sector.isScored()) {
            highlightRegion("#454545cc");
        }
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Gère les différents types d'événements liés au secteur et à la partie.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "DEPLOYED":
                // Appliquer une mise en évidence spécifique lors du déploiement
                highlightRegion("#2C2C2CCC");
                break;
            case "FINISH_DEPLOY":
                // Rendre la région transparente lorsque le déploiement est terminé
                makeTransparent();
                break;
            case "SCORED":
                // Appliquer une mise en évidence spécifique lorsque le secteur est scoré
                highlightRegion("#454545cc");
                break;
            case "UNSCORED":
                // Rendre la région transparente lorsque le secteur n'est plus scoré
                // Remarque : La ligne suivante affiche "Unscored" dans la console, ce qui peut être utilisé pour le débogage
                makeTransparent();
                break;
            default:
                // Ignorer les autres événements
                break;
        }
    }

    /**
     * Applique une mise en évidence à la région avec la couleur spécifiée.
     *
     * @param highlightColor La couleur de mise en évidence au format hexadécimal (ex. "#FF0000cc").
     */
    public void highlightRegion(String highlightColor) {
        // Mettre à jour uniquement la couleur de fond
        currentBackgroundStyle = String.format("-fx-background-color: %s;", highlightColor);
        updateStyle();
        setMouseTransparent(false); // Rendre la région interactive
    }

    /**
     * Rend la région transparente en réinitialisant la couleur de fond et en ajustant les bordures.
     */
    public void makeTransparent() {
        // Définir l'arrière-plan à transparent
        currentBackgroundStyle = "-fx-background-color: transparent;";

        // Réinitialiser le style de bordure au style par défaut basé sur la position
        currentBorderStyle = getBorderStyle(row, col);
        updateStyle();
        setMouseTransparent(true); // Rendre la région non interactive
    }

    /**
     * Définit la couleur de la bordure avec la couleur spécifiée tout en conservant les largeurs des bordures.
     *
     * @param borderColor La couleur de la bordure au format hexadécimal (ex. "#00FF00").
     */
    public void setColoredBorder(String borderColor) {
        // Mettre à jour uniquement la couleur de la bordure en remplaçant "gray" par la nouvelle couleur
        currentBorderStyle = getBorderStyle(row, col).replace("gray", borderColor);
        updateStyle();
    }

    /**
     * Réinitialise la bordure de la région au style par défaut basé sur sa position.
     */
    public void resetBorderToDefault() {
        // Réinitialiser le style de bordure au style par défaut basé sur la position
        currentBorderStyle = getBorderStyle(row, col);
        updateStyle();
    }

    /**
     * Génère le style de bordure en fonction de la position de la région sur le plateau.
     *
     * @param row La ligne du secteur sur le plateau.
     * @param col La colonne du secteur sur le plateau.
     * @return Une chaîne de caractères représentant le style CSS de la bordure.
     */
    private String getBorderStyle(int row, int col) {
        // Déterminer les largeurs des bordures en fonction de la position
        String topWidth = (row == 0) ? "2px" : "1px";
        String rightWidth = (col == 2) ? "2px" : "1px";
        String bottomWidth = (row == 2) ? "2px" : "1px";
        String leftWidth = (col == 0) ? "2px" : "1px";

        // Combiner les largeurs des bordures dans une seule chaîne de style
        return String.format(
                "-fx-border-width: %s %s %s %s; -fx-border-color: gray;",
                topWidth, rightWidth, bottomWidth, leftWidth
        );
    }

    /**
     * Met à jour le style CSS de la région en combinant les styles de bordure et d'arrière-plan actuels.
     */
    private void updateStyle() {
        // Combiner les styles actuels pour la bordure et l'arrière-plan
        setStyle(currentBorderStyle + currentBackgroundStyle);
    }
}
