package reilaender.connection;

import java.io.Console;
import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;

public class Parser {
	private Options options;
	private Console console;
	private CommandLine cmd;
	private CommandLineParser parser;
	public String USERNAME = System.getProperty("user.name"),
			PASSWORD = "",
			IP = "localhost",
			DATABASE = null,
			SORT_FIELD = "",
			SORT_DIRECTION = "ASC",
			FILTER = "",
			DELIMITER = ";",
			COLUMNS = null,
			OUTPUT_FILENAME = null,
			TABLENAME = null,
			FILE_EXTENSION = null
			;
	/*
	 * 1st value is the option itself.
	 * 2nd value is true if the option it has a value, is false when the option has no value e.g. password --> password prompt (option should just be -p).
	 * 3rd Description of the option.
	 * 4th is true if this option is required to successfully start the program.
	 */
	private final String[][] opt = new String[][]{
		{"h", "true", "Hostname des DBMS. Standard: localhost", "false"},
		{"u", "true", "Benutzername. Standard: Benutzername des im Betriebssystem angemeldeten Benutzers", "false"},
		{"p", "false", "Passwort. Alternativ kann ein Passwortprompt angezeigt werden. Standard: keins", "false"},
		{"d", "true", "Name der Datenbank PFLICHT", "true"},
		{"s", "true", "Feld, nach dem sortiert werden soll (nur eines möglich, Standard: keines)", "false"},
		{"r", "true", "Sortierrichtung. Standard: ASC", "false"},
		{"w", "true", "eine Bedingung in SQL-Syntax, die zum Filtern der Tabelle verwendet wird. Standard: keine", "false"},
		{"t", "true", "Trennzeichen, dass für die Ausgabe verwendet werden soll. Standard: ; ", "false"},
		{"f", "true", "Kommagetrennte Liste (ohne Leerzeichen) der Felder, die im Ergebnis enthalten sein sollen. * soll akzeptiert werden (Pflicht)", "true"},
		{"o", "true", "Name der Ausgabedatei. Standard: keine -> Ausgabe auf der Konsole", "false"},
		{"T", "true", "Tabellenname (Pflicht)", "true"}
	};
	private HelpFormatter help;
	private boolean missing;
	
	public Parser() {
		help = new HelpFormatter();
	}
	/**
	 * Fill the options with the 4th dimensional array above
	 */
	public void fillOptions() {
		options = new Options();
		for(int i = 0;i < opt.length;++i) {
			Option tmp = new Option(opt[i][0], Boolean.parseBoolean(opt[i][1]), opt[i][2]);
			//Option required or not (4th value)
			tmp.setRequired(Boolean.parseBoolean(opt[i][3]));
			options.addOption(opt[i][0], Boolean.parseBoolean(opt[i][1]), opt[i][2]);
		}
	}
	/**
	 * Creates a parser with the given arguments
	 * @param args Arguments
	 */
	public void createParser(String[]args) {
		try {
			parser = new BasicParser();
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.out.println("Couldn't parse options, because " + e.getMessage());
			System.exit(0);
		}
	}
	/**
	 * Checks the options to be set and saves it in this class String statics
	 */
	public boolean retrieveArgs() {
		if(cmd.hasOption('h'))
			IP = cmd.getOptionValue('h');
		if(cmd.hasOption('u'))
			USERNAME = cmd.getOptionValue('u');
		if(cmd.hasOption("p")) {
			try {
				System.out.println("IP: "+cmd.getOptionValue('h'));
				console = System.console();
				PASSWORD = new String(console.readPassword("Password: "));
			} catch (NullPointerException e) {
				System.err.println("Info: Couldn't get Console Object, please run this programm from your OS CLI. Using default password for now.");
			}
		}
		if(cmd.hasOption('d'))
			DATABASE = cmd.getOptionValue('d');
		else {
			System.err.println("Database required (-d)");
			missing = true;
		}
		if(cmd.hasOption('s'))
			SORT_FIELD = cmd.getOptionValue('s');
		if(cmd.hasOption('r') && cmd.hasOption('s')) {
			SORT_DIRECTION = cmd.getOptionValue('r');
		}
		if(cmd.hasOption('w')) {
			FILTER = cmd.getOptionValue('w');
		}
		if(cmd.hasOption('t')) {
			DELIMITER = cmd.getOptionValue('t');
		}
		if(cmd.hasOption('f')) {
			COLUMNS = cmd.getOptionValue('f');
		} else {
			System.err.println("No columns specified (-f)");
			missing = true;
		}
		if(cmd.hasOption('o')) {
			OUTPUT_FILENAME = cmd.getOptionValue('o');
		}
		if(cmd.hasOption('T')) {
			TABLENAME = cmd.getOptionValue('T');
		} else {
			System.err.println("Missing Tablename (-T)");
			missing = true;
		}
		return missing;
	}
	public boolean isMissing() {
		return missing;
	}
	public Console getConsole() {
		return console;
	}
	/**
	 * Prints the help for this parser to a given PrintWriter
	 * @param pw
	 */
	public void printHelp(PrintWriter pw) {
		help.printOptions(pw, 50, options, 5, 5);
		pw.flush();
	}
}