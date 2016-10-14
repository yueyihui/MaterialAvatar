package com.example.yue_liang.materialavatarview;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
/*
 * Copyright (c) 2016, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by yue_liang on 2016/6/21.
 */
public class CombineAvatarView extends FrameLayout {
    public final static int AVATAR_COUNT_NONE = 0;
    public final static int AVATAR_COUNT_ONE = 1;
    public final static int AVATAR_COUNT_TWO = 2;
    public final static int AVATAR_COUNT_MORE = 3;

    private final ArrayList<View> AVATAR_CONTAINER = new ArrayList<>(3);

    @IntDef({AVATAR_COUNT_NONE, AVATAR_COUNT_ONE, AVATAR_COUNT_TWO, AVATAR_COUNT_MORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @Mode
    private int mAvatarCount = AVATAR_COUNT_NONE;

    private float mAvatarElevation;

    private int mOffset;

    private static final float SAM_SUNG_S6_ELEVATION = 8.0f;
    private static final int SAM_SUNG_S6_OFFSET = 12;

    public CombineAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("WrongConstant")
    public CombineAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CombineAvatarView);
        mAvatarCount = typedArray.getInt(R.styleable.CombineAvatarView_avatar_count, AVATAR_COUNT_NONE);
        mAvatarElevation = typedArray.getFloat(R.styleable.CombineAvatarView_avatar_elevation, SAM_SUNG_S6_ELEVATION);
        mOffset = typedArray.getInt(R.styleable.CombineAvatarView_avatar_top_offset, SAM_SUNG_S6_OFFSET);
        typedArray.recycle();
    }

    public void setElevationForEveryAvatar(float avatarElevation) {
        mAvatarElevation = avatarElevation;
    }

    /**
     * In initialization occasion, need use this method for efficient layout;
     *
     * @param count
     */
    public void setAvatarCount(@Mode int count) {
        mAvatarCount = count;
    }

    public int getAvatarCount() {
        return mAvatarCount;
    }

    /**
     * Fresh Layout, let it displays attached view.
     * It's usually used to switch to another count.
     *
     * @param avatarCount AVATAR_COUNT_NONE or AVATAR_COUNT_ONE or
     *                    AVATAR_COUNT_TWO or AVATAR_COUNT_MORE
     */
    public void refreshLayout(@Mode int avatarCount) {
        if (mAvatarCount != avatarCount) {
            mAvatarCount = avatarCount;
            safetyRequestLayout();
        }
    }

    public void safetyRequestLayout() {
        if (!isInLayout()) {
            Log.d("yueliang", "<============= request Layout =============>");
            requestLayout();
        }
    }

    /**
     * Clean content in current display,
     * which means is will display nothing,
     * but view still have attached to view group.
     */
    public void cleanChildrenDisplays() {
        for (int index = 0; index < mAvatarCount; index++) {
            View child = getChildAt(index);
            if (child instanceof MaterialAvatarView) {
                ((MaterialAvatarView) child).cleanDisplay();
            }
        }
    }

    /**
     * Clean all child weather remove or still retain,
     * if want to reuse removed child,
     * please also making {@link #recoveryAllView()} calls.
     * <p/>
     * Note. If this method called in view hierarchy generating.
     * no need calls requestLayout, else need.
     *
     * @param remove if true, all view couldn't recovery.
     */
    public CombineAvatarView cleanAllViews(boolean remove) {
        detachAllViewsFromParent();
        if (remove) {
            Iterator<View> iterator = AVATAR_CONTAINER.iterator();
            while (iterator.hasNext()) {
                removeDetachedView(iterator.next(), true);
            }
            AVATAR_CONTAINER.clear();
        }
        invalidate();
        return this;
    }

    /**
     * Just disappear, not remove. a call to {@link #recoveryViewAt(int)}
     * will recovery.
     * @param location
     * @return
     */
    public CombineAvatarView cleanViewAt(int location) {
        View child = AVATAR_CONTAINER.get(location);
        detachViewFromParent(child);
        invalidate();
        return this;
    }

    /**
     * Just disappear, not remove. a call to {@link #recoveryViewAt(int)}
     * will recovery.
     * @param child
     * @return
     */
    public CombineAvatarView cleanViewAt(View child, boolean remove) {
        detachViewFromParent(child);
        if(remove) {
            removeDetachedView(child, false);
            AVATAR_CONTAINER.remove(child);
        }
        invalidate();
        return this;
    }

    /**
     * Using this method for recovery. if {@link #cleanAllViews(boolean)}
     * use of false as params, this method will have no effective.
     * <p/>
     * Note. If this method called in view hierarchy generating.
     * no need calls requestLayout, else need.
     */
    public CombineAvatarView recoveryAllView() {
        Iterator<View> iterator = AVATAR_CONTAINER.iterator();
        while (iterator.hasNext()) {
            View child = iterator.next();
            attachViewToParent(child, AVATAR_CONTAINER.indexOf(child), child.getLayoutParams());
        }
        invalidate();
        return this;
    }

    public CombineAvatarView recoveryViewAt(int location) {
        View child = AVATAR_CONTAINER.get(location);
        attachViewToParent(child, location, child.getLayoutParams());
        invalidate();
        return this;
    }

    /**
     * use it for onCreate of Activity to add child to this parent
     * @param child
     * @return
     */
    public CombineAvatarView addChild(View child) {
        Log.d("yueliang", "AVATAR_CONTAINER size = " + AVATAR_CONTAINER.size());
        AVATAR_CONTAINER.add(child);
        int index = AVATAR_CONTAINER.indexOf(child);
        attachViewToParent(child, index, child.getLayoutParams());
        return this;
    }

