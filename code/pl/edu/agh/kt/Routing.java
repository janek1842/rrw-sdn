package pl.edu.agh.kt;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routing {
	
	public static IRoutingService routingService;
	public static IOFSwitchService switchService;
	
	protected static final Logger logger = LoggerFactory.getLogger(Routing.class);
	
	public static Map<IPv4Address, Switchport> switchportByIp = new HashMap<IPv4Address, Switchport>();
	public static List<DatapathId> swListGlobal = new ArrayList<DatapathId>();
	
	public static void calculateSpfTree(List<DatapathId> swList) {
		logger.info("CEF SIZE {}", switchportByIp.values().size());
		
		Routing.swListGlobal = swList;
		Map<DatapathId, IOFSwitch> switchByDatapathId = switchService.getAllSwitchMap();
		
		for (DatapathId srcId : swList) {
			
			for (IPv4Address dstAddress : switchportByIp.keySet()) {
				logger.info("internalipv4addr {}", dstAddress);
				logger.info("switchportByIp {}", switchportByIp.keySet());
				logger.info("object {}", switchportByIp.get(dstAddress));
				logger.info("object {}", switchportByIp.get(dstAddress).switchIO);
				logger.info("object {}", switchportByIp.get(dstAddress).port);
				
				DatapathId dstId = switchportByIp.get(dstAddress).switchIO.getId();
				if (dstId.equals(srcId)) {
					continue;
				}
				
				logger.info("---{}----{}----", srcId, dstId);
				Route rututu = calculateSpf(srcId, dstId);
				logger.info("Pierwszy rututu {}", rututu.getPath().toString());
				List<NodePortTuple> nodePorts = rututu.getPath();
				Map<DatapathId, NodePortTuple> exitNodePortBySwitchId = new HashMap<DatapathId, NodePortTuple>();
				
				for (NodePortTuple nodePort : nodePorts) {
					exitNodePortBySwitchId.put(nodePort.getNodeId(), nodePort);
				
				}	
				
				nodePorts = new ArrayList<NodePortTuple>(exitNodePortBySwitchId.values());
				for (int i = nodePorts.size() - 1; i >= 0; i--) {
					NodePortTuple nodePort = nodePorts.get(i);
					OFPort destPort = nodePort.getPortId();
					DatapathId switchId = nodePort.getNodeId();
					Flows.simpleAdd(switchByDatapathId.get(switchId), dstAddress, destPort);
//					Flows.simpleAddIp(switchByDatapathId.get(switchId), dstAddress, destPort);
				}
				Flows.simpleAdd(switchportByIp.get(dstAddress).switchIO, dstAddress, switchportByIp.get(dstAddress).port);
//				Flows.simpleAddIp(switchportByIp.get(dstAddress).switchIO, dstAddress, switchportByIp.get(dstAddress).port);
			}
		}
		logger.info("Calculating SPF tree");
	}
	
	public static Route calculateSpf(DatapathId src, DatapathId dst) {
		Route route = routingService.getRoute(src, dst, U64.of(0));
		return route;
	}
	
	private void generateData() {
		Map<DatapathId, IOFSwitch> switchByDatapathId = this.switchService.getAllSwitchMap();
		Routing.switchportByIp.put(IPv4Address.of("10.0.0.6"), new Switchport(switchByDatapathId.get(DatapathId.of("00:00:00:00:00:05")), 20));
		Routing.switchportByIp.put(IPv4Address.of("10.0.0.3"), new Switchport(switchByDatapathId.get(DatapathId.of("00:00:00:00:00:01")), 20));
	}
}
