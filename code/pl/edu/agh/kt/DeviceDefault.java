package pl.edu.agh.kt;

import java.util.Date;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.VlanVid;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IEntityClass;
import net.floodlightcontroller.devicemanager.SwitchPort;

public class DeviceDefault implements IDevice {

	private String mac;
	private IPv4Address[] addresses;
	private SwitchPort[] switchPorts;
	
	public DeviceDefault(String mac, IPv4Address ip, SwitchPort sp) {
		this.mac = mac;
		this.addresses = new IPv4Address[]{ip};
		this.switchPorts = new SwitchPort[]{sp};
	}
	
	
	@Override
	public Long getDeviceKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MacAddress getMACAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMACAddressString() {
		// TODO Auto-generated method stub
		return this.mac;
	}

	@Override
	public VlanVid[] getVlanId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPv4Address[] getIPv4Addresses() {
		// TODO Auto-generated method stub
		return this.addresses;
	}

	@Override
	public IPv6Address[] getIPv6Addresses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwitchPort[] getAttachmentPoints() {
		// TODO Auto-generated method stub
		return this.switchPorts;
	}

	@Override
	public SwitchPort[] getOldAP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SwitchPort[] getAttachmentPoints(boolean includeError) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VlanVid[] getSwitchPortVlanIds(SwitchPort swp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastSeen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEntityClass getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
