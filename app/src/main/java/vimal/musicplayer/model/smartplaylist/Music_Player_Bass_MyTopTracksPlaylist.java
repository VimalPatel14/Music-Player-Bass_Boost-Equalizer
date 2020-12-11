package vimal.musicplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;

import vimal.musicplayer.R;
import vimal.musicplayer.loader.Music_Player_Bass_TopAndRecentlyPlayedTracksLoader;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.provider.Music_Player_Bass_SongPlayCountStore;

import java.util.ArrayList;


public class Music_Player_Bass_MyTopTracksPlaylist extends AbsSmartPlaylist {

    public Music_Player_Bass_MyTopTracksPlaylist(@NonNull Context context) {
        super(context.getString(R.string.my_top_tracks), R.drawable.ic_trending_up_white_24dp);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return Music_Player_Bass_TopAndRecentlyPlayedTracksLoader.getTopTracks(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        Music_Player_Bass_SongPlayCountStore.getInstance(context).clear();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    protected Music_Player_Bass_MyTopTracksPlaylist(Parcel in) {
        super(in);
    }

    public static final Creator<Music_Player_Bass_MyTopTracksPlaylist> CREATOR = new Creator<Music_Player_Bass_MyTopTracksPlaylist>() {
        public Music_Player_Bass_MyTopTracksPlaylist createFromParcel(Parcel source) {
            return new Music_Player_Bass_MyTopTracksPlaylist(source);
        }

        public Music_Player_Bass_MyTopTracksPlaylist[] newArray(int size) {
            return new Music_Player_Bass_MyTopTracksPlaylist[size];
        }
    };
}
