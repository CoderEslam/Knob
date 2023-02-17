package com.doubleclick.balloonpopuplibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import static com.doubleclick.balloonpopuplibrary.R.style.fade_and_pop;
import static com.doubleclick.balloonpopuplibrary.R.style.fade_and_scale;
import static com.doubleclick.balloonpopuplibrary.R.style.instantin_fade_and_popout;
import static com.doubleclick.balloonpopuplibrary.R.style.instantin_fade_and_scaleout;
import static com.doubleclick.balloonpopuplibrary.R.style.instantin_fadeout;
import static com.doubleclick.balloonpopuplibrary.R.style.instantin_popout;
import static com.doubleclick.balloonpopuplibrary.R.style.instantin_scaleout;
import static com.doubleclick.balloonpopuplibrary.R.style.pop;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Beppi on 14/12/2016.
 */

public class BalloonPopup {
    private Context ctx;
    private View attachView;
    private BalloonGravity gravity;
    private boolean dismissOnTap;
    private boolean stayWithinScreenBounds;
    private int offsetX, offsetY;
    private int bgColor, fgColor;
    private int layoutRes;
    private View customView;
    private String text;
    private int textSize;
    private Drawable drawable;
    private BalloonAnimation balloonAnimation;
    private PopupWindow popupWindow;
    private TextView textView;
    private int timeToLive;

    private BDelay bDelay;
    private View hostedView;

    public enum BalloonShape {
        oval,
        rounded_square,
        little_rounded_square,
        square
    }

    public enum BalloonAnimation {
        pop,
        scale,
        fade,
        fade75,

        fade_and_pop,
        fade_and_scale,
        fade75_and_pop,
        fade75_and_scale,

        instantin_popout,
        instantin_scaleout,
        instantin_fadeout,
        instantin_fade75out,

        instantin_fade_and_popout,
        instantin_fade_and_scaleout,
        instantin_fade75_and_popout,
        instantin_fade75_and_scaleout
    }

    public enum BalloonGravity {
        alltop_allleft,
        alltop_halfleft,
        alltop_center,
        alltop_halfright,
        alltop_allright,
        halftop_allleft,
        halftop_halfleft,
        halftop_center,
        halftop_halfright,
        halftop_allright,
        center_allleft,
        center_halfleft,
        center,
        center_halfright,
        center_allright,
        halfbottom_allleft,
        halfbottom_halfleft,
        halfbottom_center,
        halfbottom_halfright,
        halfbottom_allright,
        allbottom_allleft,
        allbottom_halfleft,
        allbottom_center,
        allbottom_halfright,
        allbottom_allright,
    }

    public BalloonPopup(Context ctx, View attachView, BalloonGravity gravity, boolean dismissOnTap, boolean stayWithinScreenBounds, int offsetX, int offsetY, int bgColor, int fgColor, int layoutRes, View customView, String text, int textSize, Drawable drawable, BalloonAnimation balloonAnimation, int timeToLive) {
        this.ctx = ctx;
        this.attachView = attachView;
        this.gravity = gravity;
        this.dismissOnTap = dismissOnTap;
        this.stayWithinScreenBounds = stayWithinScreenBounds;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.layoutRes = layoutRes;
        this.customView = customView;
        this.text = text;
        this.textSize = textSize;
        this.drawable = drawable;
        this.balloonAnimation = balloonAnimation;
        this.timeToLive = timeToLive;
    }

    public static Builder Builder(Context ctx, View anchorView) {
        return new Builder(ctx, anchorView);
    }

    ScrollView findScrollViewParent(View v) {
        if (v == null) return null;
        else if (v instanceof ScrollView)
            return (ScrollView)v;
        else
            return findScrollViewParent((ViewGroup)v.getParent());
    }

