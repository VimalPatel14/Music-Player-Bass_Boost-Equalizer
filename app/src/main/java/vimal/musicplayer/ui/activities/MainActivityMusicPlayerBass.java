package vimal.musicplayer.ui.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.NavigationViewUtil;

import vimal.musicplayer.R;
import vimal.musicplayer.dialogs.ChangelogDialog;
import vimal.musicplayer.dialogs.Music_Player_Bass_SleepTimerDialog;
import vimal.musicplayer.dialogs.ScanMediaFolderChooserDialog;
import vimal.musicplayer.glide.Music_Player_Bass_SongGlideRequest;
import vimal.musicplayer.helper.MusicPlayerRemote;
import vimal.musicplayer.helper.Music_Player_Bass_SearchQueryHelper;
import vimal.musicplayer.loader.Music_Player_Bass_AlbumLoader;
import vimal.musicplayer.loader.Music_Player_Bass_ArtistLoader;
import vimal.musicplayer.loader.Music_Player_Bass_PlaylistSongLoader;
import vimal.musicplayer.model.Song;
import vimal.musicplayer.service.MusicService;
import vimal.musicplayer.ui.activities.base.Music_Player_Bass_AbsSlidingMusicPanelActivity;
import vimal.musicplayer.ui.activities.intro.AppIntroActivity;
import vimal.musicplayer.ui.fragments.mainactivity.folders.Music_Player_Bass_FoldersFragment;
import vimal.musicplayer.ui.fragments.mainactivity.library.Music_Player_Bass_LibraryFragment;
import vimal.musicplayer.util.Glob;
import vimal.musicplayer.util.Music_Player_Bass_MusicUtil;
import vimal.musicplayer.util.Music_Player_Bass_PreferenceUtil;

