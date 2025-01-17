package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.Partie;
import model.board.Board;
import model.board.Hex;
import model.board.Sector;
import views.components.action_components.ActionMenu;
import views.components.action_components.PlanCommandsPanel;
import views.components.action_components.SettingsMenu;
import views.components.board_components.*;
import views.components.generic.SpaceButton;
import views.components.info_components.*;

import java.util.*;

/**
 * Vue principale du plateau de jeu.
 * Cette classe gère l'affichage du plateau hexagonal, des différentes couches d'information,
 * ainsi que les composants interactifs tels que les menus et les boutons.
 */
public class BoardView {
    /** Scène associée à la vue du plateau de jeu. */
    private final Scene scene;

    /** Conteneur racine pour tous les éléments de la vue. */
    private final StackPane root;

    /** Composant affichant les instructions du jeu. */
    private final Instructions instructions;

    /** Liste des régions de secteurs superposées sur le plateau. */
    private final List<SectorRegion> overlayRectangles = new ArrayList<>();

    /** Pane contenant les hexagones du plateau. */
    private final Pane hexPane;

    /** Couche principale contenant les hexagones. */
    private Pane mainHexLayer;

    /** Tableau de score affichant les scores des joueurs. */
    private final ScoreBoard scoreBoard = new ScoreBoard(Partie.getInstance().getPlayers());

    /** Panneau de commandes pour les plans d'action. */
    private final PlanCommandsPanel planCommandsPanel;

    /** Détails de l'hexagone actuellement sélectionné. */
    private final HexDetails hexDetails;

    /** Menu d'action permettant de réaliser différentes actions sur le plateau. */
    private final ActionMenu actionMenu;

    /** Bouton pour terminer le tour en cours. */
    private final Button finishButton;

    /** Bouton pour accéder aux paramètres du jeu. */
    private final Button settingsButton;

    /** Menu des paramètres du jeu. */
    private final SettingsMenu settingsMenu;

    /** Texte indiquant le tour actuel. */
    private final TurnText turnText;

    /** Mapping entre les modèles d'hexagones et leurs représentations graphiques. */
    private final HashMap<Hex, HexPolygon> hexToPolygonMap = new HashMap<>();

    /** Mapping entre les secteurs et leurs régions graphiques. */
    private final HashMap<Sector, SectorRegion> sectorToRegionMap = new HashMap<>();

    /** Taille d'un hexagone (rayon). */
    private static final double HEX_SIZE = 40;

    /** Nombre de rangées sur le plateau. */
    private static final int ROWS = 9;

    /** Nombre maximum de colonnes sur les rangées paires. */
    private static final int MAX_COLUMNS_EVEN = 6;

    /** Nombre maximum de colonnes sur les rangées impaires. */
    private static final int MAX_COLUMNS_ODD = 5;

