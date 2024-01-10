package bayeos.frame.types;

import java.nio.ByteBuffer;

import static bayeos.frame.FrameConstants.BoardCommand;

public class BoardCommand implements ByteFrame {
	
	byte kind;
	byte[] payload;

	public BoardCommand(byte kind, byte[] payload) {
		super();
		this.kind = kind;
		this.payload = payload;
	}

	@Override
	public byte[] getBytes() {
		byte[] b = new byte[payload.length+2];
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.put(BoardCommand);
		bf.put(kind);
		bf.put(payload);
		return b;		
	}

}
