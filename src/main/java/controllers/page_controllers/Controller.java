package controllers.page_controllers;

import javafx.scene.Scene;

/**
 * Interface représentant un contrôleur de page.
 * Un contrôleur de page est responsable de fournir la scène associée à une vue spécifique.
 */
public interface Controller {

    /**
     * Retourne la scène associée au contrôleur.
     *
     * @return La scène correspondant à la vue contrôlée.
     */
    public Scene getScene();
}
