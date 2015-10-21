package freifunk.halle.tools.graph;

import java.net.InetAddress;

import freifunk.halle.tools.resource.LinkItem.LinkType;
import freifunk.halle.tools.types.KeyValuePair;

public class Edge {

	private KeyValuePair<Node, InetAddress> _from;
	private KeyValuePair<Node, InetAddress> _to;

	private KeyValuePair<Float, Float> _quality;

	private LinkType _type;

	public LinkType getType() {
		return _type;
	}

	public Edge(KeyValuePair<Node, InetAddress> from, KeyValuePair<Node, InetAddress> to, Float q1, Float q2) {
		_from = from;
		_to = to;
		_quality = new KeyValuePair<Float, Float>(q1, q2);
	}

	public Node getFromNode() {
		return _from.getKey();
	}

	public Node getToNode() {
		return _to.getKey();
	}

	public InetAddress getFromIp() {
		return _from.getValue();
	}

	public InetAddress getToIp() {
		return _to.getValue();
	}

	public InetAddress getToIp(Node from) {
		if (_from.getKey().equals(from)) {
			return _to.getValue();
		}
		return _from.getValue();
	}

	public KeyValuePair<Float, Float> getQuality() {
		return _quality;
	}

	public double getEtx() {
		return 1.0D / (_quality.getKey() * _quality.getValue());
	}

	public boolean direction(KeyValuePair<Node, InetAddress> from) {
		return _from.equals(from);
	}

	@Override
	public boolean equals(Object edge) {
		Edge edge2 = (Edge) edge;
		boolean forward = _from.equals(edge2._from) && _to.equals(edge2._to);
		boolean backward = _from.equals(edge2._to) && _from.equals(edge2._to);
		return forward || backward;
	}

	// TODO hashcode

}
