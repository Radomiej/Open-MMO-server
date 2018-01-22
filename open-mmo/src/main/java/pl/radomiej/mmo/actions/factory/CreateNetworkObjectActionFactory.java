package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class CreateNetworkObjectActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		int kind = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();
		
		float rotX = nds.GetNextFloat();
		float rotY = nds.GetNextFloat();
		float rotZ = nds.GetNextFloat();
		int currentId = nds.GetNextInteger();
		int ownerObjectId = nds.GetNextInteger();
		System.out.println("NetworkObject create kind: " + kind + " currentId: " + currentId);
		
		CreateNetworkObjectAction createCharacterAction = new CreateNetworkObjectAction(session, kind, currentId, ownerObjectId);
		createCharacterAction.setPosition(x, y, z);
		createCharacterAction.setRotation(rotX, rotY, rotZ);
		
		return createCharacterAction;
	}

}
