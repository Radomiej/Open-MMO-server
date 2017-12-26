package pl.radomiej.mmo.network.handlers;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class SystemActionHandler implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
