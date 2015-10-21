package freifunk.halle.tools;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import freifunk.halle.tools.entity.Router;
import freifunk.halle.tools.graph.EtxGraph;
import freifunk.halle.tools.graph.Node;
import freifunk.halle.tools.graph.Topology;
import freifunk.halle.tools.olsrjson.Hna;
import freifunk.halle.tools.olsrjson.Mid;
import freifunk.halle.tools.olsrjson.OlsrJsons;
import freifunk.halle.tools.resource.FreifunkHalleMapResource;
import freifunk.halle.tools.resource.FreifunkMapResource;
import freifunk.halle.tools.resource.WikiResource;
import freifunk.halle.tools.resource.validation.DoubleListValidation;
import freifunk.halle.tools.resource.validation.DoubleListValidation.DoubleListValidator;
import freifunk.halle.tools.resource.validation.FormatValidation;
import freifunk.halle.tools.resource.validation.IpListValidation;
import freifunk.halle.tools.resource.validation.IpListValidation.IpListValidator;
import freifunk.halle.tools.types.KeyValuePair;

@Controller
@EnableAutoConfiguration
public class JSONController {

	@RequestMapping("/test")
	@ResponseBody
	String test() throws RestClientException, UnknownHostException {
		InetAddress inetAddress = Inet4Address.getByName("10.62.2.131");
		Hna hna = OlsrJsons.getOlsrHna(inetAddress, 9090);
		Mid olsrMid = OlsrJsons.getOlsrMid(inetAddress, 9090);
		freifunk.halle.tools.olsrjson.Topology olsrTopology = OlsrJsons.getOlsrTopology(inetAddress, 9090);
		return "Hello World!";
	}

	@RequestMapping("/JSON.ashx")
	@Produces("application/json")
	@ResponseBody
	ResponseEntity<FreifunkHalleMapResource> ffhtopo() {
		FreifunkHalleMapResource resource = new FreifunkHalleMapResource(EtxGraph.INSTANCE.get());
		return new ResponseEntity<FreifunkHalleMapResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping("/WikiJSON.ashx")
	@Produces("application/json")
	@ResponseBody
	ResponseEntity<WikiResource> wikiJson() {
		WikiResource resource = new WikiResource(EtxGraph.INSTANCE.get());
		return new ResponseEntity<WikiResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping("/Topology.ashx")
	void topology(HttpServletResponse response, HttpServletRequest request,
			@RequestParam("maxetx") @Min(0) double maxetx, @RequestParam("zeig") @Min(0) @Max(2) int zeig,
			@RequestParam("nachkomma") @Min(0) @Max(99) int nachkomma, @RequestParam("gesehen") @Min(0) Double gesehen,
			@RequestParam("groesse") @Min(0) @Max(10000) double groesse,
			@RequestParam("ueberlapp") @Min(0) @Max(2) int ueberlapp, @RequestParam("db") boolean db,
			@RequestParam("format") @FormatValidation @DefaultValue("png") String format,
			@RequestParam("zeigip") @IpListValidation String zeigip,
			@RequestParam("erreichbar") @IpListValidation String erreichbar,
			@RequestParam("hvip") @IpListValidation String hvip,
			@RequestParam("zoom") @DoubleListValidation String zoom) throws IOException {

		FormatValidation.FormatEnum parsedFormat = FormatValidation.FormatEnum.valueOf(format.toUpperCase());
		List<InetAddress> parsedHvIp = IpListValidator.parse(hvip);
		List<InetAddress> parsedErreichbar = IpListValidator.parse(erreichbar);
		List<InetAddress> parsedZeigIp = IpListValidator.parse(zeigip);
		List<Double> parsedZoom = DoubleListValidator.parse(zoom);

		Config config = Config.getInstance();
		if (gesehen == null) {
			gesehen = config.getLastSeenGradient();
		}
		EtxGraph graph = EtxGraph.INSTANCE.get();

		Topology.topology(response, maxetx, zeig, nachkomma, gesehen, groesse, ueberlapp, db, parsedFormat,
				parsedZeigIp, parsedErreichbar, parsedHvIp, parsedZoom, config, request.getLocale(), graph);

	}

	@RequestMapping("/ffmap")
	@Produces("application/json")
	@ResponseBody
	ResponseEntity<FreifunkMapResource> ffmap() {
		EtxGraph graph = EtxGraph.INSTANCE.get();
		// TODO read from db
		List<KeyValuePair<Router, Node>> bla = null;
		FreifunkMapResource resource = new FreifunkMapResource(graph, bla);
		return new ResponseEntity<FreifunkMapResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

}
