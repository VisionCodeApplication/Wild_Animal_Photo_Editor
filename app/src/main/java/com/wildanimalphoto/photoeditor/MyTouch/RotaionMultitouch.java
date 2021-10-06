package com.wildanimalphoto.photoeditor.MyTouch;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.core.view.MotionEventCompat;

public class RotaionMultitouch implements OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    public boolean isRotateEnabled;
    public boolean isScaleEnabled;
    public boolean isTranslateEnabled;
    private int mActivePointerId;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;
    public float maximumScale;
    public float minimumScale;
    private Rect rect;
    OnRotateListner rotateListner;

    public interface OnRotateListner {
        float getRotation(float f);
    }

    private class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;

        private TransformInfo() {
        }
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D(mPivotX,mPivotY);
        }

        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            this.mPivotX = detector.getFocusX();
            this.mPivotY = detector.getFocusY();
            this.mPrevSpanVector.set(detector.getCurrentSpanVector());
            return true;
        }

        public boolean onScale(View view, ScaleGestureDetector detector) {
            float angle;
            float f = 0.0f;
            TransformInfo info = new TransformInfo();
            info.deltaScale = RotaionMultitouch.this.isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            if (RotaionMultitouch.this.isRotateEnabled) {
                angle = Vector2D.getAngle(this.mPrevSpanVector, detector.getCurrentSpanVector());
            } else {
                angle = 0.0f;
            }
            info.deltaAngle = angle;
            if (RotaionMultitouch.this.isTranslateEnabled) {
                angle = detector.getFocusX() - this.mPivotX;
            } else {
                angle = 0.0f;
            }
            info.deltaX = angle;
            if (RotaionMultitouch.this.isTranslateEnabled) {
                f = detector.getFocusY() - this.mPivotY;
            }
            info.deltaY = f;
            info.pivotX = this.mPivotX;
            info.pivotY = this.mPivotY;
            info.minimumScale = RotaionMultitouch.this.minimumScale;
            info.maximumScale = RotaionMultitouch.this.maximumScale;
            RotaionMultitouch.this.move(view, info);
            return false;
        }
    }

    public RotaionMultitouch(boolean isRotateEnabled, boolean isTranslateEnabled, boolean isScaleEnabled) {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.isRotateEnabled = isRotateEnabled;
        this.isScaleEnabled = isScaleEnabled;
        this.isTranslateEnabled = isTranslateEnabled;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    }

    public RotaionMultitouch(boolean isRotateEnabled, boolean isTranslateEnabled, boolean isScaleEnabled, OnRotateListner rotateListner) {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.isRotateEnabled = isRotateEnabled;
        this.isScaleEnabled = isScaleEnabled;
        this.isTranslateEnabled = isTranslateEnabled;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        this.rotateListner = rotateListner;
    }

    public RotaionMultitouch() {
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.minimumScale = 0.5f;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    }

    public static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            return degrees - 360.0f;
        }
        if (degrees < -180.0f) {
            return degrees + 360.0f;
        }
        return degrees;
    }

    private void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);
        float scale = Math.max(info.minimumScale, Math.min(info.maximumScale, view.getScaleX() * info.deltaScale));
        view.setScaleX(scale);
        view.setScaleY(scale);
        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        this.rotateListner.getRotation(adjustAngle(view.getRotation() + info.deltaAngle));
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = new float[]{deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() != pivotX || view.getPivotY() != pivotY) {
            float[] prevPoint = new float[]{0.0f, 0.0f};
            view.getMatrix().mapPoints(prevPoint);
            view.setPivotX(pivotX);
            view.setPivotY(pivotY);
            float[] currPoint = new float[]{0.0f, 0.0f};
            view.getMatrix().mapPoints(currPoint);
            float offsetY = currPoint[1] - prevPoint[1];
            view.setTranslationX(view.getTranslationX() - (currPoint[0] - prevPoint[0]));
            view.setTranslationY(view.getTranslationY() - offsetY);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        int newPointerIndex = 0;
        this.mScaleGestureDetector.onTouchEvent(view, event);
        if (this.isTranslateEnabled) {
            int action = event.getAction();
            int pointerIndex;
            switch (event.getActionMasked() & action) {
                case 0:
                    this.mPrevX = event.getX();
                    this.mPrevY = event.getY();
                    this.rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    this.mActivePointerId = event.getPointerId(0);
                    break;
                case 1:
                    this.mActivePointerId = -1;
                    break;
                case 2:
                    pointerIndex = event.findPointerIndex(this.mActivePointerId);
                    if (pointerIndex != -1) {
                        float currX = event.getX(pointerIndex);
                        float currY = event.getY(pointerIndex);
                        if (!this.mScaleGestureDetector.isInProgress()) {
                            adjustTranslation(view, currX - this.mPrevX, currY - this.mPrevY);
                            break;
                        }
                    }
                    break;
                case 3:
                    this.mActivePointerId = -1;
                    break;
                case 6:
                    pointerIndex = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & action) >> 8;
                    if (event.getPointerId(pointerIndex) == this.mActivePointerId) {
                        if (pointerIndex == 0) {
                            newPointerIndex = 1;
                        }
                        this.mPrevX = event.getX(newPointerIndex);
                        this.mPrevY = event.getY(newPointerIndex);
                        this.mActivePointerId = event.getPointerId(newPointerIndex);
                        break;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }
}
