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

	
								   
							   
	private static final Long ts = 946684800000L; // 2000-01-01 00:00:00.000+00
	private static final String origin = "bti3xb";
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
		File resourcesDirectory = new File("src/test/resources");
		File[] files = resourcesDirectory.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".invalid")) {			
				try {
					Parser.parseFile(file.getPath());
					fail("No exception for invalid frame:" + file.getPath());
				} catch (FrameParserException e){ 
					System.out.println("Invalid frame " + file.getPath() +  " threw expected exception: " + e.getMessage());
				} catch (IOException e) {
					fail(e.getMessage());
				}
			}			
		}
				
	}
	

}
