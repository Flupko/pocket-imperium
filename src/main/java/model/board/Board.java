package model.board;

import model.players.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Représente le plateau de jeu, contenant la grille d'hexagones et les secteurs.
 * Cette classe génère et organise l'ensemble des hexagones ainsi qu'un ensemble de
 * 9 secteurs (dont un central), chacun contenant un/plusieurs systèmes hexagonaux.
 * Ces systèmes sont placées de façon aléatoire selon les cartes données dans les règles.
 * Elle gère également l'hexagone Tri-Prime (niveau 3), placé au centre de la grille.
 */
public class Board implements Serializable {

    /** Identifiant de sérialisation pour assurer la compatibilité lors de la désérialisation. */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Tableau de 9 secteurs, organisé en 3x3.
     * L'indice 4 correspond au secteur central (celui contenant le Tri-Prime).
     */
    private final Sector[] sectors = new Sector[9];

    /**
     * Grille de 9 colonnes (indice x), chacune ayant 5 ou 6 rangées (indice y)
     * selon la parité de x. Les hexagones y sont stockés dans un tableau.
     */
    private final Hex[][] grid = new Hex[9][];

    /**
     * Association entre chaque hexagone système (niveau I, II ou III)
     * et le secteur auquel il appartient.
     */
    private final Map<Hex, Sector> systemToSectorMap = new HashMap<>();

    /**
     * Liste de tous les hexagones qui correspondent à des systèmes
     * (qu'ils soient de niveau I, II ou III).
     */
    private final List<Hex> systems = new ArrayList<>();

    /**
     * Hexagone représentant le Tri-Prime (niveau 3),
     * situé au centre de la grille et fusionnant plusieurs hexagones centraux.
     */
    private Hex triPrimeHex;

    /**
     * Coordonnée X de l'hexagone Tri-Prime.
     */
    private static final int TRI_PRIME_X = 4;

    /**
     * Coordonnée Y de l'hexagone Tri-Prime.
     */
    private static final int TRI_PRIME_Y = 2;

    /**
     * Nombre total de lignes
     */
    private static final int GRID_SIZE_X = 9;

    /**
     * Nombre de colonnes pour les lignes impaires,
     * ajusté de +1 pour les colonnes paires.
     */
    private static final int GRID_SIZE_Y = 5;


    /**
     * Constructeur de la classe Board.
     * Initialise la grille d'hexagones, positionne le Tri-Prime au centre,
     * crée les secteurs et les systèmes.
     */
    public Board() {
        generateGrid();
    }

