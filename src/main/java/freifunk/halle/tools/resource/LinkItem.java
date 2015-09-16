package freifunk.halle.tools.resource;

import java.net.InetAddress;

public class LinkItem {
	private InetAddress _dest;
	private double _quality;
	private LinkType _type;

	public enum LinkType {
		olsr, tunnel
	}
}
