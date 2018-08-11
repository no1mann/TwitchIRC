package twitch.gui.splash;

/*
 * Startup Screen values for task bar amount
 * Constructor numbers are percentage of the bar
 */
public enum StartupScreenValues {
	STARTUP(StartupScreen.MIN_PROGRESS),
	FILE_MIN(2),
	FILE_MID(6),
	FILE_MAX(10),
	STARTUP_INIT_ONE(12),
	STARTUP_INIT_TWO(18),
	STARTUP_INIT_THREE(23),
	WINDOW_MIN(28),
	WINDOW_MAX(38),
	SERVER_MIN(40),
	SERVER_MAX(45),
	CONNECTION_MIN(49),
	CONNECTION_MAX(70),
	CONNECT_MIN(73),
	CONNECT_MAX(95),
	FINISHED(StartupScreen.MAX_PROGRESS);
	
	public double val;
	
	StartupScreenValues(double val){
		this.val = val;
	}
	
	public double getValue(){
		return val;
	}
}
