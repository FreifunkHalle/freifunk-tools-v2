package freifunk.halle.tools.resource;

import java.net.InetAddress;

import freifunk.halle.tools.graph.Edge;
import freifunk.halle.tools.graph.Node;

public class LinkItem {
	private final InetAddress _dest;
	private final double _quality;
	private final LinkType _type;

	public enum LinkType {
		olsr, tunnel
	}

	public LinkItem(Node from, Edge edge) {
		_quality = edge.getEtx();
		_type = edge.getType();
		if (from.equals(edge.getFromNode())) {
			_dest = edge.getToIp();
		} else {
			_dest = edge.getFromIp();
		}
	}
}
