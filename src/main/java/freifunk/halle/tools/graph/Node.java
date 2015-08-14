package freifunk.halle.tools.graph;

import java.net.InetAddress;
import java.util.List;

import com.google.common.collect.Lists;

import freifunk.halle.tools.entity.Router;

public class Node {

	public Node(InetAddress mainAddress) {
		// TODO Auto-generated constructor stub
	}

	private Router _router;
	private InetAddress _mainIp;
	private List<InetAddress> _secIps = Lists.newArrayList();

	public void addSecIp(InetAddress ip) {
		_secIps.add(ip);

	}

}
