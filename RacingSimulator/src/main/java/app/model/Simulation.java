package app.model;

import java.util.Arrays;
import java.util.List;

import app.model.car.Car;

public class Simulation implements SimulationController {

	public Simulation() {
		
	}
	
	@Override
	public void startSimulation() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void pauseSimulation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetSimulation() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SimulationStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Car> getCars() {
		return Arrays.asList(new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car(),new Car());
	}




	
	
	
}
