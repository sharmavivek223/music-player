package Model;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class SongInfo {
public String songName,artistName,songUrl;
public Drawable imageResource;


    public String getSongName() {
        return songName;
    }



    public String getArtistName() {
        return artistName;
    }



    public String getSongUrl() {
        return songUrl;
    }

    public Drawable getImageResource() { return imageResource; }

    public  void setImageResource(Drawable imageResource){ this.imageResource=imageResource; }



    public SongInfo(String songName, String artistName, String songUrl,Drawable imageResource) {
        this.songName = songName;
        this.artistName = artistName;
        this.songUrl = songUrl;
        this.imageResource=imageResource;
    }
}
