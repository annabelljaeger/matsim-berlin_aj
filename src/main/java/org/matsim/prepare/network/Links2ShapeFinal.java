package org.matsim.prepare.network;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.utils.gis.matsim2esri.network.FeatureGeneratorBuilderImpl;
import org.matsim.utils.gis.matsim2esri.network.LanesBasedWidthCalculator;
import org.matsim.utils.gis.matsim2esri.network.LineStringBasedFeatureGenerator;
import org.matsim.utils.gis.matsim2esri.network.Links2ESRIShape;

public class Links2ShapeFinal {

	static final Network network = NetworkUtils.readNetwork("./input/v6.1/berlin-v6.1-network-bike-e_bike-added.xml.gz");
	static final String outputFile = "./src/main/java/org/matsim/prepare/network/NetworkToShape/Links2Shp.shp";
	static final String crs = "EPSG:25832";

	public static void main(String[] args) {
		FeatureGeneratorBuilderImpl builder = new FeatureGeneratorBuilderImpl(network, crs);
		builder.setFeatureGeneratorPrototype(LineStringBasedFeatureGenerator.class);
		builder.setWidthCoefficient(0.5);
		builder.setWidthCalculatorPrototype(LanesBasedWidthCalculator.class);
		(new Links2ESRIShape(network, outputFile, builder)).write();
	}

}
