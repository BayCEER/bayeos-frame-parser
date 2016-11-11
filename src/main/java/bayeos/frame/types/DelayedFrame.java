package bayeos.frame.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.binary.ByteArray;


public class DelayedFrame implements ByteFrame  {
	

	private long delay;	
	private byte[] payload;
		
	
	public DelayedFrame(long delay, ByteFrame frame){
		this(delay, frame.getBytes());
	}
	
	public DelayedFrame(long delay, byte[] payload) {
		this.delay = delay;		
		this.payload = payload;
	}
	

	public long getDelay() {
		return delay;
	}


	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public byte[] getBytes(){
		byte[] n = new byte[5 + payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)0x7);
		bf.put(ByteArray.toByteUInt32(delay));
		bf.put(payload);
		return n;		
	}
	
		

	
}
