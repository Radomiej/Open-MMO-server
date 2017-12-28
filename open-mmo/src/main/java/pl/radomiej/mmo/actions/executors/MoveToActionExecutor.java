package pl.radomiej.mmo.actions.executors;

import pl.radomiej.mmo.ActionExecutor;
import pl.radomiej.mmo.BasicGameEngine;
import pl.radomiej.mmo.actions.MoveToAction;
import pl.radomiej.mmo.models.GameAction;
import pl.radomiej.mmo.models.NetworkObject;
import pl.radomiej.mmo.models.specialized.GeoObject;

public class MoveToActionExecutor implements ActionExecutor{

	public static float DELTA_EPSILON = 0.05f;
	@Override
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine) {
		MoveToAction moveToAction = (MoveToAction) gameAction;
		
		NetworkObject findObject = basicGameEngine.findObjectById(moveToAction.objectId);
		if(findObject == null) return true;
		
		GeoObject geoObject = (GeoObject) findObject;
		
		boolean chnaged = false;
		
//		System.out.println("x: " + Math.abs(geoObject.x - moveToAction.x) + " y: " + Math.abs(geoObject.y - moveToAction.y) + " z: " + Math.abs(geoObject.z - moveToAction.z) );
//		System.out.println("rx: " + Math.abs(geoObject.getRotX() - moveToAction.rotX) + " ry: " + Math.abs(geoObject.getRotY() - moveToAction.rotY) + " rz: " + Math.abs(geoObject.getRotZ() - moveToAction.rotZ) );
		
		if(Math.abs(geoObject.x - moveToAction.x) > DELTA_EPSILON) chnaged = true;
		else if(Math.abs(geoObject.y - moveToAction.y) > DELTA_EPSILON) chnaged = true;
		else if(Math.abs(geoObject.z - moveToAction.z) > DELTA_EPSILON) chnaged = true;
		else if(Math.abs(geoObject.getRotX() - moveToAction.rotX) > DELTA_EPSILON) chnaged = true;
		else if(Math.abs(geoObject.getRotY() - moveToAction.rotY) > DELTA_EPSILON) chnaged = true;
		else if(Math.abs(geoObject.getRotZ() - moveToAction.rotZ) > DELTA_EPSILON) chnaged = true;	
			
			
//		if(geoObject.x != moveToAction.x) chnaged = true;
//		else if(geoObject.y != moveToAction.y) chnaged = true;
//		else if(geoObject.z != moveToAction.z) chnaged = true;
//		else if(geoObject.getRotX() != moveToAction.rotX) chnaged = true;
//		else if(geoObject.getRotY() != moveToAction.rotY) chnaged = true;
//		else if(geoObject.getRotZ() != moveToAction.rotZ) chnaged = true;
		
		if(!chnaged) {
//			System.out.println("Dane pozycji nie zmienione");
			return true;
		}
		
		geoObject.x = moveToAction.x;
		geoObject.y = moveToAction.y;
		geoObject.z = moveToAction.z;
		geoObject.setRotX(moveToAction.rotX);
		geoObject.setRotY(moveToAction.rotY);
		geoObject.setRotZ(moveToAction.rotZ);
		
//		geoObject.rotX = moveToAction.rotX;
//		geoObject.rotY = moveToAction.rotY;
//		geoObject.rotZ = moveToAction.rotZ;
		
		geoObject.setGeoChanged();
		
		return true;
	}

}
