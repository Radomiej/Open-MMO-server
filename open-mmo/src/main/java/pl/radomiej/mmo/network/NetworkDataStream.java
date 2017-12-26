package pl.radomiej.mmo.network;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferWrapper;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

public class NetworkDataStream {

	private byte[] data;
	private IoBuffer buffer;
	public NetworkDataStream(byte[] data) {
		this.data = data;
		buffer = new SimpleBufferAllocator().wrap(ByteBuffer.wrap(data));
	}
	
	public float GetNextFloat() {
		return buffer.getFloat();
	}

	public int GetNextInteger() {
		return buffer.getInt();
	}
}
