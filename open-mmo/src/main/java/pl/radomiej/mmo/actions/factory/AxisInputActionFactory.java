package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.AxisInputAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class AxisInputActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();
		
		AxisInputAction axisInputAction = new AxisInputAction();
		axisInputAction.objectId = datagram.receipent;
		
		axisInputAction.horizontal = nds.GetNextFloat();
		axisInputAction.vertical = nds.GetNextFloat();
		axisInputAction.jump = nds.GetNextFloat();
		axisInputAction.axis4 = nds.GetNextFloat();
		
		return axisInputAction;
	}

}