    /**
     * Génère la grille d'hexagones et assigne les voisins.
     * Puis configure l'hexagone Tri-Prime en supprimant les hexagones centraux inutiles,
     * et crée les secteurs (dont le secteur central) avec leurs systèmes.
     */
    private void generateGrid() {
        // Offsets obtenir les voisins d'un hexagone selon la parité de sa ligne
        int[][][] offsets = {
                // Lignes paires
                {
                        {0, +1}, {0, -1}, {+1, 0}, {-1, 0}, {+1, -1}, {-1, -1}
                },
                // Lignes impaires
                {
                        {0, +1}, {0, -1}, {+1, 0}, {-1, 0}, {+1, +1}, {-1, +1}
                }
        };

        // Étape 1 : Initialiser la grille principale
        for (int x = 0; x < GRID_SIZE_X; x++) {
            int rows_size = GRID_SIZE_Y + ((x & 1) ^ 1); // Détermine la parité et l'inverse
            grid[x] = new Hex[rows_size]; // Initialise le tableau pour cette ligne

            for (int y = 0; y < rows_size; y++) {
                grid[x][y] = new Hex(x, y); // Initialise les hexagones à cette position
            }
        }

        for (int x = 0; x < GRID_SIZE_X; x++) {
            // La taille de la ligne (nb colonnes) dépend de sa parité
            int rows_size = GRID_SIZE_Y + ((x & 1) ^ 1); // Détermine la parité et l'inverse

            for (int y = 0; y < rows_size; y++) {

                // Détermine la parité de la ligne
                int parity = x & 1;

                // Ajoute les voisins pour cet hexagone
                for (int[] offset : offsets[parity]) {
                    int x_offset = offset[0];
                    int y_offset = offset[1];

                    // Calcule les coordonnées du voisin
                    int neighborX = x + x_offset;
                    int neighborY = y + y_offset;

                    // Vérifie si les coordonnées du voisin sont valides
                    if (neighborX >= 0 && neighborX < 9 &&
                            neighborY >= 0 && neighborY < (5 + ((neighborX & 1) ^ 1))) {

                        // Ajoute le voisin
                        grid[x][y].addNeighbor(grid[neighborX][neighborY]);
                    }
                }
            }
        }

        // Crée le Tri-Prime

        int[] triPrimeCoord = {TRI_PRIME_X, TRI_PRIME_Y};
        int[][] triPrimeHexesCoords = {{3, 2}, {4, 2}, {4, 3}, {5, 2}};

        // Récupère l'hexagone qui représente le Tri-Prime
        this.triPrimeHex = grid[triPrimeCoord[0]][triPrimeCoord[1]];
        triPrimeHex.setSystem(3); // System of level 3
        systems.add(triPrimeHex);

        // Ensemble des hexagones centraux à fusionner en un seul Tri-Prime
        Set<Hex> triPrimeHexes = Arrays.stream(triPrimeHexesCoords)
                .map(coord -> grid[coord[0]][coord[1]])
                .collect(Collectors.toSet());

        // Collecte des voisins uniques
        Set<Hex> uniqueNeighbors = new HashSet<>();
        for (Hex centralHex : triPrimeHexes) {
            uniqueNeighbors.addAll(centralHex.getNeighbors());
        }

        // Enlève les hexagones centraux supprimés de la liste des voisins du Tri-Prime
        uniqueNeighbors.removeAll(triPrimeHexes);
        triPrimeHex.getNeighbors().removeAll(triPrimeHexes);

        // Enlève les hexagones centraux fusionnés de voisins du Tri-Prime
        // Et ajoute la nouvelle "unité" que constitue le Tri-Prime

        for (Hex neighborHex : uniqueNeighbors) {
            neighborHex.getNeighbors().removeAll(triPrimeHexes);
            neighborHex.getNeighbors().add(triPrimeHex);
        }

        // Supprimer les hexagones centraux fusionnés de la grille
        for (int[] coords : triPrimeHexesCoords) {
            if (!(coords[0] == triPrimeCoord[0] && coords[1] == triPrimeCoord[1])) {
                grid[coords[0]][coords[1]] = null;
            }
        }


        // Ajoute les voisins uniques au triPrime hex
        triPrimeHex.getNeighbors().addAll(uniqueNeighbors);

        // Création du secteur central (index 4) et marquage comme secteur central
        sectors[4] = new Sector(4);
        sectors[4].addSystemHex(triPrimeHex);
        sectors[4].setIsCentralSector(true);

        // Create the systems and sectors

        // On normalise les top / bottom carte secteurs
        // Elle sont toutes retournées avdc la bande bleue vers le haut
        // Et sont placés dans le coin en haut à gauche du plateau
        // Format: system { {lv_1}, {lvl_1}, {lvl_2} }

        int[][][] topBottomSectors = {
                {{1, 0}, {2, 1}, {0, 0}},
                {{0, 0}, {1, 0}, {2, 0}},
                {{0, 0}, {0, 1}, {2, 1}},
                {{0, 0}, {0, 1}, {1, 0}},
                {{0, 0}, {2, 0}, {1, 0}},
                {{2, 0}, {2, 1}, {1, 0}},
        };

        // Side sector cards don't require to be normalized

        int[][][] sideSectors = {
                {{1, 0}, {2, 0}, {0, 0}},
                {{0, 0}, {2, 0}, {1, 1}},
        };


        // Mélanger et assigner les secteurs top/bottom
        List<Integer> randomTopBottomSectors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            randomTopBottomSectors.add(i);
        }
        for (int i = 6; i < 9; i++) {
            randomTopBottomSectors.add(i);
        }
        Collections.shuffle(randomTopBottomSectors);

        for (int i = 0; i < 6; i++) {
            int sectorIndex = randomTopBottomSectors.get(i);
            int[][] sectorsCoords = topBottomSectors[i];

            // Si on place un secteur en bas (index >= 6), on ajuste les coords
            if (sectorIndex >= 6) {
                for (int[] coord : sectorsCoords) {
                    coord[0] = 6 + (2 - coord[0]);
                    coord[1] = (coord[0] & 1 ^ 1) - coord[1];
                }
            }

            // Décalage horizontal
            for (int[] coord : sectorsCoords) {
                coord[1] += 2 * (sectorIndex % 6);
            }

            // Placement des systèmes : les deux premiers sont niveaux I, le troisième niveau II
            for (int j = 0; j < 3; j++) {
                int[] coord = sectorsCoords[j];
                grid[coord[0]][coord[1]].setSystem(j < 2 ? 1 : 2);
            }

            // Création du secteur correspondant
            sectors[sectorIndex] = new Sector(sectorIndex);
            for (int[] coord : sectorsCoords) {
                Hex systemHex = grid[coord[0]][coord[1]];
                sectors[sectorIndex].addSystemHex(systemHex);
                systemToSectorMap.put(systemHex, sectors[sectorIndex]);
                systems.add(systemHex);
            }
        }

