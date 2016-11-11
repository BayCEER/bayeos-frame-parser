package bayeos.frame.types;

import java.nio.ByteBuffer;

public class Command implements ByteFrame {
	String value;
	byte type;
	
	public Command(byte type, String value) {
			this.value = value;
			this.type = type;
	}
	
	public byte[] getBytes(){		
		byte[] b = new byte[value.getBytes().length+2];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put((byte)0x2);
		bf.put(type);
		bf.put(value.getBytes());
		return b;		
	}

}
