package pl.radomiej.mmo.network.handlers;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class EventActionHandler implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		BasicNetworkEngine.INSTANCE.sendEvent(datagram.receipent, (byte)1, datagram.data);
		return null;
	}

}
