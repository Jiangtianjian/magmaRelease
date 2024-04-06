/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package magma.robots.nao.general.agentruntime;

import hso.autonomy.agent.decision.behavior.BehaviorMap;
import hso.autonomy.agent.model.agentmodel.IAgentModel;
import hso.autonomy.agent.model.worldmodel.IWorldModel;
import hso.autonomy.util.geometry.Angle;
import kdo.util.parameter.ParameterMap;
import magma.agent.UglyConstants;
import magma.agent.decision.behavior.IBaseWalk;
import magma.agent.decision.behavior.IBehaviorConstants;
import magma.agent.decision.behavior.IWalkEstimator;
import magma.agent.decision.behavior.basic.SendPassCommand;
import magma.agent.decision.behavior.complex.goalie.GoaliePositioning;
import magma.agent.decision.behavior.complex.kick.StabilizedKick;
import magma.agent.decision.behavior.complex.misc.*;
import magma.agent.decision.behavior.complex.path.WalkPath;
import magma.agent.decision.behavior.complex.walk.*;
import magma.agent.decision.behavior.ikMovement.*;
import magma.agent.decision.behavior.ikMovement.walk.IKWalkMovementParametersBase;
import magma.agent.decision.behavior.movement.SidedMovementBehavior;
import magma.agent.model.agentmodel.IRoboCupAgentModel;
import magma.agent.model.thoughtmodel.IKickPositionProfiler;
import magma.agent.model.thoughtmodel.IRoboCupThoughtModel;
import magma.agent.model.thoughtmodel.impl.RoboCupThoughtModel;
import magma.agent.model.thoughtmodel.impl.RoboCupThoughtModelThin;
import magma.agent.model.thoughtmodel.impl.TrainingKick8MPositionProfiler;
import magma.agent.model.thoughtmodel.strategy.IRoleManager;
import magma.agent.model.thoughtmodel.strategy.impl.RoleManager;
import magma.agent.model.thoughtmodel.strategy.impl.strategies.TrainingKick8MKickerStrategy;
import magma.agent.model.worldmeta.IRoboCupWorldMetaModel;
import magma.agent.model.worldmodel.IRoboCupWorldModel;
import magma.agent.model.worldmodel.impl.RoboCupWorldModel;
import magma.robots.nao.decision.behavior.dynamic.FocusBall;
import magma.robots.nao.decision.behavior.dynamic.FocusBallGoalie;
import magma.robots.nao.decision.behavior.movement.GetReady;
import magma.robots.nao.decision.behavior.movement.MoveZero;
import magma.robots.nao.decision.behavior.movement.fall.FallBack;
import magma.robots.nao.decision.behavior.movement.fall.FallForward;
import magma.robots.nao.decision.behavior.movement.fall.FallSide;
import magma.robots.nao.decision.behavior.movement.fall.MoveArmsToFall;
import magma.robots.nao.decision.behavior.movement.fullsearch.kickwalk.*;
import magma.robots.nao.decision.behavior.movement.getup.GetUpFromBack;
import magma.robots.nao.decision.behavior.movement.getup.GetUpFromBackParameters;
import magma.robots.nao.decision.behavior.movement.getup.GetUpFromFront;
import magma.robots.nao.decision.behavior.movement.getup.GetUpFromFrontParameters;
import magma.robots.nao.decision.behavior.movement.keep.KeepCenter;
import magma.robots.nao.decision.behavior.movement.keep.KeepSide;
import magma.robots.nao.decision.behavior.movement.kick.Kick11m;
import magma.robots.nao.decision.behavior.movement.kick.Kick11mParameters;
import magma.robots.nao.decision.behavior.movement.kick.Kick8m;
import magma.robots.nao.decision.behavior.movement.kick.TrainingKick8mParameters;
import magma.util.roboviz.RoboVizDraw;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TrainingKick8MComponentFactory extends NaoComponentFactory
{
	public static final String NAME = "TrainingKick8M";

	@Override
	protected List<String> createDefaultAvailableKicks(BehaviorMap behaviors)
	{
		List<String> kicks = new ArrayList<>();
		addKick(behaviors, kicks, IBehaviorConstants.KICK_8M.FULL);
		return kicks;
	}

	@Override
	public IRoboCupWorldModel createWorldModel(
			IRoboCupAgentModel agentModel, IRoboCupWorldMetaModel worldMetaModel, String teamName, int playerNumber)
	{
		RoboCupWorldModel worldModel =
				new RoboCupWorldModel(agentModel, createLocalizer(agentModel), worldMetaModel, teamName, playerNumber) {
					@Override
					public boolean isInCriticalArea(Vector3D position)
					{
						return position.getX() < -fieldHalfLength() + penaltyWidth() - 0.3 &&
								Math.abs(position.getY()) < penaltyHalfLength() + goalHalfWidth() - 0.3;
					}
				};
		return worldModel;
	}

	@Override
	public IRoboCupThoughtModel createThoughtModel(
			IAgentModel agentModel, IRoboCupWorldModel worldModel, RoboVizDraw roboVizDraw)
	{
		if (UglyConstants.thinClient) {
			return new RoboCupThoughtModelThin(agentModel, worldModel, roboVizDraw) {
				@Override
				protected void updateTeamStrategy()
				{

				}
			};
		} else {
			return new RoboCupThoughtModel(agentModel, worldModel, roboVizDraw) {
				@Override
				protected void updateTeamStrategy()
				{
				}
			};
		}
	}

	@Override
	public IRoleManager createRoleManager(
			IRoboCupThoughtModel thoughtModel, IWorldModel worldModel, String teamStrategyName)
	{
		return new RoleManager(worldModel, new TrainingKick8MKickerStrategy(thoughtModel));
	}

	@Override
	public IKickPositionProfiler createKickPositionProfiler(IRoboCupThoughtModel thoughtModel)
	{
		return new TrainingKick8MPositionProfiler(thoughtModel);
	}

	@Override
	protected void createSpecificBehaviors(
			IRoboCupThoughtModel thoughtModel, ParameterMap params, BehaviorMap behaviors)
	{
		// General behaviors
		behaviors.put(new GetReady(thoughtModel));
		behaviors.put(new MoveZero(thoughtModel));
		behaviors.put(new SwingArms(thoughtModel));
		behaviors.put(new SearchBall(thoughtModel, behaviors));
		behaviors.put(new FocusBall(thoughtModel, behaviors));
		behaviors.put(new FocusBallGoalie(thoughtModel));
		behaviors.put(new SendPassCommand(thoughtModel));

		// GetUp behaviors
		behaviors.put(new GetUpFromBack(thoughtModel, params));
		behaviors.put(new GetUpFromFront(thoughtModel, params));
		behaviors.put(new MoveArmsToFall(thoughtModel));
		behaviors.put(new FallBack(thoughtModel));
		behaviors.put(new FallForward(thoughtModel));
		behaviors.put(new FallSide(thoughtModel));

		// Movement behaviors
		IBaseWalk walkBehavior = createWalk(thoughtModel, params, behaviors);
		behaviors.put(new Walk(thoughtModel, behaviors, walkBehavior));

		behaviors.put(new WalkToPosition(thoughtModel, behaviors, createWalkEstimator()));
		behaviors.put(new IKStepWalkBehavior(thoughtModel, params, behaviors));

		IBaseWalk walkPathBehavior = new IKStepPlanBehavior(thoughtModel, params, behaviors);
		behaviors.put(walkPathBehavior);
		behaviors.put(new WalkPath(thoughtModel, params, behaviors, walkPathBehavior));

		// Positioning behaviors
		behaviors.put(new PassivePositioning(thoughtModel, behaviors));
		behaviors.put(new GoaliePositioning(thoughtModel, behaviors));

		// Kick behaviors
		IWalkEstimator walkEstimator = createWalkEstimator();
		final float opponentDistanceFar = 2.2f;
		float opponentDistanceMedium = 1.5f;

		behaviors.put(IKKickBehavior.getKickStabilizationLeft(KICK_8M.KICK.BASE_NAME, KICK_8M.STABILIZE, thoughtModel,
				params, opponentDistanceMedium, Kick8m.MAX_KICK_DISTANCE, walkEstimator));
		behaviors.put(IKKickBehavior.getKickStabilizationRight(KICK_8M.KICK.BASE_NAME, KICK_8M.STABILIZE, thoughtModel,
				params, opponentDistanceMedium, Kick8m.MAX_KICK_DISTANCE, walkEstimator));

		behaviors.put(IKKickBehavior.getKickStabilizationLeft(KICK_11M.KICK.BASE_NAME, KICK_11M.STABILIZE, thoughtModel,
				params, opponentDistanceFar, Kick11m.MAX_KICK_DISTANCE, walkEstimator));
		behaviors.put(IKKickBehavior.getKickStabilizationRight(KICK_11M.KICK.BASE_NAME, KICK_11M.STABILIZE,
				thoughtModel, params, opponentDistanceFar, Kick11m.MAX_KICK_DISTANCE, walkEstimator));


		behaviors.put(new Kick8m(SidedMovementBehavior.Side.LEFT, thoughtModel, params));
		behaviors.put(new Kick8m(SidedMovementBehavior.Side.RIGHT, thoughtModel, params));
		behaviors.put(new Kick11m(SidedMovementBehavior.Side.LEFT, thoughtModel, params));
		behaviors.put(new Kick11m(SidedMovementBehavior.Side.RIGHT, thoughtModel, params));


		behaviors.put(KickWalk.instance(SidedMovementBehavior.Side.LEFT, thoughtModel, params, walkEstimator));
		behaviors.put(KickWalk.instance(SidedMovementBehavior.Side.RIGHT, thoughtModel, params, walkEstimator));
		behaviors.put(
				KickWalkStanding.instance(SidedMovementBehavior.Side.LEFT, thoughtModel, params, opponentDistanceMedium, walkEstimator));
		behaviors.put(
				KickWalkStanding.instance(SidedMovementBehavior.Side.RIGHT, thoughtModel, params, opponentDistanceMedium, walkEstimator));
		behaviors.put(KickWalkSide.instance(SidedMovementBehavior.Side.LEFT, thoughtModel, params, opponentDistanceMedium, walkEstimator));
		behaviors.put(KickWalkSide.instance(SidedMovementBehavior.Side.RIGHT, thoughtModel, params, opponentDistanceMedium, walkEstimator));
		behaviors.put(
				KickWalkBackward.instance(SidedMovementBehavior.Side.LEFT, thoughtModel, params, opponentDistanceMedium, walkEstimator));
		behaviors.put(
				KickWalkBackward.instance(SidedMovementBehavior.Side.RIGHT, thoughtModel, params, opponentDistanceMedium, walkEstimator));

		opponentDistanceMedium -= 1;

		// Keep behaviors
		behaviors.put(new KeepSide(SidedMovementBehavior.Side.LEFT, thoughtModel));
		behaviors.put(new KeepSide(SidedMovementBehavior.Side.RIGHT, thoughtModel));
		behaviors.put(new KeepCenter(thoughtModel));

		// Other behaviors
		behaviors.put(Dribbling.getLeftVersion(
				DRIBBLE.LEFT, thoughtModel, behaviors, opponentDistanceMedium, Angle.ZERO, walkEstimator));
		behaviors.put(Dribbling.getRightVersion(
				DRIBBLE.RIGHT, thoughtModel, behaviors, opponentDistanceMedium, Angle.ZERO, walkEstimator));

		for (int i = 30; i <= 120; i += 30) {
			behaviors.put(Dribbling.getLeftVersion(
					DRIBBLE.LEFT + i, thoughtModel, behaviors, opponentDistanceMedium, Angle.deg(i), walkEstimator));
			behaviors.put(Dribbling.getRightVersion(
					DRIBBLE.RIGHT + i, thoughtModel, behaviors, opponentDistanceMedium, Angle.deg(i), walkEstimator));
			behaviors.put(Dribbling.getLeftVersion(DRIBBLE.LEFT + (-i), thoughtModel, behaviors, opponentDistanceMedium,
					Angle.deg(-i), walkEstimator));
			behaviors.put(Dribbling.getRightVersion(DRIBBLE.RIGHT + (-i), thoughtModel, behaviors,
					opponentDistanceMedium, Angle.deg(-i), walkEstimator));
		}

		Consumer<StabilizedKickConstants> createStabilizedKick = constants ->
		{
			behaviors.put(new StabilizedKick(constants.FULL.LEFT, thoughtModel, params, behaviors,
					constants.STABILIZE.LEFT, constants.KICK.LEFT));
			behaviors.put(new StabilizedKick(constants.FULL.RIGHT, thoughtModel, params, behaviors,
					constants.STABILIZE.RIGHT, constants.KICK.RIGHT));
		};
		createStabilizedKick.accept(KICK_8M);
		createStabilizedKick.accept(KICK_11M);

		List<String> defaultAvailableKicks = createDefaultAvailableKicks(behaviors);
		if (UglyConstants.thinClient) {
			behaviors.put(new AttackThin(thoughtModel, behaviors, defaultAvailableKicks));
		} else {
			behaviors.put(new Attack(thoughtModel, behaviors, defaultAvailableKicks));
		}

		behaviors.put(new Kick_off(thoughtModel, behaviors, defaultAvailableKicks));

		behaviors.put(new KickChallengeAttack(thoughtModel, behaviors, defaultAvailableKicks));

		List<String> passingKicks = new ArrayList<>();
		addKick(behaviors, passingKicks, IBehaviorConstants.KICK_8M.FULL);
		behaviors.put(new PassingChallengeAttack(thoughtModel, behaviors, passingKicks));

		// Experimental behaviors
		behaviors.put(new SimpleIKMovementBehavior(thoughtModel, params));
		behaviors.put(IKStabilizeOnLegBehavior.getStabilizeOnLeftLeg(thoughtModel, params));
		behaviors.put(IKStabilizeOnLegBehavior.getStabilizeOnRightLeg(thoughtModel, params));
	}

	@Override
	protected ParameterMap createSpecificParameters() {
		System.out.println("Here we Normal run");
		ParameterMap result = new ParameterMap();

		result.put(IK_WALK_STEP, new IKWalkMovementParametersBase());
		result.put(IK_WALK, new IKWalkMovementParametersBase());
		result.put(IK_STEP_PLAN, new IKWalkMovementParametersBase());
		result.put(IK_MOVEMENT, new IKWalkMovementParametersBase());

		result.put(STABILIZE.BASE_NAME, new IKWalkMovementParametersBase());

		result.put(KICK_8M.KICK.BASE_NAME, new TrainingKick8mParameters());
		result.put(KICK_8M.STABILIZE.BASE_NAME, new IKWalkMovementParametersBase());

		result.put(KICK_11M.KICK.BASE_NAME, new Kick11mParameters());
		result.put(KICK_11M.STABILIZE.BASE_NAME, new IKWalkMovementParametersBase());

		result.put(KICK_WALK.BASE_NAME, ParameterListComposite.fromSingle(KickWalkParameters.instance()));
		result.put(
				KICK_WALK_STANDING.BASE_NAME, ParameterListComposite.fromSingle(KickWalkStandingParameters.instance()));

		// result.put(KICK_WALK_STANDING.BASE_NAME, new KickWalkStandingGridParameters());
		// result.put(KICK_WALK.BASE_NAME, new KickWalkStandingGridParameters());

		result.put(KICK_WALK_SIDE.BASE_NAME, ParameterListComposite.fromSingle(KickWalkSideParameters.instance()));
		result.put(
				KICK_WALK_BACKWARD.BASE_NAME, ParameterListComposite.fromSingle(KickWalkBackwardParameters.instance()));

		result.put(GET_UP_BACK, new GetUpFromBackParameters());
		result.put(GET_UP_FRONT, new GetUpFromFrontParameters());



		return result;
	}

}