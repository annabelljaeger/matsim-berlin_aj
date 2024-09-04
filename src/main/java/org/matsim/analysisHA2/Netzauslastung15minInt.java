package org.matsim.analysisHA2;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.ControlerDefaults;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.handler.BasicEventHandler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public class Netzauslastung15minInt implements LinkEnterEventHandler {

	private final Map<Integer, Integer> vehicleCountsPerTimeBin;
	private static final int TIME_BIN_SIZE = 900; // 15 Minuten in Sekunden

	public Netzauslastung15minInt() {
		this.vehicleCountsPerTimeBin = new TreeMap<>();
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		int timeBin = (int) (event.getTime() / TIME_BIN_SIZE);
		vehicleCountsPerTimeBin.put(timeBin, vehicleCountsPerTimeBin.getOrDefault(timeBin, 0) + 1);
	}

	@Override
	public void reset(int iteration) {
		vehicleCountsPerTimeBin.clear();
	}

	public Map<Integer, Integer> getVehicleCountsPerTimeBin() {
		return vehicleCountsPerTimeBin;
	}

	public static void main(String[] args) {
		// Pfad zur MATSim-Konfigurationsdatei direkt im Code angeben
		String configFilePath = "C:\\Users\\jamps\\MatSim\\BerlinHA2\\input\\v6.1\\berlin-v6.1.config.xml"; // Ändern Sie dies auf den Pfad zu Ihrer Konfigurationsdatei

		// Laden der Konfiguration
		Config config = ConfigUtils.loadConfig(configFilePath);

		// Erstellen des Controlers
		Controler controler = new Controler(config);

		// Handler hinzufügen
		Netzauslastung15minInt handler = new Netzauslastung15minInt();
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addEventHandlerBinding().toInstance(handler);
			}
		});

		// Starten der Simulation
		controler.run();

		// Ergebnisse nach Abschluss der Simulation ausgeben
		System.out.println("Vehicle counts per 15-minute interval:");
		for (Map.Entry<Integer, Integer> entry : handler.getVehicleCountsPerTimeBin().entrySet()) {
			System.out.println("Time Bin: " + (entry.getKey() * 15) + " minutes, Count: " + entry.getValue());
		}
	}
}
