package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{

	
	int counter = 0;
	private ConnectionMaker realConnection;
	
	public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
		this.realConnection = realConnectionMaker;
	}
	
	@Override
	public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
		this.counter++;
		return realConnection.makeNewConnection();
	}
	
	public int getCounter() {
		return this.counter;
	}
	
}
