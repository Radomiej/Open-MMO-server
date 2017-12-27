package pl.radomiej.mmo.actions;

import pl.radomiej.mmo.models.GameAction;

public class AttackAction extends GameAction {

	public final int attacker, defender, attackType;
	public final float dmg, x, y, z;
	
	public AttackAction(int whoAttack, int whoDeffend, int attackType, float dmg, float x, float y, float z) {
		this.attacker = whoAttack;
		this.defender = whoDeffend;
		this.attackType = attackType;
		this.dmg = dmg;
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
