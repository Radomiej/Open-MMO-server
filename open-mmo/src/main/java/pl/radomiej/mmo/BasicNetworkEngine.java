package pl.radomiej.mmo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;
import pl.radomiej.mmo.models.specialized.CharacterObject;
import pl.radomiej.mmo.network.ACKEventManager;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.PlayerEventHistory;
import pl.radomiej.mmo.network.data.UdpEventDatagram;
import pl.radomiej.mmo.network.handlers.SystemActionHandler;

public enum BasicNetworkEngine {
	INSTANCE;

	public static final String SESSION_ATTRIBUTE_PLAYER_ID = "PLAYER_ID";
	public static final String SESSION_ATTRIBUTE_PLAYER_OBJECT_ID = "PLAYER_OBJECT_ID";
	public static final String SESSION_ATTRIBUTE_EVENTS_HISTORY = "EVENTS_HISTORY";
	
	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<>());

	private Timer timerUpdate;
	private Timer timerCreate;
	private Timer timerCurrentInfo;

	private long startTime = System.currentTimeMillis();

	private ACKEventManager ackManager = new ACKEventManager();

	public void start() {
		timerUpdate = new Timer();
		timerUpdate.schedule(new TimerTask() {

			@Override
			public void run() {
				update();
			}
		}, 0, (1000 / 20));

		timerCreate = new Timer();
		timerCreate.schedule(new TimerTask() {

			@Override
			public void run() {
				resendNotAckEvents();
				resendNearestGeo();
			}
		}, 0, 200);

		timerCurrentInfo = new Timer();
		timerCurrentInfo.schedule(new TimerTask() {

			@Override
			public void run() {
				// resendCreateEvent();
				sendCurrentTime();
			}
		}, 0, 1000);
	}

	protected void resendNearestGeo() {
		// TODO Auto-generated method stub
		
	}

	protected void resendNotAckEvents() {
		synchronized (ackManager) {
			Set<Integer> eventIds = ackManager.getNotAckEventsId();
			for (Integer eventId : eventIds) {
				IoSession session = ackManager.getSessionByEventId(eventId);
				UdpEventDatagram datagram = ackManager.getUdpEventDatagramByEventId(eventId);
				// sendEventRaw(datagram, false);

				IoBuffer writeBuffer = IoBuffer.wrap(datagram.toBytes());
				session.write(writeBuffer);
			}

			if (eventIds.size() > 0)

			{
				// System.out.println("Resend events: " + eventIds.size());
			}
		}
	}

	protected void update() {

		synchronized (BasicGameEngine.INSTANCE.getObjects()) {
			for (NetworkObject networkObject : BasicGameEngine.INSTANCE.getObjects()) {
				boolean sendGeo = true;
				boolean sendUpdate = true;
				boolean sendPhysic = true;

				byte[] updateObjectData = getUpdateDataFromNetworkObject(networkObject);
				if (updateObjectData == null || updateObjectData.length <= 0) {
					sendUpdate = false;
				}
				byte[] geoObjectData = getGeoDataFromNetworkObject(networkObject);
				if (geoObjectData == null || geoObjectData.length <= 0) {
					sendGeo = false;
				}
				byte[] physicObjectData = getPhysicDataFromNetworkObject(networkObject);
				if (physicObjectData == null || physicObjectData.length <= 0) {
					sendPhysic = false;
				}

				IoBuffer writeBufferUpdateEvent = null;
				IoBuffer writeBufferGeoEvent = null;
				IoBuffer writeBufferPhysicEvent = null;

				if (sendUpdate)
					writeBufferUpdateEvent = IoBuffer.wrap(updateObjectData);
				if (sendGeo)
					writeBufferGeoEvent = IoBuffer.wrap(geoObjectData);
				if (sendPhysic)
					writeBufferPhysicEvent = IoBuffer.wrap(physicObjectData);

				synchronized (sessions) {
					for (IoSession session : sessions) {
						if (sendUpdate)
							session.write(writeBufferUpdateEvent);
						if (sendGeo)
							session.write(writeBufferGeoEvent);
						if (sendPhysic)
							session.write(writeBufferPhysicEvent);
					}
				}
			}
		}

	}

	private byte[] getPhysicDataFromNetworkObject(NetworkObject networkObject) {
		if (!networkObject.isNewPhysicData()) {
			return null;
		}
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(networkObject.id, (byte) 4);
		networkObject.getPhysicData(udpEventDatagram);
		return udpEventDatagram.toBytes();
	}

	private byte[] getGeoDataFromNetworkObject(NetworkObject networkObject) {
		if (!networkObject.isNewGeoData()) {
			return null;
		}
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(networkObject.id, (byte) 5);
		networkObject.getGeoData(udpEventDatagram);
		return udpEventDatagram.toBytes();
	}

	private byte[] getUpdateDataFromNetworkObject(NetworkObject networkObject) {
		if (!networkObject.isNewUpdateData()) {
			return null;
		}
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(networkObject.id, (byte) 2);

		byte type = networkObject.getUpdateData(udpEventDatagram);
		udpEventDatagram.setType(type);

		// System.out.println("data lenght: " + udpEventDatagram.lenght + "
		// data: " + Arrays.toString(udpEventDatagram.data));
		// udpEventDatagram.putFloat(geoObject.x);
		// udpEventDatagram.putFloat(geoObject.y);
		// udpEventDatagram.putFloat(geoObject.z);

		return udpEventDatagram.toBytes();
	}

	public void resendCreateEvent() {
		
		synchronized (BasicGameEngine.INSTANCE.getObjects()) {
			for (NetworkObject networkObject : BasicGameEngine.INSTANCE.getObjects()) {
				NetworkDataStream dataStream  = getCreateDataFromNetworkObject(networkObject);
//				if (updateObjectData == null || updateObjectData.length <= 0) {
//					System.err.println("Blad podczas zapisu do bytes");
//					continue;
//				}

				synchronized (sessions) {
					for (IoSession session : sessions) {
						sendAddresedSystemEvent(session, networkObject.id, SystemActionHandler.CREATE_NETWORK_OBJECT, dataStream);
					}
				}
			}
		}
	}

	public void resendAddressedCreateEvents(IoSession session) {
		synchronized (BasicGameEngine.INSTANCE.getObjects()) {
			for (NetworkObject networkObject : BasicGameEngine.INSTANCE.getObjects()) {
				NetworkDataStream dataStream = getCreateDataFromNetworkObject(networkObject);
//				if (updateObjectData == null || updateObjectData.length <= 0) {
//					System.err.println("Bład podczas zapisu do bytes");
//					continue;
//				}
				
				sendAddresedSystemEvent(session, networkObject.id, SystemActionHandler.CREATE_NETWORK_OBJECT, dataStream);
			}
		}
	}

	private NetworkDataStream getCreateDataFromNetworkObject(NetworkObject networkObject) {
		NetworkDataStream dataStream = new NetworkDataStream();
		networkObject.getCreateData(dataStream);

		return dataStream;
	}

	public void addSession(IoSession session) {
		PlayerEventHistory peh = new PlayerEventHistory();
		session.setAttribute(SESSION_ATTRIBUTE_EVENTS_HISTORY, peh);
		
		synchronized (sessions) {
			sessions.add(session);
		}
	}

	public void removeSession(IoSession session) {
		synchronized (sessions) {
			sessions.remove(session);
		}
	}

	public void sendRemoveEvent(int removeObjectId) {
		
		NetworkDataStream eventData = new NetworkDataStream();
		
		System.out.println("Wysylam RemoveEvent: " + removeObjectId);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				sendAddresedSystemEvent(session, removeObjectId, SystemActionHandler.REMOVE_NETWORK_OBJECT, eventData);
			}
			
		}
	}

	/**
	 * 
	 * @param playerObjectId
	 * @param playerLogout true when player leave the server
	 */
	public void sendLogoutEvent(int playerObjectId, boolean playerLogout) {
		NetworkDataStream dataStream = new NetworkDataStream();
		if(playerLogout) dataStream.PutNextInteger(1);
		
		synchronized (sessions) {
			for (IoSession session : sessions) {
				sendAddresedSystemEvent(session, playerObjectId, SystemActionHandler.UNREGISTER_PLAYER_OBJECT, dataStream);
				//session.write(writeBuffer);
			}
		}
	}

	public void sendCreateEvent(NetworkObject networkObject) {
		
		NetworkDataStream dataStream = new NetworkDataStream();
		networkObject.getCreateData(dataStream);

		// System.out.println("Send CreateEvent: " + udpEventDatagram);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				sendAddresedSystemEvent(session, networkObject.id, SystemActionHandler.CREATE_NETWORK_OBJECT, dataStream);
				//session.write(writeBuffer);
			}
		}
	}

	private void sendCurrentTime() {
		int currentTime = getServerTime();
		NetworkDataStream nds = new NetworkDataStream();
		nds.PutNextInteger(currentTime);

		// System.out.println("Wysy�am CurrentTime: " + currentTime);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				BasicNetworkEngine.INSTANCE.sendAddresedSystemEvent(session,
						SystemActionHandler.SEND_CURRENT_SERVER_TIME, nds);
			}
		}
	}

	public int getServerTime() {
		int time = (int) (System.currentTimeMillis() - startTime);
		return time;
	}

	public void sendAddressedAck(IoSession session, int eventId) {
		NetworkDataStream nds = new NetworkDataStream();
		nds.PutNextInteger(eventId);
		byte[] data = nds.getDataArray();

		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(0, (byte) 254, data.length, data);
		IoBuffer writeBuffer = IoBuffer.wrap(udpEventDatagram.toBytes());
		session.write(writeBuffer);
	}

	public void sendUdpEvent(int receipent, byte type, byte[] data) {
		if (type == 1) {
			sendEvent(receipent, data);
			return;
		}

		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(receipent, type, data.length, data);
		IoBuffer writeBuffer = IoBuffer.wrap(udpEventDatagram.toBytes());

		synchronized (sessions) {
			for (IoSession session : sessions) {
				// System.out.println("SendEvent: " + session + " receipent: " +
				// receipent);
				session.write(writeBuffer);
			}
		}
	}

	public void sendEvent(int receipent, byte[] dataArray) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(receipent, (byte) 1, dataArray.length, dataArray);

		sendEventRaw(udpEventDatagram, null);
	}

	public void sendEventWithout(int receipent, byte[] dataArray, IoSession withoutSession) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(receipent, (byte) 1, dataArray.length, dataArray);

		sendEventRaw(udpEventDatagram, withoutSession);
	}

	private void sendEventRaw(UdpEventDatagram udpEventDatagram, IoSession withoutSession) {
		byte[] data = udpEventDatagram.getData();
	
		synchronized (sessions) {
			for (IoSession session : sessions) {
				if(session == withoutSession) continue;
				
				int eventId = ackManager.getNextId();
				NetworkDataStream nds = new NetworkDataStream();
				nds.PutNextInteger(eventId);
				nds.PutNextBytes(data);
				byte[] content = nds.getDataArray();
	
				UdpEventDatagram ued = new UdpEventDatagram(udpEventDatagram.receipent, udpEventDatagram.type,
						content.length, content);
				IoBuffer writeBuffer = IoBuffer.wrap(ued.toBytes());
				ackManager.registerEvent(eventId, session, udpEventDatagram);
				session.write(writeBuffer);
			}
		}
	}

	public void sendSystemEvent(int receipent, int systemEventId, NetworkDataStream eventData) {
		synchronized (sessions) {
			for (IoSession session : sessions) {
				sendAddresedSystemEvent(session, receipent, systemEventId, eventData);
			}
		}
		
	}

	public void sendAddresedSystemEvent(IoSession receiver, int receipent, int systemEventId,
			NetworkDataStream eventData) {
		byte[] data = eventData.getDataArray();
		int eventId = ackManager.getNextId();
	
		NetworkDataStream nds = new NetworkDataStream();
		nds.PutNextInteger(eventId);
		nds.PutNextInteger(systemEventId);
		nds.PutNextBytes(data);
		data = nds.getDataArray();
	
		UdpEventDatagram ued = new UdpEventDatagram(receipent, (byte) 0, data.length, data);
		ackManager.registerEvent(eventId, receiver, ued);
	
		IoBuffer writeBuffer = IoBuffer.wrap(ued.toBytes());
		receiver.write(writeBuffer);
		//System.out.println("Send System Event: " + ued);
	
	}

	/**
	 * Skrocona wersja metody do wysylania zdarzenia systemowego
	 * 
	 * @param receiver
	 * @param systemEventId
	 * @param eventData
	 */
	public void sendAddresedSystemEvent(IoSession receiver, int systemEventId, NetworkDataStream eventData) {
		sendAddresedSystemEvent(receiver, 0, systemEventId, eventData);
	}

	public int sessionsCount() {
		synchronized (sessions) {
			return sessions.size();
		}
	}

	public ACKEventManager getAckManager() {
		return ackManager;
	}

	public void setAckManager(ACKEventManager ackManager) {
		this.ackManager = ackManager;
	}

}
