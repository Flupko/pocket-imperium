package views.components.board_components;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.board.Hex;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Classe abstraite représentant un hexagone graphique sur le plateau de jeu.
 * Cette classe gère l'affichage des informations liées à l'hexagone, telles que le niveau,
 * le nombre de ships présents, le contrôleur de l'hexagone, ainsi que les effets visuels.
 * Elle écoute les mises à jour de l'hexagone pour rafraîchir l'affichage en conséquence.
 */
public abstract class HexPolygon extends StackPane implements PropertyChangeListener {

    /** Instance de l'hexagone associé à ce graphique. */
    protected final Hex hex;

    /** Texte affichant le niveau de l'hexagone ("I", "II", "III"). */
    protected final Text levelText = new Text();

    /** Polygon représentant la forme de l'hexagone. */
    protected final Polygon hexagon = new Polygon();

    /** Conteneur pour afficher les ships présents sur l'hexagone. */
    protected final StackPane shipsPane = new StackPane();

    /** Conteneur pour afficher les ships en vol au-dessus de l'hexagone. */
    protected final StackPane flyingShipPane = new StackPane();

    /** ImageView pour afficher les étincelles lors d'une invasion. */
    private ImageView invadeSparksImageView = null;

    /** Taille de l'hexagone graphique. */
    protected double size;

    /** Indique si l'hexagone est actuellement sélectionné. */
    private boolean isSelected = false;

    /**
     * Constructeur de la classe HexPolygon.
     * Initialise les composants graphiques, applique les styles CSS, et configure les écouteurs d'événements.
     *
     * @param hex  L'instance de l'hexagone associé à ce composant graphique.
     * @param size La taille de l'hexagone sur le UI.
     */
    public HexPolygon(Hex hex, double size) {
        this.hex = hex;
        this.size = size;

        // Ajout de cet objet en tant qu'écouteur des mises à jour de l'hexagone
        hex.addPropertyChangeListener("HEX_UPDATE", this);

        // Configuration de la forme de l'hexagone
        drawPolygon(size);

        // Définir la forme du StackPane à celle de l'hexagone
        setPickOnBounds(false);
        setShape(hexagon);

        // Effets de survol pour l'hexagone
        hexagon.setOnMouseEntered(event -> {
            if (!isSelected) {
                hexagon.setFill(getDefaultFillColor().brighter());
            }
        });
        hexagon.setOnMouseExited(event -> {
            if (!isSelected) {
                hexagon.setFill(getDefaultFillColor());
            }
        });

        // Définir le contour et la couleur de remplissage par défaut
        setDefaultStroke();
        hexagon.setFill(getDefaultFillColor());

        // Configuration du texte affichant le niveau de l'hexagone
        levelText.setFont(new Font("Orbitron", size / 3));
        levelText.setFill(Color.web("#B0C4DE")); // LightSteelBlue pour un texte futuriste

        // Configuration des conteneurs pour les ships et les ships en vol
        shipsPane.setAlignment(Pos.CENTER);
        shipsPane.setMouseTransparent(true);
        flyingShipPane.setAlignment(Pos.CENTER);
        flyingShipPane.setMouseTransparent(true);

        // Mise à jour initiale des informations de l'hexagone
        updateLevelHex();
        updateHexDetails();

        // Configuration de l'effet d'étincelles lors d'une invasion
        Image invadeSparksImage = new Image("spark_effect.png");
        invadeSparksImageView = new ImageView(invadeSparksImage);
        invadeSparksImageView.setFitWidth(size * 1.8);
        invadeSparksImageView.setFitHeight(size * 1.8);
        invadeSparksImageView.setMouseTransparent(true);
        invadeSparksImageView.setOpacity(0.9);
        hideInvadeSparks(); // Masque les étincelles par défaut

        // Ajout des éléments graphiques au StackPane
        setAlignment(Pos.CENTER);
        getChildren().addAll(hexagon, invadeSparksImageView, shipsPane, flyingShipPane, levelText);
    }

    /**
     * Méthode abstraite pour dessiner la forme polygonale de l'hexagone.
     * Doit être implémentée par les classes concrètes.
     *
     * @param size La taille de l'hexagone.
     */
    protected abstract void drawPolygon(double size);

