package de.unibayreuth.bayceer.bayeos.frame.types;

import static de.unibayreuth.bayceer.bayeos.frame.FrameConstants.BoardCommand;

import java.nio.ByteBuffer;

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
