package app;

import controllers.page_controllers.Controller;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Le routeur (Router) gère la navigation entre les différentes scènes de l'application.
 * Il supporte la mise en cache des scènes pour améliorer les performances et permet à certaines
 * scènes de contourner la mise en cache en fonction des préférences d'enregistrement.
 * Le routeur est responsable de l'enregistrement des écrans, de la gestion de leur création,
 * de la navigation entre eux, et du contrôle de la mise en cache pour optimiser les ressources.
 */
public class Router {
    /**
     * La scène principale de l'application.
     */
    private final Stage primaryStage;

    /**
     * Mappage entre les noms uniques des écrans et leurs classes contrôleurs correspondantes.
     */
    private final Map<String, Class<? extends Controller>> screenControllers = new HashMap<>();

    /**
     * Mappage des scènes mises en cache pour une réutilisation ultérieure.
     */
    private final Map<String, Scene> cachedScenes = new HashMap<>();

    /**
     * Ensemble des noms d'écrans qui ne doivent pas être mis en cache.
     */
    private final Set<String> notCachedScenes = new HashSet<>();

    /**
     * Construit un routeur avec la scène principale spécifiée.
     *
     * @param primaryStage La scène principale de l'application.
     */
    public Router(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Enregistre un écran avec la mise en cache activée par défaut.
     *
     * @param name            Le nom unique de l'écran.
     * @param controllerClass La classe contrôleur associée à l'écran.
     */
    public void addScreen(String name, Class<? extends Controller> controllerClass) {
        screenControllers.put(name, controllerClass);
        // Par défaut, les scènes sont mises en cache sauf indication contraire
    }

    /**
     * Marque un écran pour contourner la mise en cache.
     * Cela signifie qu'une nouvelle scène sera créée chaque fois que navigateTo sera appelé pour cet écran.
     *
     * @param name Le nom unique de l'écran à ne pas mettre en cache.
     */
    public void notCacheScene(String name) {
        notCachedScenes.add(name);
    }

    /**
     * Navigue vers l'écran spécifié.
     *
     * @param name Le nom unique de l'écran vers lequel naviguer.
     * @throws IllegalArgumentException Si l'écran n'est pas enregistré.
     */
    public void navigateTo(String name) {
        if (!screenControllers.containsKey(name)) {
            throw new IllegalArgumentException("Screen " + name + " is not registered.");
        }

        Scene scene;

        if (!notCachedScenes.contains(name)) {
            // Si l'écran doit être mis en cache, le récupérer du cache ou le créer et le mettre en cache
            scene = cachedScenes.computeIfAbsent(name, this::createSceneForScreen);
        } else {
            // Si l'écran ne doit pas être mis en cache, toujours créer une nouvelle instance
            scene = createSceneForScreen(name);
        }

        // Définir la scène sans affecter l'état plein écran
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Crée une nouvelle instance de scène pour l'écran spécifié.
     *
     * @param screenName Le nom unique de l'écran.
     * @return La nouvelle instance de {@link Scene}.
     * @throws RuntimeException Si la création de la scène échoue.
     */
    private Scene createSceneForScreen(String screenName) {
        try {
            Class<? extends Controller> controllerClass = screenControllers.get(screenName);
            Controller controllerInstance = controllerClass.getConstructor(Router.class).newInstance(this);
            return controllerInstance.getScene();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create scene for screen: " + screenName, e);
        }
    }

    /**
     * Supprime un écran mis en cache, réinitialisant ainsi son état.
     *
     * @param name Le nom unique de l'écran à réinitialiser.
     */
    public void resetScreen(String name) {
        cachedScenes.remove(name);
    }

    /**
     * Démarre l'application avec l'écran initial spécifié.
     *
     * @param name Le nom unique de l'écran initial.
     */
    public void startWith(String name) {
        navigateTo(name);
    }
}
