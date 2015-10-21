package freifunk.halle.tools.resource;

import java.util.Map;

import com.google.common.collect.Maps;

import freifunk.halle.tools.graph.EtxGraph;
import freifunk.halle.tools.graph.Node;

public class FreifunkHalleMapResource {

	private final Map<String, FreifunkHalleMapRouterItem> _topo;

	public FreifunkHalleMapResource(EtxGraph graph) {
		_topo = Maps.newHashMap();
		for (Node node : graph.getNodeList()) {
			_topo.put(node.getMainIp().toString(), new FreifunkHalleMapRouterItem(node, graph.getAdjacentEdges(node)));
		}
	}
}
