package pl.radomiej.mmo.network.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import pl.radomiej.mmo.network.NetworkDataStream;

public class UdpEventDatagram {
	public int receipent;
	public byte type;
	public int lenght;
	public byte[] data;

	public UdpEventDatagram(int receipent, byte type, int lenght, byte[] content) {
		this.receipent = receipent;
		this.type = type;
		this.lenght = lenght;
		this.data = content;
	}

	public UdpEventDatagram(int receipent, byte type) {
		this.receipent = receipent;
		this.type = type;
	}

	public int getReceipent() {
		return receipent;
	}

	public void setReceipent(int receipent) {
		this.receipent = receipent;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getLenght() {
		return lenght;
	}

	public void setLenght(int lenght) {
		this.lenght = lenght;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "UdpEventDatagram [receipent=" + receipent + ", type=" + type + ", lenght=" + lenght + ", data="
				+ Arrays.toString(data) + "]";
	}

	public void putInt(int value) {
		byte[] intArray = integer2ByteArray(value);
		data = (byte[]) ArrayUtils.addAll(data, intArray);
		lenght = data.length;
	}

	public void putFloat(float value) {
		byte[] floatArray = float2ByteArray(value);
		data = (byte[]) ArrayUtils.addAll(data, floatArray);
		lenght = data.length;
	}

	public void putByte(byte b) {
		byte[] byteArray = { b };
		data = (byte[]) ArrayUtils.addAll(data, byteArray);
		lenght = data.length;
	}

	public byte[] toBytes() {
		byte[] receipentBytes = integer2ByteArray(receipent);
		byte[] typeBytes = { type };
		byte[] lenghtBytes = integer2ByteArray(data.length);

		byte[] all = (byte[]) ArrayUtils.addAll(receipentBytes, typeBytes);
		all = (byte[]) ArrayUtils.addAll(all, lenghtBytes);
		all = (byte[]) ArrayUtils.addAll(all, data);
		return all;
	}

	public static byte[] long2ByteArray(long value) {
		return ByteBuffer.allocate(8).putLong(value).array();
	}

	public static byte[] integer2ByteArray(int value) {
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public static byte[] float2ByteArray(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	public NetworkDataStream getNetworkDataStream() {
		NetworkDataStream result = new NetworkDataStream(data);
		return result;
	}
}
