package views.components.board_components;

import model.board.Hex;

/**
 * Classe concrète représentant un hexagone simple sur le plateau de jeu.
 * Cette classe étend la classe abstraite {@link HexPolygon} et implémente
 * la méthode {@link #drawPolygon(double)} pour dessiner un hexagone régulier.
 */
public class SimpleHex extends HexPolygon {

    /**
     * Constructeur de la classe SimpleHex.
     * Initialise l'hexagone avec la taille spécifiée et le l'hexagone lié.
     *
     * @param size       La taille de l'hexagone sur le UI.
     * @param linkedHex  L'instance de l'hexagone associé à ce composant graphique.
     */
    public SimpleHex(double size, Hex linkedHex) {
        super(linkedHex, size);
        drawPolygon(size); // Dessine l'hexagone avec la taille spécifiée
    }

    /**
     * Méthode qui dessine la forme polygonale de l'hexagone.
     * Cette méthode calcule les points des sommets d'un hexagone régulier centré en (0,0)
     * et les ajoute au polygon {@link #hexagon}.
     *
     * @param size La taille de l'hexagone.
     */
    @Override
    protected void drawPolygon(double size) {
        // Dessiner un hexagone centré à (0,0)
        for (int i = 0; i < 6; i++) {
            double angleDeg = 30 + 60 * i; // Calcul de l'angle en degrés pour chaque sommet
            double angleRad = Math.toRadians(angleDeg); // Conversion de l'angle en radians
            double x = size * Math.cos(angleRad); // Calcul de la coordonnée X
            double y = size * Math.sin(angleRad); // Calcul de la coordonnée Y
            hexagon.getPoints().addAll(x, y); // Ajout du point au polygon
        }

        // Définir la couleur de remplissage par défaut
        hexagon.setFill(getDefaultFillColor());
    }
}
