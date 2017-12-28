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
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public enum BasicNetworkEngine {
	INSTANCE;

	public static final String SESSION_ATTRIBUTE_PLAYER_OBJECT_ID = "";

	private Set<IoSession> sessions = Collections.synchronizedSet(new HashSet<>());

	private Timer timerUpdate;
	private Timer timerCreate;

	private long startTime = System.currentTimeMillis();

	private ACKEventManager ackManager = new ACKEventManager();

	public void start() {
		timerUpdate = new Timer();
		timerUpdate.schedule(new TimerTask() {

			@Override
			public void run() {
				update();
			}
		}, 0, (1000 / 5));

		timerCreate = new Timer();
		timerCreate.schedule(new TimerTask() {

			@Override
			public void run() {
				resendNotAckEvents();
			}
		}, 0, 200);

		timerCreate = new Timer();
		timerCreate.schedule(new TimerTask() {

			@Override
			public void run() {
				// resendCreateEvent();
				sendCurrentTime();
			}
		}, 0, 1000);
	}

	protected void resendNotAckEvents() {
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
			System.out.println("Resend events: " + eventIds.size());
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
				byte[] updateObjectData = getCreateDataFromNetworkObject(networkObject);
				if (updateObjectData == null || updateObjectData.length <= 0) {
					System.err.println("B³ad podczas zapisu do bytes");
					continue;
				}
				IoBuffer writeBuffer = IoBuffer.wrap(updateObjectData);

				synchronized (sessions) {
					for (IoSession session : sessions) {
						session.write(writeBuffer);
					}
				}
			}
		}
	}

	public void resendCreateEvent(IoSession session) {
		synchronized (BasicGameEngine.INSTANCE.getObjects()) {
			for (NetworkObject networkObject : BasicGameEngine.INSTANCE.getObjects()) {
				byte[] updateObjectData = getCreateDataFromNetworkObject(networkObject);
				if (updateObjectData == null || updateObjectData.length <= 0) {
					System.err.println("B³ad podczas zapisu do bytes");
					continue;
				}
				IoBuffer writeBuffer = IoBuffer.wrap(updateObjectData);
				session.write(writeBuffer);
			}
		}
	}

	private byte[] getCreateDataFromNetworkObject(NetworkObject networkObject) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(networkObject.id, (byte) 2);
		networkObject.getCreateData(udpEventDatagram);

		return udpEventDatagram.toBytes();
	}

	public void addSession(IoSession session) {
		synchronized (sessions) {
			sessions.add(session);
		}
	}

	public void removeSession(IoSession session) {
		synchronized (sessions) {
			sessions.remove(session);
		}
	}

	public void sendPlayerObjectId(IoSession ownerSession, int playerObjectId) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(playerObjectId, (byte) 0);
		udpEventDatagram.putInt(0);

		byte[] data = udpEventDatagram.toBytes();
		IoBuffer writeBuffer = IoBuffer.wrap(data);
		ownerSession.write(writeBuffer);
		// System.out.println("Wysy³am Ownership");
	}

	public void sendRemoveEvent(int removeObjectId) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(removeObjectId, (byte) 255);
		udpEventDatagram.putByte((byte) 0);

		byte[] data = udpEventDatagram.toBytes();
		IoBuffer writeBuffer = IoBuffer.wrap(data);

		System.out.println("Wysy³am RemoveEvent: " + udpEventDatagram);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				session.write(writeBuffer);
			}
		}
	}

	public void sendCreateEvent(NetworkObject networkObject) {
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(networkObject.id, (byte) 2);
		networkObject.getCreateData(udpEventDatagram);

		byte[] data = udpEventDatagram.toBytes();
		IoBuffer writeBuffer = IoBuffer.wrap(data);

		// System.out.println("Wysy³am CreateEvent: " + udpEventDatagram);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				session.write(writeBuffer);
			}
		}
	}

	private void sendCurrentTime() {
		int currentTime = getServerTime();
		UdpEventDatagram udpEventDatagram = new UdpEventDatagram(1, (byte) 0);
		udpEventDatagram.putInt(2);
		udpEventDatagram.putInt(currentTime);

		byte[] data = udpEventDatagram.toBytes();
		IoBuffer writeBuffer = IoBuffer.wrap(data);

		// System.out.println("Wysy³am CurrentTime: " + currentTime);
		synchronized (sessions) {
			for (IoSession session : sessions) {
				session.write(writeBuffer);
			}
		}
	}

	public int getServerTime() {
		int time = (int) (System.currentTimeMillis() - startTime);
		return time;
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

		sendEventRaw(udpEventDatagram, true);
	}

	private void sendEventRaw(UdpEventDatagram udpEventDatagram, boolean addToAckManger) {
		byte[] data = udpEventDatagram.getData();
		
		synchronized (sessions) {
			for (IoSession session : sessions) {
				int eventId = ackManager.getNextId();
				NetworkDataStream nds = new NetworkDataStream();
				nds.PutNextInteger(eventId);
				nds.PutNextBytes(data);
				byte[] content = nds.getDataArray();
				
				UdpEventDatagram ued = new UdpEventDatagram(udpEventDatagram.receipent, udpEventDatagram.type, content.length, content);
				IoBuffer writeBuffer = IoBuffer.wrap(ued.toBytes());
				ackManager.registerEvent(eventId, session, udpEventDatagram);
				session.write(writeBuffer);
			}
		}
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
