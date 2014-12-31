package reilaender.start;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import reilaender.connection.Connector;
import reilaender.connection.Parser;

public class start {

	public static void main(String[] args) {

		Parser parser = new Parser();
		parser.fillOptions();
		parser.createParser(args);
		parser.retrieveArgs();
		if(args.length == 0 || args == null) {
			parser.printHelp(new PrintWriter(System.out));
		}
		else {
			//get the result of the SQL statment
			String query = "SELECT " + parser.COLUMNS + " FROM " + parser.TABLENAME;
			if(!(parser.SORT_FIELD.equals("")))
				query += " ORDER BY " + parser.SORT_FIELD + parser.SORT_DIRECTION;
			
			//Connect
			Connector con = new Connector(parser);
			ResultSet rs = null;
			try {
				con.connect();
				rs = con.executeQuery(query);
			} catch (SQLException e) {
				String tmp = e.getMessage();
				if(tmp.contains("Access denied")) {
					System.err.println("Access denied (maybe wrong password, or the specified user has no permissions to the database?)");
					System.exit(0);
				} 
				else if(tmp.contains("Unknown column")) {
					System.err.print("Some of this columns doesn't exist in your table: " + parser.COLUMNS);
					System.exit(0);
				} 
				else if(tmp.contains("Table") && tmp.contains("doesn't")) {
					System.err.println("Table " + parser.TABLENAME + " doesn't exist in your database");
					System.exit(0);
				}
				else if(tmp.contains("Communications link failure")) {
					System.err.println("Can't connect to database (Maybe wrong IP or Hostname, database down or theres a problem with your connection).");
					System.exit(0);
				}
				else {
					e.printStackTrace();
				}
			}
			File f = new File("result.txt");
			try {
				//create file, printwriter
				f.createNewFile();
				PrintWriter pw = new PrintWriter(f);
				
				//Writer to the file
				while(rs.next()) {
					try {
						for(int i = 1;true;++i) {
							pw.print(rs.getString(i) + parser.DELIMITER);
						}
					} catch (SQLException e) {
						//Last column reached
					}
					pw.flush();
					pw.println();
				}
				
				//close writer
				pw.close();
				
				System.out.println("Successfully created File " + f.getAbsolutePath());
			} catch(IOException e) {
				System.err.println("Couldn't create file (maybe no permissions?).");
			} catch (SQLException e) {
				System.err.println("Couldn't fetch next Row (maybe the network connection broke).");
			}
		}
	}
}