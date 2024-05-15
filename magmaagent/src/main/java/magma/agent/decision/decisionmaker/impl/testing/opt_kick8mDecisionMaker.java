package magma.agent.decision.decisionmaker.impl.testing;

import hso.autonomy.agent.decision.behavior.BehaviorMap;
import hso.autonomy.util.geometry.Area2D;
import hso.autonomy.util.geometry.Pose2D;
import hso.autonomy.util.properties.PropertyWriter;
import magma.agent.agentruntime.PlayerParameters;
import magma.agent.decision.behavior.IBeam;
import magma.agent.decision.behavior.IBehaviorConstants;
import magma.agent.decision.decisionmaker.impl.SoccerDecisionMaker;
import magma.util.roboviz.RoboVizParameters;
import magma.agent.model.thoughtmodel.IRoboCupThoughtModel;
import magma.agent.model.worldmodel.IBall;
import magma.util.train_server.ITrainServer;
import magma.util.train_server.impl.PlayModeParameters;
import magma.util.roboviz.RoboVizPort;
import magma.util.train_server.impl.TrainServer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.IOException;
import java.util.Random;

import static java.lang.Math.abs;

public class opt_kick8mDecisionMaker extends SoccerDecisionMaker {

    static {
        lastballpos = new Vector3D(0,0,0);
        lasttime = 0;
    }

    public enum Opt_State {
        RESET,
        MOVING,
        CALCULATE_FITNESS,
        WAIT,
        DONE
    }
    protected ITrainServer TrainServer;
    private  int CalculateTime;
    private  int WaitTime;
    protected boolean haveBeamed;
    private Vector3D resetBallPos;
    private int CoolTime = 0;
    private int runtime;
    private double fitness;
    private static Vector3D lastballpos;
    private static double lasttime;
    private int MovingBufferTime;
    private double kickstartime;
    private double kickBias;
    private double kickhigh;
    private double kickdis;

    private double kicktimetake;
    private boolean fallen;
    private Opt_State AgentState;
    boolean isReset;
    int TimeCost;
    boolean outoftime;
    boolean ballisstop;

    Random rand = new Random();

    public opt_kick8mDecisionMaker(BehaviorMap behaviors, IRoboCupThoughtModel thoughtModel) {
        super(behaviors, thoughtModel);
        this.isReset = false;
        this.AgentState = Opt_State.RESET;
        this.CoolTime = 0;
        this.CalculateTime = 0;
        this.TimeCost = 0;
        this.MovingBufferTime = 0;
        this.WaitTime = 0;
        this.runtime = 0;
        this.TrainServer = new TrainServer("localhost", RoboVizPort.monitorport);
        lastballpos = new Vector3D(0,0,0);
        lasttime = getWorldModel().getGameTime();
        this.fitness = 0;
        this.kickhigh = 0;
        this.kickdis = 0;
        this.kickstartime=0;
        this.outoftime=false;
        this.ballisstop=true;
    }


    @Override
    protected String beamHome() {
        if (!haveBeamed) {
            haveBeamed = true;
            ((IBeam) behaviors.get(IBehaviorConstants.BEAM_TO_POSITION)).setPose(new Pose2D(-10f, 0.0f));
            return IBehaviorConstants.BEAM_TO_POSITION;
        }
        return null;
    }

    @Override
    protected String reactToGameEnd() {
        return null;
    }

    @Override
    protected String waitForGameStart() {
        return null;
    }
    @Override
    protected String searchBall() {
        return null;
    }

    @Override
    protected String getReady() {
        return null;
    }

