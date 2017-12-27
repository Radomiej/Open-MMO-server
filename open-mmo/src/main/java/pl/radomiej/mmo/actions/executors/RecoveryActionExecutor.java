package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.AttackAction;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.actions.RecoveryAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.CharacterObject;
import pl.radomiej.mmo.network.NetworkDataStream;

public class RecoveryActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		RecoveryAction recoveryAction = (RecoveryAction) gameAction;

		NetworkObject findObject = basicGameEngine.findObjectById(recoveryAction.revoceryObjectId);
		if(findObject == null) {
			System.err.println("Brak obiektu: " + recoveryAction.revoceryObjectId);
			return true;
		}
		if(!(findObject instanceof CharacterObject)){
			System.err.println("Obiektu: " + recoveryAction.revoceryObjectId + " nie jest typu: " + CharacterObject.class.getSimpleName());
			return true;
		}
		CharacterObject lifableObject = (CharacterObject) findObject;
		
		lifableObject.hp += recoveryAction.recoveryHp;
		
		if(lifableObject.hp > lifableObject.hpMax){
			lifableObject.hp = lifableObject.hpMax;
		}
		
		NetworkDataStream nds = new NetworkDataStream();
		nds.PutNextInteger(7); // Event Type: Leczenie
		nds.PutNextFloat(recoveryAction.recoveryHp);
		nds.PutNextFloat(lifableObject.hp);
		
		BasicNetworkEngine.INSTANCE.sendEvent(lifableObject.id, nds.getDataArray());
		
		return true;
	}

}
