package pl.radomiej.mmo.actions.factory;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.actions.AttackAction;
import pl.radomiej.mmo.actions.RecoveryAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class RecoveryActionFactory implements ActionFactory {

	@Override
	public GameAction createGameActionFromNetworkEvent(UdpEventDatagram datagram, IoSession session) {
		NetworkDataStream nds = datagram.getNetworkDataStream();

		int revoceryObject = datagram.receipent;
		float recoveryHp = nds.GetNextFloat();

		RecoveryAction recoveryAction = new RecoveryAction(revoceryObject, recoveryHp);
		return recoveryAction;
	}

}
