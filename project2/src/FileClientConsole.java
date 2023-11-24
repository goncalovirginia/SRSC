import clients.FileClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FileClientConsole {

	// Output messages
	private static final String USERNAME = "Username: ", PASSWORD = "Password: ", INVALID_CREDENTIALS = "Invalid credentials", INVALID_COMMAND = "Invalid command", INVALID_ARGUMENTS = "Invalid arguments";

	// Input commands
	private static final String LOGIN = "login", LIST = "ls", MAKE_DIRECTORY = "mkdir", PUT = "put", GET = "get", COPY = "cp", REMOVE = "rm", INFO = "info";

	private static final FileClient fileClient = new FileClient();
	private static String username, password;

	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		while (!login(bufferedReader)) {
			System.out.println(INVALID_CREDENTIALS);
		}

		String line;
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

	private static void get(String[] commandArgs) {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
		}
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
