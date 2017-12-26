package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.actions.AxisInputAction;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;
import pl.radomiej.mmo.models.specialized.CharacterObject;

public class AxisInputActionExecutor implements ActionExecutor{

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		AxisInputAction axisInputAction = (AxisInputAction) gameAction;
		
		NetworkObject findObject = basicGameEngine.findObjectById(axisInputAction.objectId);
		if(findObject == null) {
			System.out.println("Brak obiektu: " + axisInputAction.objectId);
			return true;
		}
		
		CharacterObject playerObject = (CharacterObject) findObject;
		
		playerObject.horizontal = axisInputAction.horizontal;
		playerObject.vertical = axisInputAction.vertical;
		playerObject.jump = axisInputAction.jump;
		
//		System.out.println("playerObject: " + playerObject);
		return true;
	}

}
