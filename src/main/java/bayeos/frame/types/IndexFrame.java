package bayeos.frame.types;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import bayeos.binary.ByteArray;

public class IndexFrame implements ByteFrame  {
	
	private HashMap<Integer,Number> values;
	private NumberType numberType;
		
	public IndexFrame(NumberType numberType, Number...values){
		this.numberType = numberType;		
		HashMap<Integer, Number> v = new HashMap<>();
		for(int i=0;i<values.length;i++){
			v.put(i+1, values[i]);			
		}
		this.values = v;
	}
	
	public IndexFrame(NumberType numberType, HashMap<Integer, Number> values){
		this.numberType = numberType;
		this.values = values;
	}

	public byte[] getBytes(){
		byte[] n = new byte[2+values.size()*(numberType.getLength()+1)];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put((byte)0x1);
		bf.put((byte)(0x40 + numberType.getIndex()));		
		for(Map.Entry<Integer, Number> e: values.entrySet()){
			bf.put(ByteArray.toByteUInt8(e.getKey()));
			bf.put(numberType.toByte(e.getValue()));
		}		
		return n;
	}
	
	
}
