package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.specialized.CharacterObject;

public class CreateCharacterActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		CharacterObject player = new CharacterObject();
		CreateCharacterAction createPlayerAction = (CreateCharacterAction) gameAction;

		player.kind = createPlayerAction.kind;
		player.x = createPlayerAction.x;
		player.y = createPlayerAction.y;
		player.z = createPlayerAction.z;

		basicGameEngine.addObject(player);

		if (createPlayerAction.kind == 0) {
			createPlayerAction.ownerSession.setAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_OBJECT_ID,
					player.id);
			BasicNetworkEngine.INSTANCE.resendCreateEvent(createPlayerAction.ownerSession);
		}
		
		BasicNetworkEngine.INSTANCE.sendPlayerObjectId(createPlayerAction.ownerSession, player.id);
		BasicNetworkEngine.INSTANCE.sendCreateEvent(player);
		return true;
	}

}
