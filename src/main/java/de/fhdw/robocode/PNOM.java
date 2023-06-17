package de.fhdw.robocode;

import robocode.*;
import robocode.util.Utils;

public class PNOM extends AdvancedRobot {
    int moveDirection = 1;
    double x= 0,y = 0;
    int counter = 10;
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
            move();
            //setTurnLeft(90)
            setTurnRadarRight(45);
        } while (true);
    }


    public void onScannedRobot(ScannedRobotEvent e) {
        String enemyName = e.getName(); // Get the name of the scanned enemy
        double enemyBearing = e.getBearing(); // Get the bearing of the scanned enemy
        double enemyDistance = e.getDistance(); // Get the distance to the scanned enemy


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
        fire(getEnergy()/34);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        back(30);
        setTurnLeft(30);
    }
    public void move(){
        if(x==getX()&&y==getY()&&counter<10) {
            counter++;
        }else if(counter>10){
            back(30);
            if(getHeading()>180){
                turnRight(90+90*Math.random());
            }else {
                turnLeft(90+90*Math.random());
            }
            counter=0;
        } else{
            if (getTime() % 13 == 0) {
                moveDirection *= -1; // Reverse the movement direction every 20 turns
            }

            // Move in the current direction
            ahead(100 * moveDirection); // Move forward 100 units or backward 100 units based on the moveDirection

            // Turn the robot slightly to create a smoother movement pattern
            turnRight(8 * moveDirection);
        }
        x=getX();
        y=getY();
    }
}
