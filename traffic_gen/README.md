# RRW traffic generators

## HTTP Traffic generator
- After running mininet topology with below command
> sudo mn --custom rr_topo.py --topo rr_topo
- Run server xterm interface with below command
> xterm <selected_server_host>

> xterm <selected_client_host>  
- On server side (xterm interface) 
> python2 -m SimpleHTTPServer 80
- On client side 
> python2 http_traffic_gen.py <SERVER_IP> <MEAN_PACKET_INTERVAL>
