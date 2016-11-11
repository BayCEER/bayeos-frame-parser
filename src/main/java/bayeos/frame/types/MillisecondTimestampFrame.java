package bayeos.frame.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;



public class MillisecondTimestampFrame implements ByteFrame  {
	
	private Date timeStamp;
	protected byte[] payload;
	
	public MillisecondTimestampFrame(Date timeStamp, byte[] payload) {
		this.timeStamp = timeStamp;	
		this.payload = payload;
	}
	
	public MillisecondTimestampFrame(Date timeStamp, ByteFrame frame){
		this(timeStamp, frame.getBytes());
	}

	
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public byte[] getBytes(){
		byte[] n = new byte[9+ payload.length];
		ByteBuffer bf = ByteBuffer.wrap(n);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put((byte)0xc);
		bf.putLong(timeStamp.getTime());
		bf.put(payload);
		return n;		
	}
	

}
