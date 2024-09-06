/*
package org.matsim.analysisHA2;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.handler.BasicEventHandler;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Netzauslastung15minInt implements LinkEnterEventHandler {

	private final Collection<SimpleFeature> hundekopfFeatures;
	private final Scenario scenario;
	private final Map<Integer, Integer> vehicleCountsPerTimeBin;

	// 15-Minuten-Intervalle
	private static final int TIME_BIN_SIZE = 900; // 15 Minuten in Sekunden

	public Netzauslastung15minInt(Scenario scenario, URL hundekopfShapefileURL) throws IOException {
		this.scenario = scenario;
		this.hundekopfFeatures = ShapeFileReader.getAllFeatures(hundekopfShapefileURL);
		this.vehicleCountsPerTimeBin = new TreeMap<>();
	}

	public Netzauslastung15minInt() {
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		Link link = scenario.getNetwork().getLinks().get(event.getLinkId());
		int timeBin = (int) (event.getTime() / TIME_BIN_SIZE);

		// Check if the link is inside the Hundekopf area
		if (isLinkInHundekopfArea(link)) {
			vehicleCountsPerTimeBin.put(timeBin, vehicleCountsPerTimeBin.getOrDefault(timeBin, 0) + 1);
		}
	}

	private boolean isLinkInHundekopfArea(Link link) {
		for (SimpleFeature feature : hundekopfFeatures) {
			if (feature.getBounds().contains(link.getCoord().getX(), link.getCoord().getY())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset(int iteration) {
		vehicleCountsPerTimeBin.clear();
	}

	public Map<Integer, Integer> getVehicleCountsPerTimeBin() {
		return vehicleCountsPerTimeBin;
	}

	public static void main(String[] args) {
		Controler controler = new Controler(args);

		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				try {
					// Ersetzen Sie den Pfad durch den tatsächlichen Pfad zu Ihrem Shapefile
					File hundekopfShapefileFile = new File("input/v6.1/hundekopf/hundekopf25832eineForm.shp");
					URL hundekopfShapefileURL = hundekopfShapefileFile.toURI().toURL();
					Netzauslastung15minInt handler = new Netzauslastung15minInt(controler.getScenario(), hundekopfShapefileURL);
					addEventHandlerBinding().toInstance(handler);
					controler.addControlerListener(new EventHandlerListener(handler));
				} catch (MalformedURLException e) {
					throw new RuntimeException("Fehler beim Erstellen der URL für das Shapefile", e);
				} catch (IOException e) {
					throw new RuntimeException("Fehler beim Laden des Shapefiles", e);
				}
			}
		});

		controler.run();
	}

	private static class EventHandlerListener implements BasicEventHandler {
		private final Netzauslastung15minInt handler;

		public EventHandlerListener(Netzauslastung15minInt handler) {
			this.handler = handler;
		}

		@Override
		public void handleEvent(Event event) {
			if (event.getEventType().equals("iterationEnds")) {
				// Nach jeder Iteration die Ergebnisse ausgeben
				System.out.println("Vehicle counts per 15-minute interval in Hundekopf area:");
				for (Map.Entry<Integer, Integer> entry : handler.getVehicleCountsPerTimeBin().entrySet()) {
					System.out.println("Time Bin: " + entry.getKey() * 15 + " minutes, Count: " + entry.getValue());
				}
			}
		}
	}
}
*/
