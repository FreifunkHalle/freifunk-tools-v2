package freifunk.halle.tools.graph;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import freifunk.halle.tools.Config;
import freifunk.halle.tools.olsrjson.OlsrJsons;
import freifunk.halle.tools.olsrjson.Topology;
import freifunk.halle.tools.olsrjson.TopologyEntry;
import freifunk.halle.tools.types.KeyValuePair;

public class EtxGraph {

	private static final Supplier<EtxGraph> parseFromOlsr = new Supplier<EtxGraph>() {
		@Override
		public EtxGraph get() {
			try {
				return new EtxGraph(OlsrJsons.getOlsrTopology(Config.OLSR_HOST, Config.OLSR_PORT));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	};

	private static final Function<Edge, Node> adjacentNode(final Node self) {
		return new Function<Edge, Node>() {
			@Override
			public Node apply(Edge input) {
				if (input.getToNode().equals(self)) {
					return input.getFromNode();
				}
				return input.getToNode();
			}
		};
	}

	public static final Supplier<EtxGraph> INSTANCE = Suppliers.memoizeWithExpiration(parseFromOlsr, 5,
			TimeUnit.MINUTES);

	private final List<Node> _vpnAdjacenceList;
	private final Map<Node, Set<Edge>> _adjacenceList;
	private final AliasMap _aliasMap;

	private EtxGraph(Topology topology) throws UnknownHostException {
		_aliasMap = AliasMap.INSTANCE.get();
		_adjacenceList = Maps.newHashMap();
		_vpnAdjacenceList = Lists.newArrayList();

		Map<InetAddress, Node> mainAddressNodeMapping = Maps.newHashMap();
		Set<Edge> edges = Sets.newHashSet();

		Preconditions.checkNotNull(topology, "topology cannot be null");
		List<TopologyEntry> topoEntries = Preconditions.checkNotNull(topology.getTopology(),
				"topology.getTopology() cannot be null");

		for (TopologyEntry entry : topoEntries) {
			// PreEdit
			Node fromNode;
			InetAddress fromIp = InetAddress.getByName(entry.getLastHopIP());
			if (!_aliasMap.isMainIP(fromIp)) {
				InetAddress mainFromIp = _aliasMap.getMainAddress(fromIp);
				fromNode = getOrCreateNodeOf(mainAddressNodeMapping, mainFromIp);
				fromNode.addSecIp(fromIp);
			} else {
				fromNode = getOrCreateNodeOf(mainAddressNodeMapping, fromIp);
			}
			Node toNode;
			InetAddress toIp = InetAddress.getByName(entry.getDestinationIP());
			if (!_aliasMap.isMainIP(toIp)) {
				InetAddress mainToIp = _aliasMap.getMainAddress(toIp);
				toNode = getOrCreateNodeOf(mainAddressNodeMapping, mainToIp);
				toNode.addSecIp(toIp);
			} else {
				toNode = getOrCreateNodeOf(mainAddressNodeMapping, toIp);
			}

			// Main Adjacent

			KeyValuePair<Node, InetAddress> from = new KeyValuePair<Node, InetAddress>(fromNode, fromIp);
			KeyValuePair<Node, InetAddress> to = new KeyValuePair<Node, InetAddress>(toNode, toIp);

			Edge newEdge = new Edge(from, to, entry.getLinkQuality(), entry.getNeighborLinkQuality());
			if (edges.contains(newEdge)) {
				continue;
			}
			edges.add(newEdge);

			Set<Edge> fromEdges = _adjacenceList.get(fromNode);
			if (fromEdges == null) {
				fromEdges = Sets.newHashSet();
				_adjacenceList.put(fromNode, fromEdges);
			}
			_adjacenceList.get(fromNode).add(newEdge);

			Set<Edge> toEdges = _adjacenceList.get(toNode);
			if (toEdges == null) {
				toEdges = Sets.newHashSet();
				_adjacenceList.put(fromNode, toEdges);
			}
			_adjacenceList.get(toNode).add(newEdge);

			// VPN adjacent
			if (!_vpnAdjacenceList.contains(fromNode) && !_vpnAdjacenceList.contains(toNode)) {
				if (Config.vpnConcentrators.contains(toIp)) {
					_vpnAdjacenceList.add(fromNode);
				}
				if (Config.vpnConcentrators.contains(fromIp)) {
					_vpnAdjacenceList.add(toNode);
				}

			}
		}

	}

	private Node getOrCreateNodeOf(Map<InetAddress, Node> mainAddressNodeMapping, InetAddress mainAddress) {
		if (mainAddressNodeMapping.containsKey(mainAddress)) {
			return mainAddressNodeMapping.get(mainAddress);
		}
		Node newNode = new Node(mainAddress);
		mainAddressNodeMapping.put(mainAddress, newNode);
		return newNode;
	}

	private EtxGraph(Map<Node, Set<Edge>> adjacenceList, List<Node> vpnAdjacenceList) {
		_aliasMap = AliasMap.INSTANCE.get();
		_adjacenceList = adjacenceList;
		_vpnAdjacenceList = vpnAdjacenceList;

	}

	public boolean isHna(Node ip) {
		return _vpnAdjacenceList.contains(ip) && _adjacenceList.containsKey(ip);
	}

	public Set<Node> getNodeIpList() {
		return ImmutableSet.copyOf(_adjacenceList.keySet());
	}

	public Set<Node> getAdjacentNodes(Node ip) {
		if (!_adjacenceList.containsKey(ip)) {
			return Sets.newHashSet();
		}
		Set<Edge> edgeSet = _adjacenceList.get(ip);
		Iterable<Node> transform = Iterables.transform(edgeSet, adjacentNode(ip));
		return Sets.newHashSet(transform);
	}

	public List<Node> getTunneledNodeIps(Node ip) {
		List<Node> result = Lists.newArrayList();
		if (_vpnAdjacenceList.contains(ip)) {
			for (Node vpnNode : _vpnAdjacenceList) {
				if (!vpnNode.equals(ip)) {
					result.add(vpnNode);
				}

			}
		}
		return result;
	}

	public boolean isTunneledNode(Node ip) {
		return _vpnAdjacenceList.contains(ip);
	}

	// entfernt bestimmte Knoten und erstellt einen neuen Graphen
	public EtxGraph removeNodes(Iterable<Node> nodes) {
		Map<Node, Set<Edge>> adjacenceList = Maps.newHashMap(_adjacenceList);
		List<Node> vpnAdjacenceList = Lists.newArrayList(_vpnAdjacenceList);
		for (Node node : nodes) {
			if (adjacenceList.containsKey(node)) {
				Set<Edge> temp = adjacenceList.get(node);
				for (Edge edge : temp) {
					Set<Edge> temp2 = adjacenceList.get(edge);
					if (temp2.equals(_adjacenceList.get(edge))) {
						temp2 = Sets.newHashSet(temp2);
						adjacenceList.put(adjacentNode(node).apply(edge), temp2);
					}
					temp.remove(node);
				}
				adjacenceList.remove(node);
			}
			vpnAdjacenceList.remove(node);
		}
		return new EtxGraph(adjacenceList, vpnAdjacenceList);
	}

	// entfernt alle nicht von bestimmten Knoten aus erreichbaren Knoten und
	// erstellt einen neuen Graphen
	public EtxGraph removeUnreachableNodes(Iterable<Node> nodes) {
		Map<Node, Set<Edge>> unvisitedNodes = ImmutableMap.copyOf(_adjacenceList);

		Stack<Node> nodesToVisit = new Stack<Node>();
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext()) {
			nodesToVisit.push(it.next());
		}

		while (!nodesToVisit.isEmpty()) {
			Node node = nodesToVisit.pop();
			if (unvisitedNodes.containsKey(node)) {
				Set<Edge> edges = unvisitedNodes.get(node);
				for (Edge edge : edges)
					nodesToVisit.push(adjacentNode(node).apply(edge));
				unvisitedNodes.remove(node);
			}
		}
		return removeNodes(unvisitedNodes.keySet());
	}

	// entfernt unverbundene Knoten und erstellt einen neuen Graphen; mode > 1
	// alle unverbunden, mode = 1 nur unverbundene Nicht-HNAs, mode < 1 keine
	public EtxGraph removeUnconnectedNodes(int mode) {
		List<Node> nodesToRemove = Lists.newArrayList();
		for (Entry<Node, Set<Edge>> node : _adjacenceList.entrySet())
			if (node.getValue().size() == 0 && (mode > 1 || (mode == 1 && !_vpnAdjacenceList.contains(node.getKey()))))
				nodesToRemove.add(node.getKey());
		return removeNodes(nodesToRemove);
	}

	private static boolean startsWithAny(String text, String[] prefixes) {
		for (int ZVar1 = 0; ZVar1 < prefixes.length; ZVar1++)
			if (text.startsWith(prefixes[ZVar1]))
				return true;
		return false;
	}
}
