package net.jtwonkyscrobbler;

import net.jtwonkyscrobbler.controllers.TwonkyController;

/**
 * Entry class
 *
 * @author J. Dijkstra
 */
public class Main {

    /**
     * Application entrypoint
     * 
     * @param args Application arguments
     */
    public static void main(String[] args) {

        // Retrieve singleton instance and start the fetching and scrobbling
        TwonkyController.getInstance().start();
    }
}
