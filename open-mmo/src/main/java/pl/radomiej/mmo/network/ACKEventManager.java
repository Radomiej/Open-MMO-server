package pl.radomiej.mmo.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class ACKEventManager {
	private Map<Integer, IoSession> eventIdToSession = new HashMap<>();
	private Map<Integer, UdpEventDatagram> eventIdToDatagram = new HashMap<>();

	private AtomicInteger currentGenerateId = new AtomicInteger(1);

	public int getNextId() {
		int nextId = currentGenerateId.getAndIncrement();
		// System.out.println("nextId: " + nextId);
		return nextId;
	}

	public synchronized void registerEvent(Integer eventId, IoSession session, UdpEventDatagram eventData) {
		synchronized (this) {
			eventIdToSession.put(eventId, session);
			eventIdToDatagram.put(eventId, eventData);
		}
	}

	public synchronized void acknowledgeEvent(Integer eventId) {
		// System.out.println("acknowledgeEvent: " + eventId);
		synchronized (this) {
			eventIdToSession.remove(eventId);
			eventIdToDatagram.remove(eventId);
		}
	}

	public synchronized Set<Integer> getNotAckEventsId() {
		return eventIdToDatagram.keySet();
	}

	public UdpEventDatagram getUdpEventDatagramByEventId(Integer eventId) {
		return eventIdToDatagram.get(eventId);
	}

	public IoSession getSessionByEventId(Integer eventId) {
		return eventIdToSession.get(eventId);
	}
}
