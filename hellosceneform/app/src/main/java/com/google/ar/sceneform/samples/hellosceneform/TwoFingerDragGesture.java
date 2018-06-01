package com.google.ar.sceneform.samples.hellosceneform;

import android.util.Log;
import android.view.MotionEvent;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.BaseGesture;
import com.google.ar.sceneform.ux.GesturePointersUtility;

/**
 * Gesture for when the user performs a two-finger drag motion on the touch screen.
 */
public class TwoFingerDragGesture extends BaseGesture<TwoFingerDragGesture> {

  private static final String TAG = TwoFingerDragGesture.class.getSimpleName();

  /**
   * Interface definition for callbacks to be invoked by a {@link TwoFingerDragGesture}.
   */
  public interface OnGestureEventListener
      extends BaseGesture.OnGestureEventListener<TwoFingerDragGesture> {

  }

  private static final boolean TWO_FINGER_DRAG_GESTURE_DEBUG = true;

  private final int pointerId1;
  private final int pointerId2;
  private final Vector3 startPosition1;
  private final Vector3 startPosition2;
  private final Vector3 previousPosition1;
  private final Vector3 previousPosition2;

  private final Vector3 averageDeltaPosition = new Vector3();

  private static final float SLOP_INCHES = 0.1f;

  public TwoFingerDragGesture(
      GesturePointersUtility gesturePointersUtility, MotionEvent motionEvent, int pointerId2) {
    super(gesturePointersUtility);

    pointerId1 = motionEvent.getPointerId(motionEvent.getActionIndex());
    this.pointerId2 = pointerId2;
    startPosition1 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId1);
    startPosition2 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId2);
    previousPosition1 = new Vector3(startPosition1);
    previousPosition2 = new Vector3(startPosition2);
    debugLog("Created");
  }

  public Vector3 getAverageDeltaPosition() {
    return averageDeltaPosition;
  }

  @Override
  protected boolean canStart(HitTestResult hitTestResult, MotionEvent motionEvent) {
    if (gesturePointersUtility.isPointerIdRetained(pointerId1)
        || gesturePointersUtility.isPointerIdRetained(pointerId2)) {
      cancel();
      return false;
    }

    int actionId = motionEvent.getPointerId(motionEvent.getActionIndex());
    int action = motionEvent.getActionMasked();

    if (action == MotionEvent.ACTION_CANCEL) {
      cancel();
      return false;
    }

    boolean touchEnded = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP;

    if (touchEnded && (actionId == pointerId1 || actionId == pointerId2)) {
      cancel();
      return false;
    }

    if (action != MotionEvent.ACTION_MOVE) {
      return false;
    }

    Vector3 newPosition1 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId1);
    Vector3 newPosition2 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId2);
    Vector3 deltaPosition1 = Vector3.subtract(newPosition1, previousPosition1);
    Vector3 deltaPosition2 = Vector3.subtract(newPosition2, previousPosition2);
    previousPosition1.set(newPosition1);
    previousPosition2.set(newPosition2);

    // Check that both fingers are moving.
    if (Vector3.equals(deltaPosition1, Vector3.zero())
        || Vector3.equals(deltaPosition2, Vector3.zero())) {
      return false;
    }

    // Check that both fingers are moving in approximately the same direction.
    if (Vector3.angleBetweenVectors(deltaPosition1, deltaPosition2) > 25.0f) {
      return false;
    }

    // Check that both fingers have moved beyond the slop threshold.
    float diff1 = Vector3.subtract(newPosition1, startPosition1).length();
    float diff2 = Vector3.subtract(newPosition1, startPosition2).length();
    float slopPixels = gesturePointersUtility.inchesToPixels(SLOP_INCHES);
    if (diff1 >= slopPixels && diff2 >= slopPixels) {
      Vector3 previousAverage = calculateAverageVector(previousPosition1, previousPosition2);
      Vector3 newAverage = calculateAverageVector(newPosition1, newPosition2);
      averageDeltaPosition.set(Vector3.subtract(newAverage, previousAverage));
      return true;
    }

    return false;
  }

  @Override
  protected void onStart(HitTestResult hitTestResult, MotionEvent motionEvent) {
    debugLog("Started");
    gesturePointersUtility.retainPointerId(pointerId1);
    gesturePointersUtility.retainPointerId(pointerId2);
  }

  @Override
  protected boolean updateGesture(HitTestResult hitTestResult, MotionEvent motionEvent) {
    int actionId = motionEvent.getPointerId(motionEvent.getActionIndex());
    int action = motionEvent.getActionMasked();

    if (action == MotionEvent.ACTION_CANCEL) {
      cancel();
      return false;
    }

    boolean touchEnded = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP;

    if (touchEnded && (actionId == pointerId1 || actionId == pointerId2)) {
      complete();
      return false;
    }

    if (action != MotionEvent.ACTION_MOVE) {
      return false;
    }

    Vector3 newPosition1 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId1);
    Vector3 newPosition2 = GesturePointersUtility.motionEventToPosition(motionEvent, pointerId2);

    Vector3 previousAverage = calculateAverageVector(previousPosition1, previousPosition2);
    Vector3 newAverage = calculateAverageVector(newPosition1, newPosition2);
    averageDeltaPosition.set(Vector3.subtract(newAverage, previousAverage));

    previousPosition1.set(newPosition1);
    previousPosition2.set(newPosition2);

    debugLog("Update: " + averageDeltaPosition);
    return true;
  }

  @Override
  protected void onCancel() {
    debugLog("Cancelled");
  }

  @Override
  protected void onFinish() {
    debugLog("Finished");
    gesturePointersUtility.releasePointerId(pointerId1);
    gesturePointersUtility.releasePointerId(pointerId2);
  }

  @Override
  protected TwoFingerDragGesture getSelf() {
    return this;
  }

  private static void debugLog(String log) {
    if (TWO_FINGER_DRAG_GESTURE_DEBUG) {
      Log.d(TAG, "TwoFingerDragGesture:[" + log + "]");
    }
  }

  private static Vector3 calculateAverageVector(Vector3 a, Vector3 b) {
    return Vector3.add(a, b).scaled(0.5f);
  }
}
