package bayeos.frame.types;

import java.nio.ByteBuffer;

import static bayeos.frame.FrameConstants.BoardCommandResponse;


public class BoardCommandResponse implements ByteFrame {
	
	byte kind;
	byte status;
	byte[] payload;

	public BoardCommandResponse(byte kind, byte status, byte[] payload) {
		super();
		this.kind = kind;
		this.status = status;
		this.payload = payload;
	}

	@Override
	public byte[] getBytes() {
		byte[] b = new byte[payload.length+3];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put(BoardCommandResponse);
		bf.put(kind);
		bf.put(status);		
		bf.put(payload);
		return b;		
	}
}
