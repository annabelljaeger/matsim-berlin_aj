package org.matsim.analysisHA2;

	import org.matsim.api.core.v01.events.Event;
	import org.matsim.api.core.v01.events.LinkEnterEvent;
	import org.matsim.core.events.handler.BasicEventHandler;
	import org.matsim.core.events.MatsimEventsReader;
	import org.matsim.core.api.experimental.events.EventsManager;
	import org.matsim.core.events.EventsUtils;
	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
	import org.w3c.dom.NodeList;

	import java.io.*;
	import java.util.*;
	import javax.xml.parsers.DocumentBuilder;
	import javax.xml.parsers.DocumentBuilderFactory;

public class RoadCongestionDefinedArea {

	private static final String EVENTS_FILE = "output/berlin-v6.1-bike-first-50IT-10pct/berlin-v6.1-bike-first-50.output_events.xml.gz";
	private static final String DEFINED_LINKS_FILE = "input/v6.1/berlin_distanceAndTime_roadpricing.xml";
	private static final String OUTPUT_CSV_FILE = "output/Netzauslastung15MinIntervall_Bike-first-50IT.csv";

	public static void main(String[] args) {
		try {
			// Schritt 1: Links einlesen
			List<String> validLinks = readLinks(DEFINED_LINKS_FILE);
			// Schritt 2: Ereignisse zählen
			EventCounter eventCounter = new EventCounter(validLinks);
			readEvents(EVENTS_FILE, eventCounter);
			// Schritt 3: Ergebnisse in CSV-Datei speichern
			writeEventsSummaryToCSV(eventCounter.getEventCounts(), OUTPUT_CSV_FILE);
			// Schritt 4: Ergebnisse in der Konsole ausgeben
			printEventsSummaryToConsole(eventCounter.getEventCounts());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readEvents(String filePath, EventCounter eventCounter) {
		// Erstellen Sie eine Instanz des EventsManager
		EventsManager eventsManager = EventsUtils.createEventsManager();
		// Erstellen und registrieren Sie Ihren Event-Handler
		eventsManager.addHandler(eventCounter);
		// Erstellen Sie eine Instanz von MatsimEventsReader
		MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
		// Lesen Sie die Events-Datei ein
		eventsReader.readFile(filePath);

	}

	private static void writeEventsSummaryToCSV(Map<Integer, Integer> eventCounts, String outputPath) throws IOException {
		try (FileWriter fileWriter = new FileWriter(outputPath);
			 PrintWriter printWriter = new PrintWriter(fileWriter)) {

			// CSV-Kopfzeile schreiben
			printWriter.println("Zeitraum,Anzahl der Ereignisse");

			// Ereignisse schreiben
			for (Map.Entry<Integer, Integer> entry : eventCounts.entrySet()) {
				Integer interval = entry.getKey();
				Integer count = entry.getValue();
				printWriter.printf("%d,%s%n", interval, count.toString());
			}
		}
	}

	// Neue Methode zur Konsolenausgabe der Ergebnisse
	private static void printEventsSummaryToConsole(Map<Integer, Integer> eventCounts) {
		System.out.println("Zeitraum\tAnzahl der Ereignisse");
		for (Map.Entry<Integer, Integer> entry : eventCounts.entrySet()) {
			Integer interval = entry.getKey();
			Integer count = entry.getValue();
			System.out.printf("%d\t%d%n", interval, count);
		}
	}

	private static List<String> readLinks(String filePath) throws Exception {
		List<String> linkIds = new ArrayList<>();

		// XML-Datei einlesen
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(filePath));

		// Alle Links auslesen
		NodeList links = document.getElementsByTagName("link");
		for (int i = 0; i < links.getLength(); i++) {
			Element link = (Element) links.item(i);
			String id = link.getAttribute("id");
			linkIds.add(id);
		}

		return linkIds;
	}

	private static class EventCounter implements BasicEventHandler {

		private final List<String> validLinks;
		private final Map<Integer, Integer> eventCounts = new HashMap<>();

		public EventCounter(List<String> validLinks) {
			this.validLinks = validLinks;
		}

		@Override
		public void handleEvent(Event event) {
			// Beispiel für den Umgang mit Events
			double eventTimeSeconds = event.getTime();
			String linkId = event.getAttributes().get("link");

			if (event instanceof LinkEnterEvent && linkId != null && validLinks.contains(linkId)) {
				try {

					// Zeitstempel auf Intervall runden
					int intervalKey = get15MinuteInterval(eventTimeSeconds);
					eventCounts.put(intervalKey, eventCounts.getOrDefault(intervalKey, 0) + 1);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		public Map<Integer, Integer> getEventCounts() {
			return eventCounts;
		}

		private int get15MinuteInterval(double seconds) {
			int interval = (int) (seconds / 60 / 15);
			return interval;
		}
		private int get30MinuteInterval(double seconds) {
			int interval = (int) (seconds / 60 / 30);
			return interval;
		}
	}
}
