package vimal.musicplayer.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vimal.musicplayer.adapter.base.MediaEntryViewHolder;
import vimal.musicplayer.model.Genre;
import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;
import vimal.musicplayer.util.Music_Player_Bass_NavigationUtil;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    @NonNull
    private final AppCompatActivity activity;
    private ArrayList<Genre> dataSet;
    private int itemLayoutRes;

    public GenreAdapter(@NonNull AppCompatActivity activity, ArrayList<Genre> dataSet, @LayoutRes int itemLayoutRes) {
        this.activity = activity;
        this.dataSet = dataSet;
        this.itemLayoutRes = itemLayoutRes;
    }

    public ArrayList<Genre> getDataSet() {
        return dataSet;
    }

    public void swapDataSet(ArrayList<Genre> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return dataSet.get(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Genre genre = dataSet.get(position);

        if (holder.getAdapterPosition() == getItemCount() - 1) {
            if (holder.separator != null) {
                holder.separator.setVisibility(View.GONE);
            }
        } else {
            if (holder.separator != null) {
                holder.separator.setVisibility(View.VISIBLE);
            }
        }
        if (holder.shortSeparator != null) {
            holder.shortSeparator.setVisibility(View.GONE);
        }
        if (holder.menu != null) {
            holder.menu.setVisibility(View.GONE);
        }
        if (holder.title != null) {
            holder.title.setText(genre.name);
        }
        if (holder.text != null) {
            holder.text.setText(Music_Player_Bass_MusicUtil.getGenreInfoString(activity, genre));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        final Genre genre = dataSet.get(position);
        return genre.id == -1 ? "" : Music_Player_Bass_MusicUtil.getSectionName(dataSet.get(position).name);
    }

    public class ViewHolder extends MediaEntryViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            Genre genre = dataSet.get(getAdapterPosition());
            Music_Player_Bass_NavigationUtil.goToGenre(activity, genre);
        }
    }
}
