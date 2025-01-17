package controllers.state_controllers;

import model.Partie;
import model.players.Player;
import views.BoardView;
import views.components.info_components.EndGameScreen;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Contrôleur dédié à la phase de fin de jeu (End Game) dans le jeu.
 * Gère l'affichage de l'écran de fin de jeu lorsqu'un joueur remporte la partie.
 * Ce contrôleur écoute l'événement "END_GAME" de la partie et, lorsqu'il est déclenché,
 * il affiche l'écran de fin de jeu avec le joueur gagnant et les scores de tous les joueurs.
 */
public class EndGameController implements PropertyChangeListener {

    /**
     * Vue principale du plateau de jeu.
     */
    private final BoardView boardView;

    /**
     * Instance unique de la partie en cours.
     */
    private final Partie partie;

    /**
     * Constructeur du contrôleur de fin de jeu.
     * Initialise les composants nécessaires et configure les écouteurs d'événements.
     *
     * @param boardView La vue principale du plateau de jeu.
     */
    public EndGameController(BoardView boardView) {
        this.boardView = boardView;
        this.partie = Partie.getInstance();
        // Écouteur pour l'événement de fin de jeu
        partie.addPropertyChangeListener("END_GAME", this);
    }

    /**
     * Gère les changements de propriétés observées.
     * Réagit spécifiquement à l'événement "END_GAME" pour afficher l'écran de fin de jeu.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object newValue = evt.getNewValue();
        if (evt.getPropertyName().equals("END_GAME")) {
            if (newValue instanceof Player winner) {
                displayWinner(winner);
            }
        }
    }

    /**
     * Affiche l'écran de fin de jeu avec le joueur gagnant et les scores de tous les joueurs.
     *
     * @param winner Le joueur qui a remporté la partie.
     */
    private void displayWinner(Player winner) {
        // Création d'une map des scores des joueurs
        HashMap<String, Integer> playersScores = new HashMap<>();
        for(Player player : partie.getPlayers()) {
            playersScores.put(player.getName(), player.getScore());
        }

        // Initialisation de l'écran de fin de jeu avec le gagnant et les scores
        EndGameScreen endGameScreen = new EndGameScreen(winner, playersScores);

        // Configuration de l'action du bouton "Close" pour masquer l'écran de fin de jeu
        endGameScreen.getCloseButton().setOnMouseClicked(event -> {
            endGameScreen.setVisible(false);
        });

        // Affichage de l'écran de fin de jeu dans la vue principale
        boardView.showEndGameScreen(endGameScreen);

        // Mise à jour des instructions pour indiquer que le jeu est terminé
        boardView.getInstructions().setInstructions("Game Over.");
    }
}
