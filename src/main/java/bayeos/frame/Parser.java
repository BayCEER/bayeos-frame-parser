package bayeos.frame;


import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import bayeos.binary.ByteArray;
import bayeos.binary.CheckSum;

public class Parser {
	
	public static Map<String,Object> parse(byte[] b) throws FrameParserException {
		ByteBuffer bf  = ByteBuffer.wrap(b);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		return parseByteBuffer(bf);
	}
	
	
	public static Map<String,Object> parse(byte[] b, Date ts, String origin, Integer rssi) throws FrameParserException {
		ByteBuffer bf  = ByteBuffer.wrap(b);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		return parseByteBuffer(bf,ts,origin,rssi);
	}
	

	public static Map<String, Object> parseBase64(String base64) throws FrameParserException {
		return parseBase64(base64,new Date(),"", null);
	}
	
	public static Map<String, Object> parseBase64(String base64, Date ts, String origin, Integer rssi) throws FrameParserException{
		byte[] decoded = DatatypeConverter.parseBase64Binary(base64);
		ByteBuffer bf = ByteBuffer.wrap(decoded);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		return parseByteBuffer(bf,ts,origin,rssi);		
	}
	
	public static Map<String, Object> parseFile(String path) throws FrameParserException, IOException {
		return parseFile(path,new Date(),"",null);
	}
	
	public static Map<String, Object> parseFile(String path, Date ts, String origin, Integer rssi) throws FrameParserException, IOException{
		ByteBuffer bf;			
		bf = ByteBuffer.wrap(Files.readAllBytes(Paths.get(path)));		
		bf.order(ByteOrder.LITTLE_ENDIAN);
		return parseByteBuffer(bf,ts,origin,rssi);
	}
	
	public static Map<String, Object> parseByteBuffer(ByteBuffer bf) throws FrameParserException {
		return parseByteBuffer(bf, new Date(),"", null);
	}

	public static Map<String, Object> parseByteBuffer(ByteBuffer bf, Date ts, String origin, Integer rssi)
			throws FrameParserException {
		Map<String, Object> result = new HashMap<String, Object>(10);
		result.put("ts", ts.getTime() * 1000 * 1000);
		result.put("origin", origin);
		result.put("rssi", rssi);
		result.put("validchecksum", Boolean.FALSE);
		parseData(bf, result);
		return result;
	}

	
	// Private methods
	private static void parseData(ByteBuffer bf, Map<String, Object> result) throws FrameParserException {
		short myId;
		short panId;
		int rssi;
		StringBuffer b;
		long ts;
		long delay;
		int length;
		
		Map<String,Object> value;
		
		try {
		
		while (bf.remaining() > 1) {
			byte ft = bf.get();
			
			switch (ft) {
			case 0x1:
				result.put("type", "DataFrame");
				parseDataFrame(bf, result);
				break;
			case 0x2:
				result.put("type", "Command");
				value = new Hashtable();
				value.put("cmd", bf.get());
				value.put("value", getRemaining(bf));
				result.put("value", value);
				break;
			case 0x3:
				result.put("type", "CommandResponse");
				value = new Hashtable();				
				value.put("cmd", bf.get());
				value.put("value", getRemaining(bf));
				result.put("value", value);
				break;
			case 0x4:
				result.put("type", "Message");
				result.put("value", getString(bf));				
				break;
			case 0x5:
				result.put("type", "ErrorMessage");				
				result.put("value", getString(bf));								
				break;
			case 0x6:
				// RoutedFrame 
				myId = bf.getShort();
				panId = bf.getShort();
			    b = new StringBuffer((String)result.get("origin"));
				b.append("/XBee").append(panId).append(":").append(myId);
				result.put("origin", b.toString());		
				parseData(bf, result);
				break;
			case 0x7:
			case 0x10:				
				// DelayedFrame and DelayedSecondFrame 
				ts = (Long) result.get("ts");				
				delay = ByteArray.fromByteUInt32(bf) * 1000 * 1000 * ((ft==0x10)?1000:1);			
				result.put("ts", ts - delay);
				parseData(bf, result);
				break;																	
			case 0x8:
				// RoutedFrameRSSI
				myId = bf.getShort();
				panId = bf.getShort();
				b = new StringBuffer((String)result.get("origin"));
				b.append("/XBee").append(panId).append(":").append(myId);
				result.put("origin", b.toString());				
				rssi = (bf.get() & 0xff);				
				
				if (result.get("rssi") == null) {
					result.put("rssi", rssi);
				} else {
					if (rssi > (Integer)result.get("rssi") ){
						result.put("rssi", rssi);
					}						
				}
				parseData(bf, result);
				break;
			case 0x9:
				// TimestampFrame
				ts = ByteArray.fromByteUInt32(bf);
				result.put("ts", DateAdapter.getDate(ts).getTime() * 1000 * 1000);
				parseData(bf, result);
				break;			
			case 0xa:
				// BinaryFrame
				result.put("type", "BinaryFrame");				
				Long d = ByteArray.fromByteUInt32(bf);				
				value = new Hashtable();					
				value.put("pos", d);							
				value.put("value",getRemaining(bf));
				result.put("value", value);
				break;
			case 0xb:
				// OriginFrame
				length = bf.get() & 0xff;						
				String o = "";
				if (length > 0) {
					byte[] s = new byte[length];
					bf.get(s);
					o = new String(s);					
				}
				result.put("origin", o);
				parseData(bf, result);
				break;
			case 0xc:
				// MillisecondTimestampFrame
				result.put("ts",bf.getLong() * 1000 * 1000);
				parseData(bf, result);
				break;
			case 0xd:
				// RoutedOriginFrame
				b = new StringBuffer((String)result.get("origin"));
				b.append("/");
				length = bf.get() & 0xff;		
				if (length > 0) {
					byte[] s = new byte[length];
					bf.get(s);
					b.append(new String(s));					
				}
				result.put("origin", b.toString());
				parseData(bf, result);
				break;
			case 0xe:
				// GatewayCommand
				break;
			case 0xf:
				// ChecksumFrame
				CheckSum chk = new CheckSum();
				chk.addByte(ft); 				
				byte[] payload = new byte[bf.remaining() - 2];				
				bf.get(payload);				
				chk.addBytes(payload);				
				boolean validchecksum = ByteArray.fromByteUInt16(bf) == chk.twoByte();
				
				if (validchecksum){
					result.put("validchecksum",true);										
					ByteBuffer p = ByteBuffer.wrap(payload);
					p.order(ByteOrder.LITTLE_ENDIAN);
					parseData(p, result);
				} else {
					throw new InvalidChecksumException("Invalid checksum detected.");
				}
				break;
			default:
				throw new InvalidFrameTypeException("Invalid frame type:" + ByteArray.toString(ft));
			}
		}
		
		} catch (BufferUnderflowException e){			
			throw new IncompleteFrameException("Failed to read frame.");
		}

	}

