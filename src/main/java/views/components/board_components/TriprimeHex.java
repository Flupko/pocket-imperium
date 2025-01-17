package views.components.board_components;

import model.board.Hex;

/**
 * Classe concrète représentant le Tri-Prime sur le plateau de jeu.
 * Cette classe étend la classe abstraite {@link HexPolygon} et implémente
 * la méthode {@link #drawPolygon(double)} pour dessiner un hexagone Tri-Prime.
 */
public class TriprimeHex extends HexPolygon {

    /**
     * Constructeur de la classe TriprimeHex.
     * Initialise l'hexagone avec la taille spécifiée et le secteur lié.
     *
     * @param size       La taille de l'hexagone sur le UI.
     * @param linkedHex  L'instance de l'hexagone associé à ce composant graphique.
     */
    public TriprimeHex(double size, Hex linkedHex) {
        super(linkedHex, size);
        drawPolygon(size); // Dessine l'hexagone avec la taille spécifiée
    }

    /**
     * Méthode qui dessine la forme polygonale de l'hexagone Tri-Prime.
     * Cette méthode calcule les points des sommets spécifiques à l'hexagone Tri-Prime
     * et les ajoute au polygon {@link #hexagon}.
     *
     * @param size La taille de l'hexagone.
     */
    @Override
    protected void drawPolygon(double size) {
        // Calcul des dimensions nécessaires pour le dessin de l'hexagone Tri-Prime
        double hexWidth = size * Math.sqrt(3);
        double verticalSpacing = size * 1.5;
        double horizontalOffset = hexWidth / 2;

        // Ajout des points du premier quadrilatère
        for (int i = 0; i < 4; i++) {
            double angleDeg = 30 + (60 * (i + 1)); // Calcul de l'angle en degrés pour chaque sommet
            double angleRad = Math.toRadians(angleDeg); // Conversion de l'angle en radians
            double x = size * Math.cos(angleRad); // Calcul de la coordonnée X
            double y = size * Math.sin(angleRad); // Calcul de la coordonnée Y
            hexagon.getPoints().addAll(x, y); // Ajout du point au polygon
        }

        // Ajout des points du second quadrilatère
        for (int i = 0; i < 4; i++) {
            double angleDeg = 30 + (60 * (i + 3)); // Calcul de l'angle en degrés pour chaque sommet
            double angleRad = Math.toRadians(angleDeg); // Conversion de l'angle en radians
            double x = horizontalOffset + size * Math.cos(angleRad); // Calcul de la coordonnée X avec décalage horizontal
            double y = -verticalSpacing + size * Math.sin(angleRad); // Calcul de la coordonnée Y avec décalage vertical
            hexagon.getPoints().addAll(x, y); // Ajout du point au polygon
        }

        // Ajout des points du troisième quadrilatère
        for (int i = 0; i < 4; i++) {
            double angleDeg = 30 + (60 * (i + 4)); // Calcul de l'angle en degrés pour chaque sommet
            double angleRad = Math.toRadians(angleDeg); // Conversion de l'angle en radians
            double x = hexWidth + size * Math.cos(angleRad); // Calcul de la coordonnée X avec décalage horizontal supplémentaire
            double y = size * Math.sin(angleRad); // Calcul de la coordonnée Y
            hexagon.getPoints().addAll(x, y); // Ajout du point au polygon
        }

        // Ajout des points du dernier triangle
        for (int i = 0; i < 3; i++) {
            double angleDeg = 30 + (60 * i); // Calcul de l'angle en degrés pour chaque sommet
            double angleRad = Math.toRadians(angleDeg); // Conversion de l'angle en radians
            double x = horizontalOffset + size * Math.cos(angleRad); // Calcul de la coordonnée X avec décalage horizontal
            double y = verticalSpacing + size * Math.sin(angleRad); // Calcul de la coordonnée Y avec décalage vertical
            hexagon.getPoints().addAll(x, y); // Ajout du point au polygon
        }

        // Définir la couleur de remplissage par défaut
        hexagon.setFill(getDefaultFillColor());
    }
}
