package pl.radomiej.mmo.network.models;

public class PlayerData {
	private int playerId;
	private int playerObjectId;
	private float spawnX, spawnY, spawnZ;

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getPlayerObjectId() {
		return playerObjectId;
	}

	public void setPlayerObjectId(int playerObjectId) {
		this.playerObjectId = playerObjectId;
	}

	public float getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(float spawnX) {
		this.spawnX = spawnX;
	}

	public float getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(float spawnY) {
		this.spawnY = spawnY;
	}

	public float getSpawnZ() {
		return spawnZ;
	}

	public void setSpawnZ(float spawnZ) {
		this.spawnZ = spawnZ;
	}

}
