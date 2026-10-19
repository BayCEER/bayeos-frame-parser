package de.unibayreuth.bayceer.bayeos.frame;

public class InvalidChecksumException extends FrameParserException{
	public InvalidChecksumException(String msg) {
		super(msg);
	}			
}