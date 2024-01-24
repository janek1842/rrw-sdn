package pl.edu.agh.kt;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.debugcounter.IDebugCounter;
import net.floodlightcontroller.loadbalancer.LBVip;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected ITopologyService topologyService;
	protected IRoutingService routingService;
	protected IOFSwitchService switchService;
	protected IRestApiService restApiService;
	private Ethernet eth;
	protected static Routing routing;
	public Integer D2_COUNTER=0;
	public Integer D3_COUNTER=0;
	
	private IDebugCounter counterPacketOut;
	protected HashMap<String, LBVip> vips;
	
	public boolean D2_RUN = true;
	public boolean D3_RUN = false;
	
	
	
	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}
	
	public String roundRobin() {
		// Priority of D3 Server [0,10]
		short D2_PRIORITY = 7;
		short D3_PRIORITY = 3;
		logger.info("HELKO_RR");
		if (D2_RUN) {
			D2_COUNTER += 1;
			
			if (D2_COUNTER==D2_PRIORITY){
				D2_RUN=false;
			}
			return "D2";
		}
		
		else {
			D3_COUNTER += 1;
			if(D3_COUNTER == D3_PRIORITY){
					D2_RUN = true;
				}
			return "D3";
		}
	}
	
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {

		logger.info("************* NEW PACKET IN *************");
		PacketExtractor extractor = new PacketExtractor();
		IPv4Address ipk = extractor.packetExtract(cntx);
		eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
//		logger.info("newP"+sw.getId().toString());
//		logger.info(sw.getId().toString().equals("00:00:00:00:00:00:00:01")+"");
		OFPacketIn pin = (OFPacketIn) msg;
		
		if (sw.getId().toString().equals("00:00:00:00:00:00:00:01")){
			
			if ( eth.getEtherType().equals(EthType.IPv4 ) ) {
				Flows.simpleAddIp(sw, IPv4Address.of("10.0.0.1"), OFPort.of(2));
				if (ipk.toString().equals("10.0.0.100")) {
					logger.info("IDE Z D1");
					Flows.simpleAddIp(sw, ipk, OFPort.of(1));
				}
				else{
					logger.info("WRACAM DO D1");
					logger.info(ipk.toString());
				}
			}
			
			else if (eth.getEtherType().equals(EthType.ARP)){
				Flows.simpleAdd(sw, ipk, OFPort.of(1));
			}
		}
		
		else {
			if ( eth.getEtherType().equals(EthType.IPv4 ) ) {
				if (ipk.toString().equals("10.0.0.100")) {
					if (roundRobin().equals("D2") ){
						 logger.info("LB choice: D2");
						 IPv4 ipv4 = (IPv4) eth.getPayload();
                		 
                		        		
                		 TCP tcp = (TCP) ipv4.getPayload();                				 
                		 Flows2.simpleAddFlowToLB(sw, ipv4.getDestinationAddress() ,OFPort.of(2), tcp.getSourcePort(), IPv4Address.of("10.0.0.2"), MacAddress.of("00:00:00:00:00:02"));
                		 Flows2.simpleAddFlowFromLB(sw, ipv4.getSourceAddress() , OFPort.of(1));
                		 
                		 ipv4.setDestinationAddress(IPv4Address.of("10.0.0.2"));
                		 pushPacket(eth.getPayload(), sw, pin.getBufferId(), (pin.getVersion().compareTo(OFVersion.OF_12) < 0) ? pin.getInPort() : pin.getMatch().get(MatchField.IN_PORT), OFPort.TABLE,
                                cntx, true); 
					}
					else {
						 logger.info("LB choice: D3");
						 IPv4 ipv4 = (IPv4) eth.getPayload();
               		 	 
						
	               		 TCP tcp = (TCP) ipv4.getPayload(); 
	            		 Flows2.simpleAddFlowToLB(sw, ipv4.getDestinationAddress() ,OFPort.of(3), tcp.getSourcePort(), IPv4Address.of("10.0.0.3"), MacAddress.of("00:00:00:00:00:03"));
	            		 Flows2.simpleAddFlowFromLB(sw, ipv4.getSourceAddress() , OFPort.of(1));
	            		 
	            		 ipv4.setDestinationAddress(IPv4Address.of("10.0.0.3"));
	            		 pushPacket(eth.getPayload(), sw, pin.getBufferId(), (pin.getVersion().compareTo(OFVersion.OF_12) < 0) ? pin.getInPort() : pin.getMatch().get(MatchField.IN_PORT), OFPort.TABLE,
	                            cntx, true); 
					}
				}
				else {
					Flows.simpleAdd(sw, ipk, OFPort.of(1));
				}
			}
			
			else if (eth.getEtherType().equals(EthType.ARP)) {
				vipProxyArpReply(sw,pin,cntx);
			}
		}
		
		
//
		// TODO LAB 6

//		Flows.simpleAdd(sw, pin, cntx, outPort);

		return Command.STOP;
	}
	
	protected void vipProxyArpReply(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx) {
            
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                                                              IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        // retrieve original arp to determine host configured gw IP address                                          
        if (! (eth.getPayload() instanceof ARP))
            return;
        ARP arpRequest = (ARP) eth.getPayload();
        
        if (arpRequest.getTargetProtocolAddress().toString().equals("10.0.0.100")) {
        	
        
        IPacket arpReply = new Ethernet()
            .setSourceMACAddress("00:00:00:00:00:02")
            .setDestinationMACAddress(eth.getSourceMACAddress())
            .setEtherType(EthType.ARP)
            .setVlanID(eth.getVlanID())
            .setPriorityCode(eth.getPriorityCode())
            .setPayload(
                new ARP()
                .setHardwareType(ARP.HW_TYPE_ETHERNET)
                .setProtocolType(ARP.PROTO_TYPE_IP)
                .setHardwareAddressLength((byte) 6)
                .setProtocolAddressLength((byte) 4)
                .setOpCode(ARP.OP_REPLY)
                .setSenderHardwareAddress( MacAddress.of("00:00:00:00:00:02") )
                .setSenderProtocolAddress(arpRequest.getTargetProtocolAddress())
                .setTargetHardwareAddress(eth.getSourceMACAddress())
                .setTargetProtocolAddress(arpRequest.getSenderProtocolAddress()));
                
        // push ARP reply out
        	pushPacket(arpReply, sw, OFBufferId.NO_BUFFER, OFPort.ANY, (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT)), cntx, true);
        }
        return;
    }
	
	 public void pushPacket(IPacket packet, 
             IOFSwitch sw,
             OFBufferId bufferId,
             OFPort inPort,
             OFPort outPort, 
             FloodlightContext cntx,
             boolean flush) {
			
				OFPacketOut.Builder pob = sw.getOFFactory().buildPacketOut();
				
				// set actions
				List<OFAction> actions = new ArrayList<OFAction>();
				actions.add(sw.getOFFactory().actions().buildOutput().setPort(outPort).setMaxLen(Integer.MAX_VALUE).build());
				
				pob.setActions(actions);
				
				// set buffer_id, in_port
				pob.setBufferId(bufferId);
				pob.setInPort(inPort);
				
				// set data - only if buffer_id == -1
				if (pob.getBufferId() == OFBufferId.NO_BUFFER) {
				if (packet == null) {
				  
				  return;
				}
				byte[] packetData = packet.serialize();
				pob.setData(packetData);
				}
				
				sw.write(pob.build());
				}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRoutingService.class);
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		restApiService = context.getServiceImpl(IRestApiService.class);
		Routing.routingService = routingService;
		Routing.switchService = switchService;
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		logger.info("******************* START **************************");
		topologyService.addListener(new SdnLabTopologyListener());
		restApiService.addRestletRoutable(new RestHandler());
	}

	public static Routing getRouting() {
		return routing;
	}
}
