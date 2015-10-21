package freifunk.halle.tools.resource;

import java.net.InetAddress;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import freifunk.halle.tools.entity.Router;
import freifunk.halle.tools.graph.Node;

public class FreifunkMapRouterItem {

	private final Flags _flags;
	private final String _firstseen;
	private final String _lastseen;
	private final NodeInfo _nodeinfo;
	private final Statistics _statistics;

	public FreifunkMapRouterItem(Router router, Node node) {
		_lastseen = router.getLastChanged().toString();
		_firstseen = router.getFirstSeen().toString();
		_statistics = new Statistics(router);
		_nodeinfo = new NodeInfo(router, node);
		_flags = new Flags(node != null, node.hasTunnelLink());
	}

	private static class NodeInfo {
		private final Network _network;
		private final String _hostname;
		private final Hardware _hardware;
		private final String _node_id;
		private final Location _location;
		private final Software _software;

		public NodeInfo(Router router, Node node) {
			_hostname = router.getHostname();
			_hardware = new Hardware(router);
			_node_id = node.getMainIp().toString();
			_location = new Location(router);
			_software = new Software(router);
			_network = new Network(router);
		}
	}

	private static class Network {
		private final List<String> _mesh_interfaces;
		private final List<String> _addresses;
		private final String _mac;

		public Network(Router router) {
			_mac = router.getMainIp().toString();
			_addresses = Lists.transform(router.getAddresses(), toString);
			_mesh_interfaces = router.getInterfaces();
		}
	}

	private static final Function<InetAddress, String> toString = new Function<InetAddress, String>() {

		@Override
		public String apply(InetAddress input) {
			return input.toString();
		}

	};

	private static class Hardware {
		private final String _model;

		public Hardware(Router router) {
			_model = router.getBoardType();
		}
	}

	private static class Location {
		private final double _latitude;
		private final double _longitude;

		public Location(Router router) {
			_latitude = router.getLatitude();
			_longitude = router.getLongitude();
		}
	}

	private static class Software {
		@JsonProperty("batman-adv")
		private final Batman _batman;
		private final Firmware _firmware;
		private final Fastd _fastd = new Fastd();
		private final Autoupdater _autoupdater = new Autoupdater();

		public Software(Router router) {
			_firmware = new Firmware(router);
			_batman = new Batman(router);
		}
	}

	private static class Batman {
		private final int _compat;
		private final String _version;

		public Batman(Router router) {
			_compat = router.getBatmanCompat();
			_version = router.getBatmanVersion();
		}
	}

	private static class Firmware {
		private final String _release;
		private final String _base;

		public Firmware(Router router) {
			_release = router.getFirmwareVersion();
			_base = "Openwrt";
		}
	}

	private static class Fastd {
		private final String _version = "";
		private final boolean _enabled = false;
	}

	private static class Autoupdater {
		private final boolean _enabled = false;
		private final String _branch = "";
	}

	private static class Statistics {
		private final double _uptime;
		private final double _memory_usage;
		private final double _clients;
		private final double _rootds_usage;
		private final double _loadavg;
		private final String _gateway; // MAC

		public Statistics(Router router) {
			_uptime = router.getUptime();
			_memory_usage = router.getMemoryUsage();
			_clients = router.getClients();
			_rootds_usage = router.getRootUsage();
			_loadavg = router.getAvgLoad();
			_gateway = router.getGateway();
		}
	}

	private static class Flags {
		private final boolean _online;
		private final boolean _gateway;

		public Flags(boolean online, boolean gateway) {
			_online = online;
			_gateway = gateway;
		}
	}
}
