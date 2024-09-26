package org.matsim.prepare.network;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.bicycle.BicycleUtils;
import org.matsim.core.network.LinkFactory;
import org.matsim.core.network.NetworkUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.matsim.core.network.NetworkUtils.getLinks;

public class AddRsvAttributeToNetwork {

	public static void main(String[] args) {

		Network network = NetworkUtils.readNetwork("./input/v6.1/berlin-v6.1-network-bike-e_bike-added.xml.gz");
		String outputPath = "./input/v6.1/berlin-v6.1-network-rsv.xml.gz";
		String csvPath = "./src/main/java/org/matsim/prepare/network/RSV_Links.csv";
		String line;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(csvPath));
			while ((line = reader.readLine()) != null) {
				String[] links = line.split(",");
				for (String linkString : links) {
					Id linkId = Id.createLinkId(linkString.trim());
					Link link = network.getLinks().get(linkId);
					if (link != null) {
						link.getAttributes().putAttribute(BicycleUtils.BICYCLE_INFRASTRUCTURE_SPEED_FACTOR, 1.5);
					}
				}
			}

			new NetworkWriter(network).write(outputPath);
			System.out.println("Network written to: " + outputPath);

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
