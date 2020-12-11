package vimal.musicplayer.model;

import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;

import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;

import java.util.ArrayList;



public abstract class AbsCustomPlaylist extends Playlist {
    public AbsCustomPlaylist(int id, String name) {
        super(id, name);
    }

    public AbsCustomPlaylist() {
    }

    public AbsCustomPlaylist(Parcel in) {
        super(in);
    }

    @NonNull
    public abstract ArrayList<Song> getSongs(Context context);

    @NonNull
    @Override
    public String getInfoString(@NonNull Context context) {
        int songCount = getSongs(context).size();
        String songCountString = Music_Player_Bass_MusicUtil.getSongCountString(context, songCount);

        return Music_Player_Bass_MusicUtil.buildInfoString(
            songCountString,
            super.getInfoString(context)
        );
    }
}
