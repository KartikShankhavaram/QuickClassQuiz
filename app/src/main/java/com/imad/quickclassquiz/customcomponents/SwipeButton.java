package com.imad.quickclassquiz.customcomponents;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imad.quickclassquiz.R;

public class SwipeButton extends RelativeLayout {
    private ImageView slidingButton;
    private float initialX;
    private boolean active;
    private int initialButtonWidth;
    private TextView centerText;
    private float startedFrom;
    private Drawable disabledDrawable;
    private Drawable enabledDrawable;

    public SwipeButton(Context context) {
        super(context);

        init(context, null, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(21)
    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        RelativeLayout background = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        background.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded));

        addView(background, layoutParamsView);

        final TextView centerText = new TextView(context);
        this.centerText = centerText;

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        centerText.setText(R.string.test_submit_button_text); //add any text you need
        centerText.setTextColor(Color.WHITE);
        centerText.setPadding(35, 35, 35, 35);
        background.addView(centerText, layoutParams);

        final ImageView swipeButton = new ImageView(context);
        this.slidingButton = swipeButton;

        disabledDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_submit_outline);
        enabledDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_submit_filled);

        slidingButton.setImageDrawable(disabledDrawable);
        slidingButton.setPadding(40, 40, 40, 40);

        LayoutParams layoutParamsButton = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        swipeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
        swipeButton.setImageDrawable(disabledDrawable);
        addView(swipeButton, layoutParamsButton);

        setOnTouchListener(getButtonTouchListener());
    }

    private OnTouchListener getButtonTouchListener() {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startedFrom = event.getX();
                    return event.getX() < slidingButton.getX() + slidingButton.getWidth();
                case MotionEvent.ACTION_MOVE:
                    if (initialX == 0) {
                        initialX = slidingButton.getX();
                    }
                    if (startedFrom > slidingButton.getX() + slidingButton.getWidth())
                        return false;
                    if (event.getX() > initialX + slidingButton.getWidth() / 2 &&
                            event.getX() + slidingButton.getWidth() / 2 < getWidth()) {
                        slidingButton.setX(event.getX() - slidingButton.getWidth() / 2);
                        centerText.setAlpha(1 - 1.3f * (slidingButton.getX() + slidingButton.getWidth()) / getWidth());
                    }

                    if (event.getX() + slidingButton.getWidth() / 2 > getWidth() &&
                            slidingButton.getX() + slidingButton.getWidth() / 2 < getWidth()) {
                        slidingButton.setX(getWidth() - slidingButton.getWidth());
                    }

                    if (event.getX() < slidingButton.getWidth() / 2 &&
                            slidingButton.getX() > 0) {
                        slidingButton.setX(0);
                    }
                    Resources r = getResources();
                    Drawable[] layers = new Drawable[2];
                    layers[1] = r.getDrawable(R.drawable.ic_submit_filled);
                    layers[0] = r.getDrawable(R.drawable.ic_submit_outline);
                    int alphaValue = (int)(256 * (slidingButton.getX() + slidingButton.getWidth() / 2) / getWidth());
                    layers[1].setAlpha(alphaValue);
                    layers[0].setAlpha(256 - alphaValue);
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    slidingButton.setImageDrawable(layerDrawable);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (active) {
                        collapseButton();
                    } else {
                        initialButtonWidth = slidingButton.getWidth();

                        if (slidingButton.getX() + slidingButton.getWidth() > getWidth() * 0.99) {
                            //expandButton();
                            v.performClick();
                        } else {
                            moveButtonBack();
                        }
                    }
                    return true;
            }

            return false;
        };
    }

    private void expandButton() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(slidingButton.getX(), 0);
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            slidingButton.setX(x);
        });


        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                slidingButton.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = slidingButton.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            slidingButton.setLayoutParams(params);
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                active = true;
                slidingButton.setImageDrawable(enabledDrawable);
            }
        });

        animatorSet.playTogether(positionAnimator, widthAnimator);
        animatorSet.start();
    }

    private void collapseButton() {
        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                slidingButton.getWidth(),
                initialButtonWidth);

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = slidingButton.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            slidingButton.setLayoutParams(params);
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = false;
                slidingButton.setImageDrawable(disabledDrawable);
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(objectAnimator, widthAnimator);
        animatorSet.start();
    }

    private void moveButtonBack() {
        Resources r = getResources();
        Drawable[] layers = new Drawable[2];
        layers[1] = r.getDrawable(R.drawable.ic_submit_filled);
        layers[0] = r.getDrawable(R.drawable.ic_submit_outline);
        layers[1].setAlpha(0);
        layers[0].setAlpha(255);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        slidingButton.setImageDrawable(layerDrawable);

        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(slidingButton.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            slidingButton.setX(x);
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);

        positionAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, positionAnimator);
        animatorSet.start();
    }

    public void shrinkButton() {
        moveButtonBack();
    }
}