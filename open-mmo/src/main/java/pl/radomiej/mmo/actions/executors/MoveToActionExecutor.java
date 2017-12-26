package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;

public class MoveToActionExecutor implements ActionExecutor{

	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		MoveToAction moveToAction = (MoveToAction) gameAction;
		
		NetworkObject findObject = basicGameEngine.findObjectById(moveToAction.objectId);
		if(findObject == null) return true;
		
		GeoObject geoObject = (GeoObject) findObject;
		
		geoObject.x = moveToAction.x;
		geoObject.y = moveToAction.y;
		geoObject.z = moveToAction.z;
		geoObject.setRotX(moveToAction.rotX);
		geoObject.setRotY(moveToAction.rotY);
		geoObject.setRotZ(moveToAction.rotZ);
		
//		geoObject.rotX = moveToAction.rotX;
//		geoObject.rotY = moveToAction.rotY;
//		geoObject.rotZ = moveToAction.rotZ;
		
		return true;
	}

}
