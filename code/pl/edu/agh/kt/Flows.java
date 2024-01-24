package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.OFVlanVidMatch;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.VlanVid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

public class Flows {

	private static final Logger logger = LoggerFactory.getLogger(Flows.class);

	public static short FLOWMOD_DEFAULT_IDLE_TIMEOUT = 0; // in seconds
	public static short FLOWMOD_DEFAULT_HARD_TIMEOUT = 0; // infinite
	public static short FLOWMOD_DEFAULT_PRIORITY = 100;

	protected static boolean FLOWMOD_DEFAULT_MATCH_VLAN = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_MAC = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_IP_ADDR = true;
	protected static boolean FLOWMOD_DEFAULT_MATCH_TRANSPORT = true;

	public Flows() {
		logger.info("Flows() begin/end");
	}

	public static void simpleAdd(IOFSwitch sw, IPv4Address ipAddress, OFPort outPort) {
		
		// FlowModBuilder
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		
		// match
		Match m = createMatchFromPacket(sw, ipAddress);

		// actions
		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();
		aob.setPort(outPort);
		aob.setMaxLen(Integer.MAX_VALUE);
		actions.add(aob.build());
		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT).setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT).setOutPort(outPort).setPriority(FLOWMOD_DEFAULT_PRIORITY);
		fmb.setActions(actions);
		
		// write flow to switch
		try {
			sw.write(fmb.build());
		} catch (Exception e) {
			logger.error("error {}", e);
		}
	}
	
	public static void simpleAddIp(IOFSwitch sw, IPv4Address ipAddress, OFPort outPort) {
		
		// FlowModBuilder
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
		
		// match
		Match m = createMatchFromPacketIp(sw, ipAddress);

		// actions
		OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
		List<OFAction> actions = new ArrayList<OFAction>();
		aob.setPort(outPort);
		aob.setMaxLen(Integer.MAX_VALUE);
		actions.add(aob.build());
		fmb.setMatch(m).setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT).setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT).setOutPort(outPort).setPriority(FLOWMOD_DEFAULT_PRIORITY);
		fmb.setActions(actions);
		
		// write flow to switch
		try {
			sw.write(fmb.build());
		} catch (Exception e) {
			logger.error("error {}", e);
		}
	}

	public static Match createMatchFromPacket(IOFSwitch sw, IPv4Address ipAddress) {
		Match.Builder mb = sw.getOFFactory().buildMatch();
		mb.setExact(MatchField.ETH_TYPE, EthType.ARP).setExact(MatchField.ARP_TPA, ipAddress);
		logger.info("Matcher {}", mb.toString());
		return mb.build();
	}
	
	public static Match createMatchFromPacketIp(IOFSwitch sw, IPv4Address ipAddress) {
		Match.Builder mb = sw.getOFFactory().buildMatch();
		mb.setExact(MatchField.ETH_TYPE, EthType.IPv4).setExact(MatchField.IPV4_DST, ipAddress);
		logger.info("Matcher {}", mb.toString());
		return mb.build();
	}
	
	
	
	
	
	
	
	
	
	
}
