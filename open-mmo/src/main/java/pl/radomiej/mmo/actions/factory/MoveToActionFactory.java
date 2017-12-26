package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class MoveToActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		
		MoveToAction moveToAction = new MoveToAction();
		moveToAction.objectId = datagram.receipent;
		moveToAction.x = nds.GetNextFloat();
		moveToAction.y = nds.GetNextFloat();
		moveToAction.z = nds.GetNextFloat();
		moveToAction.rotX = nds.GetNextFloat();
		moveToAction.rotY = nds.GetNextFloat();
		moveToAction.rotZ = nds.GetNextFloat();
		
		return moveToAction;
	}

}
