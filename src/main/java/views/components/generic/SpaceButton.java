package views.components.generic;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

/**
 * Classe représentant un bouton paramétrable stylisé avec un thème spatial.
 * Ce bouton applique des styles CSS spécifiques
 * Il permet de configurer dynamiquement les couleurs de fond, de police et de bordure.
 */
public class SpaceButton extends Button {

    /**
     * Constructeur de la classe SpaceButton.
     * Initialise le bouton avec le texte spécifié et applique les styles et effets visuels.
     *
     * @param text               Le texte à afficher sur le bouton.
     * @param backgroundColorHex La couleur de fond du bouton au format hexadécimal
     * @param fontColorHex       La couleur de la police du texte au format hexadécimal.
     * @param borderColorHex     La couleur de la bordure du bouton au format hexadécimal.
     */
    public SpaceButton(String text, String backgroundColorHex, String fontColorHex, String borderColorHex) {
        super(text); // Définir le texte du bouton

        // Définit la famille de polices et la taille du texte
        setStyle("-fx-font-family: 'Orbitron Bold'; -fx-font-size: 14px;");

        // Définit la couleur du texte
        setTextFill(Color.web(fontColorHex));

        // Crée une couleur de fond lisse avec des coins arrondis
        setBackground(new Background(new BackgroundFill(
                Color.web(backgroundColorHex),
                new CornerRadii(8), Insets.EMPTY
        )));

        // Calcule la couleur de bordure plus claire pour l'effet de survol
        Color startColor = Color.web(borderColorHex);
        Color hoverColor = startColor.brighter(); // Version plus claire de la couleur originale

        // Définit la couleur de bordure initiale et d'autres styles
        setStyle(getStyle() +
                " -fx-border-color: " + toHex(startColor) + ";" +
                " -fx-border-width: 2;" +
                " -fx-border-radius: 8;" +
                " -fx-padding: 10 25;" + // Padding ajusté pour une meilleure apparence
                " -fx-cursor: hand;" // Curseur interactif
        );

        // Ajoute une ombre portée subtile pour l'effet de lueur
        setEffect(new DropShadow(8, startColor)); // Effet de lueur par défaut

        // Ajoute un effet de survol
        setOnMouseEntered(event -> {
            // Éclaircir la couleur de la bordure au survol
            setStyle(getStyle().replace(toHex(startColor), toHex(hoverColor)));
            setEffect(new DropShadow(15, hoverColor)); // Renforcer l'effet de lueur
        });

        setOnMouseExited(event -> {
            // Réinitialise la couleur de la bordure lors de la sortie du curseur
            setStyle(getStyle().replace(toHex(hoverColor), toHex(startColor)));
            setEffect(new DropShadow(8, startColor)); // Réinitialiser l'effet de lueur
        });
    }

    /**
     * Convertit un objet Color de JavaFX en sa représentation HEX.
     *
     * @param color L'objet Color à convertir.
     * @return La chaîne de caractères représentant la couleur au format HEX (ex. "#FF0000").
     */
    private String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
}