import vimal.musicplayer.util.Music_Player_Bass_AdjustBitmap;
import vimal.musicplayer.util.Music_Player_Bass__Utils;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityMusicPlayerBass extends Music_Player_Bass_AbsSlidingMusicPanelActivity {

    public static final String TAG = MainActivityMusicPlayerBass.class.getSimpleName();
    public static final int APP_INTRO_REQUEST = 100;
    public static final int PURCHASE_REQUEST = 101;
    public static final int RESULT_FROM_GALLERY = 2;
    private static final int LIBRARY = 0;
    private static final int FOLDERS = 1;
    private Uri selectedImageUri;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    Bitmap bmp;
    int screenWidth, screenHeight;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Nullable
    MainActivityFragmentCallbacks currentFragment;
    SharedPreferences settings;
    @Nullable
    private View navigationDrawerHeader;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private boolean blockRequestPermissions;
    String path;
    File file;
    private Uri mImageSavedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar();
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            navigationView.setFitsSystemWindows(false); // for header to go below statusbar
        }

        setUpDrawerLayout();

        if (savedInstanceState == null) {
            setMusicChooser(Music_Player_Bass_PreferenceUtil.getInstance(this).getLastMusicChooser());
        } else {
            restoreCurrentFragment();
        }

        if (!checkShowIntro()) {
            showChangelog();
        }


        DisplayMetrics metrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        settings = getSharedPreferences("YOUR_PREF_NAME", 0);
        int snowDensity = settings.getInt("SNOW_DENSITY", 1);
        Log.e("selectedImageUri", snowDensity + "");

        if (snowDensity == 2) {

            String path = Environment.getExternalStorageDirectory() + "/"
                    + "music" + "/" + "mv_image" + ".png";
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            drawerLayout.setBackground(d);

        }





    }

    private void setMusicChooser(int key) {


        Music_Player_Bass_PreferenceUtil.getInstance(this).setLastMusicChooser(key);
        switch (key) {
            case LIBRARY:
                navigationView.setCheckedItem(R.id.nav_library);
                setCurrentFragment(Music_Player_Bass_LibraryFragment.newInstance());
                break;
            case FOLDERS:
                navigationView.setCheckedItem(R.id.nav_folders);
                setCurrentFragment(Music_Player_Bass_FoldersFragment.newInstance(this));
                break;
        }
    }

    private void setCurrentFragment(@SuppressWarnings("NullableProblems") Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, null).commit();
        currentFragment = (MainActivityFragmentCallbacks) fragment;
    }

    private void restoreCurrentFragment() {
        currentFragment = (MainActivityFragmentCallbacks) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_INTRO_REQUEST) {
            blockRequestPermissions = false;
            if (!hasPermissions()) {
                requestPermissions();
            }// good chance that pro version check was delayed on first start
        } else if (requestCode == PURCHASE_REQUEST) {
            if (resultCode == RESULT_OK) {

            }
        }

        if (resultCode == RESULT_OK) {
            Intent intent2;
            switch (requestCode) {
                case RESULT_FROM_GALLERY:


                    selectedImageUri = data.getData();
                    Music_Player_Bass__Utils.selectedImageUri = selectedImageUri;


                    settings = getSharedPreferences("YOUR_PREF_NAME", 0);
                    editor = settings.edit();
                    editor.putInt("SNOW_DENSITY", 2);
                    editor.commit();

                    Log.e("selectedImageUri", Music_Player_Bass__Utils.selectedImageUri.toString());

                    try {
                        bmp = Music_Player_Bass_AdjustBitmap
                                .getCorrectlyOrientedImage(getApplicationContext(),
                                        selectedImageUri, screenHeight);
                        bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
//                        saveToInternalStorage(bmp);
                        Drawable d = new BitmapDrawable(getResources(), bmp);

                        path = getOutPutPath();
                        file = new File(path);
                        if (file.exists()) {
                            file.delete();
                        }

                        FileOutputStream fos = null;
                        mImageSavedUri = Uri.parse("file://" + file.getPath());
                        fos = new FileOutputStream(file);
                        bmp = Music_Player_Bass__Utils.TrimBitmap(bmp);
                        bmp.compress(Bitmap.CompressFormat.PNG, 80, fos);
                        fos.flush();
                        fos.close();

                        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, new String[]{"image/png"},
                                null);

                        refreshGallery(file);

                        drawerLayout.setBackground(d);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

//                    Intent newIntent2 = new Intent(MainActivityMusicPlayerBass.this, CropActivity.class);
//                    startActivity(newIntent2);

                    break;
            }
        }
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    public String getOutPutPath() {
        // TODO Auto-generated method stub

        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "music");

        if (!folder.exists()) {
            folder.mkdirs();
            return Environment.getExternalStorageDirectory() + "/"
                    + "music" + "/" + "mv_image" + ".png";
        } else {
            return Environment.getExternalStorageDirectory() + "/"
                    + "music" + "/" + "mv_image"
                    + ".png";
        }
    }

    @Override
    protected void requestPermissions() {
        if (!blockRequestPermissions) super.requestPermissions();
    }

    @Override
    protected View createContentView() {
        @SuppressLint("InflateParams")
        View contentView = getLayoutInflater().inflate(R.layout.activity_main_drawer_layout, null);
        ViewGroup drawerContent = contentView.findViewById(R.id.drawer_content_container);
        drawerContent.addView(wrapSlidingMusicPanel(R.layout.activity_main_content));
        return contentView;
    }

    private void setUpNavigationView() {
        int accentColor = ThemeStore.accentColor(this);
        NavigationViewUtil.setItemIconColors(navigationView, ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), accentColor);
        NavigationViewUtil.setItemTextColors(navigationView, ThemeStore.textColorPrimary(this), accentColor);


        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.nav_library:
                    new Handler().postDelayed(() -> setMusicChooser(LIBRARY), 200);
                    break;
                case R.id.nav_folders:
                    new Handler().postDelayed(() -> setMusicChooser(FOLDERS), 200);
                    break;

                case R.id.nav_equalizer:

                        new Handler().postDelayed(() -> startActivity(new Intent(MainActivityMusicPlayerBass.this, Music_Player_Bass_EqualizerActivity.class)), 200);

                    break;
                case R.id.nav_cam:
                    Intent it_gallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(it_gallary, RESULT_FROM_GALLERY);
                    break;
                case R.id.nav_sleep_timer:

                        new Music_Player_Bass_SleepTimerDialog().show(getSupportFragmentManager(), "SET_SLEEP_TIMER");

                    break;
                case R.id.action_scan:

                        new Handler().postDelayed(() -> {
                            ScanMediaFolderChooserDialog dialog = ScanMediaFolderChooserDialog.create();
                            dialog.show(getSupportFragmentManager(), "SCAN_MEDIA_FOLDER_CHOOSER");
                        }, 200);

                    break;
                case R.id.nav_settings:

                        new Handler().postDelayed(() -> startActivity(new Intent(MainActivityMusicPlayerBass.this, Music_Player_Bass_SettingsActivity.class)), 200);

                    break;
                case R.id.nav_about:
                    new Handler().postDelayed(() -> startActivity(new Intent(MainActivityMusicPlayerBass.this, Music_Player_Bass_AboutActivity.class)), 200);
                    break;
                case R.id.nav_rate:
                    gotoStore();
                    break;
                case R.id.nav_share:
                    if (!isOnline()) {
                        Toast.makeText(this, "No Internet Connection..", Toast.LENGTH_SHORT).show();
                    }
                    moreapp();
                    break;
            }
            return true;
        });
    }



    public void gotoStore() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You don't have Google Play installed", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        @SuppressLint("WrongConstant") NetworkInfo netInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }

    private void share() {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/*");
        shareIntent.putExtra("android.intent.extra.TEXT", Glob.app_link);
        shareIntent.putExtra("android.intent.extra.STREAM", Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeResource(getResources(), R.drawable.banner), null, null)));
        startActivity(Intent.createChooser(shareIntent, "Share App using"));
    }

    private void moreapp() {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Glob.acc_link)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You don't have Google Play installed", Toast.LENGTH_SHORT).show();
        }
    }



    private void setUpDrawerLayout() {
        setUpNavigationView();
    }

    private void updateNavigationDrawerHeader() {
        if (!MusicPlayerRemote.getPlayingQueue().isEmpty()) {
            Song song = MusicPlayerRemote.getCurrentSong();
            if (navigationDrawerHeader == null) {
                navigationDrawerHeader = navigationView.inflateHeaderView(R.layout.navigation_drawer_header);
                //noinspection ConstantConditions
                navigationDrawerHeader.setOnClickListener(v -> {
                    drawerLayout.closeDrawers();
                    if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        expandPanel();
                    }
                });
            }
            ((TextView) navigationDrawerHeader.findViewById(R.id.title)).setText(song.title);
            ((TextView) navigationDrawerHeader.findViewById(R.id.text)).setText(Music_Player_Bass_MusicUtil.getSongInfoString(song));
            Music_Player_Bass_SongGlideRequest.Builder.from(Glide.with(this), song)
                    .checkIgnoreMediaStore(this).build()
                    .into(((ImageView) navigationDrawerHeader.findViewById(R.id.image)));
        } else {
            if (navigationDrawerHeader != null) {
                navigationView.removeHeaderView(navigationDrawerHeader);
                navigationDrawerHeader = null;
            }
        }
    }

    @Override
    public void onPlayingMetaChanged() {
        super.onPlayingMetaChanged();
        updateNavigationDrawerHeader();
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        updateNavigationDrawerHeader();
        handlePlaybackIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleBackPress() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
            return true;
        }
        return super.handleBackPress() || (currentFragment != null && currentFragment.handleBackPress());
    }

    private void handlePlaybackIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();
        String mimeType = intent.getType();
        boolean handled = false;

        if (intent.getAction() != null && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
            final ArrayList<Song> songs = Music_Player_Bass_SearchQueryHelper.getSongs(this, intent.getExtras());
            if (MusicPlayerRemote.getShuffleMode() == MusicService.SHUFFLE_MODE_SHUFFLE) {
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
            } else {
                MusicPlayerRemote.openQueue(songs, 0, true);
            }
            handled = true;
        }

        if (uri != null && uri.toString().length() > 0) {
            MusicPlayerRemote.playFromUri(uri);
            handled = true;
        } else if (MediaStore.Audio.Playlists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "playlistId", "playlist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                ArrayList<Song> songs = new ArrayList<>(Music_Player_Bass_PlaylistSongLoader.getPlaylistSongList(this, id));
                MusicPlayerRemote.openQueue(songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Albums.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "albumId", "album");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(Music_Player_Bass_AlbumLoader.getAlbum(this, id).songs, position, true);
                handled = true;
            }
        } else if (MediaStore.Audio.Artists.CONTENT_TYPE.equals(mimeType)) {
            final int id = (int) parseIdFromIntent(intent, "artistId", "artist");
            if (id >= 0) {
                int position = intent.getIntExtra("position", 0);
                MusicPlayerRemote.openQueue(Music_Player_Bass_ArtistLoader.getArtist(this, id).getSongs(), position, true);
                handled = true;
            }
        }
        if (handled) {
            setIntent(new Intent());
        }
    }

    private long parseIdFromIntent(@NonNull Intent intent, String longKey,
                                   String stringKey) {
        long id = intent.getLongExtra(longKey, -1);
        if (id < 0) {
            String idString = intent.getStringExtra(stringKey);
            if (idString != null) {
                try {
                    id = Long.parseLong(idString);
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return id;
    }

    @Override
    public void onPanelExpanded(View view) {
        super.onPanelExpanded(view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onPanelCollapsed(View view) {
        super.onPanelCollapsed(view);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private boolean checkShowIntro() {
        if (!Music_Player_Bass_PreferenceUtil.getInstance(this).introShown()) {
            Music_Player_Bass_PreferenceUtil.getInstance(this).setIntroShown();
            ChangelogDialog.setChangelogRead(this);
            blockRequestPermissions = true;
            new Handler().postDelayed(() -> startActivityForResult(new Intent(MainActivityMusicPlayerBass.this, AppIntroActivity.class), APP_INTRO_REQUEST), 50);
            return true;
        }
        return false;
    }

    private void showChangelog() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int currentVersion = pInfo.versionCode;
            if (currentVersion != Music_Player_Bass_PreferenceUtil.getInstance(this).getLastChangelogVersion()) {
                ChangelogDialog.create().show(getSupportFragmentManager(), "CHANGE_LOG_DIALOG");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface MainActivityFragmentCallbacks {
        boolean handleBackPress();
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
