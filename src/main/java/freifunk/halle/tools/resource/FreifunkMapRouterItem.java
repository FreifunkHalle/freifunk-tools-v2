package freifunk.halle.tools.resource;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FreifunkMapRouterItem {

	private Flags _flags;
	private String _firstseen;
	private String _lastseen;
	private NodeInfo _nodeinfo;
	private Statistics _statistics;

	private static class NodeInfo {
		private Network _network;
		private String _hostname;
		private Hardware _hardware;
		private String _node_id;
		private Location _location;
		private Software _software;
	}

	private static class Network {
		private List<String> _mesh_interfaces;
		private List<String> _addresses;
		private String _mac;
	}

	private static class Hardware {
		private String model;
	}

	private static class Location {
		double latitude;
		double longitude;
	}

	private static class Software {
		@JsonProperty("batman-adv")
		Batman _batman;
		Firmware _firmware;
		Fastd _fastd;
		Autoupdater _autoupdater;
	}

	private static class Batman {
		int compat;
		String version;
	}

	private static class Firmware {
		String _release;
		String _base;
	}

	private static class Fastd {
		String _version;
		boolean _enabled;
	}

	private static class Autoupdater {
		boolean _enabled;
		String _branch;
	}

	private static class Statistics {
		private double _uptime;
		private double _memory_usage;
		private double _clients;
		private double _rootds_usage;
		private double _loadavg;
		private String _gateway; // MAC
	}

	private static class Flags {
		private boolean _online;
		private boolean _gateway;
	}
}
