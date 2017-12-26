package pl.radomiej.mmo.models.specialized;

import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class CharacterObject extends GeoObject {
	public int kind;
	public float hp, hpMax, gold;
	public float horizontal, vertical, jump;

	@Override
	public byte getUpdateData(UdpEventDatagram udpEventDatagram) {
		udpEventDatagram.putFloat(horizontal);
		udpEventDatagram.putFloat(vertical);
		udpEventDatagram.putFloat(jump);
//		udpEventDatagram.putFloat(jump);
		
		return 3;
	}

	@Override
	public String toString() {
		return "CharacterObject [horizontal=" + horizontal + ", vertical=" + vertical + ", jump=" + jump + "]";
	}

	@Override
	public void getCreateData(UdpEventDatagram udpEventDatagram) {
		udpEventDatagram.putInt(kind);
		super.getGeoData(udpEventDatagram);
	}
	
	
}
