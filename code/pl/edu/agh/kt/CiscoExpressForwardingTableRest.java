package pl.edu.agh.kt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.floodlightcontroller.restserver.RestletRoutable;
import java.util.ArrayList;


public class CiscoExpressForwardingTableRest extends ServerResource {
	
	@Get("json")
	public String handleGet() throws JsonProcessingException {
		List<String> serializedObjects = new ArrayList<String>();
		for (IPv4Address ipAddr : Routing.switchportByIp.keySet()) {
			serializedObjects.add(serialize(new CefEntryReturn(ipAddr.toString(), Routing.switchportByIp.get(ipAddr).switchIO.getId().toString(),Routing.switchportByIp.get(ipAddr).port.toString())));
		}
		return mapper.writeValueAsString(serializedObjects);
	}
	
	@Post("json")
	public String handlePost(String text) throws JsonProcessingException, IOException {
		CefEntry[] ipTableEntries = deserialize(text);
		List<CefEntry> entriesList = Arrays.asList(ipTableEntries);
		Map<IPv4Address, Switchport> targetTable = new HashMap<IPv4Address, Switchport>();
		for (CefEntry entry : entriesList) {
			targetTable.put(IPv4Address.of(entry.ipv4Address), new Switchport(Routing.switchService.getAllSwitchMap().get(DatapathId.of(entry.datapathId)), Integer.valueOf(entry.outPortNumber)));
		}
		Routing.switchportByIp = targetTable;
		Routing.calculateSpfTree(Routing.swListGlobal);
		return serialize(entriesList);
	}
	
	private static final ObjectMapper mapper;
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	public static String serialize(CefEntry t) throws JsonProcessingException {
		return mapper.writeValueAsString(t);
	}
	
	public static String serialize(CefEntryReturn t) throws JsonProcessingException {
		return mapper.writeValueAsString(t);
	}
	
	public static String serialize(List<CefEntry> t) throws JsonProcessingException {
		return mapper.writeValueAsString(t);
	}
	
	public static CefEntry[] deserialize(String text) throws IOException {
		return mapper.readValue(text, CefEntry[].class);
	}
	
	class CefEntryReturn {
		public String ipv4Address;
		public String datapathId;
		public String outPortNumber;
		
		public CefEntryReturn(String ip, String dpId, String opn) {
			this.ipv4Address = ip;
			this.datapathId = dpId;
			this.outPortNumber = opn;
		}
		
	}
}
