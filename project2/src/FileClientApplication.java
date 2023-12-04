import fileService.FileClient;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;

public class FileClientApplication {

	// Output messages
	private static final String LOGIN = "Login", USERNAME = "Username: ", PASSWORD = "Password: ", INVALID_CREDENTIALS = "Invalid credentials, try again", INVALID_COMMAND = "Invalid command", INVALID_ARGUMENTS = "Invalid arguments";

	// Input commands
	private static final String LIST = "ls", MAKE_DIRECTORY = "mkdir", PUT = "put", GET = "get", COPY = "cp", REMOVE = "rm", INFO = "info", EXIT = "exit";

	private static String username, password;

	private static String fileHost;
	private static int filePort;

	private static Properties properties;

	public static void main(String[] args) throws Exception {
		properties = new Properties();
		properties.load(new BufferedReader(new FileReader("project2/src/fileService/fileclient.properties")));

		System.setProperty("javax.net.ssl.trustStore", properties.getProperty("trustStore"));

		fileHost = properties.getProperty("fileHost");
		filePort = Integer.parseInt(properties.getProperty("filePort"));

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

	private static boolean login(BufferedReader bufferedReader) throws Exception {
		FileClient fileClient = new FileClient(fileHost, filePort, properties);

		System.out.print(USERNAME);
		username = bufferedReader.readLine();
		System.out.print(PASSWORD);
		password = bufferedReader.readLine();

		return fileClient.login(username, password);
	}

	private static void list(String[] commandArgs) throws Exception {
		if (commandArgs.length > 1) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
	}

	private static void makeDirectory(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
	}

	private static void put(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		byte[] fileBytes = getFileBytes(commandArgs[0]);
		FileClient fileClient = new FileClient(fileHost, filePort, properties);
		String requestResult = fileClient.postFile(username, commandArgs[1], fileBytes);
		System.out.println(requestResult);
	}

	private static byte[] getFileBytes(String filePath) throws IOException {
		File f = new File(filePath);
		int length = (int) (f.length());
		if (length == 0) {
			throw new IOException("File length is zero: " + filePath);
		}

		DataInputStream in = new DataInputStream(new FileInputStream(f));
		byte[] fileBytes = new byte[length];
		in.readFully(fileBytes);
		return fileBytes;
	}

	private static void get(String[] commandArgs) throws Exception {
		if (commandArgs.length != 1) {
			System.out.println("get <file path>");
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
		byte[] fileBytes = fileClient.getFile(username, commandArgs[0]);
		System.out.println(new String(fileBytes));
	}

	private static void copy(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
	}

	private static void remove(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
	}

	private static void info(String[] commandArgs) throws Exception {
		if (commandArgs.length != 2) {
			System.out.println(INVALID_ARGUMENTS);
			return;
		}

		FileClient fileClient = new FileClient(fileHost, filePort, properties);
	}

}
