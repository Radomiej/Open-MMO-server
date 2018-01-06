package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class RemoveCharacterActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		System.out.println("RemoveCharacterActionFactory: " + datagram.receipent);
		RemoveCharacterAction removeCharacterAction = new RemoveCharacterAction(datagram.receipent);
		
		return removeCharacterAction;
	}

}
