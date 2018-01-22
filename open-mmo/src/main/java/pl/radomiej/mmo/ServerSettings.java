package pl.radomiej.mmo;

public enum ServerSettings {
	CURRENT;
	
	public float[] playerStartPosition;
	
	/**
	 * Zapisywanie pozycji gracza pomiedzy logowaniami
	 */
	public boolean savePlayerPositions = true;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z GeoEvent
	 */
	public boolean sendOnlyChangedGeoData = false;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z PhysicEvent
	 */
	public boolean sendOnlyChangedPhysicData = false;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z AxisEvent
	 */
	public boolean sendOnlyChangedAxisData = false;
	
	public boolean resetServerWhenNoPlayer = false;
	
	public boolean destroyPlayerObjectWhenLogout = true;
}