    @Override
    protected String move() {

        if (this.AgentState == Opt_State.RESET) {
            this.CoolTime = this.CoolTime + 1;
            if (!this.isReset) {
                // Connect to Simspark
                this.TrainServer.connect();
                this.TrainServer.setPlayMode(PlayModeParameters.PlayOn.getName());

                // Move the ball back
                this.resetBallPos = new Vector3D(-9, 0, 0);
                this.TrainServer.moveBall(this.resetBallPos);
                double Rotate = (45 * (this.runtime - 1));

                // Move the agent to different places in order to check kick in several conditions
                this.TrainServer.moveAgent(new Vector3D(-10 - 2f * Math.cos(Rotate), -2f * Math.sin(Rotate), 0.3), true, getWorldModel().getThisPlayer().getID());
                this.TrainServer.disconnect();
                this.isReset = true;

                // Init the judgement variables
                this.kickdis = 0;
                this.kickhigh = 0;
                this.kickstartime = getWorldModel().getGameTime();
                this.kickBias = 0;
                this.outoftime =false;
                this.ballisstop = true;
                this.TimeCost = 0;
                this.MovingBufferTime =0;
                this.CalculateTime = 0;
            }

            // Wait for some times before moving
            if (this.CoolTime > 100) {
                this.AgentState = Opt_State.MOVING;
                this.CoolTime = 0;
                return IBehaviorConstants.ATTACK;
            }

        } else if (this.AgentState == Opt_State.MOVING) {
            if(Checkfallen()){
                this.fallen =true;
            }

            Area2D.Float kickable = new Area2D.Float((double) 0, 0.25d, -0.15d, 0.15d);
            if (getWorldModel().getThisPlayer().isInsideArea(getWorldModel().getBall().getPosition(), kickable)) {
                this.TimeCost = this.TimeCost + 1;
            }

            // Not wait for too long
            if (this.TimeCost > 350){
                this.fitness -= 5000;
                this.runtime += 1;
                this.AgentState = Opt_State.CALCULATE_FITNESS;
                this.isReset = false;
                this.outoftime =true;
            }
            if (getWorldModel().getBall().getPosition().getX() > -7 &&(!CheckBallSpeed())) {
                this.runtime += 1;
                this.kickBias = Math.abs(getWorldModel().getBall().getPosition().getY());
                this.AgentState = Opt_State.CALCULATE_FITNESS;
                this.kicktimetake = getWorldModel().getGameTime() - this.kickstartime;
                return IBehaviorConstants.SWING_ARMS;
            }

            // Update the ball position
            if (getWorldModel().getBall().getPosition().getZ() > kickhigh){
                kickhigh = getWorldModel().getBall().getPosition().getZ();
            }
            if (getWorldModel().getBall().getPosition().distance(resetBallPos) > this.kickdis){
                this.kickdis=getWorldModel().getBall().getPosition().distance(resetBallPos);
            }
            return IBehaviorConstants.ATTACK;
        } else if (this.AgentState == Opt_State.CALCULATE_FITNESS) {
            this.CalculateTime++;
            if (fallen) {
                this.fitness -= 500;
            }
            if (this.CalculateTime > 150) {
                if (!this.outoftime) {
                    System.out.println("dis");
                    System.out.println(this.kickdis);
                    if (this.kickdis > 8)
                        this.fitness += -abs(8 - this.kickdis) * 200 + (50 - this.TimeCost) * 2.5 + (1 - kickBias) * 1000 ;
                    else
                        this.fitness += -abs(8 - this.kickdis) * 500 + (50 - this.TimeCost) * 2.5 + (1 - kickBias) * 1000 ;
                }

                if (this.runtime == 20) {
                    this.AgentState = Opt_State.WAIT;
                }
                else {
                    this.AgentState = Opt_State.RESET;
                    this.isReset =false;
                }
            }
        } else if (this.AgentState == Opt_State.WAIT) {
            this.WaitTime++;
            if (this.WaitTime > 100) {
                System.out.println("write");
                try {
                    PropertyWriter.saveFitness(this.fitness / 20);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.fitness = 0;
                this.runtime=0;
                this.outoftime = false;
                this.isReset = false;
                this.AgentState = Opt_State.RESET;
                this.MovingBufferTime = 0;

                this.WaitTime = 0;
                this.fallen = false;
            }
        } else if (this.AgentState == Opt_State.DONE) {
            return IBehaviorConstants.SWING_ARMS;
        } else {
            return null;
        }
        return IBehaviorConstants.SWING_ARMS;

    }


    protected boolean Checkfallen(){
        return getWorldModel().getThisPlayer().getPosition().getZ() < 0.02;
    }

    protected boolean CheckBallSpeed() {
        double thisTime = getWorldModel().getGameTime();
        Vector3D thisballpos = getWorldModel().getBall().getPosition();
        if (getWorldModel().getBall().getSpeed().getNorm()>0.005) {
            lastballpos = thisballpos;
            lasttime = thisTime;
            return true;
        }
        return false;

    }


}
