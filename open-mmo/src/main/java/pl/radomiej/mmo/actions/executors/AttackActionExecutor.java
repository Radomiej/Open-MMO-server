package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.AttackAction;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.CharacterObject;
import pl.radomiej.mmo.network.NetworkDataStream;

public class AttackActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		AttackAction attackAction = (AttackAction) gameAction;

		NetworkObject findObject = basicGameEngine.findObjectById(attackAction.defender);
		if(findObject == null) {
			System.err.println("Brak obiektu: " + attackAction.defender);
			return true;
		}
		if(!(findObject instanceof CharacterObject)){
			System.err.println("Obiektu: " + attackAction.defender + " nie jest typu: " + CharacterObject.class.getSimpleName());
			return true;
		}
		CharacterObject lifableObject = (CharacterObject) findObject;
		
		lifableObject.hp -= attackAction.dmg;
		
		if(lifableObject.hp <= 0){
			RemoveCharacterAction removeCharacterAction = new RemoveCharacterAction(lifableObject.id);
			BasicGameEngine.INSTANCE.addGameAction(removeCharacterAction);
		}
		
		NetworkDataStream nds = new NetworkDataStream();
		nds.PutNextInteger(4); // Event Type
		nds.PutNextInteger(attackAction.attacker); // Who Attack
		nds.PutNextInteger(attackAction.attackType); // Kind of Attack: Normal, Poison, Magic etc...
		nds.PutNextFloat(attackAction.dmg);
		nds.PutNextFloat(attackAction.x);
		nds.PutNextFloat(attackAction.y);
		nds.PutNextFloat(attackAction.z);
		
		BasicNetworkEngine.INSTANCE.sendEvent(lifableObject.id, nds.getDataArray());
		
		return true;
	}

}
