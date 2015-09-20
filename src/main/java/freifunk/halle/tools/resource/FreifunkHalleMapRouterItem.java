package freifunk.halle.tools.resource;

import java.util.Map;

public class FreifunkHalleMapRouterItem extends AbstractRouterItem {

	private String _hostname;
	private String _nick;
	private double longitude;
	private double latitude;
	private long mtime;
	private int llaccuracy;
	private Map<String, LinkItem> _links;
}
