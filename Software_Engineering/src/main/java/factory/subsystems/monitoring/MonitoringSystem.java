package factory.subsystems.monitoring;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import app.gui.GUIHandler;
import factory.shared.AbstractSubsystem;
import factory.shared.FactoryEvent;
import factory.shared.Position;
import factory.shared.ResourceBox;
import factory.shared.enums.EventKind;
import factory.shared.enums.EventKind.EventSeverity;
import factory.shared.enums.SubsystemStatus;
import factory.shared.interfaces.Monitorable;
import factory.subsystems.agv.AgvCoordinator;
import factory.subsystems.agv.AgvTask;
import factory.subsystems.monitoring.interfaces.MonitoringInterface;
import factory.subsystems.monitoring.onlineshop.Order;
import factory.subsystems.warehouse.StorageSite;
import factory.subsystems.warehouse.WarehouseSystem;
import factory.subsystems.warehouse.WarehouseTask;

public class MonitoringSystem implements MonitoringInterface {
	private static final Logger LOGGER = Logger.getLogger(MonitoringSystem.class.getName());

	private final GUIHandler handler;
	private final ErrorEventHandler errorHandler;

	private final List<Order> orderList;
	private final List<WarehouseTask> warehouseTaskList;
	private final List<AgvTask> agvTaskList;

	private SubsystemStatus status;
	private AgvCoordinator agvSystem;
	private WarehouseSystem warehouseSystem;
	
	private ResourceBox shippingBox;
	private Position shippingBoxPosition;

	public MonitoringSystem() {
		this.handler = new GUIHandler(this);
		this.errorHandler = new ErrorEventHandler(this);
		this.orderList = new ArrayList<>();
		this.warehouseTaskList = new ArrayList<>();
		this.agvTaskList = new ArrayList<>();
	}

	@Override
	public synchronized void handleEvent(FactoryEvent event) {
		try {
			LOGGER.log(INFO, String.format("handling event %s ...", event));
			Monitorable source = event.getSource();
			EventKind eventKind = event.getKind();
			EventSeverity severity = eventKind.severity;
			switch (severity) {
			case GLOBAL_EROR:
				this.getErrorHandler().handleGlobalError(event);
				this.setStatus(SubsystemStatus.BROKEN);
				break;
			case ERROR:
				getErrorHandler().handleError(source, eventKind);
				break;
			case IMPORTANT:
				break;
			case INFO:
				break;
			case NORMAL:
				break;
			default:
				break;
			}
		} catch (Exception ex) {
			handleEventHandlingException(event, ex);
		}
	}

	@Override
	public void start() {
		Objects.requireNonNull(this.agvSystem);// TODO @thomas throw exception
		Objects.requireNonNull(this.warehouseSystem);

		this.agvSystem.start();
		this.warehouseSystem.start();

		this.handler.start();
		this.setStatus(SubsystemStatus.RUNNING);
	}

	@Override
	public void stop() {
		try {
			this.agvSystem.stop();
			this.warehouseSystem.stop();
		} catch (Exception ex) {
			LOGGER.log(SEVERE, ex.toString(), ex);
		}
		this.setStatus(SubsystemStatus.STOPPED);
	}

	@Override
	public void addOrder(Order order) {
		LOGGER.log(INFO, "order placed: "+order);
		this.orderList.add(order);
		handleNewOrder(order);
	}

	private void handleNewOrder(Order order) {
		WarehouseTask wht = new WarehouseTask();
		StorageSite taskHandlingStorageSite = warehouseSystem.receiveTask(wht);
		System.out.println("added warehouse task "+wht);
		warehouseTaskList.add(wht);

		Position pickUpPosition = taskHandlingStorageSite.getPosition();
		Position dropOffPosition = new Position(100, 100);
		AgvTask agvTask = new AgvTask(new ResourceBox(), pickUpPosition, dropOffPosition);
		agvSystem.addTask(agvTask);
		System.out.println("added agv task "+agvTask);
		agvTaskList.add(agvTask);
	}

	private void handleEventHandlingException(FactoryEvent event, Exception ex) {
		LOGGER.log(SEVERE, ex.toString(), ex);
		getErrorHandler().handleGlobalError(event);
		this.setStatus(SubsystemStatus.BROKEN);
	}

	private ErrorEventHandler getErrorHandler() { 
		return errorHandler;
	}

	@Override
	public SubsystemStatus getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(SubsystemStatus status) {
		LOGGER.log(INFO, String.format("Status set to %s", status));
		this.status = status;
	}

	@Override
	public void setCurrentSubsystemToShow(AbstractSubsystem subsystem) {
		this.handler.setCurrentSubsystem(subsystem);
	}

	@Override
	public AgvCoordinator getAgvSystem() {
		return agvSystem;
	}

	@Override
	public void setAgvSystem(AgvCoordinator agvSystem) {
		this.handler.addToFactoryPanel(agvSystem);
		this.agvSystem = agvSystem;
	}

	@Override
	public WarehouseSystem getWarehouseSystem() {
		return warehouseSystem;
	}

	@Override
	public void setWarehouseSystem(WarehouseSystem warehouseSystem) {
		this.handler.addToFactoryPanel(warehouseSystem);
		this.warehouseSystem = warehouseSystem;
	}

	@Override
	public List<WarehouseTask> getWarehouseTaskList() {
		return warehouseTaskList;
	}

	@Override
	public List<AgvTask> getAgvTaskList() {
		return agvTaskList;
	}

	@Override
	public ResourceBox getShippingBox() {
		return shippingBox;
	}

	@Override
	public void setShippingBox(ResourceBox shippingBox) {
		this.shippingBox = shippingBox;
	}

	@Override
	public Position getShippingBoxPosition() {
		return shippingBoxPosition;
	}

	@Override
	public void setShippingBoxPosition(Position shippingBoxPosition) {
		this.shippingBoxPosition = shippingBoxPosition;
	}

	
	
}
