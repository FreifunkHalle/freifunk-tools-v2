package freifunk.halle.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import freifunk.halle.tools.olsrjson.Hna;
import freifunk.halle.tools.olsrjson.Mid;
import freifunk.halle.tools.olsrjson.OlsrJsons;
import freifunk.halle.tools.olsrjson.Topology;
import freifunk.halle.tools.resource.FreifunkHalleMapResource;
import freifunk.halle.tools.resource.FreifunkMapResource;
import freifunk.halle.tools.resource.RenderedTopologyResource;
import freifunk.halle.tools.resource.WikiResource;
import freifunk.halle.tools.resource.validation.Format;

@Controller
@EnableAutoConfiguration
public class JSONController {

	@RequestMapping("/test")
	@ResponseBody
	String test() throws RestClientException, MalformedURLException, URISyntaxException, UnknownHostException {

		InetAddress inetAddress = Inet4Address.getByName("10.62.7.26");
		Hna hna = OlsrJsons.getOlsrHna(inetAddress, 9090);
		Mid olsrMid = OlsrJsons.getOlsrMid(inetAddress, 9090);
		Topology olsrTopology = OlsrJsons.getOlsrTopology(inetAddress, 9090);
		return "Hello World!";
	}

	@RequestMapping("/JSON.ashx")
	@ResponseBody
	ResponseEntity<FreifunkHalleMapResource> ffhtopo()
			throws RestClientException, MalformedURLException, URISyntaxException, UnknownHostException {
		FreifunkHalleMapResource resource = null;
		return new ResponseEntity<FreifunkHalleMapResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping("/WikiJSON.ashx")
	@ResponseBody
	ResponseEntity<WikiResource> wikiJson() {
		WikiResource resource = null;
		return new ResponseEntity<WikiResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping("/Topology.ashx")
	@ResponseBody
	ResponseEntity<RenderedTopologyResource> topology(@RequestParam("maxetx") @Min(0) double maxetx,
			@RequestParam("zeig") @Min(0) @Max(2) int zeig, @RequestParam("nachkomma") @Min(0) @Max(99) int nachkomma,
			@RequestParam("gesehen") @Min(0) double gesehen,
			@RequestParam("groesse") @Min(0) @Max(10000) double groesse,
			@RequestParam("ueberlapp") @Min(0) @Max(2) int ueberlapp, @RequestParam("format") String Format,
			@RequestParam("db") boolean db, @RequestParam("format") @Format String format) {
		RenderedTopologyResource resource = null;
		return new ResponseEntity<RenderedTopologyResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping("/ffmap")
	@ResponseBody
	ResponseEntity<FreifunkMapResource> ffmap() {
		FreifunkMapResource resource = null;
		return new ResponseEntity<FreifunkMapResource>(resource, HttpStatus.NOT_IMPLEMENTED);
	}

}
