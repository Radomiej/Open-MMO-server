package pl.radomiej.mmo.models.specialized;

import java.util.concurrent.atomic.AtomicInteger;

import pl.radomiej.mmo.BasicNetworkEngine;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.network.data.UdpEventDatagram;

public abstract class GeoObject extends NetworkObject{
	public float x,y,z;
	private AtomicInteger rotX = new AtomicInteger(0);
	private AtomicInteger rotY = new AtomicInteger(0);
	private AtomicInteger rotZ = new AtomicInteger(0);
	
	@Override
	public void getGeoData(UdpEventDatagram udpEventDatagram) {
		udpEventDatagram.putFloat(x);
		udpEventDatagram.putFloat(y);
		udpEventDatagram.putFloat(z);
		
		float rotX = getRotX();
		float rotY = getRotY();
		float rotZ = getRotZ();
		
		udpEventDatagram.putFloat(rotX);
		udpEventDatagram.putFloat(rotY);
		udpEventDatagram.putFloat(rotZ);
		
		int sendTime = BasicNetworkEngine.INSTANCE.getServerTime();
		udpEventDatagram.putInt(sendTime);
//		System.out.println("rotation: " + rotX + " , " + rotY + " , " + rotZ);
	}

	public float getRotX() {
		return Float.intBitsToFloat(rotX.get());
	}

	public void setRotX(float rotX) {
		this.rotX.set(Float.floatToIntBits(rotX));
	}

	public float getRotY() {
		return Float.intBitsToFloat(rotY.get());
	}

	public void setRotY(float rotY) {
		this.rotY.set(Float.floatToIntBits(rotY));
	}

	public float getRotZ() {
		return Float.intBitsToFloat(rotZ.get());
	}

	public void setRotZ(float rotZ) {
		this.rotZ.set(Float.floatToIntBits(rotZ));
	}
}
