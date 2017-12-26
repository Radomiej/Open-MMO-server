package pl.radomiej.mmo;

import pl.radomiej.mmo.models.GameAction;

public interface ActionExecutor {
	/**
	 * 
	 * @param gameAction
	 * @param basicGameEngine 
	 * @return true if remove action
	 */
	public boolean execute(GameAction gameAction, BasicGameEngine basicGameEngine);
}
