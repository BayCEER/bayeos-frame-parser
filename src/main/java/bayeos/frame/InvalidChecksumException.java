package bayeos.frame;

public class InvalidChecksumException extends FrameParserException{
	public InvalidChecksumException(String msg) {
		super(msg);
	}			
}