package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.AttackAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class AttackActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		int whoAttack = datagram.receipent;
		int whoDeffend = nds.GetNextInteger();
		float dmg = nds.GetNextFloat();
		int attackType = nds.GetNextInteger();
		float x = nds.GetNextFloat();
		float y = nds.GetNextFloat();
		float z = nds.GetNextFloat();

		AttackAction attackAction = new AttackAction(whoAttack, whoDeffend, attackType, dmg, x, y, z);
		return attackAction;
	}

}
