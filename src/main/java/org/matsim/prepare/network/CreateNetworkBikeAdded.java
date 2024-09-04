package org.matsim.prepare.network;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;

import java.util.HashSet;


public class CreateNetworkBikeAdded {

	private static final String inputNetwork = "./input/v6.1/berlin-v6.1-network-with-pt.xml.gz";
	private static final String outputFile = "./input/v6.1/berlin-v6.1-network-bike-e_bike-added.xml.gz";

	public static void main(String[] args) {

		HashSet<String> modes = new HashSet<String>();
		modes.add("car");
		modes.add("bike");
		modes.add("e-bike");
		modes.add("freight");
		modes.add("ride");
		modes.add("truck");
		Network network = NetworkUtils.readNetwork(inputNetwork);

		for(Link link : network.getLinks().values() ){
			if (link.getAllowedModes().contains(TransportMode.car)) {
				link.setAllowedModes(modes);
			}
		}
		new NetworkWriter(network).write(outputFile);





	}

}
