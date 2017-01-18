package com.hpe.sb.mobile.app.common.uiComponents.commonLayout;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtilsImpl;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class EntityDescriptionView extends LinearLayout
        implements ScaleGestureDetector.OnScaleGestureListener {

    private static final long DABBLE_CLICK_IN_MILLISECONDS = -200;

    private static final int MODE_NONE = 1;

    private static final int MODE_DRAG = 2;

    private static final int MODE_ZOOM = 3;

    private boolean withShowMore;

    private long lastDown;

    private static final String TAG = "ZoomLayout";

    private static final float MIN_ZOOM = 1.0f;

    private static final float MAX_ZOOM = 4.0f;

    private int mode = MODE_NONE;

    private float scale = 1.0f;

    private float lastScaleFactor = 0f;

    // Where the finger first  touches the screen
    private float startX = 0f;

    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;

    private float dy = 0f;

    private float prevDx = 0f;

    private float prevDy = 0f;

    private com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView requestDescription;

    private TextView showMore;

    private TextView showLess;

    private int maxLines;

    private Target target;

    public EntityDescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EntityDescriptionView,
                0, 0);
        withShowMore = a.getBoolean(R.styleable.EntityDescriptionView_withShowMore, true);
        init();
    }

    private void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", maxLines);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                showMore.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showLess.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.setDuration(300).start();
    }

    private void collapseTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", 3);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                showLess.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showMore.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.setDuration(300).start();
    }

    private void init() {
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), this);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (lastDown - System.currentTimeMillis() > DABBLE_CLICK_IN_MILLISECONDS) {
//                            if (scale == 1) {
//                                scale = 2;
//                            } else {
//                                dx = 0;
//                                dy = 0;
//                                scale = 1;
//                            }
//                            applyScaleAndTranslation();
                        } else {

                            if (scale > MIN_ZOOM) {
                                mode = MODE_DRAG;
                                startX = motionEvent.getX() - prevDx;
                                startY = motionEvent.getY() - prevDy;
                            }
                        }
                        lastDown = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == MODE_DRAG) {
                            dx = motionEvent.getX() - startX;
                            dy = motionEvent.getY() - startY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = MODE_ZOOM;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = MODE_DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = MODE_NONE;
                        prevDx = dx;
                        prevDy = dy;
                        break;
                }
                scaleDetector.onTouchEvent(motionEvent);

                if ((mode == MODE_DRAG && scale >= MIN_ZOOM) || mode == MODE_ZOOM) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
                    float maxDy = (child().getHeight() - (child().getHeight() / scale)) / 2 * scale;
                    dx = Math.min(Math.max(dx, -maxDx), maxDx);
                    dy = Math.min(Math.max(dy, -maxDy), maxDy);
                    applyScaleAndTranslation();
                }

                return false;
            }
        });

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.entity_description_view, this, true);
        requestDescription = (com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView) view
                .findViewById(android.R.id.title);
        initShowMoreButtonsIfNeeded(view);
    }

    private void initShowMoreButtonsIfNeeded(View view) {
        if (withShowMore) {
            showMore = (TextView) view.findViewById(R.id.show_more);
            showLess = (TextView) view.findViewById(R.id.show_less);
            showMore.setVisibility(VISIBLE);
            showLess.setVisibility(INVISIBLE);
            if (showMore != null) {
                showMore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expandTextView(requestDescription);
                    }
                });
            }
            showLess.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapseTextView(requestDescription);
                }
            });
        }
    }

    public void setDescription(String string,
            @Nullable ConnectionContextService connectionContextService) {
        if (string == null) {
            string = "";
        }
        if (connectionContextService != null) {
            string = makeRelativeToHost(string, connectionContextService);
        }

        requestDescription.setText(HtmlUtil.fromHtml(string, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                final LevelListDrawable levelListDrawable = new LevelListDrawable();
                target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        BitmapDrawable d = new BitmapDrawable(bitmap);
                        levelListDrawable.addLevel(1, 1, d);
                        levelListDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        levelListDrawable.setLevel(1);
                        requestDescription.invalidate();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Drawable empty = getResources().getDrawable(R.drawable.placeholder);
                        levelListDrawable.addLevel(0, 0, empty);
                        levelListDrawable.setBounds(0, 0, empty.getIntrinsicWidth(),
                                empty.getIntrinsicHeight());
                    }
                };
                Picasso.with(getContext())
                        .load(source)
                        .into(target);
                return levelListDrawable;
            }
        }));
        initShowMoreActionIfNeeded();
    }

    private void initShowMoreActionIfNeeded() {
        if (withShowMore) {
            requestDescription.post(new Runnable() {
                @Override
                public void run() {
                    if (maxLines == 0) {
                        maxLines = requestDescription.getLineCount();
                    }
                    requestDescription.setMaxLines(3);
                    if (maxLines <= 3) {
                        showMore.setVisibility(INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleDetector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleDetector) {

    }

    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }

    private View child() {
        return getChildAt(0);
    }


    private String makeRelativeToHost(String request,
            ConnectionContextService connectionContextService) {
        HttpLookupUtilsImpl httpLookupUtilsImpl = new HttpLookupUtilsImpl(connectionContextService);
        return request.replace("../rest/", httpLookupUtilsImpl.getBaseUrl() + "/rest/");
    }

}
