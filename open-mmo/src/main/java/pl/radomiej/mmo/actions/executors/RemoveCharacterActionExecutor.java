package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.specialized.CharacterObject;

public class RemoveCharacterActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		
		RemoveCharacterAction removePlayerAction = (RemoveCharacterAction) gameAction;
		BasicGameEngine.INSTANCE.removeObject(removePlayerAction.removeObjectId);
		BasicNetworkEngine.INSTANCE.sendRemoveEvent(removePlayerAction.removeObjectId);
		
		return true;
	}

}
