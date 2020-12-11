

package vimal.musicplayer.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import vimal.musicplayer.model.Song;
import vimal.musicplayer.provider.Music_Player_Bass_HistoryStore;
import vimal.musicplayer.provider.Music_Player_Bass_SongPlayCountStore;
import vimal.musicplayer.util.Music_Player_Bass_PreferenceUtil;

import java.util.ArrayList;

public class Music_Player_Bass_TopAndRecentlyPlayedTracksLoader {
    public static final int NUMBER_OF_TOP_TRACKS = 100;

    @NonNull
    public static ArrayList<Song> getRecentlyPlayedTracks(@NonNull Context context) {
        return Music_Player_Bass_SongLoader.getSongs(makeRecentTracksCursorAndClearUpDatabase(context));
    }

    @NonNull
    public static ArrayList<Song> getNotRecentlyPlayedTracks
(@NonNull Context context) {
        ArrayList<Song> allSongs = Music_Player_Bass_SongLoader.getSongs(
            Music_Player_Bass_SongLoader.makeSongCursor(
                context,
                null, null,
                MediaStore.Audio.Media.DATE_ADDED + " ASC"));

        ArrayList<Song> recentlyPlayedSongs = Music_Player_Bass_SongLoader.getSongs(
            makeRecentTracksCursorAndClearUpDatabase(context));

        allSongs.removeAll(recentlyPlayedSongs);

        return allSongs;
    }

    @NonNull
    public static ArrayList<Song> getTopTracks(@NonNull Context context) {
        return Music_Player_Bass_SongLoader.getSongs(makeTopTracksCursorAndClearUpDatabase(context));
    }

    @Nullable
    public static Cursor makeRecentTracksCursorAndClearUpDatabase(@NonNull final Context context) {
        Music_Player_Bass_SortedLongCursor retCursor = makeRecentTracksCursorImpl(context);

        // clean up the databases with any ids not found
        if (retCursor != null) {
            ArrayList<Long> missingIds = retCursor.getMissingIds();
            if (missingIds != null && missingIds.size() > 0) {
                for (long id : missingIds) {
                    Music_Player_Bass_HistoryStore.getInstance(context).removeSongId(id);
                }
            }
        }
        return retCursor;
    }

    @Nullable
    public static Cursor makeTopTracksCursorAndClearUpDatabase(@NonNull final Context context) {
        Music_Player_Bass_SortedLongCursor retCursor = makeTopTracksCursorImpl(context);

        // clean up the databases with any ids not found
        if (retCursor != null) {
            ArrayList<Long> missingIds = retCursor.getMissingIds();
            if (missingIds != null && missingIds.size() > 0) {
                for (long id : missingIds) {
                    Music_Player_Bass_SongPlayCountStore.getInstance(context).removeItem(id);
                }
            }
        }
        return retCursor;
    }

    @Nullable
    private static Music_Player_Bass_SortedLongCursor makeRecentTracksCursorImpl(@NonNull final Context context) {
        // first get the top results ids from the internal database
        final long cutoff = Music_Player_Bass_PreferenceUtil.getInstance(context).getRecentlyPlayedCutoffTimeMillis();
        Cursor songs = Music_Player_Bass_HistoryStore.getInstance(context).queryRecentIds(cutoff);

        try {
            return makeSortedCursor(context, songs,
                    songs.getColumnIndex(Music_Player_Bass_HistoryStore.RecentStoreColumns.ID));
        } finally {
            if (songs != null) {
                songs.close();
            }
        }
    }

    @Nullable
    private static Music_Player_Bass_SortedLongCursor makeTopTracksCursorImpl(@NonNull final Context context) {
        // first get the top results ids from the internal database
        Cursor songs = Music_Player_Bass_SongPlayCountStore.getInstance(context).getTopPlayedResults(NUMBER_OF_TOP_TRACKS);

        try {
            return makeSortedCursor(context, songs,
                    songs.getColumnIndex(Music_Player_Bass_SongPlayCountStore.SongPlayCountColumns.ID));
        } finally {
            if (songs != null) {
                songs.close();
            }
        }
    }

    @Nullable
    private static Music_Player_Bass_SortedLongCursor makeSortedCursor(@NonNull final Context context, @Nullable final Cursor cursor, final int idColumn) {
        if (cursor != null && cursor.moveToFirst()) {
            // create the list of ids to select against
            StringBuilder selection = new StringBuilder();
            selection.append(BaseColumns._ID);
            selection.append(" IN (");

            // this tracks the order of the ids
            long[] order = new long[cursor.getCount()];

            long id = cursor.getLong(idColumn);
            selection.append(id);
            order[cursor.getPosition()] = id;

            while (cursor.moveToNext()) {
                selection.append(",");

                id = cursor.getLong(idColumn);
                order[cursor.getPosition()] = id;
                selection.append(String.valueOf(id));
            }

            selection.append(")");

            // get a list of songs with the data given the selection statement
            Cursor songCursor = Music_Player_Bass_SongLoader.makeSongCursor(context, selection.toString(), null);
            if (songCursor != null) {
                // now return the wrapped TopTracksCursor to handle sorting given order
                return new Music_Player_Bass_SortedLongCursor(songCursor, order, BaseColumns._ID);
            }
        }

        return null;
    }
}
