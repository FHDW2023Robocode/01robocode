package de.fhdw.robocode;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.AdvancedRobot;

public class FirstRobot01 extends AdvancedRobot {

    @Override
    public void run() {

        double radius = 100.0;
        double angle = 90.0;

        //make idividually rotateable
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        setTurnRadarRight(Double.POSITIVE_INFINITY);

        while (true) {
            ahead(radius);
            turnLeft(angle);
            turnGunLeft(angle);
            fireBullet(getEnergy());
        }
    }


    public void onScannedRobot(ScannedRobotEvent e) {
        setTurnRadarRight(2.0 * Utils.normalRelativeAngleDegrees(getHeading() + e.getBearing() - getRadarHeading()));
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }

}
