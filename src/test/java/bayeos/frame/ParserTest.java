package bayeos.frame;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParserTest {

	
	
	private static final Long ts = 1457222400000L;
	private static final String origin = "Site-1";
	private static final Integer rssi = 0;
	

	@Test
	public void testValidFiles() throws ParseException, JsonParseException, JsonMappingException, IOException {
		
		File resourcesDirectory = new File("src/test/resources");
		File[] files = resourcesDirectory.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".json")) {
				// Read json
				String json = file.getPath();
				String bin = json.substring(0, json.length() - 4) + "bin";
				System.out.println("Parsing file:" + bin);
				try {					
					// Parse binary file 
					Map<String, Object> ret = Parser.parseFile(bin, new Date(ts), origin, rssi);
										
					// Marshal and unmarshal with jackson to adapt data types    
					ObjectMapper mapper = new ObjectMapper();										
					Map<String, Object> r = mapper.readValue(new ObjectMapper().writeValueAsString(ret), Map.class);
															  
					// Read expected result 										
					Map<String, Object> e = mapper.readValue(new File(json), Map.class);

					// Compare values 
					if (!e.equals(r)) {						
						fail("Result:" + r + "\nExpected:" + e);
					}
																				
				} catch (FrameParserException e) {
					fail(e.getMessage());
				}
			}
		}
	}
	
	@Test
	public void testInvalidFrames(){
		String[] frames = new String[]{"B9jWAAANAlAxiKU1AQ8BAwDbtcANDAJrJukAAAD4AUAC","B/DSAAANAlAx+Lw1AQ8BAwDbtcANDAJrJukAAAD4AUAC","B/DSAAANAlA0iBMAAA8BAwCskpoNYAKjIdkAEgAUAlAC"};
		for (String f : frames) {
			try {
				@SuppressWarnings("unused")
				Map<String,Object> ret = Parser.parseBase64(f);
				fail("Invalid frame type exception expected.");
			} catch (FrameParserException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	


}
