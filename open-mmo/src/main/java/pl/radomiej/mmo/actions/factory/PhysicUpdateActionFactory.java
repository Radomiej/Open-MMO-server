package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.actions.PhysicUpdateAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class PhysicUpdateActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		
		PhysicUpdateAction physicUpdateAction = new PhysicUpdateAction();
		physicUpdateAction.objectId = datagram.receipent;
		physicUpdateAction.x = nds.GetNextFloat();
		physicUpdateAction.y = nds.GetNextFloat();
		physicUpdateAction.z = nds.GetNextFloat();
//		moveToAction.rotX = nds.GetNextFloat();
//		moveToAction.rotY = nds.GetNextFloat();
//		moveToAction.rotZ = nds.GetNextFloat();
		
		return physicUpdateAction;
	}

}