    public void add(View view) {
        addView(view);
        AVATAR_CONTAINER.add(view);
    }

    /**
     * if child is MaterialAvatarView, will have animating for appear
     * @param childLocation
     */
    public CombineAvatarView animateForAppear(final int childLocation) throws IndexOutOfBoundsException {
        if (childLocation >= getChildCount()) {
            throw new IndexOutOfBoundsException();
        }
        View view = getChildAt(childLocation);
        recoveryViewAt(childLocation);
        view.setVisibility(VISIBLE);
        if(view instanceof MaterialAvatarView) {
            final MaterialAvatarView child = (MaterialAvatarView) view;
            Animator animator = child.AnimateForAppear();
            animator.start();
        }
        return this;
    }

    /**
     * If child is MaterialAvatarView, will have animating for disappear
     */
    public CombineAvatarView animateForDisappear() {
        cleanAllViews(true);
        AVATAR_CONTAINER.clear();
        setAvatarCount(AVATAR_COUNT_NONE);
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("yueliang","onLayout");
        if (checkChildCount()) {
            Log.d("yueliang", "Every avatar have elevation is = " + mAvatarElevation);
            switch (mAvatarCount) {
                case AVATAR_COUNT_ONE:
                    for (int index = 0; index < 1; index++) {
                        View child = getChildAt(index);
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
                    }
                    break;
                case AVATAR_COUNT_TWO:
                    for (int index = 0; index < 2; index++) {
                        View child = getChildAt(index);
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
                    }
                    break;
                case AVATAR_COUNT_MORE:
                    for (int index = 0; index < getChildCount(); index++) {
                        View child = getChildAt(index);
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
                    }
                    break;
            }
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("yueliang","onFinishInflate");
    }

    private boolean checkChildCount() {
        Log.d("yueliang", "mAvatarCount = " + mAvatarCount + " getChildCount() = " + getChildCount());
        return getChildCount() >= mAvatarCount && mAvatarCount != AVATAR_COUNT_NONE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("yueliang","onMeasure");
        if (checkChildCount()) {
            LayoutParams[] params = new LayoutParams[3];
            View[] child = new View[3];
            switch (mAvatarCount) {
                case AVATAR_COUNT_ONE:
                    child[0] = getChildAt(0);
                   // child[0].setElevation(mAvatarElevation);
                    measureChild(child[0], widthMeasureSpec, heightMeasureSpec);
                    params[0] = (LayoutParams) child[0].getLayoutParams();
                    params[0].x = getPaddingLeft() + (int) child[0].getElevation();
                    params[0].y = getPaddingTop() + (int) child[0].getElevation();
                    break;

                case AVATAR_COUNT_TWO:
                    child[0] = getChildAt(0);
                    //child[0].setElevation(mAvatarElevation);
                    measureChild(child[0], widthMeasureSpec, heightMeasureSpec);
                    params[0] = (LayoutParams) child[0].getLayoutParams();
                    params[0].x = getPaddingLeft() + (int) child[0].getElevation();
                    params[0].y = getPaddingTop() + (int) child[0].getElevation();

                    child[1] = getChildAt(1);
                   // child[1].setElevation(mAvatarElevation);
                    measureChild(child[1], widthMeasureSpec, heightMeasureSpec);
                    params[1] = (LayoutParams) child[1].getLayoutParams();
                    params[1].x = child[0].getMeasuredWidth() - getPaddingRight() - (int) child[1].getElevation();
                    params[1].y = child[0].getMeasuredHeight() - getPaddingBottom() - (int) child[1].getElevation();
                    break;

                case AVATAR_COUNT_MORE:
                    child[0] = getChildAt(0);
                    //child[0].setElevation(mAvatarElevation);
                    measureChild(child[0], widthMeasureSpec, heightMeasureSpec);
                    params[0] = (LayoutParams) child[0].getLayoutParams();
                    params[0].x = MeasureSpec.getSize(widthMeasureSpec) / 2 - child[0].getMeasuredWidth() / 2 + getPaddingLeft();
                    params[0].y = getPaddingTop() + (int) child[0].getElevation() + mOffset;

                    child[1] = getChildAt(1);
                    //child[1].setElevation(mAvatarElevation);
                    measureChild(child[1], widthMeasureSpec, heightMeasureSpec);
                    params[1] = (LayoutParams) child[1].getLayoutParams();
                    params[1].x = MeasureSpec.getSize(widthMeasureSpec) - child[1].getMeasuredWidth() - getPaddingRight() - (int) child[0].getElevation();
                    params[1].y = MeasureSpec.getSize(heightMeasureSpec) - child[1].getMeasuredHeight() - getPaddingBottom() - (int) child[1].getElevation();

                    child[2] = getChildAt(2);
                    //child[2].setElevation(mAvatarElevation);
                    measureChild(child[2], widthMeasureSpec, heightMeasureSpec);
                    params[2] = (LayoutParams) child[2].getLayoutParams();
                    params[2].x = getPaddingLeft() + (int) child[2].getElevation();
                    params[2].y = MeasureSpec.getSize(heightMeasureSpec) - child[2].getMeasuredHeight() - getPaddingBottom() - (int) child[2].getElevation();
                    break;
            }
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        return new LayoutParams(params);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        int x;
        int y;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
