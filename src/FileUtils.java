import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileUtils {

	public static final class OS {

		public static final OS linux;
		public static final OS macos;
		public static final OS solaris;
		public static final OS unknown;
		public static final OS windows;
		static {
			linux = new OS("linux", 0);
			solaris = new OS("solaris", 1);
			windows = new OS("windows", 2);
			macos = new OS("macos", 3);
			unknown = new OS("unknown", 4);
		}

		public int id;

		public OS(String s, int i) {
			id = i;
		}
	}

	static FilenameFilter versionFilter = new FilenameFilter() {
		@Override
		public boolean accept(File versionDir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.endsWith("-nodemo") || lowercaseName.startsWith(".")) {
				return false;
			} else {
				return true;
			}
		}
	};

	public static File getMCDir() {
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
				// System.out.println("Using APPDATA");
			} else {
				// System.out.println("APPDATA is null (using user.home)");
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
			throw new RuntimeException("The Minecraft Working Dir Does Not Exist: " + mcDir);
		}
		return mcDir;
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

	static String[] getVersions() {
		File versionsFolder = new File(getMCDir(), "versions");
		return versionsFolder.list(versionFilter);
	}

	static <T> T readJsonFromFile(File file, Class<T> classToParse) throws FileNotFoundException {
		Reader reader = new FileReader(file);
		Gson gson = new GsonBuilder().create();
		T element = gson.fromJson(reader, classToParse);
		return element;
	}

	static <T> void writeJsonToFile(T data, File file) throws IOException {
		file.delete();
		file.getParentFile().mkdirs();
		try (Writer writer = new FileWriter(file)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(data, writer);
		}
	}
}