    /**
     * Constructeur de la classe BoardView.
     * Initialise et configure tous les composants de la vue du plateau de jeu.
     */
    public BoardView() {
        // Initialisation du conteneur racine avec un fond spatial
        root = new StackPane();
        root.getStyleClass().add("space-background");

        // Contenu principal du jeu (instructions + plateau)
        VBox gameContent = new VBox();
        gameContent.setAlignment(Pos.TOP_CENTER);

        // Initialisation du composant des instructions
        instructions = new Instructions(800);
        VBox.setMargin(instructions, new Insets(20, 0, 20, 0)); // Ajout d'une marge en haut et en bas
        gameContent.getChildren().add(instructions); // Ajout des instructions au contenu principal

        // Configuration du plateau hexagonal
        StackPane boardContainer = new StackPane();
        boardContainer.setAlignment(Pos.CENTER);
        hexPane = createHexBoardPane(); // Création du pane des hexagones
        double boardWidth = hexPane.getPrefWidth();
        double boardHeight = hexPane.getPrefHeight();

        // Ajout du pane des hexagones au conteneur du plateau
        boardContainer.getChildren().add(hexPane);
        createOverlayRegions(boardWidth, boardHeight); // Création des régions superposées
        putRectanglesOnBottom(); // Envoi des rectangles en arrière-plan

        gameContent.getChildren().add(boardContainer); // Ajout du conteneur du plateau au contenu principal

        // Ajout du contenu principal au conteneur racine
        root.getChildren().add(gameContent);

        // Configuration et ajout du bouton des paramètres (icône)
        settingsButton = new Button();
        ImageView settingsIcon = new ImageView(new Image("./settings_icon.png")); // Remplacer par le chemin de votre icône
        settingsIcon.setFitWidth(24);
        settingsIcon.setFitHeight(24);
        settingsButton.setGraphic(settingsIcon);
        settingsButton.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(settingsButton, Pos.TOP_RIGHT);
        root.getChildren().add(settingsButton);

        // Configuration et ajout du tableau de score à gauche, centré verticalement
        StackPane.setAlignment(scoreBoard, Pos.CENTER_LEFT);
        StackPane.setMargin(scoreBoard, new Insets(0, 0, 0, 10));
        root.getChildren().add(scoreBoard);

        // Initialisation et ajout du panneau des commandes de plan
        planCommandsPanel = new PlanCommandsPanel(
                "cards_images/expand_card.jpg",
                "cards_images/explore_card.jpg",
                "cards_images/exterminate_card.jpg"
        );
        StackPane.setAlignment(planCommandsPanel, Pos.CENTER);

        // Initialisation et ajout des détails de l'hexagone en bas à gauche
        hexDetails = new HexDetails();
        StackPane.setAlignment(hexDetails, Pos.BOTTOM_LEFT);
        root.getChildren().add(hexDetails);

        // Initialisation et ajout du menu d'action à droite, avec une marge
        actionMenu = new ActionMenu();
        StackPane.setAlignment(actionMenu, Pos.CENTER_RIGHT);
        StackPane.setMargin(actionMenu, new Insets(0, 10, 0, 0));
        root.getChildren().add(actionMenu);

        // Initialisation et ajout du texte de tour en haut à droite avec une marge
        turnText = new TurnText();
        StackPane.setAlignment(turnText, Pos.TOP_RIGHT);
        StackPane.setMargin(turnText, new Insets(100, 10, 20, 0));
        root.getChildren().add(turnText);

        // Initialisation et configuration du bouton de fin de tour, caché par défaut
        finishButton = new SpaceButton("Finish", "#2a2a2a", "#ffffff", "#00c8ff");
        StackPane.setAlignment(finishButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(finishButton, new Insets(0, 8, 150, 0));
        finishButton.setVisible(false);
        root.getChildren().add(finishButton);

        // Initialisation et ajout du menu des paramètres
        settingsMenu = new SettingsMenu();
        StackPane.setAlignment(settingsMenu, Pos.TOP_CENTER);
        root.getChildren().add(settingsMenu);

        // Création de la scène avec une taille fixe et ajout des feuilles de style
        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add("styles/style.css");
        scene.getStylesheets().add("styles/Board.css");
    }

    /**
     * Retourne la scène associée à la vue du plateau de jeu.
     *
     * @return La scène de la vue du plateau.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Crée et configure le pane contenant les hexagones du plateau.
     *
     * @return Le pane configuré avec les hexagones.
     */
    private Pane createHexBoardPane() {
        double hexWidth = HEX_SIZE * Math.sqrt(3);
        double verticalSpacing = HEX_SIZE * 1.5;
        double horizontalOffset = hexWidth / 2;

        double boardWidth = MAX_COLUMNS_EVEN * hexWidth;
        double boardHeight = (ROWS - 1) * verticalSpacing + HEX_SIZE * 2;

        mainHexLayer = new Pane(); // Couche principale des hexagones
        Pane boardPane = new Pane();

        boardPane.getChildren().addAll(mainHexLayer);
        boardPane.setPrefSize(boardWidth, boardHeight);
        boardPane.setMaxSize(boardWidth, boardHeight);
        boardPane.setMinSize(boardWidth, boardHeight);

        // Coordonnées des hexagones tri-primes à exclure du placement standard
        Set<String> triPrimeHexesCoords = new HashSet<>();
        triPrimeHexesCoords.add("3,2");
        triPrimeHexesCoords.add("4,2");
        triPrimeHexesCoords.add("4,3");
        triPrimeHexesCoords.add("5,2");

        Board board = Partie.getInstance().getBoard(); // Récupération du plateau de jeu

        // Boucle pour créer et placer les hexagones standards
        for (int row = 0; row < ROWS; row++) {
            int columns = (row % 2 == 0) ? MAX_COLUMNS_EVEN : MAX_COLUMNS_ODD;
            for (int col = 0; col < columns; col++) {

                // Exclusion des hexagones tri-primes
                if (triPrimeHexesCoords.contains(row + "," + col)) {
                    continue;
                }

                Hex hexModel = board.getHex(row, col); // Récupération du modèle d'hexagone

                double x = col * hexWidth + ((row % 2 != 0) ? horizontalOffset : 0);
                double y = row * verticalSpacing;

                // Création et positionnement de l'hexagone graphique
                HexPolygon hexagon = new SimpleHex(HEX_SIZE, hexModel);
                hexagon.setLayoutX(x);
                hexagon.setLayoutY(y);

                mainHexLayer.getChildren().add(hexagon); // Ajout à la couche principale
                hexToPolygonMap.put(hexModel, hexagon); // Mise à jour du mapping
            }
        }

        // Création et placement de l'hexagone tri-prime
        Hex triPrimeModel = board.getHex(4, 2);
        HexPolygon triPrime = new TriprimeHex(HEX_SIZE, triPrimeModel);
        triPrime.setLayoutX(2 * hexWidth);
        triPrime.setLayoutY(3 * verticalSpacing);
        mainHexLayer.getChildren().add(triPrime);
        hexToPolygonMap.put(triPrimeModel, triPrime);

        return boardPane;
    }

    /**
     * Retourne le mapping entre les modèles d'hexagones et leurs représentations graphiques.
     *
     * @return Le HashMap contenant le mapping Hex -> HexPolygon.
     */
    public HashMap<Hex, HexPolygon> getHexToPolygonMap() {
        return hexToPolygonMap;
    }

    /**
     * Crée les régions superposées sur le plateau pour chaque secteur.
     *
     * @param boardWidth  La largeur du plateau.
     * @param boardHeight La hauteur du plateau.
     */
    private void createOverlayRegions(double boardWidth, double boardHeight) {
        Sector[] sectors = Partie.getInstance().getBoard().getSectors();

        double rectWidth = boardWidth / 3;
        double rectHeight = boardHeight / 3;

        // Boucle pour créer les régions de secteurs
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                Sector linkedSector = sectors[i * 3 + j];
                SectorRegion sectorRegion = new SectorRegion(i, j, linkedSector);
                sectorToRegionMap.put(linkedSector, sectorRegion);

                // Positionnement et taille de la région
                sectorRegion.setLayoutX(j * rectWidth);
                sectorRegion.setLayoutY(i * rectHeight);
                sectorRegion.setPrefSize(rectWidth, rectHeight);
                sectorRegion.setMinSize(rectWidth, rectHeight);
                sectorRegion.setMaxSize(rectWidth, rectHeight);

                sectorRegion.makeTransparent(); // Rendre la région transparente
                overlayRectangles.add(sectorRegion);
                hexPane.getChildren().add(sectorRegion); // Ajout direct au pane des hexagones
            }
        }
    }

    /**
     * Retourne le mapping entre les secteurs et leurs régions graphiques.
     *
     * @return Le HashMap contenant le mapping Sector -> SectorRegion.
     */
    public HashMap<Sector, SectorRegion> getSectorToRegionMap() {
        return this.sectorToRegionMap;
    }

    /**
     * Retourne la région graphique correspondant à un index donné.
     *
     * @param index L'index de la région dans la liste des régions superposées.
     * @return La région graphique correspondante.
     */
    public Region getRectangle(int index) {
        return overlayRectangles.get(index);
    }

    /**
     * Réinitialise les surlignages des régions superposées en les rendant transparentes.
     */
    public void resetHighlightsRectangles() {
        for (SectorRegion region : overlayRectangles) {
            region.makeTransparent();
        }
    }

    /**
     * Envoie les rectangles des régions superposées en arrière-plan pour ne pas interférer avec les interactions.
     */
    public void putRectanglesOnBottom() {
        for (Region rectangle : overlayRectangles) {
            rectangle.setMouseTransparent(true);
        }
    }

    /**
     * Surligne un hexagone spécifique avec une couleur donnée.
     *
     * @param hexagon L'hexagone à surligner.
     * @param color   La couleur de surlignage.
     */
    public void highlightHex(HexPolygon hexagon, String color) {
        hexagon.toFront(); // Met l'hexagone au premier plan
        hexagon.highlightHex(color); // Applique le surlignage
    }

    /**
     * Retourne le composant des instructions.
     *
     * @return Le composant Instructions.
     */
    public Instructions getInstructions() {
        return instructions;
    }

    /**
     * Retourne le panneau des commandes de plan.
     *
     * @return Le PlanCommandsPanel.
     */
    public PlanCommandsPanel getPlanCommandsPanel() {
        return planCommandsPanel;
    }

    /**
     * Affiche le panneau des commandes de plan sur la scène.
     */
    public void showPlanCommandsPanel() {
        root.getChildren().add(planCommandsPanel);
    }

    /**
     * Masque le panneau des commandes de plan de la scène.
     */
    public void hidePlanCommandsPanel() {
        root.getChildren().remove(planCommandsPanel);
    }

    /**
     * Affiche l'écran de fin de partie sur la scène.
     *
     * @param endGameScreen L'écran de fin de partie à afficher.
     */
    public void showEndGameScreen(EndGameScreen endGameScreen) {
        root.getChildren().add(endGameScreen);
    }

    /**
     * Retourne le composant affichant les détails de l'hexagone sélectionné.
     *
     * @return Le composant HexDetails.
     */
    public HexDetails getHexDetails() {
        return hexDetails;
    }

    /**
     * Retourne le menu d'action.
     *
     * @return L'ActionMenu.
     */
    public ActionMenu getActionMenu() {
        return actionMenu;
    }

    /**
     * Retourne le bouton de fin de tour.
     *
     * @return Le bouton Finish.
     */
    public Button getFinishButton() {
        return finishButton;
    }

    /**
     * Retourne le menu des paramètres.
     *
     * @return Le SettingsMenu.
     */
    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

    /**
     * Retourne le bouton des paramètres.
     *
     * @return Le bouton Settings.
     */
    public Button getSettingsButton() {
        return settingsButton;
    }
}
