package pl.radomiej.mmo.network.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.ActionHelper;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.factory.AttackActionFactory;
import pl.radomiej.mmo.actions.factory.RecoveryActionFactory;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.SessionHelpers;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class EventActionHandler implements ActionFactory {
	public static final int ATTACK_EVENT = 6; // Incoming pre-build Attack event
	public static final int RECOVERY_EVENT = 7; // Incoming pre-build Recovery
												// event

	private Map<Integer, ActionFactory> actionFactories = new HashMap<>();

	public EventActionHandler() {
		actionFactories.put(ATTACK_EVENT, new AttackActionFactory());
		actionFactories.put(RECOVERY_EVENT, new RecoveryActionFactory());
	}

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		// Send ACK
		NetworkDataStream nds = datagram.getNetworkDataStream();
		int eventId = nds.GetNextInteger();
		int eventType = nds.GetNextInteger();
		byte receiverType = nds.GetNextByte();
		BasicNetworkEngine.INSTANCE.sendAddressedAck(session, eventId);

		if(SessionHelpers.exhaustedEventFromClient(session, eventId)) return null;
		
		if (receiverType == 0) {
			ActionHelper.INSTANCE.proccesIntegerAction(eventType, actionFactories, datagram, session);
		} else if (receiverType == 1) {
			// Propagate event to all
			byte[] eventData = nds.getDataArray();
			eventData = Arrays.copyOfRange(eventData, 4, eventData.length);

			BasicNetworkEngine.INSTANCE.sendEvent(datagram.receipent, eventData);
		} else if (receiverType == 2) {
			// Propagate event to all others
			byte[] eventData = nds.getDataArray();
			eventData = Arrays.copyOfRange(eventData, 4, eventData.length);

			BasicNetworkEngine.INSTANCE.sendEventWithout(datagram.receipent, eventData, session);
		}

		return null;
	}

}
