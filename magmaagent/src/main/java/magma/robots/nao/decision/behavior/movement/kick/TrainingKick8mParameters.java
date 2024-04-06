/* Copyright 2008 - 2021 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under GPL-3 License (see gpl.txt).
 */

package magma.robots.nao.decision.behavior.movement.kick;

import hso.autonomy.util.properties.TabSeparatedPropertiesUtil;
import magma.agent.decision.behavior.base.KickDistribution;


public class TrainingKick8mParameters extends Kick8mParameters
{
    @Override
    protected void setValues()
    {
        System.out.println("load8m");
        String key = "Kick8m";

        put(Param.TIME0, TabSeparatedPropertiesUtil.getFloatProperty(key+".TIME0"));

        put(Param.TIME1, TabSeparatedPropertiesUtil.getFloatProperty(key+".TIME1"));
        put(Param.TIME2, TabSeparatedPropertiesUtil.getFloatProperty(key+".TIME2"));
        put(Param.TIME3, TabSeparatedPropertiesUtil.getFloatProperty(key+".TIME3"));
        put(Param.LHP1, TabSeparatedPropertiesUtil.getFloatProperty(key+".LHP1"));
        put(Param.RHP1,TabSeparatedPropertiesUtil.getFloatProperty( key+".RHP1"));
        put(Param.RKP1, TabSeparatedPropertiesUtil.getFloatProperty(key+".RKP1"));
        put(Param.RFP1, TabSeparatedPropertiesUtil.getFloatProperty(key+".RFP1"));
        put(Param.RTP1, TabSeparatedPropertiesUtil.getFloatProperty(key+".RTP1"));
        put(Param.LHP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".LHP2"));
        put(Param.LHYP1,TabSeparatedPropertiesUtil.getFloatProperty(key+".LHYP1") );
        put(Param.LHYPS1, TabSeparatedPropertiesUtil.getFloatProperty(key+".LHYPS1"));
        put(Param.LHYPS2, TabSeparatedPropertiesUtil.getFloatProperty(key+".LHYPS2"));
        put(Param.RHYP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RHYP2"));
        put(Param.RHP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RHP2"));
        put(Param.RKP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RKP2"));
        put(Param.RFP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RFP2"));
        put(Param.RTP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RTP2"));
        put(Param.RKPS1, TabSeparatedPropertiesUtil.getFloatProperty(key+".RKPS1"));
        put(Param.RHPS2,TabSeparatedPropertiesUtil.getFloatProperty(key+".RHPS2"));
        put(Param.RHYPS2,TabSeparatedPropertiesUtil.getFloatProperty(key+".RHYPS2"));
        put(Param.RFPS2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RFPS2"));
        put(Param.RHR2, TabSeparatedPropertiesUtil.getFloatProperty(key+".RHR2"));
        put(Param.LHR1, TabSeparatedPropertiesUtil.getFloatProperty(key+".LHR1"));
        put(Param.LKP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".LKP2"));
        put(Param.LFP2, TabSeparatedPropertiesUtil.getFloatProperty(key+".LFP2"));
        put(Param.POS_X, TabSeparatedPropertiesUtil.getFloatProperty(key+".POS_X"));
        put(Param.POS_Y, TabSeparatedPropertiesUtil.getFloatProperty(key+".POS_Y"));
        put(Param.KICK_ANGLE, TabSeparatedPropertiesUtil.getFloatProperty(key+".KICK_ANGLE"));
        put(Param.MIN_X_OFFSET, TabSeparatedPropertiesUtil.getFloatProperty(key+".MIN_X_OFFSET"));
        put(Param.RUN_TO_X, TabSeparatedPropertiesUtil.getFloatProperty(key+".RUN_TO_X"));
        put(Param.RUN_TO_Y, TabSeparatedPropertiesUtil.getFloatProperty(key+".RUN_TO_Y"));
        put(Param.CANCEL_DISTANCE,TabSeparatedPropertiesUtil.getFloatProperty(key+".CANCEL_DISTANCE") );
        put(Param.STABILIZE_TIME,TabSeparatedPropertiesUtil.getFloatProperty(key+".STABILIZE_TIME"));
        // Average utility: 5.926 averaged: 1000, properties: [
        // ballX: 6.484,
        // ballY: -0.004,
        // absBallY: 0.558,
        // maxBallHeight: 0.130,
        // supportFoot: 0.000,
        // hitBall: 0.977,
        // supportFootX: -0.086,
        // supportFootY: 0.047,
        // supportFootOrientation: -73.387
        // ]

        distribution =
                new KickDistribution(new double[] {0.028, 0.001, 0.003, 0.005, 0.007, 0.007, 0.018, 0.008, 0.002, 0.004,
                        0.004, 0.012, 0.047, 0.161, 0.425, 0.217, 0.036, 0.012, 0.003},
                        new double[] {0.149, 0.259, 0.222, 0.16, 0.077, 0.036, 0.027, 0.017, 0.01, 0.012, 0.007, 0.006,
                                0.005, 0.002, 0.001, 0.002, 0.001, 0.002, 0.002, 0.0, 0.0, 0.001, 0.0, 0.0, 0.0, 0.0,
                                0.001, 0.0, 0.0, 0.0, 0.0, 0.0, 0.001});
    }
}
