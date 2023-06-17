package de.fhdw.robocode

import robocode.*
import robocode.util.Utils
import java.awt.Color
import kotlin.math.*


data class Enemy(
    val position: Pair<Double, Double>,
    val heading: Double,
    val headingChange: Double,
    val velocity: Double,
)

class BadTankAnnihilator : AdvancedRobot() {
    private var enemyTarget: Enemy? = null

    override fun run() {
        isAdjustGunForRobotTurn = true
        isAdjustRadarForGunTurn = true
        // isAdjustRadarForRobotTurn = true

        setTurnRadarRight(Double.POSITIVE_INFINITY)
        setBulletColor(Color.GREEN)

        while (true) {
            val safetyZone = 100.0;
            if (x < safetyZone) {
                setTurnRight(90.0)
            } else if(x > battleFieldWidth - safetyZone) {
                setTurnLeft(90.0)
            } else if(y < safetyZone) {
                setTurnRight(90.0)
            } else if(y > battleFieldHeight - safetyZone) {
                setTurnLeft(90.0)
            }

            if (enemyTarget == null) {
                setAhead(10.0)
                setTurnLeft(Utils.getRandom().nextDouble(30.0) - 15.0)
                setTurnRadarRight(Double.POSITIVE_INFINITY)
                //setAhead(20.0)
                //setTurnRadarRight(90.0)
                execute()

            } else {
                execute()
                enemyTarget = null
            }
        }
    }

    override fun onHitRobot(e: HitRobotEvent) {
        val angleToEnemy = headingRadians + e.bearingRadians
        var radarTurn = Utils.normalRelativeAngle(angleToEnemy - radarHeadingRadians)
        setTurnRadarRightRadians(radarTurn)
        setTurnGunRightRadians(Utils.normalRelativeAngle(angleToEnemy - gunHeadingRadians))
        setBack(100.0)
        execute()
    }

    override fun onScannedRobot(e: ScannedRobotEvent) {
        if (e.isSentryRobot) {
            return
        }

        val angleToEnemy = headingRadians + e.bearingRadians

        var radarTurn = Utils.normalRelativeAngle(angleToEnemy - radarHeadingRadians)
        val extraTurn = min(atan(24 / e.distance), Rules.RADAR_TURN_RATE_RADIANS);

        if (radarTurn < 0){
            radarTurn -= extraTurn
        } else {
            radarTurn += extraTurn
        }

        //fire(1.0)
        enemyTarget = Enemy(0.0 to 0.0, 0.0, 0.0, 0.0)
        setTurnRadarRightRadians(radarTurn)

        val bulletPower = (1.0 - e.velocity / 8.0) * (Rules.MAX_BULLET_POWER - 1.0) + 1.0;
        val bulletVelocity = 20.0 - 3.0 * bulletPower

        var enemyX = sin(e.bearingRadians + headingRadians) * e.distance + x
        var enemyY = cos(e.bearingRadians + headingRadians) * e.distance + y
        val enemySize = 32.0;

        var bulletX = x
        var bulletY = y

        for (i in 0..10) {
            enemyX += sin(e.headingRadians) * e.velocity
            enemyY += cos(e.headingRadians) * e.velocity

            bulletX += sin(gunHeadingRadians) * bulletVelocity
            bulletY += cos(gunHeadingRadians) * bulletVelocity

            val bulletEnemyDistance = sqrt((bulletX - enemyX).pow(2) + (bulletY - enemyY).pow(2))
            if (bulletEnemyDistance < enemySize) {
                break;
            }
        }

        val angleToPrediction = Utils.normalAbsoluteAngle(
            atan2(enemyX - x, enemyY - y)
        )

        setTurnGunRightRadians(Utils.normalRelativeAngle(angleToPrediction - gunHeadingRadians))

        setFire(bulletPower)

        val targetDistance = 128.0
        if (e.distance > targetDistance) {
            setTurnRightRadians(e.bearingRadians)
            setAhead(e.distance - targetDistance)
        }

        execute()
    }

    override fun onHitWall(event: HitWallEvent?) {
        turnLeft(170.0)
        ahead(10.0)
    }

    override fun onHitByBullet(e: HitByBulletEvent) {
        //turnLeft(90 - e.bearing)
    }
}