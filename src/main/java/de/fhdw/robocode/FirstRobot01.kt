package de.fhdw.robocode

import robocode.*
import robocode.util.Utils
import java.awt.Color
import java.awt.geom.Point2D
import javax.swing.text.html.HTML.Tag.U
import kotlin.math.*


data class Enemy(
    val position: Pair<Double, Double>,
    val heading: Double,
    val headingChange: Double,
    val velocity: Double,
)

class FirstRobot01 : AdvancedRobot() {
    private var enemyTarget: Enemy? = null

    override fun run() {
        val radius = 100.0
        val angle = 90.0

        isAdjustGunForRobotTurn = true
        isAdjustRadarForGunTurn = true
        // isAdjustRadarForRobotTurn = true

        setTurnRadarRight(Double.POSITIVE_INFINITY)
        setBulletColor(Color.GREEN)

        while (true) {
            if (enemyTarget == null) {
                setAhead(10.0)
                setTurnLeft(Utils.getRandom().nextDouble(30.0) - 15.0)
                setTurnRadarRight(Double.POSITIVE_INFINITY)
                //setAhead(20.0)
                //setTurnRadarRight(90.0)
                execute()

            } else {

               /* enemyTarget?.let {
                    val predictedPosition = it.position.first + cos(it.heading) * it.velocity to
                            it.position.second + sin(it.heading) * it.velocity

                    Utils.normalAbsoluteAngle()


                }

                setFireBullet(4.0)*/
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

        var gunTurn = Utils.normalRelativeAngle(angleToEnemy - gunHeadingRadians)

        val enemyX = sin(e.bearingRadians + headingRadians) * e.distance + x
        val enemyY = cos(e.bearingRadians + headingRadians) * e.distance + y
        val predictedX = sin(e.headingRadians) * e.velocity + enemyX
        val predictedY = cos(e.headingRadians) * e.velocity + enemyY

        val angleToPrediction = Utils.normalAbsoluteAngle(
            atan2(predictedX - x, predictedY - y)
        )

        setTurnGunRightRadians(Utils.normalRelativeAngle(angleToPrediction - gunHeadingRadians))
        setFire(Rules.MAX_BULLET_POWER)

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
