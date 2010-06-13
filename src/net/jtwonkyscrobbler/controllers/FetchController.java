package net.jtwonkyscrobbler.controllers;

import net.jtwonkyscrobbler.models.Track;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Collection;

import org.apache.commons.lang.StringEscapeUtils;

import com.myjavatools.xml.Rss;
import com.myjavatools.xml.Rss.Item;


/**
 * Controller to fetch Media RSS (MRSS) from a TwonkyMedia Server URL
 *
 * @author J. Dijkstra
 */
public class FetchController {
    // RSS attribute names
    private static final String ATTRIBUTE_DURATION = "duration";
    private static final String ATTRIBUTE_PLAYCOUNT = "playcount";
    private static final String ATTRIBUTE_PLAYEDDATETIME = "playeddatetime";
    private static final String ATTRIBUTE_TIME_SEPARATOR = ":";

    /**
     *  Twonky RSS url
     */
    private URL url;

    /**
     * Initialize Twonky Fetching Controller
     *
     * @param link Twonky Media RSS URL to fetch from
     */
    public FetchController(String link) {
        try {
            url = new URL(link);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid URL provided to TwonkyMedia RSS.", ex);
        }
    }

    /**
     * Fetch tracks from stored URL
     * 
     * @param tracks List of tracks to fill
     * @return Size of filled tracklist
     */
    public int fetchTracks(List<Track> tracks) {
        try {
            Rss rss = new Rss(url);

            // Clear the list again
            tracks.clear();

            // Fetch items from RSS feed
            Collection<Item> items = rss.getItems();
            for (Item item : items) {

                // Create a track per fetched item
                Track track = new Track();

                // Parse duration
                String duration = item.getKidValue(ATTRIBUTE_DURATION);
                if (duration != null && !duration.isEmpty()) {

                    // Expected formatting
                    String[] splittedTime = duration.split(ATTRIBUTE_TIME_SEPARATOR);
                    if (splittedTime.length == 3) {
                        int seconds = (Integer.valueOf(splittedTime[0]) * 60 * 60) + (Integer.valueOf(splittedTime[1]) * 60) + Integer.valueOf(splittedTime[2]);
                        track.setDuration(seconds);
                    }

                    // If one value, parse it as ms value
                    else if (splittedTime.length == 1) {
                        track.setDuration(Integer.parseInt(duration) / 1000);
                    }

                    // No other formattings are supported, thus ignore
                }

                // Parse played date/time
                String playedDateTime = item.getKidValue(ATTRIBUTE_PLAYEDDATETIME);
                if (playedDateTime != null && !playedDateTime.isEmpty()) {
                    track.setPlayedDateTime(playedDateTime);
                }

                // Play count
                String playCount = item.getKidValue(ATTRIBUTE_PLAYCOUNT);
                if (playCount != null && !playCount.isEmpty()) {
                    track.setPlayCount(Integer.valueOf(playCount));
                }

                // Parse and unescape HTML from read items
                track.setTitle(StringEscapeUtils.unescapeHtml(item.getTitle()));
                track.setAuthor(StringEscapeUtils.unescapeHtml(item.getAuthor()));
                track.setAlbum(StringEscapeUtils.unescapeHtml(item.getDescription()));

                // Add track object to fetched array
                tracks.add(track);
            }

            // Return the size of the array
            return items.size();
        }
        catch (InstantiationException ex) {
            throw new RuntimeException("Failed to instantiate RSS feed", ex);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to retrieve RSS feed", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid formatting provided", ex);
        }
    }
}
