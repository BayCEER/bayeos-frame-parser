package de.unibayreuth.bayceer.bayeos.frame.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.unibayreuth.bayceer.bayeos.binary.ByteArray;
import de.unibayreuth.bayceer.bayeos.binary.CheckSum;

public class CheckSumFrame implements ByteFrame{
	
	byte[] payload;
	
	public CheckSumFrame(ByteFrame frame) {
		payload = frame.getBytes();
	}
				
	
	public byte[] getBytes() {
		byte[] n = new byte[3 + payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)0xf);
		bf.put(payload);
		CheckSum s = new CheckSum();
		s.addByte((byte)0xf);
		for(byte b:payload){
			s.addByte(b);
		}		
		bf.put(ByteArray.toByteUInt16(s.twoByte()));
		return n;
	}


	
	
	
	

}
