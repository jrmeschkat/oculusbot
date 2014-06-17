import org.opencv.core.MatOfByte;


public class Frame {
	private MatOfByte frame;
	private long timestamp;
	
	public MatOfByte getFrame() { return frame;	}
	public void setFrame(MatOfByte frame) { this.frame = frame;	}
	public long getTimestamp() { return timestamp; }
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	
	public Frame(MatOfByte frame, long timestamp){
		this.frame = frame;
		this.timestamp = timestamp;
	}
}
