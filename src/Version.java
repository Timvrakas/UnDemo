
public class Version {
	private String id;
	private String inheritsFrom;
	private String minecraftArguments;
	private String type;

	public String getId() {
		return id;
	}

	public String getInheritsFrom() {
		return inheritsFrom;
	}

	public String getMinecraftArguments() {
		return minecraftArguments;
	}

	public String getType() {
		return type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInheritsFrom(String inheritsFrom) {
		this.inheritsFrom = inheritsFrom;
	}

	public void setMinecraftArguments(String minecraftArguments) {
		this.minecraftArguments = minecraftArguments;
	}

	public void setType(String type) {
		this.type = type;
	}
}
