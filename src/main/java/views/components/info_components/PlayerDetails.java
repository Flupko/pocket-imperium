package views.components.info_components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import model.Partie;
import model.commands.Expand;
import model.commands.Explore;
import model.commands.Exterminate;
import model.players.Player;
import model.states.PerformState;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant graphique affichant les détails d'un joueur, y compris son nom, son score,
 * et les commandes choisies pendant son tour. Ce composant écoute les changements de
 * propriété liés au joueur et à la partie pour mettre à jour son affichage en conséquence.
 */
public class PlayerDetails extends VBox implements PropertyChangeListener {

    /** Label indiquant le joueur actuel avec une flèche. */
    private final Label arrowLabel;

    /** Label affichant le nom du joueur. */
    private final Label nameLabel;

    /** Label affichant le score du joueur. */
    private final Label scoreLabel;

    /** Instance du joueur associé à ce composant. */
    private final Player player;

    /** Liste de StackPane représentant les points indiquant les commandes choisies. */
    private final List<StackPane> dots;

    /** Instance unique de la partie en cours. */
    private final Partie partie;

    /**
     * Constructeur de la classe PlayerDetails.
     * Initialise les composants graphiques et configure les écouteurs d'événements.
     *
     * @param player Le joueur dont les détails seront affichés.
     */
    public PlayerDetails(Player player) {
        this.player = player;
        this.dots = new ArrayList<>();
        this.partie = Partie.getInstance();

        // Ajout des écouteurs pour les changements de propriété liés au score et à l'état du jeu
        player.addPropertyChangeListener("SCORE_CHANGE", this);
        partie.addPropertyChangeListener("CURRENT_PLAYER_CHANGE", this);
        partie.addPropertyChangeListener("DISPLAY_CHOSEN_COMMAND", this);
        partie.addPropertyChangeListener("END_PERFORM", this);

        // Application des styles CSS définis pour ce composant
        getStyleClass().add("player-details-not-current");

        // Configuration de l'espacement et de l'alignement des éléments dans le VBox
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setMinWidth(150);
        setMaxWidth(150);

        // Création de la première ligne contenant les détails du joueur (flèche, ship, nom, score)
        HBox firstLine = new HBox();
        firstLine.setAlignment(Pos.CENTER);
        firstLine.setSpacing(10);

        // Initialisation du label de flèche indiquant le joueur actuel
        arrowLabel = new Label(" ");
        arrowLabel.getStyleClass().add("arrow-label");

        // Création et configuration de l'icône du ship en fonction de la couleur du joueur
        String playerColor = player.getColor().toString();
        Image shipImage = new Image("ship_images/ship_" + playerColor + ".png");
        ImageView shipIcon = new ImageView(shipImage);
        shipIcon.setFitWidth(15);
        shipIcon.setFitHeight(15);

        // Initialisation du label du nom du joueur
        nameLabel = new Label(player.getName());
        nameLabel.getStyleClass().add("player-name");

        // Initialisation du label du score du joueur et mise à jour du score initial
        scoreLabel = new Label(String.valueOf(player.getScore()));
        scoreLabel.getStyleClass().add("player-score");
        updateScore();

        // Création des sections gauche et droite de la première ligne
        HBox leftSection = new HBox(arrowLabel, shipIcon, nameLabel);
        leftSection.setAlignment(Pos.CENTER_LEFT);
        leftSection.setSpacing(5);

        HBox rightSection = new HBox(scoreLabel);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        // Ajout des sections gauche et droite à la première ligne
        firstLine.getChildren().addAll(leftSection, rightSection);
        HBox.setHgrow(leftSection, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(rightSection, javafx.scene.layout.Priority.ALWAYS);

        // Création de la deuxième ligne contenant les points et une ligne horizontale
        StackPane dotsWithLine = new StackPane();

        // Création et configuration de la ligne horizontale
        Line horizontalLine = new Line(0, 0, 120, 0);
        horizontalLine.getStyleClass().add("horizontal-line");

        // Création du conteneur pour les points
        HBox dotsContainer = new HBox(15);
        dotsContainer.setAlignment(Pos.CENTER);

        // Initialisation des trois points représentant les commandes choisies
        for (int i = 0; i < 3; i++) {
            StackPane dotWithContent = create3DDotWithContent();
            dots.add(dotWithContent);
            dotsContainer.getChildren().add(dotWithContent);
        }

        // Ajout de la ligne horizontale et des points au StackPane
        dotsWithLine.getChildren().addAll(horizontalLine, dotsContainer);
        dotsContainer.setTranslateY(0);

        // Ajout des deux lignes (détails et points) au VBox principal
        getChildren().addAll(firstLine, dotsWithLine);

        // Mise à jour de l'affichage pour indiquer le joueur actuel
        updateCurrentPlayer();
    }

    /**
     * Crée un point en 3D avec un contenu par défaut (un cercle).
     *
     * @return Le StackPane contenant le cercle représentant le point.
     */
    private StackPane create3DDotWithContent() {
        // Création du cercle représentant le point
        Circle dot = new Circle(8);
        dot.getStyleClass().add("dot");
        dot.setFill(Color.web("#4d4d4d"));

        // Création du StackPane et ajout du cercle
        StackPane dotWithContent = new StackPane();
        dotWithContent.getChildren().add(dot);

        return dotWithContent;
    }

    /**
     * Met à jour le score affiché du joueur.
     */
    public void updateScore() {
        scoreLabel.setText(String.valueOf(player.getScore()));
    }

    /**
     * Définit si ce joueur est le joueur actuel et met à jour l'affichage en conséquence.
     *
     * @param isCurrent True si c'est le joueur actuel, false sinon.
     */
    private void setCurrentPlayer(boolean isCurrent) {
        if (isCurrent) {
            // Indique que c'est le joueur actuel en affichant une flèche
            arrowLabel.setText(">");
            getStyleClass().clear();
            getStyleClass().add("player-details-current");
        } else {
            // Indique que ce n'est pas le joueur actuel en retirant la flèche
            arrowLabel.setText(" ");
            getStyleClass().clear();
            getStyleClass().add("player-details-not-current");
        }
    }

    /**
     * Met à jour l'affichage pour indiquer quel joueur est actuellement actif.
     */
    private void updateCurrentPlayer() {
        Player currentPlayer = partie.getCurrentPlayer();
        setCurrentPlayer(player.equals(currentPlayer));
    }

    /**
     * Affiche la commande choisie par le joueur dans les points correspondants.
     */
    private void displayChosenCommand() {
        // Vérifie si le state actuel du jeu est PerformState
        if (partie.getCurrentGameState() instanceof PerformState performState) {
            int performPhaseNum = performState.getPerformPhaseNum();
            int commandNumber = player.getCommandNumberForPhase(performPhaseNum);
            int commandEfficiency = performState.getCurPhaseCommandEfficiency(commandNumber);
            String color = "";

            // Détermine la couleur en fonction du numéro de commande
            switch (commandNumber) {
                case 1 -> color = Expand.COLOR;
                case 2 -> color = Explore.COLOR;
                case 3 -> color = Exterminate.COLOR;
                default -> color = "#4d4d4d"; // Couleur par défaut si le numéro de commande est invalide
            }

            // Définit le contenu du point correspondant à la commande choisie
            setDotContent(performPhaseNum, color, commandEfficiency);
        }
    }

    /**
     * Réinitialise les points en retirant les contenus et en rétablissant la couleur par défaut.
     */
    public void resetDots() {
        for (StackPane dotWithContent : dots) {
            // Récupère le cercle représentant le point
            Circle dot = (Circle) dotWithContent.getChildren().get(0);
            dot.setFill(Color.web("#4d4d4d")); // Réinitialise la couleur du point

            // Supprime tous les autres éléments sauf le cercle (comme les lignes verticales)
            dotWithContent.getChildren().removeIf(node -> !(node instanceof Circle));
        }
    }

    /**
     * Définit la couleur et le contenu (lignes verticales) d'un point spécifique.
     *
     * @param index    L'index du point (commence à 0).
     * @param hexColor La couleur en hexadécimal (par exemple, "#FF0000" pour rouge).
     * @param numLines Le nombre de lignes verticales à ajouter à l'intérieur du point.
     */
    public void setDotContent(int index, String hexColor, int numLines) {
        if (index >= 0 && index < dots.size()) {
            StackPane dotWithContent = dots.get(index);
            Circle dot = (Circle) dotWithContent.getChildren().get(0);

            try {
                // Applique la couleur spécifiée au point
                dot.setFill(Color.web(hexColor));
            } catch (IllegalArgumentException e) {
                System.err.println("Format de couleur invalide : " + hexColor);
            }

            // Supprime les rectangles existants pour éviter les doublons
            dotWithContent.getChildren().removeIf(node -> node instanceof Rectangle);

            // Ajoute les lignes verticales en fonction du nombre spécifié
            for (int i = 0; i < numLines; i++) {
                Rectangle line = new Rectangle(2, 6);
                line.setFill(Color.WHITE);
                line.setTranslateX((i - (numLines - 1) / 2.0) * 4); // Positionnement horizontal des lignes
                dotWithContent.getChildren().add(line);
            }
        }
    }

    /**
     * Méthode appelée lorsqu'un événement observé change.
     * Cette méthode gère les différents types d'événements liés au joueur et à la partie.
     *
     * @param evt L'événement de changement de propriété.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "SCORE_CHANGE" -> updateScore(); // Mise à jour du score
            case "CURRENT_PLAYER_CHANGE" -> updateCurrentPlayer(); // Mise à jour du joueur actuel
            case "DISPLAY_CHOSEN_COMMAND" -> displayChosenCommand(); // Affichage de la commande choisie
            case "END_PERFORM" -> resetDots(); // Réinitialisation des points après une performance
        }
    }

    /**
     * Retourne l'instance du joueur associé à ce composant.
     *
     * @return Le joueur.
     */
    public Player getPlayer() {
        return player;
    }
}