    /**
     * Met à jour le texte affichant le niveau de l'hexagone en chiffres romains.
     */
    protected void updateLevelHex() {
        String romanNumeral = switch (hex.getLevel()) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> "";
        };
        levelText.setText(romanNumeral);
    }

    /**
     * Met à jour les détails affichés de l'hexagone, tels que le nombre de ships et le contrôleur.
     */
    protected void updateHexDetails() {
        // Efface les ships précédemment affichés
        shipsPane.getChildren().clear();

        // Si l'hexagone n'est pas occupé, ne rien afficher
        if (!hex.isOccupied()) {
            return;
        }

        int numShips = hex.getShipsCount();
        String color = hex.getController().getColor().toString();
        double radius = size / 1.7;

        // Ajout des icônes de ships disposées en cercle autour de l'hexagone
        for (int i = 0; i < numShips; i++) {
            ImageView shipIcon = createShipIcon(color);
            double angle = (2 * Math.PI * i) / numShips;
            double offsetX = Math.cos(angle) * radius;
            double offsetY = Math.sin(angle) * radius;

            shipIcon.setTranslateX(offsetX);
            shipIcon.setTranslateY(offsetY);
            shipIcon.setRotate(Math.toDegrees(angle) + 90);

            shipsPane.getChildren().add(shipIcon);
        }
    }

    /**
     * Crée une icône de ship avec la couleur spécifiée.
     *
     * @param color La couleur du ship au format hexadécimal.
     * @return Un ImageView représentant l'icône du ship.
     */
    private ImageView createShipIcon(String color) {
        Image shipImage = new Image("ship_images/ship_" + color + ".png");
        ImageView shipIcon = new ImageView(shipImage);
        shipIcon.setFitWidth(size / 2.7);
        shipIcon.setFitHeight(size / 2.7);
        return shipIcon;
    }

    /**
     * Ajoute des ships en vol au-dessus de l'hexagone.
     *
     * @param numFlyingShips Le nombre de ships en vol à ajouter.
     * @param color          La couleur des ships en vol au format hexadécimal.
     * @param angle          L'angle de rotation des ships en vol.
     */
    public void addFlyingShips(int numFlyingShips, String color, double angle) {
        // Efface les ships en vol précédemment affichés
        flyingShipPane.getChildren().clear();
        int gridCols = (int) Math.ceil(Math.sqrt(numFlyingShips));
        int gridRows = (int) Math.ceil((double) numFlyingShips / gridCols);
        double gridSpacingX = size / 4.0;
        double gridSpacingY = size / 4.0;
        double startX = -(gridCols - 1) * gridSpacingX / 2;
        double startY = -(gridRows - 1) * gridSpacingY / 2;

        // Ajout des icônes de ships en vol disposées en grille
        for (int i = 0; i < numFlyingShips; i++) {
            ImageView shipIcon = createShipIcon(color);
            int row = i / gridCols;
            int col = i % gridCols;

            double offsetX = startX + col * gridSpacingX;
            double offsetY = startY + row * gridSpacingY;

            shipIcon.setRotate(angle);
            shipIcon.setTranslateX(offsetX);
            shipIcon.setTranslateY(offsetY);

            flyingShipPane.getChildren().add(shipIcon);
        }
    }

    /**
     * Supprime tous les ships en vol affichés au-dessus de l'hexagone.
     */
    private void removeFlyingShips() {
        flyingShipPane.getChildren().clear();
    }

    /**
     * Affiche les étincelles d'invasion sur l'hexagone.
     */
    public void showInvadeSparks() {
        invadeSparksImageView.setVisible(true);
    }

    /**
     * Masque les étincelles d'invasion sur l'hexagone.
     */
    public void hideInvadeSparks() {
        invadeSparksImageView.setVisible(false);
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Cette méthode écoute les mises à jour de l'hexagone et rafraîchit les détails affichés en conséquence.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("HEX_UPDATE".equals(evt.getPropertyName())) {
            // Si l'hexagone a été mis à jour, rafraîchit les détails affichés
            updateHexDetails();
        }
    }

    /**
     * Surligne l'hexagone avec la couleur de surlignage spécifiée.
     *
     * @param highlightColor La couleur de surlignage au format hexadécimal.
     */
    public void highlightHex(String highlightColor) {
        // Définir la couleur et l'épaisseur du contour de l'hexagone
        hexagon.setStroke(Color.web(highlightColor));
        hexagon.setStrokeWidth(2);

        // Créer et appliquer un effet de DropShadow avec la couleur de surlignage
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web(highlightColor)); // Utiliser la couleur de surlignage pour l'ombre
        dropShadow.setSpread(0.3); // Augmenter la concentration de l'ombre sur les bords
        dropShadow.setRadius(10); // Rayon de diffusion de l'ombre

        // Appliquer l'effet de DropShadow à l'hexagone
        hexagon.setEffect(dropShadow);
    }

    /**
     * Réinitialise les effets visuels appliqués à l'hexagone.
     * Cela inclut la réinitialisation du contour, la suppression des ships en vol et le masquage des étincelles.
     */
    public void resetEffets() {
        setDefaultStroke(); // Réinitialiser le contour par défaut
        hexagon.setEffect(null); // Supprimer tous les effets appliqués
        removeFlyingShips(); // Supprimer les ships en vol
        hideInvadeSparks(); // Masquer les étincelles d'invasion
    }

    /**
     * Sélectionne l'hexagone en modifiant sa couleur de remplissage.
     */
    public void selectHex() {
        isSelected = true;
        hexagon.setFill(getDefaultFillColor().brighter()); // Modifier la couleur de remplissage pour indiquer la sélection
    }

    /**
     * Désélectionne l'hexagone en rétablissant sa couleur de remplissage par défaut.
     */
    public void unselectHex() {
        isSelected = false;
        hexagon.setFill(getDefaultFillColor()); // Réinitialiser la couleur de remplissage
    }

    /**
     * Retourne la couleur de remplissage par défaut de l'hexagone en fonction de son niveau.
     *
     * @return La couleur de remplissage par défaut.
     */
    protected Color getDefaultFillColor() {
        return switch (hex.getLevel()) {
            case 1 -> Color.web("#7F404D"); // Rose foncé atténué
            case 2 -> Color.web("#A66F33"); // Orange foncé atténué
            case 3 -> Color.web("#3A7F6B"); // Cyan-vert foncé atténué
            default -> Color.web("#444444"); // Gris plus foncé par défaut
        };
    }

    /**
     * Définit le contour de l'hexagone avec une couleur grise transparente par défaut.
     */
    protected void setDefaultStroke() {
        hexagon.setStroke(Color.web("rgba(200, 200, 200, 0.5)")); // Gris transparent
        hexagon.setStrokeWidth(2); // Épaisseur du contour
    }
}
