import fileService.FileClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

public class FileClientApplication {

	// Output messages
	private static final String LOGIN = "Login", USERNAME = "Username: ", PASSWORD = "Password: ", INVALID_CREDENTIALS = "Invalid credentials, try again", INVALID_COMMAND = "Invalid command", INVALID_ARGUMENTS = "Invalid arguments";

	// Input commands
	private static final String LIST = "ls", MAKE_DIRECTORY = "mkdir", PUT = "put", GET = "get", COPY = "cp", REMOVE = "rm", INFO = "info", EXIT = "exit";

	private static FileClient fileClient;
	private static String username, password;

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(new BufferedReader(new FileReader("project2/src/fileService/fileclient.properties")));

		System.setProperty("javax.net.ssl.trustStore", properties.getProperty("trustStore"));

		String fileHost = properties.getProperty("fileHost");
		int filePort = Integer.parseInt(properties.getProperty("filePort"));
		fileClient = new FileClient(fileHost, filePort, properties);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Login");
		while (!login(bufferedReader)) {
			System.out.println(INVALID_CREDENTIALS);
		}
		System.out.println("Logged in");

		String line;
		inputLoop:
		while ((line = bufferedReader.readLine()) != null) {
			String[] lineSplit = line.split(" ");
			String command = lineSplit[0];
			String[] commandArgs = lineSplit.length > 1 ? Arrays.copyOfRange(lineSplit, 1, lineSplit.length) : new String[]{};

			switch (command) {
				case LIST -> list(commandArgs);
				case MAKE_DIRECTORY -> makeDirectory(commandArgs);
				case PUT -> put(commandArgs);
				case GET -> get(commandArgs);
				case COPY -> copy(commandArgs);
				case REMOVE -> remove(commandArgs);
				case INFO -> info(commandArgs);
				case EXIT -> {
					break inputLoop;
				}
				default -> System.out.println(INVALID_COMMAND);
			}
		}
	}

	private static boolean login(BufferedReader bufferedReader) throws IOException {
		System.out.print(USERNAME);
		username = bufferedReader.readLine();
		System.out.print(PASSWORD);
		password = bufferedReader.readLine();

		return fileClient.login(username, password);
	}

	private static void list(String[] commandArgs) {
		if (commandArgs.length > 1) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

	private static void makeDirectory(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

	private static void put(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

	private static void get(String[] commandArgs) throws IOException {
		if (commandArgs.length != 1) {
			System.out.println("get <file path>");
		}

		byte[] fileBytes = fileClient.getFile(username, commandArgs[0]);
		System.out.println(new String(fileBytes));
	}

	private static void copy(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

	private static void remove(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

	private static void info(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
	}

}
