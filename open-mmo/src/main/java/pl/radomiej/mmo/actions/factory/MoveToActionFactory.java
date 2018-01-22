package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.PlayerManager;
import pl.radomiej.mmo.ServerSettings;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;
import pl.radomiej.mmo.network.models.PlayerData;

public class MoveToActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		MoveToAction moveToAction = new MoveToAction();
		moveToAction.objectId = datagram.receipent;
		moveToAction.x = nds.GetNextFloat();
		moveToAction.y = nds.GetNextFloat();
		moveToAction.z = nds.GetNextFloat();
		moveToAction.rotX = nds.GetNextFloat();
		moveToAction.rotY = nds.GetNextFloat();
		moveToAction.rotZ = nds.GetNextFloat();

		// Save current player positions for later
		IfPlayerObjectThenSaveCurrentPlayerPosition(session, moveToAction);
		return moveToAction;
	}

	private void IfPlayerObjectThenSaveCurrentPlayerPosition(IoSession session, MoveToAction moveToAction) {
//		System.out.println(
//				"MoveToActionFactory: " + session + " 2: " + BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_OBJECT_ID);
		Object sessionAttributePlayerObjectId = session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_OBJECT_ID);
		Object sessionAttributePlayerId = session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
		if (sessionAttributePlayerObjectId != null) {
			int playerObjectId = (int) sessionAttributePlayerObjectId;
			int playerId = (int) sessionAttributePlayerId;
			if (sessionAttributePlayerObjectId != null && ServerSettings.CURRENT.savePlayerPositions && playerObjectId != 0
					&& playerObjectId == moveToAction.objectId) {

				PlayerData playerData = PlayerManager.INSTANCE.getPlayerData(playerId);
				if(playerData == null){
					System.err.println("PlayerData not exist for: " + playerId);
					return;
				}
				
				playerData.setSpawnX(moveToAction.x);
				playerData.setSpawnX(moveToAction.y);
				playerData.setSpawnX(moveToAction.z);
			}
		}
	}

}
