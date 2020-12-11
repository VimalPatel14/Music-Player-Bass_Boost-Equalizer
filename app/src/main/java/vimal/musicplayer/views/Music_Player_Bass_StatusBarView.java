package vimal.musicplayer.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

public class Music_Player_Bass_StatusBarView extends View {

    public Music_Player_Bass_StatusBarView(Context context) {
        super(context);
    }

    public Music_Player_Bass_StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Music_Player_Bass_StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            lp.height = insets.getSystemWindowInsetTop();
            setLayoutParams(lp);
        }
        return super.onApplyWindowInsets(insets);
    }

}
