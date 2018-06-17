package Model;

public class SongInfo {
public String songName,artistName,songUrl;

    public String getSongName() {
        return songName;
    }



    public String getArtistName() {
        return artistName;
    }



    public String getSongUrl() {
        return songUrl;
    }



    public SongInfo(String songName, String artistName, String songUrl) {
        this.songName = songName;
        this.artistName = artistName;
        this.songUrl = songUrl;

    }
}
