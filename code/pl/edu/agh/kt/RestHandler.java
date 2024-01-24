package pl.edu.agh.kt;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RestHandler implements RestletRoutable {
	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/iptable", CiscoExpressForwardingTableRest.class);
		return router;
	}
	
	@Override
	public String basePath() {
	// TODO Auto-generated method stub
		return "/sdnlab";
	}
}
