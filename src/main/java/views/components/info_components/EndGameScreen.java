package views.components.info_components;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.players.Player;
import views.components.generic.SpaceButton;

import java.util.HashMap;

/**
 * Composant graphique affichant l'écran de fin de partie.
 * Ce composant présente un message de fin de jeu, le gagnant, les scores de tous les joueurs,
 * et un bouton de fermeture revenir sur le plateau
 * Une animation de fondu est appliquée lors de l'affichage de cet écran.
 */
public class EndGameScreen extends VBox {

    /** Bouton permettant de fermer l'écran de fin de partie. */
    private final SpaceButton closeButton;

    /**
     * Constructeur de la classe EndGameScreen.
     * Initialise les composants graphiques, applique les styles CSS, et configure les animations.
     *
     * @param winner           Le joueur gagnant de la partie.
     * @param playersScoreMap  Une map contenant les scores de tous les joueurs, avec le nom du joueur comme clé.
     */
    public EndGameScreen(Player winner, HashMap<String, Integer> playersScoreMap) {
        // Application de la classe CSS pour l'écran de fin de jeu
        getStyleClass().add("end-game-screen");

        // Configuration de l'espacement, du padding et de l'alignement des éléments dans le VBox
        setSpacing(20);
        setPadding(new Insets(60, 100, 60, 100));
        setAlignment(Pos.CENTER);

        // Création et configuration du label "Game Over"
        Label gameOverText = new Label("Game Over");
        gameOverText.getStyleClass().add("end-game-title");

        // Création et configuration du label de félicitations au gagnant
        Label winnerText = new Label("Congratulations, " + winner.getName() + "!");
        winnerText.getStyleClass().add("end-game-winner");

        // Ajout des labels "Game Over" et "Congratulations" au VBox
        getChildren().addAll(gameOverText, winnerText);

        // Création et configuration du titre pour les scores des joueurs
        Label scoresTitle = new Label("Player Scores:");
        scoresTitle.getStyleClass().add("end-game-scores-title");
        getChildren().add(scoresTitle);

        // Itération sur la map des scores des joueurs pour afficher chaque score
        playersScoreMap.forEach((playerName, score) -> {
            Label playerScoreLabel = new Label(playerName + ": " + score);
            playerScoreLabel.getStyleClass().add("end-game-player-score");
            getChildren().add(playerScoreLabel);
        });

        // Création et configuration du bouton "Close" en bas de l'écran
        closeButton = new SpaceButton("Close", "#1a1a1a", "#ffffff", "#00c8ff");
        getChildren().add(closeButton); // Ajout du bouton au VBox

        // Création et configuration de l'animation de fondu enchaîné pour l'écran de fin de jeu
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), this);
        fadeIn.setFromValue(0); // Opacité initiale
        fadeIn.setToValue(1);   // Opacité finale
        fadeIn.play();          // Démarrage de l'animation
    }

    /**
     * Retourne le bouton de fermeture de l'écran de fin de partie.
     *
     * @return Le SpaceButton permettant de fermer l'écran.
     */
    public SpaceButton getCloseButton() {
        return closeButton;
    }
}
