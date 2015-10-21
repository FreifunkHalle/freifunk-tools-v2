package freifunk.halle.tools.resource;

import java.net.InetAddress;

import freifunk.halle.tools.entity.Router;
import freifunk.halle.tools.graph.Node;

public class WikiRouterItem extends AbstractRouterItem {

	private final String _addr;
	private final boolean _hastunnellink;
	private final boolean _internet;
	private final long _stime;

	public WikiRouterItem(Node node, InetAddress address) {
		// TODO fetch router from DB
		Router router = null;
		_addr = address.toString();
		_stime = router.getLastChanged().getTime();
		// TODO change to another way to determine internet
		_internet = node.hasTunnelLink();
		_hastunnellink = node.hasTunnelLink();
	}

}
