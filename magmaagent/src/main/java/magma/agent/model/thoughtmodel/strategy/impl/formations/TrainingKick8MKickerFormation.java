/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package magma.agent.model.thoughtmodel.strategy.impl.formations;

import hso.autonomy.util.geometry.Pose2D;

public class TrainingKick8MKickerFormation extends Formation
{
	public TrainingKick8MKickerFormation()
	{
		poses.put(8, new Pose2D(-13f, 0.0f));
	}
}
