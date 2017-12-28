package pl.radomiej.mmo.network.handlers;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.ACKEventManager;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class ACKHandler implements ActionFactory {

	private ACKEventManager ackManager;
	public ACKHandler(ACKEventManager ackManager) {
		this.ackManager = ackManager;
	}

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		int eventId = nds.GetNextInteger();
		ackManager.acknowledgeEvent(eventId);
		
		return null;
	}

}
