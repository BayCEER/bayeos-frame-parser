package bayeos.frame.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import bayeos.binary.ByteArray;

public class LabeledFrame implements ByteFrame {
	
	private Map<String,Number> values;
	private NumberType numberType;
	
	public LabeledFrame(NumberType numberType, String map){
		this.numberType = numberType;
		this.values = MapUtils.getMap(map);
	}
	
	public LabeledFrame(NumberType numberType, Map<String,Number> values){
		this.numberType = numberType;
		this.values = values;
	}
	
	public byte[] getBytes(){
		int len = 2;
		for (String label:values.keySet()){
			len = len + 1 + label.length() + numberType.getLength();
		}
		byte[] b = new byte[len];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.order(ByteOrder.LITTLE_ENDIAN);		
		bf.put((byte)0x1);
		bf.put((byte)(0x60 + numberType.getIndex()));	
		for(Map.Entry<String, Number> e: values.entrySet()){			
			bf.put(ByteArray.toByteUInt8(e.getKey().length()));
			bf.put(e.getKey().getBytes());
			bf.put(numberType.toByte(e.getValue()));
		}		
		return b;
	}

}
