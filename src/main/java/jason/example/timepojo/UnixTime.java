package jason.example.timepojo;

public class UnixTime {

	long time;

	
	public UnixTime() {
		super();
		time = System.currentTimeMillis();
	}
	

	public UnixTime(long time) {
		super();
		this.time = time;
	}


	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	
}
