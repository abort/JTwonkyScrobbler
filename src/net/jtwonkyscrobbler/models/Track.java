package net.jtwonkyscrobbler.models;

public class Track {
    private String title = "";
    private String author = "";
    private String playedDateTime = "";
    private String album = "";
    private int duration = 0;
    private int playCount = 0;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPlayedDateTime() {
        return playedDateTime;
    }

    public void setPlayedDateTime(String playedDateTime) {
        this.playedDateTime = playedDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Track other = (Track) obj;
        if ((this.playedDateTime == null) ? (other.playedDateTime != null) : !this.playedDateTime.equals(other.playedDateTime)) {
            return false;
        }
        if (this.playCount != other.playCount) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.playedDateTime != null ? this.playedDateTime.hashCode() : 0);
        return hash;
    }
}
