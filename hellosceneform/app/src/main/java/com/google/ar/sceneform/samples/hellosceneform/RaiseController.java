package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.MathHelper;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.BaseTransformationController;
import com.google.ar.sceneform.ux.TransformableNode;

/**
 * Raises a {@link MyTransformableNode} using a {@link TwoFingerDragGestureRecognizer}. The object
 * being raised must be a child of the TransformableNode so that the gesture does not conflict with
 * the TranslationController.
 */
public class RaiseController extends BaseTransformationController<TwoFingerDragGesture> {

  private final Node raisedChild;

  private static final float DELTA_MULTIPLIER = -0.0002f;
  private static final float MAX_HEIGHT = 0.25f;

  public RaiseController(
      TransformableNode transformableNode, TwoFingerDragGestureRecognizer gestureRecognizer,
      Node raisedChild) {
    super(transformableNode, gestureRecognizer);
    this.raisedChild = raisedChild;
  }

  @Override
  public boolean canStartTransformation(TwoFingerDragGesture gesture) {
    return getTransformableNode().isSelected();
  }

  @Override
  public void onContinueTransformation(TwoFingerDragGesture gesture) {
    Vector3 localPos = raisedChild.getLocalPosition();
    float newY = localPos.y + (gesture.getAverageDeltaPosition().y * DELTA_MULTIPLIER);
    newY = MathHelper.clamp(newY, 0.0f, MAX_HEIGHT);
    localPos.y = newY;
    raisedChild.setLocalPosition(localPos);
  }

  @Override
  public void onEndTransformation(TwoFingerDragGesture gesture) {
  }

}
