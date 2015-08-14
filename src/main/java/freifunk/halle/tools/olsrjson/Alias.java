package freifunk.halle.tools.olsrjson;

import java.util.List;

public class Alias {

	private String _ipAddress;
	private List<Ip> _aliases;

	public String getMainAddress() {
		return _ipAddress;
	}

	public List<Ip> getAliases() {
		return _aliases;
	}

}
