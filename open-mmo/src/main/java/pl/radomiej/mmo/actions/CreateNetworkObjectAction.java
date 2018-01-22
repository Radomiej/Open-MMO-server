package pl.radomiej.mmo.actions;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;

public class CreateNetworkObjectAction extends GameAction {
	public final IoSession ownerSession;
	public final int kind;
	public final int currentId;
	public final int ownerObjectId;
	
	public float x, y, z;
	public float rotX, rotY, rotZ;
	
	public CreateNetworkObjectAction(IoSession ownerSession, int kind,  int currentId, int ownerObjectId) {
		this.ownerSession = ownerSession;
		this.kind = kind;
		this.currentId = currentId;
		this.ownerObjectId = ownerObjectId;
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setRotation(float rotX, float rotY, float rotZ) {
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
}
