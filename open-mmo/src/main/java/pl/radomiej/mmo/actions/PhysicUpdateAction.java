package pl.radomiej.mmo.actions;

import pl.radomiej.mmo.models.GameAction;

public class PhysicUpdateAction extends GameAction {

	public int objectId;
	public float x;
	public float y;
	public float z;
	public float rotX;
	public float rotY;
	public float rotZ;

	@Override
	public String toString() {
		return "PhysicUpdateAction [objectId=" + objectId + ", x=" + x + ", y=" + y + ", z=" + z + ", rotX=" + rotX
				+ ", rotY=" + rotY + ", rotZ=" + rotZ + "]";
	}

}
