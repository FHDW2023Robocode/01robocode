package de.fhdw.robocode;

import robocode.*;
import robocode.util.Utils;

public class PNOM extends AdvancedRobot {

    @Override
    public void run() {

        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        do {
            // Check for new targets.
            // Only necessary for Narrow Lock because sometimes our radar is already
            // pointed at the enemy and our onScannedRobot code doesn't end up telling
            // it to turn, so the system doesn't automatically call scan() for us
            // [see the javadocs for scan()].
            //scan();
            ahead(20);
            //setTurnLeft(90)
            setTurnRadarRight(10);
        } while (true);
    }


    public void onScannedRobot(ScannedRobotEvent e) {
        double radarTurn =
                // Absolute bearing to target
                getHeadingRadians() + e.getBearingRadians()
                        // Subtract current radar heading to get turn required
                        - getRadarHeadingRadians();

        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        setTurnGunRightRadians(
                robocode.util.Utils.normalRelativeAngle(absoluteBearing -
                        getGunHeadingRadians()));
        fire(3-(getEnergy()/34));
    }

    public void onHitByBullet(HitByBulletEvent e) {
        back(30);
        setTurnLeft(30);
    }
}
