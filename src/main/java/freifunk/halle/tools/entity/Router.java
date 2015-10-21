package freifunk.halle.tools.entity;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class Router {

	private List<InetAddress> _addresses;
	private int _defaultIpV4Address;
	private int _defaultIpV6Address;
	private String _hostname;
	private String _boardType;
	private String _firmwareVersion;
	private String _userNick;
	private String _userName;
	private String _userNote;
	private String _userMail;
	private String _location;
	private double _latitude;
	private double _longitude;
	private Date _lastChanged;
	private Date _firstSeen;
	private int _batmanCompat;
	private String _batmanVersion;

	private transient int _clients;
	private transient double _uptime;
	private transient double _memoryUsage;
	private transient double _rootUsage;
	private transient double _avgLoad;
	private transient String _gateway;

	public List<InetAddress> getAddresses() {
		return _addresses;
	}

	public InetAddress getMainIp() {
		return _addresses.get(_defaultIpV4Address);
	}

	public int getDefaultIpV4Address() {
		return _defaultIpV4Address;
	}

	public int getDefaultIpV6Address() {
		return _defaultIpV6Address;
	}

	public String getHostname() {
		return _hostname;
	}

	public String getBoardType() {
		return _boardType;
	}

	public String getFirmwareVersion() {
		return _firmwareVersion;
	}

	public String getUserNick() {
		return _userNick;
	}

	public String getUserName() {
		return _userName;
	}

	public String getUserNote() {
		return _userNote;
	}

	public String getUserMail() {
		return _userMail;
	}

	public String getLocation() {
		return _location;
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	public int getClients() {
		return _clients;
	}

	public Date getLastChanged() {
		return _lastChanged;
	}

	public Date getFirstSeen() {
		return _firstSeen;
	}

	public int getBatmanCompat() {
		return _batmanCompat;
	}

	public String getBatmanVersion() {
		return _batmanVersion;
	}

	public double getUptime() {
		return _uptime;
	}

	public double getMemoryUsage() {
		return _memoryUsage;
	}

	public double getRootUsage() {
		return _rootUsage;
	}

	public double getAvgLoad() {
		return _avgLoad;
	}

	public String getGateway() {
		return _gateway;
	}

	public List<String> getInterfaces() {
		// TODO Auto-generated method stub
		return null;
	}
}
