package oculusbot.basic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for simple access to a property file
 * @author Robert Meschkat
 *
 */
public class PropertyLoader {
	private Properties props;
	private String propertyFile;
	private String defaultPropertyFile;
	
	/**
	 * Creates a object to simplify property access. 
	 * @param propertyFile Path and filename of the property file.
	 * @param defaultPropertyFile Path and filename to a default file, which is used when normal file isn't found. 
	 */
	public PropertyLoader(String propertyFile, String defaultPropertyFile) {
		this.propertyFile = propertyFile;
		this.defaultPropertyFile = defaultPropertyFile;
		props = loadProperties();
	}
	
	
	/**
	 * Tries to load normal file first. If unsuccessful tries to load default file.  
	 * @return The properties object with the loaded information or an empty object if neither file was found.
	 */
	private Properties loadProperties(){
		Properties result = new Properties();
		try {
			result.load(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			try {
				System.err.println(("Couldn't find \""+propertyFile+"\". Loading default property file."));
				result.load(getClass().getClassLoader().getResourceAsStream(defaultPropertyFile));
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Looks up property and converts the value to an integer before returning it.
	 * @param key Key to find the correct property.
	 * @return Property value as integer.
	 */
	public int getPropertyAsInt(String key){
		try{
			int result = Integer.parseInt(props.getProperty(key));
			return result;
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * Returns needed property value.
	 * @param key Key to find the correct property.
	 * @return Property value.
	 */
	public String getProperty(String key){
		return props.getProperty(key);
	}
}
