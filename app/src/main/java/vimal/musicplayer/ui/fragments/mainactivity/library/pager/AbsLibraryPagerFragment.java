package vimal.musicplayer.ui.fragments.mainactivity.library.pager;

import android.os.Bundle;
import androidx.loader.app.LoaderManager;

import vimal.musicplayer.ui.fragments.AbsMusicServiceFragment;
import vimal.musicplayer.ui.fragments.mainactivity.library.Music_Player_Bass_LibraryFragment;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AbsLibraryPagerFragment extends AbsMusicServiceFragment {

    /* http://stackoverflow.com/a/2888433 */
    @Override
    public LoaderManager getLoaderManager() {
        return getParentFragment().getLoaderManager();
    }

    public Music_Player_Bass_LibraryFragment getLibraryFragment() {
        return (Music_Player_Bass_LibraryFragment) getParentFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
