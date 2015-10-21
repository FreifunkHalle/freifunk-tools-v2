package freifunk.halle.tools.resource;

import java.net.InetAddress;
import java.util.Map;

import com.google.common.collect.Maps;

import freifunk.halle.tools.graph.EtxGraph;
import freifunk.halle.tools.graph.Node;

public class WikiResource {
	private final Map<String, WikiRouterItem> _topo;

	public WikiResource(EtxGraph graph) {
		_topo = Maps.newHashMap();
		for (Node node : graph.getNodeList()) {
			for (InetAddress address : node.getAllIps()) {
				_topo.put(address.toString(), new WikiRouterItem(node, address));
			}
		}
	}
}