    private void show() {
        if (customView != null) {
            hostedView = customView;
        } else {
            hostedView = ((LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(layoutRes, null);
        }

        if (text != null) {
            textView = (TextView) hostedView.findViewById(R.id.text_view);
            textView.setText(text);
            textView.setTextColor(fgColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float)textSize);
        }

        if (popupWindow == null) { // || !popupWindow.isShowing())
            popupWindow = new PopupWindow(hostedView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (Build.VERSION.SDK_INT >= 21) popupWindow.setElevation(5.0f);
            popupWindow.setFocusable(false);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setTouchable(true);
            popupWindow.setClippingEnabled(false);
            if (drawable != null) {
                drawable.setAlpha(getDrawableAlpha());
                popupWindow.setBackgroundDrawable(drawable);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.setTint(bgColor);
                }
            }

            switch (balloonAnimation) {
                case instantin_fadeout: popupWindow.setAnimationStyle(R.style.instantin_fadeout); break;
                case instantin_popout: popupWindow.setAnimationStyle(R.style.instantin_popout); break;
                case instantin_scaleout: popupWindow.setAnimationStyle(R.style.instantin_scaleout); break;
                case instantin_fade_and_popout: popupWindow.setAnimationStyle(R.style.instantin_fade_and_popout); break;
                case instantin_fade_and_scaleout: popupWindow.setAnimationStyle(R.style.instantin_fade_and_scaleout); break;
                case pop: popupWindow.setAnimationStyle(pop); break;
                case scale: popupWindow.setAnimationStyle(R.style.scale); break;
                case fade: popupWindow.setAnimationStyle(R.style.fade); break;
                case fade_and_pop: popupWindow.setAnimationStyle(R.style.fade_and_pop); break;
                case fade_and_scale: popupWindow.setAnimationStyle(fade_and_scale); break;
                case fade75: popupWindow.setAnimationStyle(R.style.fade75); break;
                case fade75_and_pop: popupWindow.setAnimationStyle(R.style.fade75_and_pop); break;
                case fade75_and_scale: popupWindow.setAnimationStyle(R.style.fade75_and_scale); break;
                case instantin_fade75out: popupWindow.setAnimationStyle(R.style.instantin_fade75out); break;
                case instantin_fade75_and_popout: popupWindow.setAnimationStyle(R.style.instantin_fade75_and_popout); break;
                case instantin_fade75_and_scaleout: popupWindow.setAnimationStyle(R.style.instantin_fade75_and_scaleout); break;
            }
        }

        if (timeToLive > 0) {
            if (bDelay == null)
                bDelay = new BDelay((long) timeToLive, new Runnable() { @Override public void run() { kill(); }});
            else {
                bDelay.updateInterval(timeToLive);
                bDelay.setOnTickHandler(new Runnable() { @Override public void run() { kill(); }});
            }
        }

        if (dismissOnTap) {
            popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    kill();
                    return false;
                }
            });
        }

        draw(true);
    }

    int getDrawableAlpha() {
        if (    balloonAnimation == BalloonAnimation.fade75 ||
                balloonAnimation == BalloonAnimation.fade75_and_pop ||
                balloonAnimation == BalloonAnimation.fade75_and_scale ||
                balloonAnimation == BalloonAnimation.instantin_fade75_and_popout ||
                balloonAnimation == BalloonAnimation.instantin_fade75_and_scaleout ||
                balloonAnimation == BalloonAnimation.instantin_fade75out
                )
            return 192;
        else return 255;
    }

    public void dismiss() {
        kill();
    }

    private void kill() {
        try {  // window could not be attached anymore
            if (popupWindow != null) popupWindow.dismiss();
        } catch (Exception ignored) {}
        if (bDelay != null) bDelay.clear();
    }

    private void draw(final boolean restartLifeTime) {
        // calc position and size, then show

        int[] loc = new int[2];
        attachView.getLocationOnScreen(loc);

//        attachView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        attachView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int widthAttachView = attachView.getMeasuredWidth();
        int heightAttachView = attachView.getMeasuredHeight();

        if (hostedView == null) {
            new BDelay(50, new Runnable() { @Override public void run() { draw(restartLifeTime); }});
            return;
        }
//        hostedView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        hostedView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int widthHostedView = hostedView.getMeasuredWidth();
        int heightHostedView = hostedView.getMeasuredHeight();

        int posX=loc[0] + offsetX;
        switch (gravity) {
            case alltop_allleft: case halftop_allleft: case center_allleft: case halfbottom_allleft: case allbottom_allleft:
                posX -= widthHostedView; break;
            case alltop_halfleft: case halftop_halfleft: case center_halfleft: case halfbottom_halfleft: case allbottom_halfleft:
                posX -= widthHostedView / 2; break;
            case alltop_center: case halftop_center: case center: case halfbottom_center: case allbottom_center:
                posX += widthAttachView / 2 - widthHostedView / 2; break;
            case alltop_halfright: case halftop_halfright: case center_halfright: case halfbottom_halfright: case allbottom_halfright:
                posX += widthAttachView - widthHostedView / 2; break;
            case alltop_allright: case halftop_allright: case center_allright: case halfbottom_allright: case allbottom_allright:
                posX += widthAttachView; break;
        }

        int posY=loc[1] + offsetY;
        switch (gravity) {
            case alltop_allleft: case alltop_halfleft: case alltop_center: case alltop_halfright: case alltop_allright:
                posY -= heightHostedView; break;
            case halftop_allleft: case halftop_halfleft: case halftop_center: case halftop_halfright: case halftop_allright:
                posY -= heightHostedView / 2; break;
            case center_allleft: case center_halfleft: case center: case center_halfright: case center_allright:
                posY += heightAttachView / 2 - heightHostedView / 2; break;
            case halfbottom_allleft: case halfbottom_halfleft: case halfbottom_center: case halfbottom_halfright: case halfbottom_allright:
                posY += heightAttachView - heightHostedView / 2; break;
            case allbottom_allleft: case allbottom_halfleft: case allbottom_center: case allbottom_halfright: case allbottom_allright:
                posY += heightAttachView; break;
        }

        if (stayWithinScreenBounds) {
            posX = Math.max(posX, 0);
            posY = Math.max(posY, 0);

            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            posX = Math.min(metrics.widthPixels - widthHostedView, posX);
            posY = Math.min(metrics.heightPixels - heightHostedView, posY);
        }

        if (restartLifeTime && popupWindow.isShowing()) {
            popupWindow.update(posX, posY, popupWindow.getWidth(), popupWindow.getHeight());
            if (bDelay != null) {
                if (timeToLive == 0)
                    bDelay.clear();
                else
                    bDelay.updateInterval(timeToLive);
            }
            else
                bDelay = new BDelay((long) timeToLive, new Runnable() { @Override public void run() { kill(); }});
        } else {

            attachView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {    // shows the popup when the window is ready or when the layout changes when made in the onCreate() method
                                                                                        // managing the life cycle so that after a ROTATION of the screen the Builder is not run again is left to the application
                @Override public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    if (popupWindow.isShowing()) draw(true);
                }
            });
            final int x=posX;
            final int y=posY;
            attachView.post(new Runnable() {   // prevents crash when attachView is not ready yet because the popup is made in the onCreate() method
                @Override public void run() {
                      popupWindow.showAtLocation(attachView, Gravity.NO_GRAVITY, x, y);
                }});
        }
    }

    public boolean isShowing() {
        if (popupWindow == null) return false;
        return (popupWindow.isShowing());
    }

    public void updateOffset(int newOffsetX, int newOffsetY, boolean restartLifeTime) {
        offsetX = newOffsetX;
        offsetY = newOffsetY;
        draw(restartLifeTime);
    }
    public void updateOffset(int newOffsetX, int newOffsetY) {
        updateOffset(newOffsetX, newOffsetY, true);
    }

    public void updateGravity(BalloonGravity gravity, boolean restartLifeTime) {
        this.gravity = gravity;
        draw(restartLifeTime);
    }
    public void updateGravity(BalloonGravity gravity) {
        updateGravity(gravity, true);
    }

    public void updateText(String newText, boolean restartLifeTime) {
        text = newText;
        textView.setText(text);
        draw (restartLifeTime);
    }
    public void updateText(String newText) {
        updateText(newText, true);
    }
    public void updateText(int newTextRes, boolean restartLifeTime) {
        updateText(ctx.getResources().getString(newTextRes), restartLifeTime);
    }
    public void updateText(int newTextRes) {
        updateText(ctx.getResources().getString(newTextRes), true);
    }

    public void updateTextSize(int textSize, boolean restartLifeTime) {
        this.textSize = textSize;
        textView.setTextSize((float) textSize);
        draw (restartLifeTime);
    }
    public void updateTextSize(int textSize) {
        updateTextSize(textSize, true);
    }

    public void updateFgColor(int fgColor, boolean restartLifeTime) {
        this.fgColor = fgColor;
        textView.setTextColor(fgColor);
        draw (restartLifeTime);
    }
    public void updateFgColor(int fgColor) {
        updateFgColor(fgColor, true);
    }

    public void updateBgColor(int bgColor, boolean restartLifeTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (drawable != null) {
                this.bgColor = bgColor;
                drawable.setTint(bgColor);
                draw (restartLifeTime);
            }
    }
    public void updateBgColor(int bgColor) {
        updateBgColor(bgColor, true);
    }

    public void updateLifeTimeToLive (int milliseconds, boolean restartLifeTime) {
        this.timeToLive = milliseconds;
        draw (restartLifeTime);
    }
    public void updateLifeTimeToLive (int milliseconds) {
        updateLifeTimeToLive(milliseconds, true);
    }

    public void restartLifeTime() {
        if (popupWindow.isShowing()) {
            if (bDelay != null) {
                if (timeToLive == 0)
                    bDelay.clear();
                else
                    bDelay.updateInterval(timeToLive);
            } else
                bDelay = new BDelay((long) timeToLive, new Runnable() {
                    @Override
                    public void run() {
                        kill();
                    }
                });
        }
    }

    public void showAgain() {
        if (popupWindow.isShowing())
            restartLifeTime();
        else
            draw(true);
    }

    public static class Builder {
        private Context ctx;
        private View attachView;
        private BalloonGravity gravity = BalloonGravity.halftop_halfright;
        private boolean dismissOnTap = true;
        private boolean stayWithinScreenBounds = true;
        private int offsetX = 0, offsetY = 0;
        private int bgColor = Color.WHITE, fgColor = Color.BLACK;
        private int layoutRes = R.layout.text_balloon;
        private View customView;
        private String text;
        private int textSize = 12;
        private Drawable drawable;
        private BalloonAnimation balloonAnimation = BalloonAnimation.pop;
        private int timeToLive = 1500;

        Builder(Context ctx, View attachView) {
            this.ctx = ctx;
            this.attachView = attachView;
            this.drawable = ctx.getResources().getDrawable(R.drawable.bg_circle);
        }

        public Builder gravity(BalloonGravity gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder dismissOnTap(boolean dismissOnTap) {
            this.dismissOnTap = dismissOnTap;
            return this;
        }

        public Builder stayWithinScreenBounds(boolean stayWithinScreenBounds) {
            this.stayWithinScreenBounds = stayWithinScreenBounds;
            return this;
        }

        public Builder offsetX(int offsetX) {
            this.offsetX = offsetX;
            return this;
        }
        public Builder offsetY(int offsetY) {
            this.offsetY = offsetY;
            return this;
        }

        public Builder positionOffset(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public Builder bgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder fgColor(int fgColor) {
            this.fgColor = fgColor;
            return this;
        }

        public Builder layoutRes(int layoutRes) {
            this.layoutRes = layoutRes;
            return this;
        }

        public Builder customView(View customView) {
            this.customView = customView;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder text(int textRes) {
            this.text = ctx.getResources().getString(textRes);
            return this;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder shape(BalloonShape balloonShape) {
            switch (balloonShape) {
                case oval: drawable = ctx.getResources().getDrawable(R.drawable.bg_circle); break;
                case rounded_square: drawable = ctx.getResources().getDrawable(R.drawable.bg_rounded_square); break;
                case little_rounded_square: drawable = ctx.getResources().getDrawable(R.drawable.bg_little_rounded_square); break;
                case square: drawable = ctx.getResources().getDrawable(R.drawable.bg_square); break;
            }
            return this;
        }

        public Builder drawable(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }
        public Builder drawable(int drawableRes) {
            this.drawable = ctx.getResources().getDrawable(drawableRes);
            return this;
        }

        public Builder animation(BalloonAnimation balloonAnimation) {
            this.balloonAnimation = balloonAnimation;
            return this;
        }

        public Builder timeToLive(int milliseconds) {
            this.timeToLive = milliseconds;
            return this;
        }

        public BalloonPopup show() {
            BalloonPopup bp = new BalloonPopup(ctx, attachView, gravity, dismissOnTap, stayWithinScreenBounds, offsetX, offsetY, bgColor, fgColor, layoutRes, customView, text, textSize, drawable, balloonAnimation, timeToLive);

            bp.show();
            return bp;
        }
    }
}
