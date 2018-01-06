package pl.radomiej.mmo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pl.radomiej.mmo.network.models.PlayerData;

public enum PlayerManager {
	INSTANCE;
	
	private AtomicInteger playerIdGenerator = new AtomicInteger(1);
	private Map<Integer, PlayerData> playersData = new HashMap<>();
	
	public PlayerData getPlayerData(int playerId){
		return playersData.get(playerId);
	}
	
	public PlayerData getOrCreatePlayerData(int playerId){
		if(playerId == 0){
			playerId = playerIdGenerator.getAndIncrement();
		}
		
		if(!playersData.containsKey(playerId)){
			PlayerData newPlayerData = createPlayerData(playerId);
			playersData.put(playerId, newPlayerData);
			return newPlayerData;
		}
		
		return playersData.get(playerId);
	}

	private PlayerData createPlayerData(int playerId) {
		PlayerData newPlayerData = new PlayerData();
		newPlayerData.setPlayerId(playerId);
		//TODO pozycja spawnu
		
		return newPlayerData;
	}
}
