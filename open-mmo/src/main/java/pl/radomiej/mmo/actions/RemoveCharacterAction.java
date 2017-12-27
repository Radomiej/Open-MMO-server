package pl.radomiej.mmo.actions;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;

public class RemoveCharacterAction extends GameAction {
	public int removeObjectId;
	
	public RemoveCharacterAction(int removeObjectId) {
		this.removeObjectId = removeObjectId;
	}
}
