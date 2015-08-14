package freifunk.halle.tools.entity;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class Router {

	private List<InetAddress> _addresses;
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
	private transient int clients;
	private Date _lastChanged;

}
