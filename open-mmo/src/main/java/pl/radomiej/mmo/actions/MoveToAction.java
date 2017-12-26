package pl.radomiej.mmo.actions;

import pl.radomiej.mmo.models.GameAction;

public class MoveToAction extends GameAction {

	public int objectId;
	public float x;
	public float y;
	public float z;
	public float rotX;
	public float rotY;
	public float rotZ;

	@Override
	public String toString() {
		return "MoveToAction [objectId=" + objectId + ", x=" + x + ", y=" + y + ", z=" + z + ", rotX=" + rotX
				+ ", rotY=" + rotY + ", rotZ=" + rotZ + "]";
	}

}
