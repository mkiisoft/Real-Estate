package uk.co.chrisjenx.paralloid.views;

import com.renderas.soldty.utils.ScrollDirectionListener;
import com.renderas.soldty.utils.ScrollViewScrollDetector;
import com.renderas.soldty.utils.ObservableScrollView.OnScrollChangedListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ScrollView;

import uk.co.chrisjenx.paralloid.ParallaxViewController;
import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.transform.Transformer;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView.ScrollViewScrollDetectorImpl;

/**
 * Created by chris on 02/10/2013
 * Project: Paralloid
 */
public class ParallaxScrollView extends ScrollView implements Parallaxor {

    ParallaxViewController mParallaxViewController;
    private OnScrollViewListener mOnScrollViewListener;
	private View mBackground;
	private View mScrollView;
	private int mScrollDiff;
	
	private boolean mVisible;
	
	private static final int TRANSLATE_DURATION_MILLIS = 200;
	
	private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	private ScrollViewScrollDetectorImpl ScrollViewScrollDetectorImpl;

    public ParallaxScrollView(Context context) {
        super(context);
        init();
    }
    
    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    public ParallaxScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallaxScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mParallaxViewController = ParallaxViewController.wrap(this);
    }

    @Override
    public void parallaxViewBy(View view, float multiplier) {
        mParallaxViewController.parallaxViewBy(view, multiplier);
    }

    @Override
    public void parallaxViewBy(View view, Transformer transformer, float multiplier) {
        mParallaxViewController.parallaxViewBy(view, transformer, multiplier);
    }

    @Override
    public void parallaxViewBackgroundBy(View view, Drawable drawable, float multiplier) {
        mParallaxViewController.parallaxViewBackgroundBy(view, drawable, multiplier);
    }
    
    public interface OnScrollViewListener {
        void onScrollChanged(ParallaxScrollView v, int l, int t, int oldl, int oldt );
    }
    
    public void setOnScrollViewListener(OnScrollViewListener l) {
        this.mOnScrollViewListener = l;
        
    }
    
    private boolean hasHoneycombApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
    
    public void show() {
        show(true);
    }

    public void hide() {
        hide(true);
    }
    
    public void show(boolean animate) {
        toggle(true, animate, false);
    }

    public void hide(boolean animate) {
        toggle(false, animate, false);
    }
    
    private void toggle(final boolean visible, final boolean animate, boolean force) {
        if (mVisible != visible || force) {
            mVisible = visible;
            int height = getHeight();
            if (height == 0 && !force) {
                ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            ViewTreeObserver currentVto = getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            toggle(visible, animate, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int translationY = visible ? 0 : height + getBottom();
            if (animate) {
                ViewPropertyAnimator.animate(this).setInterpolator(mInterpolator)
                    .setDuration(TRANSLATE_DURATION_MILLIS)
                    .translationY(translationY);
            } else {
                ViewHelper.setTranslationY(this, translationY);
            }

            // On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            if (!hasHoneycombApi()) {
                setClickable(visible);
            }
        }
    }
    
    public abstract class ScrollViewScrollDetectorImpl extends ScrollViewScrollDetector {
        private ScrollDirectionListener mListener;

        public void setListener(ScrollDirectionListener scrollDirectionListener) {
            mListener = scrollDirectionListener;
        }
        
        public void onScrollDown() {
            show();
            if (mListener != null) {
                mListener.onScrollDown();
            }
        }

        public void onScrollUp() {
            hide();
            if (mListener != null) {
                mListener.onScrollUp();
            }
        }

    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mOnScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        mParallaxViewController.onScrollChanged(this, l, t, oldl, oldt);
    }
    
}
