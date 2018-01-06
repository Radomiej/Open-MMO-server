package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.specialized.CharacterObject;

public class CreateNetworkObjectActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		CharacterObject player = new CharacterObject();
		CreateNetworkObjectAction createPlayerAction = (CreateNetworkObjectAction) gameAction;

		player.hp = player.hpMax = 100;
		player.kind = createPlayerAction.kind;
		player.x = createPlayerAction.x;
		player.y = createPlayerAction.y;
		player.z = createPlayerAction.z;

		basicGameEngine.addObject(player);

		
		BasicNetworkEngine.INSTANCE.sendCreateEvent(player);
		
		return true;
	}

}
