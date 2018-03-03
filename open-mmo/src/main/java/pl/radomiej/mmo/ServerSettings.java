package pl.radomiej.mmo;

public enum ServerSettings {
	CURRENT;
	
	/**
	 * Zapisywanie pozycji gracza pomiedzy logowaniami
	 */
	public boolean savePlayerPositions = true;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z GeoEvent
	 */
	public boolean sendOnlyChangedGeoData = true;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z PhysicEvent
	 */
	public boolean sendOnlyChangedPhysicData = true;
	
	/**
	 * Wysyłanie tylko zmieniajacych sie danych z AxisEvent
	 */
	public boolean sendOnlyChangedAxisData = true;
	
	public boolean resetServerWhenNoPlayer = false;
	
	public boolean destroyPlayerObjectWhenLogout = true;
}
