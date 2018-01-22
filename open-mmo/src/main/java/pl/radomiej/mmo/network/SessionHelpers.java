package pl.radomiej.mmo.network;

import org.apache.mina.core.session.IoSession;

import pl.radomiej.mmo.BasicNetworkEngine;

public class SessionHelpers {
	public static boolean exhaustedEventFromClient(IoSession session, int eventId) {
		Object rawPeh = session.getAttribute(BasicNetworkEngine.SESSION_ATTRIBUTE_EVENTS_HISTORY);
		if(rawPeh == null){
			System.err.println("Player dont have PlayerEventHistory object!");
			return false;
		}
		
		PlayerEventHistory peh = (PlayerEventHistory) rawPeh;
		boolean exhausted = peh.containsInHistory(eventId);
		if(!exhausted) peh.addToHistory(eventId);
		else{
			System.out.println("Repeat event from client: " + eventId);
		}
		return exhausted;
	}
}