	private static void parseDataFrame(ByteBuffer bf, Map<String, Object> result) throws FrameParserException {

		byte dataType = bf.get();

		byte offsetType = (byte) (0xf0 & dataType);
		byte valueType = (byte) (0x0f & dataType);
		String key = "0";
		Map values;

		switch (valueType) {
		case 0x1:
			values = new Hashtable<String, Float>();
			break;
		case 0x2:
			values = new Hashtable<String, Integer>();
			break;
		case 0x3:
			values = new Hashtable<String, Short>();
			break;
		case 0x4:
			values = new Hashtable<String, Byte>();
			break;
		default:
			throw new InvalidValueTypeException("Invalid value type:" + ByteArray.toString(valueType));
		}

		
		if (offsetType == 0x0) {
			key = String.valueOf(bf.get() & 0xff);
		}

		while (bf.remaining() > 0) {
			switch (offsetType) {
			case 0x40:
				key = String.valueOf(bf.get() & 0xff);
				break;
			case 0x60:
				byte[] ba = new byte[bf.get() & 0xff];
				bf.get(ba);
				key = new String(ba);
				break;
			default:
				key = String.valueOf(Integer.valueOf(key) + 1);
			}

			// Read Value
			switch (valueType) {
			case (0x1): // Float
				values.put(key, bf.getFloat());
				break;
			case (0x2): // Integer
				values.put(key, bf.getInt());
				break;
			case (0x3): // Short
				values.put(key, bf.getShort());
				break;
			case (0x4): // UInt8
				values.put(key, (bf.get() & 0xff));
				break;
			default:
				throw new InvalidValueTypeException("Invalid value type:" + ByteArray.toString(valueType));
			}
		}		
		result.put("value", values);
	}

	
	
	private static byte[] getRemaining(ByteBuffer bf) {				
		byte[] bytes = new byte[bf.remaining()];
		bf.get(bytes, 0, bytes.length);		
		return bytes;		
	}
	
	private static String getString(ByteBuffer bf){
		StringBuffer s = new StringBuffer(bf.remaining());
		while(bf.hasRemaining()){
			byte b = bf.get();
			if (b!=0x00){
				s.append((char)b);	
			}			
		}
		return s.toString();
	}
}
