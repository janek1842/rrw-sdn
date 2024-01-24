package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;

public class Switchport {

	protected IOFSwitch switchIO;
	protected OFPort port;
	
	public Switchport(IOFSwitch switchIO, Integer portNumber) {
		this.switchIO = switchIO;
		this.port = OFPort.of(portNumber);
	}
}
