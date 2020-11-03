import db.SQLiteManager;
import sch.ScheduleManager;
import web.HTTPServerManager;
import websockets.SocketServerManager;

public class Engine {

	public static void run() {
		SQLiteManager db_m = new SQLiteManager();
		SocketServerManager socket_m = new SocketServerManager();
		ScheduleManager sch_m = new ScheduleManager();
		HTTPServerManager http_m = new HTTPServerManager("localhost", 8080);

		socket_m.start();
		//sch_m.start();
		http_m.start();
	}
	
	public static void main(String[] args) {
		run();
	}
}
