import db.SQLiteManager;
import sch.ScheduleManager;
import websockets.SocketManager;

public class Engine {

	public static void run() {
		SQLiteManager db_m = new SQLiteManager();
		SocketManager socket_m = new SocketManager();
		ScheduleManager sch_m = new ScheduleManager();

		socket_m.start();
		sch_m.start();
	}
	
	public static void main(String[] args) {
		run();
	}
}
