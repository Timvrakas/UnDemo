import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.json.JSONObject;

import com.google.common.io.Files;

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
		File jarFile = new File(versionSubFolder, version + ".jar");
		File jsonFile = new File(versionSubFolder, version + ".json");
		if (username.isEmpty() || (!versionSubFolder.isDirectory())
				|| (!jarFile.isFile()) || (!jsonFile.isFile())) {
			return false;
		} else {
			return true;
		}
	}

	static void copyJar(String version) throws IOException {
		String newVersion = version + "-nodemo";
		File versionsDir = new File(MCDir(), "versions");
		File oldVersionDir = new File(versionsDir, version);
		File oldJar = new File(oldVersionDir, version + ".jar");
		File newVersionDir = new File(versionsDir, newVersion);
		File newJar = new File(newVersionDir, newVersion + ".jar");
		newVersionDir.mkdirs();
		newJar.delete();
		copyFile(oldJar, newJar);
	}

	@SuppressWarnings("resource")
	static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();

			// previous code: destination.transferFrom(source, 0,
			// source.size());
			// to avoid infinite loops, should be:
			long count = 0;
			long size = source.size();
			while ((count += destination.transferFrom(source, count, size
					- count)) < size)
				;
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	static String readFile(File file) {
		String x = null;
		try {
			x = Files.toString(file, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return x;
	}

	static void patchJSON(String version, String username) throws IOException {
		File versionsDir = new File(MCDir(), "versions");
		File oldVersionDir = new File(versionsDir, version);
		File oldJsonFile = new File(oldVersionDir, version + ".json");
		if (!oldJsonFile.isFile()) {
			throw new RuntimeException(
					"The Version Config File Does Not Exist: "
							+ oldJsonFile.getPath());
		}
		String data = readFile(oldJsonFile);
		JSONObject jsonObject = new JSONObject(data);
		jsonObject.put("id", version + "-nodemo");
		String oldargs = (String) jsonObject.get("minecraftArguments");
		oldargs = oldargs.replace("${auth_player_name}", username);
		jsonObject.put("minecraftArguments", oldargs + " --");
		File newVersionDir = new File(versionsDir, version + "-nodemo");
		newVersionDir.mkdir();
		File newJsonFile = new File(newVersionDir, version + "-nodemo.json");
		writeFile(jsonObject.toString(), newJsonFile.getPath(),
				Charset.forName("UTF-8"));
	}

	static void writeFile(String data, String path, Charset encoding)
			throws IOException {
		new File(path).delete();
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
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
		String user_home = System.getProperty("user.home", ".");
		File file = null;
		String name = new String("minecraft");
		String appdata = System.getenv("APPDATA");
		switch (getPlatform().id) {
		case 0: // null
		case 1: // Linux
			file = new File(user_home, "." + name);
			break;
		case 2: // PC
			if (appdata != null) {
				file = new File(appdata, "." + name);
				System.out.println("APPDATA");
			} else {
				System.out.println("APPDATA is NULL (using user.home)");
				file = new File(user_home, '.' + name);
			}
			break;
		case 3: // Mac
			file = new File(user_home, "Library/Application Support/" + name);
			break;
		default: // Other
			file = new File(user_home, name);
			break;
		}
		if (!file.isDirectory()) {
			throw new RuntimeException(
					"The Minecraft Working Dir Does Not Exist: " + file);
		}
		return file;
	}
}
