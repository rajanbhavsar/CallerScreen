package com.silvertouch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.silvertouch.sliderdemo.R;

/**
 * Created by leandroferreira on 07/03/17.
 */

public class SwipeButton extends RelativeLayout {

    private static final int ENABLED = 0;
    private static final int DISABLED = 1;

    private ImageView swipeButtonInner;
    private float initialX;
    private boolean active;
    //    private TextView centerText;
    private ViewGroup background;

    private Drawable disabledDrawable;
    private Drawable enabledDrawable;

    private OnStateChangeListener onStateChangeListener;
    private OnActiveListener onActiveListener;

    private int collapsedWidth;
    private int collapsedHeight;

    private LinearLayout layer;
    private boolean trailEnabled = false;
    private boolean hasActivationState;
    private boolean isCallAccepted = false;

    private float buttonLeftPadding;
    private float buttonTopPadding;
    private float buttonRightPadding;
    private float buttonBottomPadding;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public SwipeButton(Context context) {
        super(context);

        init(context, null, -1, -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public SwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, -1, -1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(21)
    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isActive() {
        return active;
    }

    public void setText(String text) {
//        centerText.setText(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setSlidingButtonBackground(Drawable drawable) {
        background.setBackground(drawable);
    }

    public void setDisabledDrawable(Drawable drawable) {
        disabledDrawable = drawable;

        if (!active) {
            swipeButtonInner.setImageDrawable(drawable);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setButtonBackground(Drawable buttonBackground) {
        if (buttonBackground != null) {
            swipeButtonInner.setBackground(buttonBackground);
        }
    }

    public void setEnabledDrawable(Drawable drawable) {
        enabledDrawable = drawable;

        if (active) {
            swipeButtonInner.setImageDrawable(drawable);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setOnActiveListener(OnActiveListener onActiveListener) {
        this.onActiveListener = onActiveListener;
    }

    public void setInnerTextPadding(int left, int top, int right, int bottom) {
//        centerText.setPadding(left, top, right, bottom);
    }

    public void setSwipeButtonPadding(int left, int top, int right, int bottom) {
        swipeButtonInner.setPadding(left, top, right, bottom);
    }

    public void setHasActivationState(boolean hasActivationState) {
        this.hasActivationState = hasActivationState;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        hasActivationState = true;

        background = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(background, layoutParamsView);

        final TextView centerText = new TextView(context);
//        this.centerText = centerText;
        centerText.setGravity(Gravity.CENTER);

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        background.addView(centerText, layoutParams);

        final ImageView swipeButton = new ImageView(context);
        this.swipeButtonInner = swipeButton;

        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeButton,
                    defStyleAttr, defStyleRes);

            collapsedWidth = (int) typedArray.getDimension(R.styleable.SwipeButton_button_image_width,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            collapsedHeight = (int) typedArray.getDimension(R.styleable.SwipeButton_button_image_height,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            trailEnabled = typedArray.getBoolean(R.styleable.SwipeButton_button_trail_enabled,
                    false);
            Drawable trailingDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_trail_drawable);

            Drawable backgroundDrawable = typedArray.getDrawable(R.styleable.SwipeButton_inner_text_background);

            if (backgroundDrawable != null) {
                background.setBackground(backgroundDrawable);
            } else {
                background.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rounded));
            }

            if (trailEnabled) {
                layer = new LinearLayout(context);

                if (trailingDrawable != null) {
                    layer.setBackground(trailingDrawable);
                } else {
                    layer.setBackground(typedArray.getDrawable(R.styleable.SwipeButton_button_background));
                }

                layer.setGravity(Gravity.START);
                layer.setVisibility(View.GONE);
                background.addView(layer, layoutParamsView);
            }

            centerText.setText(typedArray.getText(R.styleable.SwipeButton_inner_text));
            centerText.setTextColor(typedArray.getColor(R.styleable.SwipeButton_inner_text_color,
                    Color.WHITE));

            float textSize = DimentionUtils.converPixelsToSp(
                    typedArray.getDimension(R.styleable.SwipeButton_inner_text_size, 0), context);

            if (textSize != 0) {
                centerText.setTextSize(textSize);
            } else {
                centerText.setTextSize(12);
            }

            disabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_disabled);
            enabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_enabled);
            float innerTextLeftPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_left_padding, 0);
            float innerTextTopPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_top_padding, 0);
            float innerTextRightPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_right_padding, 0);
            float innerTextBottomPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_bottom_padding, 0);

            int initialState = typedArray.getInt(R.styleable.SwipeButton_initial_state, DISABLED);

            if (initialState == ENABLED) {
                LayoutParams layoutParamsButton = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParamsButton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                swipeButtonInner.setImageDrawable(enabledDrawable);

                addView(swipeButtonInner, layoutParamsButton);

                active = true;
            } else {
                LayoutParams layoutParamsButton = new LayoutParams(collapsedWidth, collapsedHeight);

                layoutParamsButton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                swipeButtonInner.setImageDrawable(disabledDrawable);

                addView(swipeButtonInner, layoutParamsButton);

                active = false;
            }

            centerText.setPadding((int) innerTextLeftPadding,
                    (int) innerTextTopPadding,
                    (int) innerTextRightPadding,
                    (int) innerTextBottomPadding);

            Drawable buttonBackground = typedArray.getDrawable(R.styleable.SwipeButton_button_background);

            if (buttonBackground != null) {
                swipeButtonInner.setBackground(buttonBackground);
            } else {
                swipeButtonInner.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_button));
            }

            buttonLeftPadding = typedArray.getDimension(R.styleable.SwipeButton_button_left_padding, 0);
            buttonTopPadding = typedArray.getDimension(R.styleable.SwipeButton_button_top_padding, 0);
            buttonRightPadding = typedArray.getDimension(R.styleable.SwipeButton_button_right_padding, 0);
            buttonBottomPadding = typedArray.getDimension(R.styleable.SwipeButton_button_bottom_padding, 0);

            swipeButtonInner.setPadding((int) buttonLeftPadding,
                    (int) buttonTopPadding,
                    (int) buttonRightPadding,
                    (int) buttonBottomPadding);

            hasActivationState = typedArray.getBoolean(R.styleable.SwipeButton_has_activate_state, true);

            typedArray.recycle();
        }

        setOnTouchListener(getButtonTouchListener());
    }

    private OnTouchListener getButtonTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return !TouchUtils.isTouchOutsideInitialPosition(event, swipeButtonInner);
                    case MotionEvent.ACTION_MOVE:
                        if (initialX == 0) {
                            initialX = swipeButtonInner.getX();
                            if (getWidth() != 0 || getWidth() > 0) {
                                initialX = getWidth() / 2;
                            }
                        }

                        if (event.getX() > initialX) {
                            //swipeButtonInner.clearAnimation();
                            float percent = (float) (((event.getX() * 100) / (getWidth() * 0.9)) / 100);
                            StateListDrawable bgShape = (StateListDrawable) swipeButtonInner
                                    .getBackground();
                            DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer
                                    .DrawableContainerState) bgShape.getConstantState();
                            Drawable[] children = drawableContainerState.getChildren();
                            if (children[0] instanceof GradientDrawable) {
                                ((GradientDrawable) children[0]).setColor(getcolorPercentage(percent));
                            }
                        } else if (event.getX() < initialX) {
                            //swipeButtonInner.clearAnimation();
                            float percent = (float) (((event.getX() * 100) / (getWidth() * 0.9)) / 100);
                            StateListDrawable bgShape = (StateListDrawable) swipeButtonInner
                                    .getBackground();
                            DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer
                                    .DrawableContainerState) bgShape.getConstantState();
                            Drawable[] children = drawableContainerState.getChildren();
                            if (children[0] instanceof GradientDrawable) {
                                ((GradientDrawable) children[0]).setColor(getcolorPercentageLower(percent));
                            }
                        }
                        //swipeButtonInner.clearAnimation();
                        if (event.getX() > swipeButtonInner.getWidth() / 2 &&
                                event.getX() + swipeButtonInner.getWidth() / 2 < getWidth()) {
                            swipeButtonInner.setX(event.getX() - swipeButtonInner.getWidth() / 2);
                            /*centerText.setAlpha(1 - 1.3f * (swipeButtonInner.getX() + swipeButtonInner.getWidth()) / getWidth());*/
                            setTrailingEffect();
                        }

                        if (event.getX() + swipeButtonInner.getWidth() / 2 > getWidth() &&
                                swipeButtonInner.getX() + swipeButtonInner.getWidth() / 2 < getWidth()) {
                            swipeButtonInner.setX(getWidth() - swipeButtonInner.getWidth());
                        }

                        if (event.getX() < swipeButtonInner.getWidth() / 2) {
                            swipeButtonInner.setX(0);
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        if (active) {
                            collapseButton();
                        } else {
                            if (swipeButtonInner.getX() + swipeButtonInner.getWidth() > getWidth() * 0.9) {
                                if (hasActivationState) {
                                    isCallAccepted = true;
                                    expandButton(isCallAccepted);
                                } else if (onActiveListener != null) {
                                    onActiveListener.onActive();
                                    //swipeButtonInner.startAnimation(rotate);
                                    moveButtonBack();
                                }
                            } else if (swipeButtonInner.getX() + swipeButtonInner.getWidth() <=
                                    (swipeButtonInner.getWidth() + (swipeButtonInner.getWidth() *
                                            0.9))
                                    ) {
                                if (hasActivationState) {
                                    isCallAccepted = false;
                                    expandButton(isCallAccepted);
                                } else if (onActiveListener != null) {
                                    onActiveListener.onActive();
                                    //swipeButtonInner.startAnimation(rotate);
                                    moveButtonBack();
                                }
                            } else {
                                //swipeButtonInner.startAnimation(rotate);
                                moveButtonBack();
                            }
                        }

                        return true;
                }

                return false;
            }
        };
    }

    private void expandButton(final boolean isCallAccepted) {
        float value = ((getWidth() - (buttonLeftPadding
                * 3)) / 2);
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButtonInner.getX(), value);
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                swipeButtonInner.setX(x);
                setButtonBackground(ContextCompat.getDrawable
                        (getContext(), R.drawable.shape_button));
            }
        });


        /*final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                swipeButtonInner.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                swipeButtonInner.setLayoutParams(params);
            }
        });*/


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = true;
                swipeButtonInner.clearAnimation();
                swipeButtonInner.setImageDrawable(enabledDrawable);

                if (onStateChangeListener != null) {
                    onStateChangeListener.onStateChange(active);
                }

                if (onActiveListener != null && isCallAccepted) {
                    onActiveListener.onActive();
                } else if (onActiveListener != null && !isCallAccepted) {
                    onActiveListener.onActiveFail();
                }
            }
        });

        animatorSet.playTogether(positionAnimator);
        animatorSet.start();
    }

    private int getcolorPercentage(float percent) {
        if (percent > 1) {
            return Color.parseColor("#006400");
        }
        return (Integer) new ArgbEvaluator().evaluate(percent, Color.GREEN, Color.parseColor("#006400"));
    }

    private int getcolorPercentageLower(float percent) {
        return (Integer) new ArgbEvaluator().evaluate(percent, Color.parseColor("#8B0000"), Color
                .parseColor("#FF0000"));
    }

    private void moveButtonBack() {
        float value = ((getWidth() - (buttonLeftPadding
                * 3)) / 2);
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(swipeButtonInner.getX(), value);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (Float) positionAnimator.getAnimatedValue();
                swipeButtonInner.setX(x);
                setTrailingEffect();
                setButtonBackground(ContextCompat.getDrawable
                        (getContext(), R.drawable.shape_button));
            }
        });

        positionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (layer != null) {
                    layer.setVisibility(View.GONE);
                }
            }
        });

