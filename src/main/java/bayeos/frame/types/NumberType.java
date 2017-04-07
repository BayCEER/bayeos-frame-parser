package bayeos.frame.types;

import bayeos.binary.ByteArray;

public enum NumberType {
		
	    Float32(0x1,4), 
	    Int32(0x2,4), 
	    Int16(0x3,2), 
	    UInt8(0x4,1), 
	    UInt32(0x5,4), 
	    Long64(0x6,8);
        
        
		private int index;
		
		public byte getIndex() {
			return (byte) index;
		}

		private int length;		
		
		public int getLength() {
			return length;
		}

		private NumberType(int index, int length) {
                this.index = index;
                this.length = length;
        }
        
		public byte[] toByte(Number value){
			
			switch(index) {			
			case 0x1:
				return ByteArray.toByteFloat32(value.floatValue());
			case 0x2:
				return ByteArray.toByteInt32(value.intValue());
			case 0x3:
				return ByteArray.toByteInt16(value.shortValue());
			case 0x4:
				byte[] b = new byte[1];
				b[0] = value.byteValue();
				return b;			
			case 0x5:
				return ByteArray.toByteUInt32(value.longValue());
			case 0x6:				
			
			default:
				return null;
			}
			
		}
        
};  