package vimal.musicplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;

import vimal.musicplayer.R;
import vimal.musicplayer.loader.Music_Player_Bass_SongLoader;
import vimal.musicplayer.model.Song;

import java.util.ArrayList;

public class Music_Player_Bass_ShuffleAllPlaylist extends AbsSmartPlaylist {

    public Music_Player_Bass_ShuffleAllPlaylist(@NonNull Context context) {
        super(context.getString(R.string.action_shuffle_all), R.drawable.ic_shuffle_white_24dp);
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return Music_Player_Bass_SongLoader.getAllSongs(context);
    }

    @Override
    public void clear(@NonNull Context context) {
        // Shuffle all is not a real "Smart Playlist"
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Music_Player_Bass_ShuffleAllPlaylist(Parcel in) {
        super(in);
    }

    public static final Creator<Music_Player_Bass_ShuffleAllPlaylist> CREATOR = new Creator<Music_Player_Bass_ShuffleAllPlaylist>() {
        public Music_Player_Bass_ShuffleAllPlaylist createFromParcel(Parcel source) {
            return new Music_Player_Bass_ShuffleAllPlaylist(source);
        }

        public Music_Player_Bass_ShuffleAllPlaylist[] newArray(int size) {
            return new Music_Player_Bass_ShuffleAllPlaylist[size];
        }
    };
}
