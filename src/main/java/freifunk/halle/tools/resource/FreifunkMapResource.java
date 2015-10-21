package freifunk.halle.tools.resource;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import freifunk.halle.tools.entity.Router;
import freifunk.halle.tools.graph.EtxGraph;
import freifunk.halle.tools.graph.Node;
import freifunk.halle.tools.types.KeyValuePair;

public class FreifunkMapResource {
	private final String _timestamp;
	private final Map<String, FreifunkMapRouterItem> _nodes;

	public FreifunkMapResource(EtxGraph graph, List<KeyValuePair<Router, Node>> routers) {
		_timestamp = String.valueOf(new Date().getTime());
		_nodes = Maps.newHashMap();
		for (KeyValuePair<Router, Node> router : routers) {
			_nodes.put(router.getKey().getMainIp().toString(),
					new FreifunkMapRouterItem(router.getKey(), router.getValue()));
		}
	}
}
