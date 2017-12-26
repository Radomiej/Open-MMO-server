package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class CreateCharacterActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		int kind = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();
		
		System.out.println("kind: " + kind);
		
		CreateCharacterAction createCharacterAction = new CreateCharacterAction(session, kind);
		createCharacterAction.setPosition(x, y, z);
		
		return createCharacterAction;
	}

}
