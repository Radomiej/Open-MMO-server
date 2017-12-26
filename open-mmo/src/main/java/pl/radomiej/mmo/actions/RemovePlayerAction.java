package pl.radomiej.mmo.actions;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;

public class RemovePlayerAction extends GameAction {
	public int removeObjectId;
	
	public RemovePlayerAction(int removeObjectId) {
		this.removeObjectId = removeObjectId;
	}
}
