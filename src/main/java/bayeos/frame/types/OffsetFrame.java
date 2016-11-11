package bayeos.frame.types;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class OffsetFrame implements ByteFrame {
	
	
	private NumberType numberType;
	private byte offset;
	private Number[] values;	
	public OffsetFrame(NumberType numberType, int offset, Number... values){
		this.numberType = numberType;
		this.offset = (byte)offset;
		this.values = values;
	}

	public byte[] getBytes(){
		byte[] n = new byte[3+values.length*numberType.getLength()];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put((byte)0x1);
		bf.put((byte)(0x0 + numberType.getIndex()));
		bf.put(offset);		
		for(Number nu:values){
			bf.put(numberType.toByte(nu));
		}		
		return n;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OffsetFrame a = new OffsetFrame(NumberType.UInt8,0,10,12);				
	}
}
