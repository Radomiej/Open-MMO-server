package pl.radomiej.mmo;

import java.util.Map;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public enum ActionHelper {
	INSTANCE;
	
	public GameAction proccesByteAction(Byte type, Map<Byte, ActionFactory> actionFactories, UdpEventDatagram datagram, IoSession session){
		ActionFactory actionFactory = actionFactories.get(type);
		if (actionFactory != null) {
			GameAction gameAction = actionFactory.createGameActionFromNetworkEvent(datagram, session);
			if (gameAction != null) {
				BasicGameEngine.INSTANCE.addGameAction(gameAction);
			}
			return gameAction;
		}
		
		return null;
	}
	//TODO fix DRY
	public GameAction proccesIntegerAction(Integer type, Map<Integer, ActionFactory> actionFactories, UdpEventDatagram datagram, IoSession session){
		ActionFactory actionFactory = actionFactories.get(type);
		if (actionFactory != null) {
			GameAction gameAction = actionFactory.createGameActionFromNetworkEvent(datagram, session);
			if (gameAction != null) {
				BasicGameEngine.INSTANCE.addGameAction(gameAction);
			}
			return gameAction;
		}
		
		return null;
	}
}
