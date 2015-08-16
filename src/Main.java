import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
	static FilenameFilter versionFilter = new FilenameFilter() {
		public boolean accept(File versionDir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.endsWith("-nodemo")
					|| lowercaseName.startsWith(".")) {
				return false;
			} else {
				return true;
			}
		}
	};

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		GUI gui = new GUI();

	}

	static String[] getVersions() {
		File versionsFolder = new File(MCDir(), "versions");
		return versionsFolder.list(versionFilter);
	}

	static boolean checkInput(String version, String username) {
		File versionsFolder = new File(MCDir(), "versions");
		File versionSubFolder = new File(versionsFolder, version);
		File jsonFile = new File(versionSubFolder, version + ".json");
		if (username.isEmpty() || (!versionSubFolder.isDirectory()) || (!jsonFile.isFile())) {
			return false;
		} else {
			return true;
		}
	}

	static String readFile(File file) {
		String x = null;
		try {
			x = Files.toString(file, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return x;
	}

	static void patchJSON(String version, String username) throws IOException {
		//This is where the magic happens
		//define directories and files
		File versionsDir = new File(MCDir(), "versions");
		File oldVersionDir = new File(versionsDir, version);
		File newVersionDir = new File(versionsDir, version + "-nodemo");
		File oldJsonFile = new File(oldVersionDir, version + ".json");
		File newJsonFile = new File(newVersionDir, version + "-nodemo.json");
		
		//check for the source .json file
		if (!oldJsonFile.isFile()) {
			throw new RuntimeException(
					"The Version Config File Does Not Exist: "
							+ oldJsonFile.getPath());
		}
		
		//create new gson/builder
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		
		//convert json to Version object
		Version oldVersion = gson.fromJson(readFile(oldJsonFile), Version.class);
		
		//if need be, define "jar" value
		if(oldVersion.getJar() == null){
		String[] names = oldVersion.getId().split("-");
		oldVersion.setJar(names[0]);
		}
		
		//modify Version object
		String oldArgs = oldVersion.getMinecraftArguments();
		oldArgs = oldArgs.replace("${auth_player_name}", username);
		oldVersion.setMinecraftArguments(oldArgs + " --");
		oldVersion.setInheritsFrom(oldVersion.getId());
		oldVersion.setId(oldVersion.getId() + "-nodemo");
		
		//write object to file
		String jsonString = gson.toJson(oldVersion);
		writeFile(jsonString, newJsonFile, Charset.defaultCharset());
	}

	static void writeFile(String data, File file, Charset encoding) throws IOException {
		file.delete();
		file.getParentFile().mkdirs();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(data);
		writer.close();
	}

	public static final class OS {

		public static final OS linux;
		public static final OS solaris;
		public static final OS windows;
		public static final OS macos;
		public static final OS unknown;
		public int id;

		static {
			linux = new OS("linux", 0);
			solaris = new OS("solaris", 1);
			windows = new OS("windows", 2);
			macos = new OS("macos", 3);
			unknown = new OS("unknown", 4);
		}

		public OS(String s, int i) {
			id = i;
		}
	}

	public static OS getPlatform() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return OS.windows;
		}
		if (s.contains("mac")) {
			return OS.macos;
		}
		if (s.contains("solaris")) {
			return OS.solaris;
		}
		if (s.contains("sunos")) {
			return OS.solaris;
		}
		if (s.contains("linux")) {
			return OS.linux;
		}
		if (s.contains("unix")) {
			return OS.linux;
		} else {
			return OS.unknown;
		}
	}

	public static File MCDir() {
		String userHome = System.getProperty("user.home", ".");
		File mcDir = null;
		String name = "minecraft";
		String appdata = System.getenv("APPDATA");
		switch (getPlatform().id) {
		case 0: // null
		case 1: // Linux
			mcDir = new File(userHome, "." + name);
			break;
		case 2: // PC
			if (appdata != null) {
				mcDir = new File(appdata, "." + name);
				System.out.println("Using APPDATA");
			} else {
				System.out.println("APPDATA is null (using user.home)");
				mcDir = new File(userHome, '.' + name);
			}
			break;
		case 3: // Mac
			mcDir = new File(userHome, "Library/Application Support/" + name);
			break;
		default: // Other
			mcDir = new File(userHome, name);
			break;
		}
		if (!mcDir.isDirectory()) {
			throw new RuntimeException(
					"The Minecraft Working Dir Does Not Exist: " + mcDir);
		}
		return mcDir;
	}
}
