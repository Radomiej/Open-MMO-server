package pl.radomiej.mmo.network;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class NetworkDataStream {

	private byte[] data;
	private int cursor = 0;
	
	public NetworkDataStream(byte[] data) {
		this.data = data;
	}
	
	public NetworkDataStream() {
		this.data = new byte[0];
	}

	public float GetNextFloat() {
		if(cursor + 4 > data.length)
        {
            cursor += 4;
            return 0;
        }

		byte[] valuesBytes = Arrays.copyOfRange(data, cursor, cursor + 4);
		
		ByteBuffer bb = ByteBuffer.allocate(4).put(valuesBytes);
		bb.clear();
		float value = bb.getFloat();
        cursor += 4;
        return value;
	}

	public int GetNextInteger() {
		if(cursor + 4 > data.length)
        {
            cursor += 4;
            return 0;
        }

		byte[] valuesBytes = Arrays.copyOfRange(data, cursor, cursor + 4);

		ByteBuffer bb = ByteBuffer.allocate(4).put(valuesBytes);
		bb.clear();
		int value = bb.getInt();
        
        cursor += 4;
        return value;
	}
	
	public void PutNextFloat(float value) {
		byte[] floatArray = float2ByteArray(value);
		data = (byte[]) ArrayUtils.addAll(data, floatArray);
	}

	public void PutNextInteger(int value) {
		byte[] intArray = integer2ByteArray(value);
		data = (byte[]) ArrayUtils.addAll(data, intArray);
	}

	public void PutNextBytes(byte[] contentToAdd) {
		data = (byte[]) ArrayUtils.addAll(data, contentToAdd);
	}

	public byte[] getDataArray() {
		byte[] result = Arrays.copyOf(data, data.length);
        return result;
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
}
