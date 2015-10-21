package freifunk.halle.tools.resource;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import freifunk.halle.tools.entity.Router;
import freifunk.halle.tools.graph.Edge;
import freifunk.halle.tools.graph.Node;

public class FreifunkHalleMapRouterItem extends AbstractRouterItem {

	private final String _hostname;
	private final String _nick;
	private final double _longitude;
	private final double _latitude;
	private final long _mtime;
	private final int _llaccuracy;
	private final Map<String, LinkItem> _links;

	public FreifunkHalleMapRouterItem(Node node, Set<Edge> edges) {
		// TODO fetch router from DB
		Router router = null;
		_hostname = router.getHostname();
		_nick = router.getUserNick();
		_longitude = router.getLongitude();
		_latitude = router.getLatitude();
		_mtime = router.getLastChanged().getTime();
		_links = Maps.newHashMap();
		for (Edge edge : edges) {
			_links.put(edge.getToIp(node).toString(), new LinkItem(node, edge));
		}
		// TODO what does llaccuracy means?
		_llaccuracy = 1;
	}
}
