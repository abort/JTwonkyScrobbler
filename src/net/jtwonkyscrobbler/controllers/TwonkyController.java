package net.jtwonkyscrobbler.controllers;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.jtwonkyscrobbler.models.Track;

/**
 * Twonky Main Controller
 *
 * @author J. Dijkstra
 */
public class TwonkyController {
    // Defaults
    private static final int DEFAULT_FETCH_INTERVAL = 30000;
    private static final int MINIMUM_INTERVAL = 1000;

    // Constants
    private static final String PROPERTIES_FILE = "jtwonkyscrobbler.properties";
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_INTERVAL = "interval";
    private static final String PROPERTY_PASSWORD = "password";
    private static final char MASKED_PASSWORD_CHARACTER = '*';

    // Singleton
    private static TwonkyController instance = null;

    // Properties
    private String url;
    private String username;
    private String password;
    private int interval = DEFAULT_FETCH_INTERVAL;

    /**
     * Instance of the Last FM ScrobbleController
     */
    private ScrobbleController scrobbler;

    /**
     * Retrieve instance of the TwonkyController
     *
     * @return Instance
     */
    public static TwonkyController getInstance() {
        if (instance == null) {
            instance = new TwonkyController();
        }

        return instance;
    }

    /**
     * Start monitoring and scrobbling to Last FM
     */
    public void start() {
        initScrobbler();
        outputStartup();
        monitorPlaybacks();
    }

    /**
     * Constructor
     */
    private TwonkyController() {
        initProperties();
    }

    /**
     * Load, parse and initialize properties
     */
    private void initProperties() {
        try {
            // Load properties file
            Properties properties = new Properties();
            properties.load(new FileInputStream(PROPERTIES_FILE));

            // Parse properties
            String intervalValue = properties.getProperty(PROPERTY_INTERVAL);

            // If the interval value is invalid, use the default fetching value
            if (intervalValue != null && !intervalValue.isEmpty()) {
                interval = Integer.parseInt(intervalValue);
                
                // Minimum interval = 1000ms
                if (interval < MINIMUM_INTERVAL) {
                    interval = DEFAULT_FETCH_INTERVAL;
                }
            }

            username = properties.getProperty(PROPERTY_USERNAME);
            if (username == null || username.isEmpty()) {
                throw new RuntimeException(String.format("Properties file (%s) does not contain the username property\n", PROPERTIES_FILE, PROPERTY_USERNAME));
            }

            password = properties.getProperty(PROPERTY_PASSWORD);
            if (password == null || password.isEmpty()) {
                throw new RuntimeException(String.format("Properties file (%s) does not contain the %s property\n", PROPERTIES_FILE, PROPERTY_PASSWORD));
            }

            url = properties.getProperty(PROPERTY_URL);
            if (url == null || url.isEmpty()) {
                throw new RuntimeException(String.format("Properties file (%s) does not contain the %s property\n", PROPERTIES_FILE, PROPERTY_URL));
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to read/parse properties file", ex);
        }
    }

    /**
     * Print startup message and set properties
     */
    private void outputStartup() {
        // Representation of password
        char maskedPassword[] = new char[password.length()];
        Arrays.fill(maskedPassword, MASKED_PASSWORD_CHARACTER);

        System.out.printf(
                "Starting JTwonkyScrobbler...\n\n"
                + "Properties:\n\tRSS Path:\t%s\n\tInterval:\t%ds\n\t"
                + "Username:\t%s\n\tPassword:\t%s\n\n",
                url, (interval / 1000), username, new String(maskedPassword));
    }

    /**
     * Initialize ScrobbleController and handshake
     */
    private void initScrobbler() {
        scrobbler = new ScrobbleController(username, password);
    }

    /**
     * Monitor playbacks by fetching the MRSS file, checking it for playbacks and eventually scrobble
     */
    private void monitorPlaybacks() {
        try {
            List<Track> tracks = new ArrayList<Track>();
            FetchController controller = new FetchController(url);

            boolean firstIteration = true;
            boolean running = true;

            Track lastTrack = null;

            // Infinite loop
            while (running) {
                if (controller.fetchTracks(tracks) > 0) {
                    Track lastRead = tracks.get(0);

                    // Cache old tracks so they won't be scrobble
                    if (firstIteration) {
                        firstIteration = false;
                        System.out.printf("Last track cached (%s - %s).\nReady to scrobble upcoming tracks.\n\n",
                                lastRead.getAuthor(), lastRead.getTitle());

                    }
                    else {
                        // If last track does not equal the last previous read track scrobble
                        if (!lastRead.equals(lastTrack)) {
                            System.out.printf("New track playback:\n\t\t%s - %s (%s)\n", lastRead.getAuthor(), lastRead.getTitle(), lastRead.getAlbum());
                            scrobbler.scrobbleTrack(lastRead);
                            System.out.println("\t\tScrobbled track to Last FM.\n");
                        }
                    }

                    // Store the last played track
                    lastTrack = lastRead;
                }
                else {
                    System.out.println("No previous tracks detected.\nReady to scrobble upcoming tracks.\n");
                }

                // Sleep for specified time
                Thread.sleep(interval);
            }
        }
        catch (InterruptedException ex) {
            throw new RuntimeException("Thread interrupted", ex);
        }
    }
}
