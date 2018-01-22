package pl.radomiej.mmo.models;

import java.util.concurrent.atomic.AtomicInteger;

import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public abstract class NetworkObject {
	public static AtomicInteger lastId = new AtomicInteger(1);
	
	public int id;
	public int owner;
	public int ownerPlayer;
	public int ownerGroup;
	public int ownerFraction;
	
	public NetworkObject() {
		id = lastId.getAndIncrement();
	}
	public NetworkObject(int currentId) {
		id = currentId;
	}
	
	public abstract byte getUpdateData(UdpEventDatagram udpEventDatagram);
	public abstract void getGeoData(UdpEventDatagram udpEventDatagram);
	public abstract void getPhysicData(UdpEventDatagram udpEventDatagram);
	public abstract void getCreateData(NetworkDataStream dataStream);
	public abstract boolean isNewGeoData();
	public abstract boolean isNewUpdateData();
	public abstract boolean isNewPhysicData();
	
}
