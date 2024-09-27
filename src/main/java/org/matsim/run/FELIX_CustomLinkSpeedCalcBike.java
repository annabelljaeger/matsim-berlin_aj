package org.matsim.run;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;

public class FELIX_CustomLinkSpeedCalcBike extends AbstractModule implements TravelTime {

	@Override
	public double getLinkTravelTime(Link link, double v, Person person, Vehicle vehicle) {

		String mode = vehicle.getType().getId().toString();

		double speed;

		switch (mode) {

			case "bike":
				if (link.getAttributes().getAttribute("bicycleInfrastructureSpeedFactor") != null ){
					speed = 20 / 3.6;  // Limit bike speed
					break;
				}
				else
					speed = 3.3; //from Base Case ~ 12 km/h
				break;

			case "e-bike":
				if (link.getAttributes().getAttribute("bicycleInfrastructureSpeedFactor") != null ){
					speed = 25 / 3.6;
					break;
				}
				else
					speed = 4.5; //from Base Case ~ 16 km/h
				break;

			default:
				speed = link.getFreespeed();  // Default free speed for cars
				break;

		}

		return link.getLength() / speed; // Travel time

	}

	@Override
	public void install() {

	}
}
