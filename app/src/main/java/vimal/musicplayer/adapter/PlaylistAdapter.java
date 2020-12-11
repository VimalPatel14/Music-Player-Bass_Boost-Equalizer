package vimal.musicplayer.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kabouzeid.appthemehelper.util.ATHUtil;
import vimal.musicplayer.App;
import vimal.musicplayer.R;
import vimal.musicplayer.adapter.base.AbsMultiSelectAdapter;
import vimal.musicplayer.adapter.base.MediaEntryViewHolder;
import vimal.musicplayer.dialogs.ClearSmartPlaylistDialog;
import vimal.musicplayer.dialogs.DeletePlaylistDialog;
import vimal.musicplayer.helper.menu.PlaylistMenuHelper;
import vimal.musicplayer.helper.menu.SongsMenuHelper;
import vimal.musicplayer.interfaces.CabHolder;
import vimal.musicplayer.loader.Music_Player_Bass_PlaylistSongLoader;
import vimal.musicplayer.misc.WeakContextAsyncTask;
import vimal.musicplayer.model.AbsCustomPlaylist;
import vimal.musicplayer.model.Playlist;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.model.smartplaylist.AbsSmartPlaylist;
import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;
import vimal.musicplayer.util.Music_Player_Bass_NavigationUtil;
import vimal.musicplayer.util.Music_Player_Bass_PlaylistsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends AbsMultiSelectAdapter<PlaylistAdapter.ViewHolder, Playlist> {

    private static final int SMART_PLAYLIST = 0;
    private static final int DEFAULT_PLAYLIST = 1;

    protected final AppCompatActivity activity;
    protected ArrayList<Playlist> dataSet;
    protected int itemLayoutRes;

    public PlaylistAdapter(AppCompatActivity activity, ArrayList<Playlist> dataSet, @LayoutRes int itemLayoutRes, @Nullable CabHolder cabHolder) {
        super(activity, cabHolder, R.menu.menu_playlists_selection);
        this.activity = activity;
        this.dataSet = dataSet;
        this.itemLayoutRes = itemLayoutRes;
        setHasStableIds(true);
    }

    public ArrayList<Playlist> getDataSet() {
        return dataSet;
    }

    public void swapDataSet(ArrayList<Playlist> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).id;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false);
        return createViewHolder(view, viewType);
    }

    protected ViewHolder createViewHolder(View view, int viewType) {
        return new ViewHolder(view, viewType);
    }

    protected String getPlaylistTitle(Playlist playlist) {
        return playlist.name;
    }

    protected String getPlaylistText(Playlist playlist) {
        Context context = App.getInstance().getApplicationContext();
        return playlist.getInfoString(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Playlist playlist = dataSet.get(position);

        holder.itemView.setActivated(isChecked(playlist));

        if (holder.title != null) {
            holder.title.setText(getPlaylistTitle(playlist));
        }
        if (holder.text != null) {
            holder.text.setText(getPlaylistText(playlist));
        }

        if (holder.getAdapterPosition() == getItemCount() - 1) {
            if (holder.shortSeparator != null) {
                holder.shortSeparator.setVisibility(View.GONE);
            }
        } else {
            if (holder.shortSeparator != null && !(dataSet.get(position) instanceof AbsSmartPlaylist)) {
                holder.shortSeparator.setVisibility(View.VISIBLE);
            }
        }

        if (holder.image != null) {
            holder.image.setImageResource(getIconRes(playlist));
        }
    }

    private int getIconRes(Playlist playlist) {
        if (playlist instanceof AbsSmartPlaylist) {
            return ((AbsSmartPlaylist) playlist).iconRes;
        }
        return Music_Player_Bass_MusicUtil.isFavoritePlaylist(activity, playlist) ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_queue_music_white_24dp;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position) instanceof AbsSmartPlaylist ? SMART_PLAYLIST : DEFAULT_PLAYLIST;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    protected Playlist getIdentifier(int position) {
        return dataSet.get(position);
    }

    @Override
    protected String getName(Playlist playlist) {
        return playlist.name;
    }

    @Override
    protected void onMultipleItemAction(@NonNull MenuItem menuItem, @NonNull ArrayList<Playlist> selection) {
        switch (menuItem.getItemId()) {
            case R.id.action_delete_playlist:
                for (int i = 0; i < selection.size(); i++) {
                    Playlist playlist = selection.get(i);
                    if (playlist instanceof AbsSmartPlaylist) {
                        AbsSmartPlaylist absSmartPlaylist = (AbsSmartPlaylist) playlist;
                        if (absSmartPlaylist.isClearable()) {
                            ClearSmartPlaylistDialog.create(absSmartPlaylist).show(activity.getSupportFragmentManager(), "CLEAR_PLAYLIST_" + absSmartPlaylist.name);
                        }
                        selection.remove(playlist);
                        i--;
                    }
                }
                if (selection.size() > 0) {
                    DeletePlaylistDialog.create(selection).show(activity.getSupportFragmentManager(), "DELETE_PLAYLIST");
                }
                break;
            case R.id.action_save_playlist:
                if (selection.size() == 1) {
                    PlaylistMenuHelper.handleMenuClick(activity, selection.get(0), menuItem);
                } else {
                    new SavePlaylistsAsyncTask(activity).execute(selection);
                }
                break;
            default:
                SongsMenuHelper.handleMenuClick(activity, getSongList(selection), menuItem.getItemId());
                break;
        }
    }

    private static class SavePlaylistsAsyncTask extends WeakContextAsyncTask<ArrayList<Playlist>, String, String> {
        public SavePlaylistsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(ArrayList<Playlist>... params) {
            int successes = 0;
            int failures = 0;

            String dir = "";

            for (Playlist playlist : params[0]) {
                try {
                    dir = Music_Player_Bass_PlaylistsUtil.savePlaylist(App.getInstance().getApplicationContext(), playlist).getParent();
                    successes++;
                } catch (IOException e) {
                    failures++;
                    e.printStackTrace();
                }
            }

            return failures == 0
                    ? String.format(App.getInstance().getApplicationContext().getString(R.string.saved_x_playlists_to_x), successes, dir)
                    : String.format(App.getInstance().getApplicationContext().getString(R.string.saved_x_playlists_to_x_failed_to_save_x), successes, dir, failures);
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Context context = getContext();
            if (context != null) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    private ArrayList<Song> getSongList(@NonNull List<Playlist> playlists) {
        final ArrayList<Song> songs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            if (playlist instanceof AbsCustomPlaylist) {
                songs.addAll(((AbsCustomPlaylist) playlist).getSongs(activity));
            } else {
                songs.addAll(Music_Player_Bass_PlaylistSongLoader.getPlaylistSongList(activity, playlist.id));
            }
        }
        return songs;
    }

    public class ViewHolder extends MediaEntryViewHolder {

        public ViewHolder(@NonNull View itemView, int itemViewType) {
            super(itemView);

            if (itemViewType == SMART_PLAYLIST) {
                if (shortSeparator != null) {
                    shortSeparator.setVisibility(View.GONE);
                }
                itemView.setBackgroundColor(ATHUtil.resolveColor(activity, R.attr.cardBackgroundColor));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setElevation(activity.getResources().getDimensionPixelSize(R.dimen.card_elevation));
                }
            }

            if (image != null) {
                int iconPadding = activity.getResources().getDimensionPixelSize(R.dimen.list_item_image_icon_padding);
                image.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
                image.setColorFilter(ATHUtil.resolveColor(activity, R.attr.iconColor), PorterDuff.Mode.SRC_IN);
            }

            if (menu != null) {
                menu.setOnClickListener(view -> {
                    final Playlist playlist = dataSet.get(getAdapterPosition());
                    final PopupMenu popupMenu = new PopupMenu(activity, view);
                    if (playlist instanceof AbsSmartPlaylist) {
                        popupMenu.inflate(R.menu.menu_item_smart_playlist);
                        final AbsSmartPlaylist smartPlaylist = (AbsSmartPlaylist) playlist;
                        if (!smartPlaylist.isClearable()) {
                            popupMenu.getMenu().findItem(R.id.action_clear_playlist).setVisible(false);
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.action_clear_playlist) {
                                ClearSmartPlaylistDialog.create(smartPlaylist).show(activity.getSupportFragmentManager(), "CLEAR_SMART_PLAYLIST_" + smartPlaylist.name);
                                return true;
                            }
                            return PlaylistMenuHelper.handleMenuClick(
                                activity, dataSet.get(getAdapterPosition()), item);
                        });
                    }
                    else {
                        popupMenu.inflate(R.menu.menu_item_playlist);
                        popupMenu.setOnMenuItemClickListener(item -> {
                            return PlaylistMenuHelper.handleMenuClick(
                                activity, dataSet.get(getAdapterPosition()), item);
                        });
                    }
                    popupMenu.show();
                });
            }
        }

        @Override
        public void onClick(View view) {
            if (isInQuickSelectMode()) {
                toggleChecked(getAdapterPosition());
            } else {
                Playlist playlist = dataSet.get(getAdapterPosition());
                Music_Player_Bass_NavigationUtil.goToPlaylist(activity, playlist);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            toggleChecked(getAdapterPosition());
            return true;
        }
    }
}