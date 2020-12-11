package vimal.musicplayer.model.smartplaylist;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;

import vimal.musicplayer.R;
import vimal.musicplayer.loader.Music_Player_Bass_TopAndRecentlyPlayedTracksLoader;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;
import vimal.musicplayer.util.Music_Player_Bass_PreferenceUtil;

import java.util.ArrayList;


public class Music_Player_Bass_NotRecentlyPlayedPlaylist extends AbsSmartPlaylist {

    public Music_Player_Bass_NotRecentlyPlayedPlaylist(@NonNull Context context) {
        super(context.getString(R.string.not_recently_played), R.drawable.ic_watch_later_white_24dp);
    }

    @NonNull
    @Override
    public String getInfoString(@NonNull Context context) {
        String cutoff = Music_Player_Bass_PreferenceUtil.getInstance(context).getRecentlyPlayedCutoffText(context);

        return Music_Player_Bass_MusicUtil.buildInfoString(
            cutoff,
            super.getInfoString(context)
        );
    }

    @NonNull
    @Override
    public ArrayList<Song> getSongs(@NonNull Context context) {
        return Music_Player_Bass_TopAndRecentlyPlayedTracksLoader.getNotRecentlyPlayedTracks(context);
    }

    @Override
    public void clear(@NonNull Context context) {
    }

    @Override
    public boolean isClearable() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Music_Player_Bass_NotRecentlyPlayedPlaylist(Parcel in) {
        super(in);
    }

    public static final Creator<Music_Player_Bass_NotRecentlyPlayedPlaylist> CREATOR = new Creator<Music_Player_Bass_NotRecentlyPlayedPlaylist>() {
        public Music_Player_Bass_NotRecentlyPlayedPlaylist createFromParcel(Parcel source) {
            return new Music_Player_Bass_NotRecentlyPlayedPlaylist(source);
        }

        public Music_Player_Bass_NotRecentlyPlayedPlaylist[] newArray(int size) {
            return new Music_Player_Bass_NotRecentlyPlayedPlaylist[size];
        }
    };
}
