package com.jws.jwsapi.feature.formulador.ui.animations;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jws.jwsapi.R;

public class AnimationsAdapter {
    public static int setAnimationSlideInLeft(View viewToAnimate, int position, int lastPositionAdapter, Context context) {
        if (position > lastPositionAdapter) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            return position;
        }
        return lastPositionAdapter;
    }
    public static int setAnimationPivot(View viewToAnimate, int position, int lastPositionAdapter, Context context) {
        if (position > lastPositionAdapter) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pivot);
            viewToAnimate.startAnimation(animation);
            return position;
        }
        return lastPositionAdapter;
    }
}
