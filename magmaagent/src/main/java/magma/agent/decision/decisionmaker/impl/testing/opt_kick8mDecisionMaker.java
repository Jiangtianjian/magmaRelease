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
    private  int EvaluateBuffertime;
    private  int EndBufferTime;
    protected boolean haveBeamed;
    private Vector3D resetBallPos;
    private int ResetbufferTime;
    private int runtime;
    private double fitness;
    private static Vector3D lastballpos;
    private static double lasttime;

    static {
        lastballpos = new Vector3D(0,0,0);
        lasttime = 0;
    }
    protected ITrainServer TrainServer;
    private int MovingBufferTime;
    private double kickstartime;
    private double kickBias;
    private double kickhigh;
    private double kickdis;
    public enum State {
        RESET,
        MOVING,
        EVALUATE,
        END,
        FINISH
    }
    private double kicktimetake;
    private boolean fallen;
    private State RobotCurrentState;
    boolean isReset;
    int adjustmentTime;
    boolean outoftime;
    boolean ballisstop;

    Random rand = new Random();

    public opt_kick8mDecisionMaker(BehaviorMap behaviors, IRoboCupThoughtModel thoughtModel) {
        super(behaviors, thoughtModel);
        ResetbufferTime = 0;
        this.isReset = false;
        this.RobotCurrentState = State.RESET;
        this.ResetbufferTime = 0;
        this.EvaluateBuffertime = 0;
        this.adjustmentTime = 0;
        this.MovingBufferTime = 0;
        this.EndBufferTime = 0;
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
            ((IBeam) behaviors.get(IBehaviorConstants.BEAM_TO_POSITION)).setPose(new Pose2D(-2f, 0.0f));
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

        if (this.RobotCurrentState == State.RESET) {
            this.ResetbufferTime++;
            if (!this.isReset) {
                this.TrainServer.connect();
                this.TrainServer.setPlayMode(PlayModeParameters.PlayOn.getName());
                this.resetBallPos = new Vector3D(-9, 0, 0);
                this.TrainServer.moveBall(this.resetBallPos);
                double Rotate = (45 * (this.runtime - 1));
                this.TrainServer.moveAgent(new Vector3D(-10 - 2f * Math.cos(Rotate), -2f * Math.sin(Rotate), 0.3), true, getWorldModel().getThisPlayer().getID());
                this.TrainServer.disconnect();
                this.isReset = true;
                this.kickdis = 0;
                this.kickhigh = 0;
                this.kickstartime = getWorldModel().getGameTime();
                this.kickBias = 0;
                this.outoftime =false;
                this.ballisstop = true;
                this.adjustmentTime = 0;
                this.MovingBufferTime =0;
                this.EvaluateBuffertime = 0;
            }
            if (this.ResetbufferTime > 200) {
                this.RobotCurrentState = State.MOVING;
                this.ResetbufferTime = 0;
                return IBehaviorConstants.ATTACK;
            }

        }else if (this.RobotCurrentState == State.MOVING) {
            Area2D.Float kickable = new Area2D.Float((double) 0, 0.25d, -0.15d, 0.15d);
            if (getWorldModel().getThisPlayer().isInsideArea(getWorldModel().getBall().getPosition(), kickable)) {
                this.adjustmentTime++;

            }
            if (this.adjustmentTime >350){
                this.fitness -=5000;
                this.runtime += 1;
                this.RobotCurrentState = State.EVALUATE;
                this.isReset = false;
                this.outoftime =true;
                System.out.println("failed");
            }
            if (getWorldModel().getBall().getPosition().getX() > -7.5 &&(!isballmoving())) {
                this.runtime += 1;

                this.kickBias = Math.abs(getWorldModel().getBall().getPosition().getY());

                this.RobotCurrentState = State.EVALUATE;
                this.kicktimetake = getWorldModel().getGameTime() - this.kickstartime;

                System.out.println("next");
                return IBehaviorConstants.SWING_ARMS;
            }
            if(judgefallen()){
                this.fallen =true;
            }

            if (getWorldModel().getBall().getPosition().getZ()>kickhigh){kickhigh = getWorldModel().getBall().getPosition().getZ();}
            if (getWorldModel().getBall().getPosition().distance(resetBallPos)>this.kickdis){this.kickdis=getWorldModel().getBall().getPosition().distance(resetBallPos);}
            return IBehaviorConstants.ATTACK;

        } else if (this.RobotCurrentState == State.EVALUATE) {
            this.EvaluateBuffertime++;

            IBall ball = getWorldModel().getBall();
            Vector3D ballpos = ball.getPosition();
            if (fallen) {
                this.fitness -= 500;
            }
            if (this.EvaluateBuffertime > 150) {
                if (!this.outoftime) {
                    System.out.println("dis");
                    System.out.println(this.kickdis);
                    if (this.kickdis > 8)
                        this.fitness += -abs(8 - this.kickdis) * 200 + (50 - this.adjustmentTime) * 2.5 + (1 - kickBias) * 1000 ;
                    else
                        this.fitness += -abs(8 - this.kickdis) * 500 + (50 - this.adjustmentTime) * 2.5 + (1 - kickBias) * 1000 ;
                }
                //                try {
//                    PropertyWriter.saveFitness(fitness);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }


                System.out.println(fitness);
                if (this.runtime == 20) {
                    this.RobotCurrentState = State.END;
                }
                else {
                    this.RobotCurrentState = State.RESET;
                    this.isReset =false;
                }
            }
        } else if (this.RobotCurrentState == State.END) {
            this.EndBufferTime++;
            if (this.EndBufferTime> 100) {
                System.out.println("write");
                try {
                    PropertyWriter.saveFitness(this.fitness/20);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.fitness = 0;
                this.runtime=0;
                this.outoftime = false;
                this.isReset = false;
                this.RobotCurrentState = State.RESET;
                this.MovingBufferTime = 0;

                this.EvaluateBuffertime = 0;
                this.fallen = false;
            }
        } else if (this.RobotCurrentState == State.FINISH) {
            return IBehaviorConstants.SWING_ARMS;
        } else {
            return null;
        }
        return IBehaviorConstants.SWING_ARMS;

    }


    protected boolean isballmoving() {
        double thisTime = getWorldModel().getGameTime();
        Vector3D thisballpos = getWorldModel().getBall().getPosition();

        // Assuming Vector3D has a distance method
        if (getWorldModel().getBall().getSpeed().getNorm()>0.005) { // Use a significant distance threshold, like 0.01
            lastballpos = thisballpos;
            lasttime = thisTime;
            return true;
        } else {
            return false;
        }
    }
    protected boolean judgefallen(){
        if (getWorldModel().getThisPlayer().getPosition().getZ()<0.02){
            return  true;
        }else {return false;}
    }

}
