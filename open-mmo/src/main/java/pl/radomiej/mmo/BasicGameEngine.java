package pl.radomiej.mmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.radomiej.mmo.actions.AttackAction;
import pl.radomiej.mmo.actions.AxisInputAction;
import pl.radomiej.mmo.actions.CreateCharacterAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.actions.RemoveCharacterAction;
import pl.radomiej.mmo.actions.executors.AttackActionExecutor;
import pl.radomiej.mmo.actions.executors.AxisInputActionExecutor;
import pl.radomiej.mmo.actions.executors.CreateCharacterActionExecutor;
import pl.radomiej.mmo.actions.executors.MoveToActionExecutor;
import pl.radomiej.mmo.actions.executors.RemoveCharacterActionExecutor;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;

public enum BasicGameEngine {
	INSTANCE;

	private List<NetworkObject> objects = Collections.synchronizedList(new ArrayList<NetworkObject>());
	private List<GameAction> actions = Collections.synchronizedList(new ArrayList<GameAction>());

	private Map<Class<? extends GameAction>, ActionExecutor> executors = new HashMap<>();

	private Timer timer;

	public void start() {
		executors.put(CreateCharacterAction.class, new CreateCharacterActionExecutor());
		executors.put(RemoveCharacterAction.class, new RemoveCharacterActionExecutor());
		executors.put(MoveToAction.class, new MoveToActionExecutor());
		executors.put(AxisInputAction.class, new AxisInputActionExecutor());
		executors.put(AttackAction.class, new AttackActionExecutor());

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				update();
			}
		}, 0, (1000 / 60));
	}

	public void update() {

		List<GameAction> toRemove = new LinkedList<>();

		synchronized (actions) {
			for(int x = 0; x < actions.size(); x++){
				GameAction gameAction = actions.get(x);
				// System.out.println("Wykonuje akcje: " +
				// gameAction.getClass().getSimpleName());
				ActionExecutor actionExecutor = executors.get(gameAction.getClass());
				if (actionExecutor == null) {
					toRemove.add(gameAction);
					continue;
				}

				boolean removeAction = actionExecutor.execute(gameAction, this);
				if (removeAction) {
					toRemove.add(gameAction);
				}
			}
			actions.removeAll(toRemove);
		}
	}

	public void addGameAction(GameAction gameAction) {
		synchronized (actions) {
			actions.add(gameAction);
		}
	}

	public NetworkObject findObjectById(int objectId) {
		synchronized (BasicGameEngine.INSTANCE.objects) {
			for (NetworkObject networkObject : objects) {
				if (networkObject.id == objectId) {
					return networkObject;
				}
			}
		}
		return null;
	}

	public void addObject(NetworkObject addObject) {
		synchronized (BasicGameEngine.INSTANCE.objects) {
			objects.add(addObject);
		}
	}

	public void removeObject(int removeObjectId) {
		NetworkObject findObject = findObjectById(removeObjectId);
		synchronized (BasicGameEngine.INSTANCE.objects) {
			objects.remove(findObject);
		}
	}

	public List<NetworkObject> getObjects() {
		return objects;
	}

}