//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
//                centerText, "alpha", 1);

        positionAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(positionAnimator);
        animatorSet.start();
    }

    public void collapseButton() {
        int finalWidth;

        if (collapsedWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            finalWidth = swipeButtonInner.getHeight();
        } else {
            finalWidth = collapsedWidth;
        }

        final ValueAnimator widthAnimator = ValueAnimator.ofInt(swipeButtonInner.getWidth(), finalWidth);

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
                params.width = (Integer) widthAnimator.getAnimatedValue();
                swipeButtonInner.setLayoutParams(params);
                setTrailingEffect();
            }
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                active = false;
                swipeButtonInner.setImageDrawable(disabledDrawable);
                if (onStateChangeListener != null) {
                    onStateChangeListener.onStateChange(active);
                }
                if (layer != null) {
                    layer.setVisibility(View.GONE);
                }
            }
        });

       /* ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                centerText, "alpha", 1);*/

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(widthAnimator);
        animatorSet.start();
    }

    public void setEnabledStateNotAnimated() {
        swipeButtonInner.setX(0);

        ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        swipeButtonInner.setLayoutParams(params);

        swipeButtonInner.setImageDrawable(enabledDrawable);
        active = true;
//        centerText.setAlpha(0);
    }

    public void setDisabledStateNotAnimated() {
        ViewGroup.LayoutParams params = swipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        collapsedWidth = ViewGroup.LayoutParams.WRAP_CONTENT;

        swipeButtonInner.setImageDrawable(disabledDrawable);
        active = false;
//        centerText.setAlpha(1);

        swipeButtonInner.setPadding((int) buttonLeftPadding,
                (int) buttonTopPadding,
                (int) buttonRightPadding,
                (int) buttonBottomPadding);

        swipeButtonInner.setLayoutParams(params);
    }

    private void setTrailingEffect() {
        if (trailEnabled) {
            layer.setVisibility(View.VISIBLE);
            layer.setLayoutParams(new LayoutParams(
                    (int) (swipeButtonInner.getX() + swipeButtonInner.getWidth() / 3),
                    0/*centerText.getHeight()*/));
        }
    }

    public void setTrailBackground(@NonNull Drawable trailingDrawable) {
        if (trailEnabled) {
            layer.setBackground(trailingDrawable);
        }
    }

    /*public void toggleState() {
        if (isActive()) {
            collapseButton();
        } else {
            expandButton();
        }
    }*/

    public void setCenterTextColor(Context context, int color) {
//        centerText.setTextColor(context.getResources().getColor(color));
    }
}
