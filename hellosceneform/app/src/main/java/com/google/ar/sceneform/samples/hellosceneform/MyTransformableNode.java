package com.google.ar.sceneform.samples.hellosceneform;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class MyTransformableNode extends TransformableNode {

  private final RaiseController raiseController;

  public MyTransformableNode(TransformationSystem transformationSystem,
      TwoFingerDragGestureRecognizer twoFingerDragGestureRecognizer, Node raisedChild) {
    super(transformationSystem);
    raiseController = new RaiseController(this, twoFingerDragGestureRecognizer, raisedChild);
  }

  public boolean isTransforming() {
    return super.isTransforming() || raiseController.isTransforming();
  }

  @Override
  public void onActivate() {
    super.onActivate();
    raiseController.onNodeActivated();
  }

  @Override
  public void onDeactivate() {
    super.onDeactivate();
    raiseController.onNodeDeactivated();
  }
}
