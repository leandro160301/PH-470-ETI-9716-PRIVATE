package com.jws.jwsapi.general.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomSpinnerListView extends ListView {

    private boolean isTouchingScrollbar = false;

    public CustomSpinnerListView(Context context) {
        super(context);
    }

    public CustomSpinnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchingScrollbar = isTouchOnScrollbar(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouchingScrollbar = false;
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchingScrollbar) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float y = event.getY();
                    int scrollRange = computeVerticalScrollRange() - computeVerticalScrollExtent();
                    float proportion = y / (float) getHeight();
                    int targetScroll = (int) (scrollRange * proportion);
                    scrollBy(0, targetScroll - computeVerticalScrollOffset());
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isTouchingScrollbar = false;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchOnScrollbar(MotionEvent event) {
        int scrollBarWidth = getVerticalScrollbarWidth();
        int x = (int) event.getX();
        int viewWidth = getWidth();
        return x >= (viewWidth - scrollBarWidth);
    }
}