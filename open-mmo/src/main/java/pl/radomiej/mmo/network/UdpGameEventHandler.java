package pl.radomiej.mmo.network;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.ActionFactory;
import pl.radomiej.mmo.ActionHelper;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.actions.CreateNetworkObjectAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.actions.factory.AttackActionFactory;
import pl.radomiej.mmo.actions.factory.AxisInputActionFactory;
import pl.radomiej.mmo.actions.factory.CreateNetworkObjectActionFactory;
import pl.radomiej.mmo.actions.factory.MoveToActionFactory;
import pl.radomiej.mmo.actions.factory.PhysicUpdateActionFactory;
import pl.radomiej.mmo.actions.factory.RecoveryActionFactory;
import pl.radomiej.mmo.actions.factory.RemoveCharacterActionFactory;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.network.data.UdpEventDatagram;
import pl.radomiej.mmo.network.handlers.ACKHandler;
import pl.radomiej.mmo.network.handlers.EventActionHandler;
import pl.radomiej.mmo.network.handlers.SystemActionHandler;

public class UdpGameEventHandler extends IoHandlerAdapter {

	private Map<Byte, ActionFactory> actionFactories = new HashMap<>();
	
	public UdpGameEventHandler() {
		actionFactories.put((byte) 0, new SystemActionHandler());
		actionFactories.put((byte) 1, new EventActionHandler());
		actionFactories.put((byte) 2, new CreateNetworkObjectActionFactory());
		actionFactories.put((byte) 3, new AxisInputActionFactory());
		actionFactories.put((byte) 4, new PhysicUpdateActionFactory());
		actionFactories.put((byte) 5, new MoveToActionFactory());
		actionFactories.put((byte) 6, new AttackActionFactory());
		actionFactories.put((byte) 7, new RecoveryActionFactory());
		actionFactories.put((byte) 254, new ACKHandler(BasicNetworkEngine.INSTANCE.getAckManager()));
		actionFactories.put((byte) 255, new RemoveCharacterActionFactory());
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();

		BasicNetworkEngine.INSTANCE.addSession(session);
		System.out.println("sessionCreated: " + remoteAddress);

		BasicNetworkEngine.INSTANCE.resendAddressedCreateEvents(session);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer) message;
			int receipent = buffer.getInt();
			byte type = buffer.get();
			int lenght = buffer.getInt();
			byte[] content = new byte[lenght];
			buffer.get(content);

			UdpEventDatagram datagram = new UdpEventDatagram(receipent, type, lenght, content);
//			System.out.println("messageReceived: " + datagram);

			ActionHelper.INSTANCE.proccesByteAction(type, actionFactories, datagram, session);

		}

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();
		BasicNetworkEngine.INSTANCE.removeSession(session);
		System.out.println("sessionClosed: " + remoteAddress);
		
//		RemoveCharacterAction removePlayerAction = new RemoveCharacterAction((int) session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_PLAYER_OBJECT_ID));
//		BasicGameEngine.INSTANCE.addGameAction(removePlayerAction);
		
		if(BasicNetworkEngine.INSTANCE.sessionsCount() == 0){
			BasicGameEngine.INSTANCE.reset();
		}
	}
}
