package vimal.musicplayer.ui.fragments.mainactivity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import vimal.musicplayer.ui.activities.MainActivityMusicPlayerBass;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public abstract class AbsMainActivityFragment extends Fragment {

    public MainActivityMusicPlayerBass getMainActivity() {
        return (MainActivityMusicPlayerBass) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
