package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.specialized.CharacterObject;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.handlers.SystemActionHandler;

public class CreateNetworkObjectActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		CreateNetworkObjectAction createPlayerAction = (CreateNetworkObjectAction) gameAction;
		CharacterObject player;
		
		if(createPlayerAction.currentId == 0) player = new CharacterObject();
		else {
			player = new CharacterObject(createPlayerAction.currentId);
			
			Object playerId = createPlayerAction.ownerSession.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
			if(playerId != null) player.owner = (int) playerId;
			NetworkDataStream response = new NetworkDataStream();
			BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(createPlayerAction.ownerSession, player.id, SystemActionHandler.REQUEST_OWNER, response);
		}
		
		player.hp = player.hpMax = 100;
		player.kind = createPlayerAction.kind;
		player.x = createPlayerAction.x;
		player.y = createPlayerAction.y;
		player.z = createPlayerAction.z;
		player.setRotX(createPlayerAction.rotX);
		player.setRotY(createPlayerAction.rotY);
		player.setRotZ(createPlayerAction.rotZ);
		player.owner = createPlayerAction.ownerObjectId;
		
		basicGameEngine.addObject(player);

		
		BasicNetworkEngine.INSTANCE.sendCreateEvent(player);
		
		return true;
	}

}
