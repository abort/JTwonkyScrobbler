package net.jtwonkyscrobbler.controllers;

import net.jtwonkyscrobbler.models.Track;

import java.io.IOException;

import net.roarsoftware.lastfm.scrobble.ResponseStatus;
import net.roarsoftware.lastfm.scrobble.Scrobbler;
import net.roarsoftware.lastfm.scrobble.Source;

/**
 * Last.FM Scrobble Controller
 *
 * @author J. Dijkstra
 */
public class ScrobbleController {
    // API properties/information
    private static final String CLIENT_ID = "tst";
    private static final String CLIENT_VERSION = "1.0";

    /**
     * Last.FM Scrobbler
     */
    private Scrobbler scrobbler;

    /**
     * Initialize Last.FM Scrobbler
     *
     * @param username Last.FM Username
     * @param password Last.FM Password
     */
    public ScrobbleController(String username, String password) {
        scrobbler = Scrobbler.newScrobbler(CLIENT_ID, CLIENT_VERSION, username);

        if (scrobbler == null) {
            throw new RuntimeException("Could not create Last FM Scrobbler instance");
        }

        ResponseStatus status = null;
        try {
            status = scrobbler.handshake(password);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to communicate with Last FM", ex);
        }

        if (!status.ok()) {
            throw new RuntimeException("Could not handshake with Last FM");
        }
    }

    /**
     * Scrobble track to Last.FM
     *
     * @param track Track object
     */
    public void scrobbleTrack(Track track) {
        try {
            ResponseStatus status;

            scrobbler.nowPlaying(track.getAuthor(), track.getTitle(), track.getAlbum(), track.getDuration(), -1);
            scrobbler.submit(track.getAuthor(), track.getTitle(), track.getAlbum(), track.getDuration(), -1, Source.USER, (System.currentTimeMillis() / 1000)).ok();
        }
        catch (NumberFormatException ex) {
            throw new RuntimeException("Failed to parse duration of track", ex);
        }
        catch (IOException ex) {
            throw new RuntimeException("An IO Exception occurred during scrobbling", ex);
        }
    }
}
