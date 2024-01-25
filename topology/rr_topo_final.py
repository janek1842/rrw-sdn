from mininet.topo import Topo


class RrTopo(Topo):
    def __init__(self):
        Topo.__init__(self)
        datacenter_dict = {}
        switch_dict = {}
        
	for i in range(1,4):
            datacenter_key = 'D' + str(i)
            datacenter_dict[datacenter_key] = self.addHost(datacenter_key)
	
	for i in range(1,3):
	    switch_key = 'S' + str(i)
	    switch_dict[switch_key] = self.addSwitch(switch_key)
	
	# Client side 
        self.addLink(switch_dict["S1"], switch_dict["S2"])
	self.addLink(datacenter_dict["D1"], switch_dict["S1"])
        
        # Server conn
        self.addLink(switch_dict["S2"], datacenter_dict["D2"])
        self.addLink(switch_dict["S2"], datacenter_dict["D3"])


topos = {'rr_topo': (lambda: RrTopo())}
