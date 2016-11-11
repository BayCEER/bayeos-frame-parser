package bayeos.frame.types;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataFrame implements ByteFrame {
	
	private Number[] values;
	private NumberType numberType;
		
	public DataFrame(NumberType numberType, Number... values){
		this.numberType = numberType;
		this.values = values;
	}

	public byte[] getBytes(){
		byte[] n = new byte[2+values.length*numberType.getLength()];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put((byte)0x1);
		bf.put((byte)(0x20 + numberType.getIndex()));
		for(Number nu:values){
			bf.put(numberType.toByte(nu));
		}		
		return n;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		DataFrame a = new DataFrame(NumberType.UInt8,10,10,12);				
	}
}
