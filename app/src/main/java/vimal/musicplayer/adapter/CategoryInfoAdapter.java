package vimal.musicplayer.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import vimal.musicplayer.R;
import vimal.musicplayer.model.CategoryInfo;
import vimal.musicplayer.util.Music_Player_Bass_SwipeAndDragHelper;

import java.util.ArrayList;

public class CategoryInfoAdapter extends RecyclerView.Adapter<CategoryInfoAdapter.ViewHolder> implements Music_Player_Bass_SwipeAndDragHelper.ActionCompletionContract {
    private ArrayList<CategoryInfo> categoryInfos;
    private ItemTouchHelper touchHelper;

    public CategoryInfoAdapter(ArrayList<CategoryInfo> categoryInfos) {
        this.categoryInfos = categoryInfos;
        Music_Player_Bass_SwipeAndDragHelper musicPlayerBassSwipeAndDragHelper = new Music_Player_Bass_SwipeAndDragHelper(this);
        touchHelper = new ItemTouchHelper(musicPlayerBassSwipeAndDragHelper);
    }

    @Override
    @NonNull
    public CategoryInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.preference_dialog_library_categories_listitem, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull CategoryInfoAdapter.ViewHolder holder, int position) {
        CategoryInfo categoryInfo = categoryInfos.get(position);

        holder.checkBox.setChecked(categoryInfo.visible);
        holder.title.setText(holder.title.getResources().getString(categoryInfo.category.stringRes));

        holder.itemView.setOnClickListener(v -> {
            if (!(categoryInfo.visible && isLastCheckedCategory(categoryInfo))) {
                categoryInfo.visible = !categoryInfo.visible;
                holder.checkBox.setChecked(categoryInfo.visible);
            } else {
                Toast.makeText(holder.itemView.getContext(), R.string.you_have_to_select_at_least_one_category, Toast.LENGTH_SHORT).show();
            }
        });

        holder.dragView.setOnTouchListener((view, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder);
                    }
                    return false;
                }
        );
    }

    @Override
    public int getItemCount() {
        return categoryInfos.size();
    }

    public void setCategoryInfos(ArrayList<CategoryInfo> categoryInfos) {
        this.categoryInfos = categoryInfos;
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        CategoryInfo categoryInfo = categoryInfos.get(oldPosition);
        categoryInfos.remove(oldPosition);
        categoryInfos.add(newPosition, categoryInfo);
        notifyItemMoved(oldPosition, newPosition);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public ArrayList<CategoryInfo> getCategoryInfos() {
        return categoryInfos;
    }

    private boolean isLastCheckedCategory(CategoryInfo categoryInfo) {
        if (categoryInfo.visible) {
            for (CategoryInfo c : categoryInfos) {
                if (c != categoryInfo && c.visible) return false;
            }
        }
        return true;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView title;
        public View dragView;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox);
            title = view.findViewById(R.id.title);
            dragView = view.findViewById(R.id.drag_view);
        }
    }
}

