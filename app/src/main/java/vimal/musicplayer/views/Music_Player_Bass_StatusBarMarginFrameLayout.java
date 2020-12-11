package vimal.musicplayer.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class Music_Player_Bass_StatusBarMarginFrameLayout extends FrameLayout {


    public Music_Player_Bass_StatusBarMarginFrameLayout(Context context) {
        super(context);
    }

    public Music_Player_Bass_StatusBarMarginFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Music_Player_Bass_StatusBarMarginFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            lp.topMargin = insets.getSystemWindowInsetTop();
            setLayoutParams(lp);
        }
        return super.onApplyWindowInsets(insets);
    }
}
