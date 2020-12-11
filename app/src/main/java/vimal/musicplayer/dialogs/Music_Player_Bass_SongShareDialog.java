package vimal.musicplayer.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import vimal.musicplayer.R;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;


public class Music_Player_Bass_SongShareDialog extends DialogFragment {
    @NonNull
    public static Music_Player_Bass_SongShareDialog create(final Song song) {
        final Music_Player_Bass_SongShareDialog dialog = new Music_Player_Bass_SongShareDialog();
        final Bundle args = new Bundle();
        args.putParcelable("song", song);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Song song = getArguments().getParcelable("song");
        final String currentlyListening = getString(R.string.currently_listening_to_x_by_x, song.title, song.artistName);
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.what_do_you_want_to_share)
                .items(getString(R.string.the_audio_file), "\u201C" + currentlyListening + "\u201D")
                .itemsCallback((materialDialog, view, i, charSequence) -> {
                    switch (i) {
                        case 0:
                            startActivity(Intent.createChooser(Music_Player_Bass_MusicUtil.createShareSongFileIntent(song, getContext()), null));
                            break;
                        case 1:
                            getActivity().startActivity(
                                    Intent.createChooser(
                                            new Intent()
                                                    .setAction(Intent.ACTION_SEND)
                                                    .putExtra(Intent.EXTRA_TEXT, currentlyListening)
                                                    .setType("text/plain"),
                                            null
                                    )
                            );
                            break;
                    }
                })
                .build();
    }
}
