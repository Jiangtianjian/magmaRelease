/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package magma.agent.model.thoughtmodel.strategy.impl.strategies;

import magma.agent.model.thoughtmodel.IRoboCupThoughtModel;
import magma.agent.model.thoughtmodel.strategy.impl.formations.TrainingKick8MKickerFormation;
import magma.agent.model.thoughtmodel.strategy.impl.roles.DummyRole;

public class TrainingKick8MKickerStrategy extends BaseStrategy
{
	public static final String NAME = "TrainingKick8MKicker";

	public TrainingKick8MKickerStrategy(IRoboCupThoughtModel thoughtModel)
	{
		super(NAME, thoughtModel);

		ownKickOffFormation = new TrainingKick8MKickerFormation();
		// probably not needed, just to be safe
		opponentKickOffFormation = new TrainingKick8MKickerFormation();

		availableRoles.add(DummyRole.INSTANCE);
	}
}
