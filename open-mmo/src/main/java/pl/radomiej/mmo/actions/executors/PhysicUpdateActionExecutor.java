package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.ServerSettings;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.actions.PhysicUpdateAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;

public class PhysicUpdateActionExecutor implements ActionExecutor{

	public static float DELTA_EPSILON = 0.01f;
	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		PhysicUpdateAction physicUpdateToAction = (PhysicUpdateAction) gameAction;
		
		NetworkObject findObject = basicGameEngine.findObjectById(physicUpdateToAction.objectId);
		if(findObject == null) return true;
		
		GeoObject geoObject = (GeoObject) findObject;
		
		boolean changed = false;
		
		
		if(Math.abs(geoObject.x - physicUpdateToAction.x) > DELTA_EPSILON) changed = true;
		else if(Math.abs(geoObject.y - physicUpdateToAction.y) > DELTA_EPSILON) changed = true;
		else if(Math.abs(geoObject.z - physicUpdateToAction.z) > DELTA_EPSILON) changed = true;
			
			
		
		if(!changed && ServerSettings.CURRENT.sendOnlyChangedGeoData) {
			return true;
		}
		
		geoObject.velX = physicUpdateToAction.x;
		geoObject.velY = physicUpdateToAction.y;
		geoObject.velZ = physicUpdateToAction.z;
//		geoObject.setRotX(physicUpdateToAction.rotX);
//		geoObject.setRotY(physicUpdateToAction.rotY);
//		geoObject.setRotZ(physicUpdateToAction.rotZ);
		
		
		geoObject.setPhysicChanged();
		
		return true;
	}

}
