package vimal.musicplayer.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;
import vimal.musicplayer.glide.artistimage.ArtistImage;
import vimal.musicplayer.glide.artistimage.ArtistImageLoader;
import vimal.musicplayer.glide.audiocover.AudioFileCover;
import vimal.musicplayer.glide.audiocover.AudioFileCoverLoader;

import java.io.InputStream;


public class Music_Player_Bass_PhonographGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(AudioFileCover.class, InputStream.class, new AudioFileCoverLoader.Factory());
        glide.register(ArtistImage.class, InputStream.class, new ArtistImageLoader.Factory(context));
    }
}
