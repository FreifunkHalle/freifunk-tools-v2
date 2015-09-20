package freifunk.halle.tools.graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Period;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freifunk.halle.tools.Config;
import freifunk.halle.tools.resource.validation.FormatValidation;
import freifunk.halle.tools.resource.validation.FormatValidation.FormatEnum;
import freifunk.halle.tools.types.KeyValuePair;

public class Topology {

	private static final Function<InetAddress, Node> toNode = new Function<InetAddress, Node>() {

		@Override
		public Node apply(InetAddress input) {
			return new Node(input);
		}
	};

	public static void topology(HttpServletResponse response, double maxetx,
			int zeig, int nachkomma, double gesehen, Double groesse,
			int ueberlapp, boolean db, FormatValidation.FormatEnum format,
			List<InetAddress> zeigip, List<InetAddress> erreichbar,
			List<InetAddress> hvip, List<Double> zoom, Config config,
			Locale locale) throws IOException {
		EtxGraph Graph = EtxGraph.INSTANCE.get();
		Graph = Graph.removeUnconnectedNodes(zeig);
		if (erreichbar.size() > 0) {
			Graph = Graph.removeUnreachableNodes(Iterables.transform(
					erreichbar, toNode));
		}

		double ZoomNodeText = 1D;
		double ZoomNodeBorder = 1D;
		double ZoomLinkText = 1D;
		double ZoomLinkThickness = 1D;
		if (zoom != null && zoom.size() >= 1) {
			ZoomNodeText = zoom.get(0);
			ZoomNodeBorder = zoom.size() > 1 ? zoom.get(1) : ZoomNodeText;
			ZoomLinkText = zoom.size() > 2 ? zoom.get(2) : ZoomNodeText;
			ZoomLinkThickness = zoom.size() > 3 ? zoom.get(3) : ZoomNodeBorder;
		}

		String SortOrder = ueberlapp == 0 ? "breadthfirst"
				: (ueberlapp < 0 ? "nodesfirst" : "edgesfirst");

		String responseContentType;
		switch (format) {
		case PNG:
			responseContentType = "image/png";
			break;
		case SVG:
			responseContentType = "image/svg+xml; charset=utf-8";
			break;
		case PDF:
			responseContentType = "application/pdf";
			break;
		case INPUT:
			responseContentType = "text/plain; charset=utf-8";
			break;
		case DOT:
			responseContentType = "text/plain; charset=utf-8";
			break;
		default:
			responseContentType = "image/png";
			format = FormatEnum.PNG;
		}
		response.setContentType(responseContentType);

		OutputStreamWriter Output;
		Process GVProcess = null;

		if (format == FormatEnum.INPUT) {
			Output = new OutputStreamWriter(response.getOutputStream());
		} else {
			List<String> command = Lists.newArrayList();
			command.add(config.getGraphVizPath() + " -T "
					+ format.toString().toLowerCase());
			command.add("/A");
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(new File(System.getenv("temp")));
			GVProcess = builder.start();
			// StartInfo.RedirectStandardInput = true;
			// StartInfo.RedirectStandardOutput = true;
			// StartInfo.CreateNoWindow = true;
			// StartInfo.UseShellExecute = false;
			Output = new OutputStreamWriter(GVProcess.getOutputStream());
		}

		double Value;
		Map<String, KeyValuePair<DateTime, String>> nodeData;
		Output.write("graph Topologie {\n");
		if (format != FormatValidation.FormatEnum.PNG && groesse == null) {
			Output.write(String
					.format(locale,
							"Graph[ charset=\"utf-8\", start=\"0\", epsilon=\"0.01\", bgcolor=\"#ffffff\", outputorder=\"%s\"];\n",
							SortOrder));
		} else {
			Output.write(String
					.format(locale,
							"Graph[ charset=\"utf-8\", start=\"0\", size=\"%f,%f\", epsilon=\"0.01\", bgcolor=\"#ffffff\", outputorder=\"%s\"];\n",
							groesse, groesse, SortOrder));
		}
		Output.write(String.format(locale,
				"Edge[ fontname=\"BitStream\", fontsize=\"%f\"];\n",
				12D * ZoomLinkText));
		if (!db) {
			nodeData = Maps.newHashMap();
			Output.write(String
					.format(locale,
							"Node[ fontname=\"BitStream\", shape=\"ellipse\", style=\"filled\", height=\"0\", fontsize=\"%f\", color=\"red\", penwidth=\"%f\"];\n",
							12D * ZoomNodeText, ZoomNodeBorder));

		} else {
			Output.write(String
					.format(locale,
							"Node[ fontname=\"BitStream\", shape=\"ellipse\", style=\"filled\", height=\"%f\", fontsize=\"%f\", color=\"red\", penwidth=\"%f\"];\n",
							0.6D * ZoomNodeText, 8D * ZoomNodeText,
							ZoomNodeBorder));
			nodeData = getNodeDataFromDb();
		}
		for (Node node : Graph.getNodeList()) {
			KeyValuePair<DateTime, String> nodeProps;
			String nodeIp = node.getMainIp().getHostAddress();
			String shortIp = config.getNetTools().formatToShort(nodeIp);
			if (nodeData.containsValue(nodeIp)) {
				nodeProps = nodeData.get(nodeIp);
				shortIp += (nodeProps.getValue() == null ? "" : "\n"
						+ nodeProps.getValue());
				Value = Math.max(new Period(DateTime.now().getMillis(),
						nodeProps.getKey().getMillis()).getDays(), 0D);
			} else
				Value = 0D;
			Output.write(String.format(
					locale,
					"\"%s\" [label=\"%s\", fillcolor=\"#%s\"]\n",
					nodeIp,
					shortIp,
					getNodeColor(Math.pow(0.5D, Value / gesehen),
							Graph.isHna(node), hvip.contains(node.getMainIp()))));
			for (Edge link : Graph.getAdjacentEdges(node)) {
				Node toNode;
				if (node.equals(link.getToNode())) {
					toNode = link.getFromNode();
				} else {
					toNode = link.getToNode();
				}
				Output.write(String
						.format(locale,
								"\"%s\" -- \"%s\" [label=\"%f\", color=\"#%s\", penwidth=\"%f\", len=\"%f\"];\n",
								nodeIp, toNode.getMainIp().getHostAddress(),
								link.getEtx(), getLinkColor(link.getEtx()),
								getLinkThickness(link.getEtx())
										* ZoomLinkThickness,
								getLinkLength(link.getEtx())));
			}
			Output.write("}\n");
		}

		Output.close();
		if (GVProcess != null) {
			InputStream inputStream = GVProcess.getInputStream();
			ServletOutputStream outputStream = response.getOutputStream();
			byte[] Buffer = new byte[1024];
			while (true) {
				int Read = inputStream.read(Buffer, 0, 1024);
				if (Read == 0)
					break;
				outputStream.write(Buffer, 0, Read);
				outputStream.flush();
			}
			GVProcess.destroy();
		}
	}

