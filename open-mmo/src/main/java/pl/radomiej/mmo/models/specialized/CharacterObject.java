package pl.radomiej.mmo.models.specialized;

import java.util.concurrent.atomic.AtomicBoolean;

import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.network.NetworkDataStream;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public class CharacterObject extends GeoObject {
	public int kind;
	public float hp, hpMax, gold;
	public float horizontal, vertical, jump;
	public float axis4;
	
	private AtomicBoolean isNewUpdateData = new AtomicBoolean(true);
	
	
	@Override
	public byte getUpdateData(UdpEventDatagram udpEventDatagram) {
		udpEventDatagram.putFloat(horizontal);
		udpEventDatagram.putFloat(vertical);
		udpEventDatagram.putFloat(jump);
		udpEventDatagram.putFloat(axis4);
		
		return 3;
	}

	@Override
	public String toString() {
		return "CharacterObject [horizontal=" + horizontal + ", vertical=" + vertical + ", jump=" + jump + "]";
	}

	@Override
	public void getCreateData(NetworkDataStream dataStream) {
		dataStream.PutNextInteger(kind);	

		dataStream.PutNextFloat(x);
		dataStream.PutNextFloat(y);
		dataStream.PutNextFloat(z);

		float rotX = getRotX();
		float rotY = getRotY();
		float rotZ = getRotZ();

		dataStream.PutNextFloat(rotX);
		dataStream.PutNextFloat(rotY);
		dataStream.PutNextFloat(rotZ);

		int sendTime = BasicNetworkEngine.INSTANCE.getServerTime();
		dataStream.PutNextInteger(sendTime);
	}


	@Override
	public boolean isNewUpdateData() {
		return isNewUpdateData.getAndSet(false);
	}
	
	public void setNewUpdateData(){
		isNewUpdateData.set(true);
	}
	
}
