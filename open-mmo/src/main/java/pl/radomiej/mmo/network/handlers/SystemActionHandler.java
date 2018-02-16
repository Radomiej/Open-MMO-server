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
import pl.radomiej.mmo.network.PlayerEventHistory;
import pl.radomiej.mmo.network.SessionHelpers;
import pl.radomiej.mmo.network.data.UdpEventDatagram;
import pl.radomiej.mmo.network.models.PlayerData;

public class SystemActionHandler implements ActionFactory {
	public static final int LOGIN_TO_SERVER = 1;
	public static final int REGISTER_PLAYER_OBJECT = 2;
	public static final int UNREGISTER_PLAYER_OBJECT = 3;
	public static final int REQUEST_OWNER = 4;
	public static final int DROP_OWNER = 5;
	public static final int CHANGE_OWNER_PROPERTIES = 6;
	
	public static final int CREATE_NETWORK_OBJECT = 12;
	public static final int REMOVE_NETWORK_OBJECT = 255;
	
	public static final int SEND_CURRENT_SERVER_TIME = 44; //Refer to SendServerTime
	public static final int RESERVED_ID = 55; //Reserv IDs for clients 
	
	
	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		int eventId = nds.GetNextInteger();
		BasicNetworkEngine.INSTANCE.sendAddressedAck(session, eventId);
		
		if(SessionHelpers.exhaustedEventFromClient(session, eventId)) return null;
		
		int systemEventId = nds.GetNextInteger();
		
		if(systemEventId == LOGIN_TO_SERVER){
			loginToServer(nds, session);
		}else if(systemEventId == REGISTER_PLAYER_OBJECT){
			registerPlayerObject(nds, session);
		}else if(systemEventId == CREATE_NETWORK_OBJECT){
			createNetworkObject(datagram.receipent, nds, session);
		}else if(systemEventId == REQUEST_OWNER){
			requestOwner(datagram.receipent, nds, session);
		}else if(systemEventId == DROP_OWNER){
			dropOwner(datagram.receipent, nds, session);
		}else if(systemEventId == CHANGE_OWNER_PROPERTIES){
			changeOwnerProperties(datagram.receipent, nds, session);
		}else if(systemEventId == REMOVE_NETWORK_OBJECT){
			destroyObject(datagram.receipent, nds, session);
		}else if(systemEventId == RESERVED_ID){
			reservIds(datagram.receipent, nds, session);
		}else{
			System.err.println("Unhandled ServerEvent type: " + systemEventId);
		}
		
		return null;
	}

	private void reservIds(int receipent, NetworkDataStream nds, IoSession session) {
		int reservedId = NetworkObject.lastId.getAndIncrement();
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(reservedId);
		
		BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(session, RESERVED_ID ,response);
		System.out.println("Odesłano reserved id: " + reservedId);
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
		System.out.println("requestOwner receipent: " + receipent);
		
		NetworkObject networkObject = BasicGameEngine.INSTANCE.findObjectById(receipent);
		if(networkObject == null) return;
		
		int priority = nds.GetNextInteger();
		if(networkObject.owner != 0 && priority < 1) {
			System.err.println("Odrzucono requestOwner receipent: " + receipent);
			return;
		}
		
		networkObject.owner = (int) session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(networkObject.owner);
		response.PutNextInteger(networkObject.ownerPlayer);
		response.PutNextInteger(networkObject.ownerGroup);
		response.PutNextInteger(networkObject.ownerFraction);
		BasicNetworkEngine.INSTANCE.sendSystemEvent(receipent, REQUEST_OWNER, response);
//		BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(session, receipent, REQUEST_OWNER, response);
		
		System.out.println("requestOwner receipent: " + receipent + " new owner: " + networkObject.owner);
	
	}
	
	private void dropOwner(int receipent, NetworkDataStream nds, IoSession session) {
		NetworkObject networkObject = BasicGameEngine.INSTANCE.findObjectById(receipent);
		if(networkObject == null) return;
		
		int ownerId = nds.GetNextInteger();
		int ownerPlayer = (int) session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_ID);
		if(networkObject.owner == 0 || (networkObject.owner != ownerId && networkObject.ownerPlayer != ownerPlayer)) return;
		
		networkObject.owner = 0;
		networkObject.ownerFraction = 0;
		networkObject.ownerGroup = 0;
		networkObject.ownerPlayer = 0;
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(networkObject.owner);
		response.PutNextInteger(networkObject.ownerPlayer);
		response.PutNextInteger(networkObject.ownerGroup);
		response.PutNextInteger(networkObject.ownerFraction);
		BasicNetworkEngine.INSTANCE.sendSystemEvent(receipent, DROP_OWNER, response);
		
	}

	private void changeOwnerProperties(int receipent, NetworkDataStream nds, IoSession session) {
		NetworkObject networkObject = BasicGameEngine.INSTANCE.findObjectById(receipent);
		if(networkObject == null) return;
		
		int groupId = nds.GetNextInteger();
		int fractionId = nds.GetNextInteger();
		
		networkObject.ownerGroup = groupId;
		networkObject.ownerFraction = fractionId;
		
		NetworkDataStream response = new NetworkDataStream();
		response.PutNextInteger(groupId);
		response.PutNextInteger(fractionId);
		
		BasicNetworkEngine.INSTANCE.sendSystemEvent(receipent, CHANGE_OWNER_PROPERTIES, response);
	}

	private void createNetworkObject(int receipent, NetworkDataStream nds, IoSession session){
		int kind = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();
		
		float rotX = nds.GetNextFloat();
		float rotY = nds.GetNextFloat();
		float rotZ = nds.GetNextFloat();
		
		int currentId = nds.GetNextInteger();
		int ownerObjectId = nds.GetNextInteger();
		int ownerGroupId = nds.GetNextInteger();
		int ownerFractionId  = nds.GetNextInteger();
		
		System.out.println("NetworkObject create kind: " + kind + " currentId: " + currentId + " owners: " + ownerObjectId);
		
		CreateNetworkObjectAction createCharacterAction = new CreateNetworkObjectAction(session, kind, currentId, ownerObjectId, ownerGroupId, ownerFractionId);
		createCharacterAction.setPosition(x, y, z);
		createCharacterAction.setRotation(rotX, rotY, rotZ);
		
		BasicGameEngine.INSTANCE.addGameAction(createCharacterAction);
	}
}
