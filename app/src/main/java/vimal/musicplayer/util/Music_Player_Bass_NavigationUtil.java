package vimal.musicplayer.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.widget.Toast;

import vimal.musicplayer.R;
import vimal.musicplayer.helper.MusicPlayerRemote;
import vimal.musicplayer.model.Genre;
import vimal.musicplayer.model.Playlist;
import vimal.musicplayer.ui.activities.Music_Player_Bass_AlbumDetailActivityMusicPlayerBass;
import vimal.musicplayer.ui.activities.Music_Player_Bass_ArtistDetailActivityMusicPlayerBass;
import vimal.musicplayer.ui.activities.Music_Player_Bass_EqualizerActivity;
import vimal.musicplayer.ui.activities.Music_Player_Bass_GenreDetailActivityMusicPlayerBass;
import vimal.musicplayer.ui.activities.Music_Player_Bass_PlaylistDetailActivityMusicPlayerBass;

public class Music_Player_Bass_NavigationUtil {

    public static void goToArtist(@NonNull final Activity activity, final int artistId, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, Music_Player_Bass_ArtistDetailActivityMusicPlayerBass.class);
        intent.putExtra(Music_Player_Bass_ArtistDetailActivityMusicPlayerBass.EXTRA_ARTIST_ID, artistId);

        //noinspection unchecked
        if (sharedElements != null && sharedElements.length > 0) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void goToAlbum(@NonNull final Activity activity, final int albumId, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, Music_Player_Bass_AlbumDetailActivityMusicPlayerBass.class);
        intent.putExtra(Music_Player_Bass_AlbumDetailActivityMusicPlayerBass.EXTRA_ALBUM_ID, albumId);

        //noinspection unchecked
        if (sharedElements != null && sharedElements.length > 0) {
            activity.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    public static void goToGenre(@NonNull final Activity activity, final Genre genre, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, Music_Player_Bass_GenreDetailActivityMusicPlayerBass.class);
        intent.putExtra(Music_Player_Bass_GenreDetailActivityMusicPlayerBass.EXTRA_GENRE, genre);

        activity.startActivity(intent);
    }

    public static void goToPlaylist(@NonNull final Activity activity, final Playlist playlist, @Nullable Pair... sharedElements) {
        final Intent intent = new Intent(activity, Music_Player_Bass_PlaylistDetailActivityMusicPlayerBass.class);
        intent.putExtra(Music_Player_Bass_PlaylistDetailActivityMusicPlayerBass.EXTRA_PLAYLIST, playlist);

        activity.startActivity(intent);
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        final int sessionId = MusicPlayerRemote.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_audio_ID), Toast.LENGTH_LONG).show();
        } else {
            try {
//                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
//                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
//                activity.startActivityForResult(effects, 0);
                activity.startActivity(new Intent(activity, Music_Player_Bass_EqualizerActivity.class));
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, activity.getResources().getString(R.string.no_equalizer), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
