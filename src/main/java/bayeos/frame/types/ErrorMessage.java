package bayeos.frame.types;

import java.nio.ByteBuffer;

public class ErrorMessage implements ByteFrame {
	String value;
		
	public ErrorMessage(String value) {
			this.value = value;			
	}
	
	public byte[] getBytes(){		
		byte[] b = new byte[value.getBytes().length+1];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put((byte)0x5);
		bf.put(value.getBytes());
		return b;		
	}

}
