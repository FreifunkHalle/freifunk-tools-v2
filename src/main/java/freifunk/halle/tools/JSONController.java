package freifunk.halle.tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import freifunk.halle.tools.olsrjson.Hna;
import freifunk.halle.tools.olsrjson.Mid;
import freifunk.halle.tools.olsrjson.OlsrJsons;
import freifunk.halle.tools.olsrjson.Topology;

@Controller
@EnableAutoConfiguration
public class JSONController {

	@RequestMapping("/JSON.ashx")
	@ResponseBody
	String json() throws RestClientException, MalformedURLException, URISyntaxException, UnknownHostException {

		InetAddress inetAddress = Inet4Address.getByName("10.62.7.26");
		Hna hna = OlsrJsons.getOlsrHna(inetAddress, 9090);
		Mid olsrMid = OlsrJsons.getOlsrMid(inetAddress, 9090);
		Topology olsrTopology = OlsrJsons.getOlsrTopology(inetAddress, 9090);
		return "Hello World!";
	}

	@RequestMapping("/WikiJSON.ashx")
	@ResponseBody
	String wikiJson() {
		return "Hello World!";
	}

	@RequestMapping("/Topology.ashx")
	@ResponseBody
	String topology() {
		return "Hello World!";
	}

	@RequestMapping("/NodeData.ashx")
	@ResponseBody
	String topoData() {
		return "Hello World!";
	}

	@RequestMapping("/TopoData.ashx")
	@ResponseBody
	String nodeData() {
		return "Hello World!";
	}

	@RequestMapping("/ffmap")
	@ResponseBody
	String ffmap() {
		return "Hello World!";
	}

}