        // Mélanger et assigner les secteurs latéraux (side)
        List<Integer> randomSideSectors = new ArrayList<>();
        for (int i = 3; i < 6; i += 2) {
            randomSideSectors.add(i);
        }
        Collections.shuffle(randomTopBottomSectors);

        for (int i = 0; i < 2; i++) {
            int sectorIndex = randomSideSectors.get(i);
            int[][] sectorsCoords = sideSectors[i];

            // Possibilité de retourner aléatoirement les coordonnées
            if (Math.random() < 0.5) {
                for (int[] coord : sectorsCoords) {
                    coord[0] = 2 - coord[0];
                    coord[1] = (coord[0] & 1) - coord[1];
                }
            }


            // Décalage pour placer ces secteurs latéralement
            for (int[] coord : sectorsCoords) {
                coord[0] += 3;
                coord[1] += 2 * (sectorIndex % 3);
            }

            // Assignation de niveaux (1,1,2)
            for (int j = 0; j < 3; j++) {
                int[] coord = sectorsCoords[j];
                grid[coord[0]][coord[1]].setSystem(j < 2 ? 1 : 2);
            }

            // Création du secteur correspondant
            sectors[sectorIndex] = new Sector(sectorIndex);
            for (int[] coord : sectorsCoords) {
                Hex systemHex = grid[coord[0]][coord[1]];
                sectors[sectorIndex].addSystemHex(systemHex);
                systemToSectorMap.put(systemHex, sectors[sectorIndex]);
                systems.add(systemHex);
            }
        }

    }

    /**
     * Retourne le tableau de secteurs (9 secteurs).
     *
     * @return Tableau de 9 secteurs.
     */
    public Sector[] getSectors() {
        return sectors;
    }


    /**
     * Retourne le secteur contenant un hexagone-système.
     * @param hex L'hexagone-système à chercher.
     * @return Secteur auquel appartient l'hexagone, ou null si non trouvé.
     */
    public Sector getSectorContainingHex(Hex hex) {
        return systemToSectorMap.get(hex);
    }

    /**
     * Permet de récupérer un hexagone à partir de ses coordonnées (x, y).
     *
     * @param x Coordonnée x de l'hexagone.
     * @param y Coordonnée y de l'hexagone.
     * @return L'hexagone correspondant, ou null si les coordonnées sont hors limites ou vides.
     */
    public Hex getHex(int x, int y) {
        if (x >= 0 && x < grid.length && grid[x] != null &&
                y >= 0 && y < grid[x].length && grid[x][y] != null) {
            return grid[x][y];
        }
        return null;
    }


    /**
     * Enlève, dans chaque hexagone, les ships en trop
     * que l'hexagone ne peut pas contenir (niveau + 1).
     */
    public void removeUnsustainableShips() {
        for (Hex[] row : grid) {
            for (Hex hex : row) {
                if (hex != null) {
                    hex.removeUnsustainableShips();
                }
            }
        }
    }

    /**
     * Retourne la liste des hexagones systèmes actuellement contrôlés par un joueur donné.
     *
     * @param player Joueur dont on recherche les hexagones contrôlés.
     * @return Liste d'hexagones contrôlés par le joueur.
     */
    public List<Hex> getSystemsControlledBy(Player player) {
        return systems.stream()
                .filter(hex -> hex.isControlledBy(player))
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste de tous les hexagones (systèmes ou non) qu'un joueur contrôle.
     *
     * @param player Joueur dont on recherche les hexagones contrôlés.
     * @return Liste d'hexagones où le joueur est présent.
     */
    public List<Hex> getHexesPlayerOn(Player player) {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(hex -> hex.isControlledBy(player))
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste des hexagones systèmes non contrôlés par un joueur donné.
     *
     * @param player Joueur pour lequel on cherche les systèmes non contrôlés.
     * @return Liste d'hexagones systèmes que le joueur ne contrôle pas.
     */
    public List<Hex> getSystemsNotControlledBy(Player player) {
        return systems.stream()
                .filter(hex -> !hex.isControlledBy(player))
                .collect(Collectors.toList());
    }

    /**
     * Retourne l'hexagone Tri-Prime (niveau 3) placé au centre de la grille.
     *
     * @return L'hexagone Tri-Prime.
     */
    public Hex getTriPrimeHex() {
        return triPrimeHex;
    }
}



