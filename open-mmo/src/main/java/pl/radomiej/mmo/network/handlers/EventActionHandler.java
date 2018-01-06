package pl.radomiej.mmo.network.handlers;

import java.util.Arrays;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class EventActionHandler implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		//Send ACK
		NetworkDataStream nds = datagram.getNetworkDataStream();
		int eventId = nds.GetNextInteger();
		BasicNetworkEngine.INSTANCE.sendAddressedAck(session, eventId);
		
		//Propagate event
		byte[] eventData = nds.getDataArray();
		eventData = Arrays.copyOfRange(eventData, 4, eventData.length);
		
		BasicNetworkEngine.INSTANCE.sendEvent(datagram.receipent, eventData);
		return null;
	}

}
