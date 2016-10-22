package oculusbot.basic;
/**
 * Contains names to access all properties in the server config file. 
 * @author Robert Meschkat
 *
 */
public interface ServerProperties {
	String PROPERTY_FILENAME = "./server.cfg";
	String DEFAULT_PROPERTY_FILENAME = "config/default_server.cfg";
	String PORT_DISCOVERY = "port.discovery";
	String PORT_BOT = "port.bot";
	String PORT_VIDEO = "port.video";
	String CAM_WIDTH = "cam.width";
	String CAM_HEIGHT = "cam.height";
}
