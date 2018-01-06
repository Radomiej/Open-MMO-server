package pl.radomiej.mmo;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.UdpGameEventHandler;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public interface ActionFactory {
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session);
}
