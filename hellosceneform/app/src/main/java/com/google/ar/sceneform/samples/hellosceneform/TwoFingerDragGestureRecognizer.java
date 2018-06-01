package com.google.ar.sceneform.samples.hellosceneform;

import android.view.MotionEvent;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.ux.BaseGestureRecognizer;
import com.google.ar.sceneform.ux.GesturePointersUtility;

/**
 * Gesture Recognizer for when the user performs a two-finger drag motion on the touch screen.
 */
public class TwoFingerDragGestureRecognizer extends BaseGestureRecognizer<TwoFingerDragGesture> {

  /**
   * Interface definition for a callbacks to be invoked when a {@link TwoFingerDragGesture} starts.
   */
  public interface OnGestureStartedListener
      extends BaseGestureRecognizer.OnGestureStartedListener<TwoFingerDragGesture> {

  }

  public TwoFingerDragGestureRecognizer(GesturePointersUtility gesturePointersUtility) {
    super(gesturePointersUtility);
  }

  @Override
  protected void tryCreateGestures(HitTestResult hitTestResult, MotionEvent motionEvent) {
    // two finger drag gestures require at least two fingers to be touching.
    if (motionEvent.getPointerCount() < 2) {
      return;
    }

    int actionId = motionEvent.getPointerId(motionEvent.getActionIndex());
    int action = motionEvent.getActionMasked();
    boolean touchBegan =
        action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN;

    if (!touchBegan || gesturePointersUtility.isPointerIdRetained(actionId)) {
      return;
    }

    // Determine if there is another pointer Id that has not yet been retained.
    for (int i = 0; i < motionEvent.getPointerCount(); i++) {
      int pointerId = motionEvent.getPointerId(i);
      if (pointerId == actionId) {
        continue;
      }

      if (gesturePointersUtility.isPointerIdRetained(pointerId)) {
        continue;
      }

      gestures.add(new TwoFingerDragGesture(gesturePointersUtility, motionEvent, pointerId));
    }
  }
}
