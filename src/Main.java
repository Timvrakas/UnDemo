import java.io.File;
import java.io.IOException;

public class Main {

	static boolean checkInput(String version, String username) {
		File versionsFolder = new File(FileUtils.getMCDir(), "versions");
		File versionSubFolder = new File(versionsFolder, version);
		File jsonFile = new File(versionSubFolder, version + ".json");
		if (username.isEmpty() || (!versionSubFolder.isDirectory()) || (!jsonFile.isFile())) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		GUI gui = new GUI();
	}

	static void patchJSON(String version, String username) throws IOException {
		// This is where the magic happens

		// define directories and files
		File versionsDir = new File(FileUtils.getMCDir(), "versions");
		File oldVersionDir = new File(versionsDir, version);
		File oldJsonFile = new File(oldVersionDir, version + ".json");

		System.out.print("Parsing Input File:  ");
		System.out.println(oldJsonFile.toString());

		// check for the source .json file
		if (!oldJsonFile.isFile()) {
			throw new RuntimeException("The Version Config File Does Not Exist: " + oldJsonFile.getPath());
		}

		// convert json to Version object
		Version newVersion = FileUtils.readJsonFromFile(oldJsonFile, Version.class);

		System.out.println("Patching...");

		// modify Version object
		String oldArgs = newVersion.getMinecraftArguments();
		oldArgs = oldArgs.replace("${auth_player_name}", username);
		System.out.println("New username: " + username);
		newVersion.setMinecraftArguments(oldArgs + " --");
		newVersion.setInheritsFrom(newVersion.getId());
		newVersion.setId(newVersion.getId() + "-nodemo");

		// define output files
		File newVersionDir = new File(versionsDir, version + "-nodemo");
		File newJsonFile = new File(newVersionDir, version + "-nodemo.json");

		System.out.print("Saving Output File:  ");
		System.out.println(newJsonFile.toString());

		// write Version object to .json file
		FileUtils.writeJsonToFile(newVersion, newJsonFile);
	}

}
