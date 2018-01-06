package pl.radomiej.mmo.network.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.ActionHelper;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.PlayerManager;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.actions.CreatePlayerObjectAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.actions.factory.AttackActionFactory;
import pl.radomiej.mmo.actions.factory.RecoveryActionFactory;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;
import pl.radomiej.mmo.network.models.PlayerData;

public class SystemActionHandler implements ActionFactory {
	public static final int LOGIN_TO_SERVER = 1;
	public static final int REGISTER_PLAYER_OBJECT = 2;
	public static final int UNREGISTER_PLAYER_OBJECT = 3;
	public static final int REQUEST_OWNER = 4;
	public static final int DROP_OWNER = 5;
	
	public static final int CREATE_NETWORK_OBJECT = 12;
	public static final int REMOVE_NETWORK_OBJECT = 255;
	
	public static final int SEND_CURRENT_SERVER_TIME = 44; //Refer to SendServerTime
	
	
	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		int eventId = nds.GetNextInteger();
		BasicNetworkEngine.INSTANCE.sendAddressedAck(session, eventId);
		
		int systemEventId = nds.GetNextInteger();
		
		if(systemEventId == LOGIN_TO_SERVER){
			loginToServer(nds, session);
		}else if(systemEventId == REGISTER_PLAYER_OBJECT){
			registerPlayerObject(nds, session);
		}else if(systemEventId == CREATE_NETWORK_OBJECT){
			createNetworkObject(nds, session);
		}else if(systemEventId == REQUEST_OWNER){
			requestOwner(datagram.receipent, nds, session);
		}else if(systemEventId == REMOVE_NETWORK_OBJECT){
			destroyObject(datagram.receipent, nds, session);
		}else{
			System.err.println("Unhandled ServerEvent type: " + systemEventId);
		}
		
		return null;
	}

	private void destroyObject(int receipent, NetworkDataStream nds, IoSession session) {
		RemoveCharacterAction removeCharacterAction = new RemoveCharacterAction(receipent);
		BasicGameEngine.INSTANCE.addGameAction(removeCharacterAction);
	}

	private void loginToServer(NetworkDataStream nds, IoSession session) {
		int playerId = nds.GetNextInteger();
		PlayerData playerData = PlayerManager.INSTANCE.getOrCreatePlayerData(playerId);
		
		session.setAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID,
				playerData.getPlayerId());
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(playerData.getPlayerId());
		response.PutNextInteger(playerData.getPlayerObjectId());
		response.PutNextFloat(playerData.getSpawnX());
		response.PutNextFloat(playerData.getSpawnY());
		response.PutNextFloat(playerData.getSpawnZ());
		
		
		BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(session, LOGIN_TO_SERVER ,response);
		
		System.out.println("Login to server incoming");
	}

	private void registerPlayerObject(NetworkDataStream nds, IoSession session) {
		int kind = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();
		
		CreatePlayerObjectAction createPlayerObject = new CreatePlayerObjectAction(session, kind);
		createPlayerObject.setPosition(x, y, z);
		
		System.out.println("player object create kind: " + kind);
		BasicGameEngine.INSTANCE.addGameAction(createPlayerObject);
	}

	private void requestOwner(int receipent, NetworkDataStream nds, IoSession session) {
		NetworkObject networkObject = BasicGameEngine.INSTANCE.findObjectById(receipent);
		if(networkObject == null) return;
		if(networkObject.owner != 0) return;
		
		networkObject.owner = (int) session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
		
		NetworkDataStream response = new NetworkDataStream();
		BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(session, receipent, REQUEST_OWNER, response);
	
	}
	
	private void createNetworkObject(NetworkDataStream nds, IoSession session){
		int kind = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();
		
		System.out.println("character create kind: " + kind);
		
		CreateNetworkObjectAction createCharacterAction = new CreateNetworkObjectAction(session, kind);
		createCharacterAction.setPosition(x, y, z);
		
		BasicGameEngine.INSTANCE.addGameAction(createCharacterAction);
	}
}
