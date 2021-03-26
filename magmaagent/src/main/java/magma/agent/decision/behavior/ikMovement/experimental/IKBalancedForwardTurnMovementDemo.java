/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package magma.agent.decision.behavior.ikMovement.experimental;

import hso.autonomy.util.geometry.Angle;
import magma.agent.decision.behavior.ikMovement.walk.IKDynamicWalkMovement;
import magma.agent.decision.behavior.ikMovement.walk.IKWalkMovementParametersBase;
import magma.agent.decision.behavior.ikMovement.walk.IKWalkMovementParametersBase.Param;
import magma.agent.model.thoughtmodel.IRoboCupThoughtModel;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class IKBalancedForwardTurnMovementDemo extends IKDynamicWalkMovement
{
	public IKBalancedForwardTurnMovementDemo(IRoboCupThoughtModel thoughtModel, IKWalkMovementParametersBase params)
	{
		// super("BalancedForwardDemo", null);
		super(thoughtModel, params);

		params.put(Param.WALK_WIDTH, 0.065f);
		params.put(Param.WALK_HEIGHT, -0.255f);
		params.put(Param.WALK_OFFSET, 0f);
		params.put(Param.PUSHDOWN_FACTOR, 0.5f);
		params.put(Param.FOOT_SLANT_ANGLE, 0);

		currentStep.upward = 0;
		currentStep.forward = 0;

		isStatic = false;
	}

	@Override
	public boolean update()
	{
		if (currentStep.forward < 0.06) {
			currentStep.forward += 0.0005;
		} else {
			currentStep.turn = Angle.deg(-25);
		}

		if (currentStep.upward < 0.02) {
			currentStep.upward += 0.0005;
		}

		return super.update();
	}

	@Override
	public Vector3D getIntendedLeaningVector()
	{
		// return Vector3D.PLUS_K;
		return new Rotation(Vector3D.PLUS_J, Math.toRadians(3), RotationConvention.VECTOR_OPERATOR)
				.applyTo(Vector3D.PLUS_K);
	}
}