	private static Map<String, KeyValuePair<DateTime, String>> getNodeDataFromDb() {
		Map<String, KeyValuePair<DateTime, String>> result = Maps.newHashMap();

		// DB
		// "select \"IPv4\", \"LastSeen\", \"Data\" from " + Prefix +
		// "Node\" left outer join (select \"NodeId\", \"Data\" from " + Prefix
		// + "InfoValue\" where \"NameId\" = (select \"Id\" from " + Prefix +
		// "InfoName\" where \"Data\" = 'ff_adm_loc') and \"Step\" = 0) \"Values\"  on \"Node\".\"Id\" = \"Values\".\"NodeId\"";
		// NodeData.Add(Reader.GetString(0), new KeyValuePair<Date,
		// String>(Reader.GetDateTime(1), Reader.IsDBNull(2) ? null :
		// Reader.GetString(2)));

		return result;
	}

	private static String getNodeColor(double seen, boolean hna,
			boolean highlight) {
		byte a;
		int b;
		if (highlight) {
			a = (byte) 0xC8;
			b = 0;
		} else {
			a = (byte) 0xFF;
			b = 0xA0;
		}
		int c = b + 0x18;
		if (!hna)
			c += a - (b + 0x18);
		b = a - (b + 0x18);
		if (!hna)
			b += 0x18;
		return getColor((byte) c, a, (byte) (a - b * seen));
	}

	private static double getLinkLength(double etx) {
		return Math.log(etx) * 2D + 2D;
	}

	private static double getLinkThickness(double etx) {
		if (etx < 4D)
			return -5D / 3D * etx + 23D / 3D;
		return 1D;
	}

	private static String getLinkColor(double etx) {
		return getColor(boundColor(382.5D - Math.abs(85D * (etx - 5.5D))),
				(byte) 0, boundColor(595D - 85D * etx));
	}

	private static byte boundColor(double Value) {
		return ((Double) Math.min(Math.max(Value, 0D), 255D)).byteValue();
	}

	private static String getColor(byte r, byte g, byte b) {
		String RetVal = ((Integer) ((r << 16) | (g << 8) | b)).toString();
		return Strings.repeat("0", 6 - RetVal.length()) + RetVal;
	}

}
