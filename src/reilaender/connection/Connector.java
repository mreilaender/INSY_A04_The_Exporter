package reilaender.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Connector {
	private Connection con;
	private Statement st;
	private MysqlDataSource ds;
	private Parser parser;
	
	/**
	 * 
	 * @param parser Needed to set Username, password and something like that.
	 */
	public Connector(Parser parser) {
		this.parser = parser;
	}
	public void connect() throws SQLException {
		ds = new MysqlDataSource();
		
		if(parser.getConsole() == null) {
			//this is just needed on my computer
			if(System.getProperty("user.name").equals("manuel")) {
				System.err.println("Username is manuel");
				parser.PASSWORD = "manuel";
			}
		}
		ds.setServerName(parser.IP);
		ds.setUser(parser.USERNAME);
		ds.setPassword(parser.PASSWORD);
		ds.setDatabaseName(parser.DATABASE);
		
		con = ds.getConnection();
		st = con.createStatement();
	}
	/**
	 * Executes a query
	 * @param query Query to be executed
	 * @return ResultSet of the query
	 * @throws SQLException Exception will be thrown if the connection wasn't successfully created.
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		return st.executeQuery(query);
	}
	/**
	 * Closes all Streams (Statement, Connection)
	 */
	public void closeStreams() {
		try {
			st.close();
		} catch (SQLException e) {
			System.err.println("Statement has already been closed");
		}
		try {
			con.close();
		} catch (SQLException e) {
			System.err.println("Connection has already been closed");
		}
	}
	public Connection getCon() {
		return con;
	}
	public Statement getSt() {
		return st;
	}
}