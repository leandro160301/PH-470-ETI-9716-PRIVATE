package com.jws.jwsapi.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;

public class AdapterHelper {

    public static int setAnimationSlideInLeft(View viewToAnimate, int position, int lastPositionAdapter, Context context) {
        return animate(position, lastPositionAdapter, context, android.R.anim.slide_in_left, viewToAnimate);
    }

    public static int setAnimationPivot(View viewToAnimate, int position, int lastPositionAdapter, Context context) {
        return animate(position, lastPositionAdapter, context, R.anim.pivot, viewToAnimate);
    }

    private static int animate(int position, int lastPositionAdapter, Context context, int pivot, View viewToAnimate) {
        if (position > lastPositionAdapter) {
            Animation animation = AnimationUtils.loadAnimation(context, pivot);
            viewToAnimate.startAnimation(animation);
            return position;
        }
        return lastPositionAdapter;
    }

    @SuppressWarnings("unused")
    public static void selectItem(int position, RecyclerView recyclerView, Runnable runnable) {
        recyclerView.post(() -> {
            recyclerView.scrollToPosition(position);
            recyclerView.post(() -> {
                recyclerView.smoothScrollToPosition(position);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    viewHolder.itemView.performClick();
                    if (runnable != null) runnable.run();
                } else {
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                                if (viewHolder != null) {
                                    viewHolder.itemView.performClick();
                                    if (runnable != null) runnable.run();
                                }
                                recyclerView.removeOnScrollListener(this);
                            }
                        }
                    });
                }
            });
        });
    }

}
