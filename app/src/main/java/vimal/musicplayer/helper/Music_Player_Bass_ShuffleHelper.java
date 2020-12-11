package vimal.musicplayer.helper;

import androidx.annotation.NonNull;

import vimal.musicplayer.model.Song;

import java.util.Collections;
import java.util.List;


public class Music_Player_Bass_ShuffleHelper {

    public static void makeShuffleList(@NonNull List<Song> listToShuffle, final int current) {
        if (listToShuffle.isEmpty()) return;
        if (current >= 0) {
            Song song = listToShuffle.remove(current);
            Collections.shuffle(listToShuffle);
            listToShuffle.add(0, song);
        } else {
            Collections.shuffle(listToShuffle);
        }
    }
}
