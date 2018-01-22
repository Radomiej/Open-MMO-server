package pl.radomiej.mmo.network;

import java.util.HashSet;
import java.util.Set;

public class PlayerEventHistory {
	private Set<Integer> eventHistory = new HashSet<>(1000);
	
	public void addToHistory(int eventId){
		eventHistory.add(eventId);
	}
	
	public boolean containsInHistory(int eventId){
		return eventHistory.contains(eventId);
	}
}
