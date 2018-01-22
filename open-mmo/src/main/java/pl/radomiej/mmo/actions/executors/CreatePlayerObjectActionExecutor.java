package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.PlayerManager;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.actions.CreatePlayerObjectAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.specialized.CharacterObject;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.handlers.SystemActionHandler;
import pl.radomiej.mmo.network.models.PlayerData;

public class CreatePlayerObjectActionExecutor implements ActionExecutor {

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		CharacterObject player = new CharacterObject();
		CreatePlayerObjectAction createPlayerAction = (CreatePlayerObjectAction) gameAction;

		player.hp = player.hpMax = 100;
		player.kind = createPlayerAction.kind;
		player.x = createPlayerAction.x;
		player.y = createPlayerAction.y;
		player.z = createPlayerAction.z;

		basicGameEngine.addObject(player);

		createPlayerAction.ownerSession.setAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_OBJECT_ID,
				player.id);
		
		int playerId = (int) createPlayerAction.ownerSession.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
		PlayerData playerData = PlayerManager.INSTANCE.getOrCreatePlayerData(playerId);
		playerData.setPlayerObjectId(player.id);
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(1); // 1 = success
		//response.PutNextInteger(player.id); // Object Id
		BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(createPlayerAction.ownerSession, player.id, SystemActionHandler.REGISTER_PLAYER_OBJECT , response);
		BasicNetworkEngine.INSTANCE.sendCreateEvent(player);
		
		System.out.println("Stworzono obiekt gracza o id: " + player.id);
		
		return true;
	}

}
