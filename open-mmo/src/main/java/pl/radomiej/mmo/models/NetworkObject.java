package pl.radomiej.mmo.models;

import pl.radomiej.mmo.network.data.UdpEventDatagram;

public abstract class NetworkObject {
	private static int lastId = 1;
	public int id = lastId++;
	public int owner;
	public abstract byte getUpdateData(UdpEventDatagram udpEventDatagram);
	public abstract void getGeoData(UdpEventDatagram udpEventDatagram);
	public abstract void getCreateData(UdpEventDatagram udpEventDatagram);
	
}
