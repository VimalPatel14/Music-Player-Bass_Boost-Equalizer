package vimal.musicplayer.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import vimal.musicplayer.R;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.util.Music_Player_Bass_PlaylistsUtil;

import java.util.ArrayList;


public class CreatePlaylistDialog extends DialogFragment {

    private static final String SONGS = "songs";

    @NonNull
    public static CreatePlaylistDialog create() {
        return create((Song) null);
    }

    @NonNull
    public static CreatePlaylistDialog create(@Nullable Song song) {
        ArrayList<Song> list = new ArrayList<>();
        if (song != null)
            list.add(song);
        return create(list);
    }

    @NonNull
    public static CreatePlaylistDialog create(ArrayList<Song> songs) {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(SONGS, songs);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.new_playlist_title)
                .positiveText(R.string.create_action)
                .negativeText(android.R.string.cancel)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(R.string.playlist_name_empty, 0, false, (materialDialog, charSequence) -> {
                    if (getActivity() == null)
                        return;
                    final String name = charSequence.toString().trim();
                    if (!name.isEmpty()) {
                        if (!Music_Player_Bass_PlaylistsUtil.doesPlaylistExist(getActivity(), name)) {
                            final int playlistId = Music_Player_Bass_PlaylistsUtil.createPlaylist(getActivity(), name);
                            if (getActivity() != null) {
                                //noinspection unchecked
                                ArrayList<Song> songs = getArguments().getParcelableArrayList(SONGS);
                                if (songs != null && !songs.isEmpty()) {
                                    Music_Player_Bass_PlaylistsUtil.addToPlaylist(getActivity(), songs, playlistId, true);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(
                                    R.string.playlist_exists, name), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();
    }
}