package pl.radomiej.mmo.actions;

import pl.radomiej.mmo.models.GameAction;

public class RecoveryAction extends GameAction {

	public final int revoceryObjectId;
	public final float recoveryHp;

	public RecoveryAction(int revoceryObjectId, float recoveryHp) {
		this.revoceryObjectId = revoceryObjectId;
		this.recoveryHp = recoveryHp;
	}

}
