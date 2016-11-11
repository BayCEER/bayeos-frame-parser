package bayeos.frame.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import bayeos.binary.ByteArray;

public class BinaryFrame implements ByteFrame {
	
	private Long position;
	private byte[] payload;

	public BinaryFrame(Long position, byte[] payload) {
			this.position = position;
			this.payload = payload	;		
	}

	public byte[] getBytes(){
		byte[] n = new byte[5+ payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)0xa);
		bf.put(ByteArray.toByteUInt32(position));
		bf.put(payload);
		return n;		
	}
	

}
