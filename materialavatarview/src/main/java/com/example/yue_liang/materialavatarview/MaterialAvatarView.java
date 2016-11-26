package com.example.yue_liang.materialavatarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
 * Created by yue_liang on 2016/6/20.
 */
public class MaterialAvatarView extends ImageView {
    private VectorDrawable mSingleAvatar;
    private int mBackgroundColor;
    private int mRadius;
    private Rect rect;

    /**
     * Using ViewGroup.LayoutParams.WRAP_CONTENT to set width and height,
     * MaterialAvatarView will measure by own algorithm
     *
     * @param context
     */
    private MaterialAvatarView(Context context) {
        super(context);
        init();
        setLayoutParams(new CombineAvatarView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public MaterialAvatarView(Context context, int width, int height) {
        super(context);
        init();
        setLayoutParams(new CombineAvatarView.LayoutParams(width, height));
    }

    public MaterialAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static MaterialAvatarView obtain(Context context) {
        return new MaterialAvatarView(context);
    }

    private void init() {
        rect = new Rect();
        setOutlineProvider(new CircleOutLineProvider());
        setClipToOutline(true);
        setElevation(8.0f);
    }

    private class CircleOutLineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    }

    private ShapeDrawable createCircleDrawable(int color) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new ArcShape(0, 360));// Circle shape
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    /**
     * @param drawable
     * @param backGroundColor getResources().getColor()的返回值
     */
    public MaterialAvatarView setSingleDrawable(VectorDrawable drawable, int backGroundColor) {
        if (mSingleAvatar != drawable) {
            Drawable existingDrawable = mSingleAvatar;
            mSingleAvatar = drawable;
            mBackgroundColor = backGroundColor;
            if (rect.isEmpty()
                    || existingDrawable.getIntrinsicHeight() != mSingleAvatar.getIntrinsicHeight()
                    || existingDrawable.getIntrinsicWidth() != mSingleAvatar.getIntrinsicWidth()) {
                requestLayout();
            } else {
                drawMergedDrawable(createCircleDrawable(mBackgroundColor), mSingleAvatar, rect);
            }
        }
        return this;
    }

    /**
     * Clean content in current display, which means is will display nothing,
     * but view still have attached to window.
     */
    public void cleanDisplay() {
        setSingleDrawable(null, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT &&
                getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int width = 0;
            int height = 0;
            if (getParent() instanceof CombineAvatarView) {
                switch (((CombineAvatarView) getParent()).getAvatarCount()) {
                    case CombineAvatarView.AVATAR_COUNT_ONE:
                        width = Math.round(MeasureSpec.getSize(widthMeasureSpec) / 1.2f);
                        height = Math.round(MeasureSpec.getSize(heightMeasureSpec) / 1.2f);
                        break;
                    case CombineAvatarView.AVATAR_COUNT_TWO:
                        width = Math.round(MeasureSpec.getSize(widthMeasureSpec) / 2);
                        height = Math.round(MeasureSpec.getSize(heightMeasureSpec) / 2);
                        break;
                    case CombineAvatarView.AVATAR_COUNT_MORE:
                        width = Math.round(MeasureSpec.getSize(widthMeasureSpec) / 2.2f);
                        height = Math.round(MeasureSpec.getSize(heightMeasureSpec) / 2.2f);
                        break;
                }
            } else {
                width = MeasureSpec.getSize(widthMeasureSpec) / 2;
                height = MeasureSpec.getSize(heightMeasureSpec) / 2;
            }
            setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        if (getParent() instanceof CombineAvatarView) { // be used to measure drawable
            int decrease = 0;
            switch (((CombineAvatarView) getParent()).getAvatarCount()) {
                case CombineAvatarView.AVATAR_COUNT_ONE:
                    mRadius = getMeasuredWidth() < getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight() / 2;
                    decrease = Math.round(mRadius / 3);
                    rect.left = decrease;
                    rect.top = decrease;
                    rect.right = decrease;
                    rect.bottom = decrease;
                    break;
                case CombineAvatarView.AVATAR_COUNT_TWO:
                    mRadius = getMeasuredWidth() < getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight() / 2;
                    decrease = Math.round(mRadius / 1.8f);
                    rect.left = decrease;
                    rect.top = decrease;
                    rect.right = decrease;
                    rect.bottom = decrease;
                    break;
                case CombineAvatarView.AVATAR_COUNT_MORE:
                    mRadius = getMeasuredWidth() < getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight() / 2;
                    decrease = Math.round(mRadius / 1.4f);
                    rect.left = decrease;
                    rect.top = decrease;
                    rect.right = decrease;
                    rect.bottom = decrease;
                    break;
            }
            drawMergedDrawable(createCircleDrawable(mBackgroundColor), mSingleAvatar, rect);
        } else {
            mRadius = getMeasuredWidth() < getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight() / 2;
            int decrease = Math.round(mRadius / 3);
            rect.left = decrease;
            rect.top = decrease;
            rect.right = decrease;
            rect.bottom = decrease;
            drawMergedDrawable(createCircleDrawable(mBackgroundColor), mSingleAvatar, rect);
        }
    }

    /**
     * Will merge underlying into top as one drawable, the drawable will drawing.
     *
     * @param underlying
     * @param top
     * @param rect       The rect as restrict region that drawable will drawing.
     */
    private void drawMergedDrawable(Drawable underlying, Drawable top, Rect rect) {
        Drawable drawable = MergeDrawable.obtainMergedDrawable(underlying, top, rect);
        setImageDrawable(drawable);
    }

    public Animator AnimateForDisappear() {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int startRadius = Math.max(getWidth(), getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, startRadius, 0);
        anim.setDuration(500);
        return anim;
    }

    public Animator AnimateForAppear() {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int finalRadius = Math.max(getWidth(), getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0, finalRadius);
        anim.setDuration(500);
        return anim;
    }

}