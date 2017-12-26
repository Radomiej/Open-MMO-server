package pl.radomiej.mmo.actions;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;

public class CreateCharacterAction extends GameAction {
	public final IoSession ownerSession;
	public final int kind;

	public float x, y, z;

	public CreateCharacterAction(IoSession ownerSession, int kind) {
		this.ownerSession = ownerSession;
		this.kind = kind;
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
