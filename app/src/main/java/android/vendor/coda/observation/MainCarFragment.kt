package android.vendor.coda.observation

import android.R.attr.visible
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import androidx.fragment.app.Fragment

class MainCarFragment : Fragment() {

    private lateinit var carImage: ImageView
    private lateinit var carLeftImage: ImageView
    private lateinit var carRightImage: ImageView
    private lateinit var carLeftRearImage: ImageView
    private lateinit var carRightRearImage: ImageView
    private lateinit var warningRearLeft: ImageView
    private lateinit var warningRearCenter: ImageView
    private lateinit var warningRearRight: ImageView
    private lateinit var speedometer: AwesomeSpeedometer
    private lateinit var rpmMeter: AwesomeSpeedometer

    enum class WarningState {
        NORMAL,
        BEWARE,
        CRITICAL
    }

    enum class WarningPosition {
        REAR_LEFT,
        REAR_RIGHT,
        REAR_CENTER
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_car, container, false)

        carImage = view.findViewById(R.id.car)
        carLeftImage = view.findViewById(R.id.car_left)
        carRightImage = view.findViewById(R.id.car_right)
        carLeftRearImage = view.findViewById(R.id.car_left_rear)
        carRightRearImage = view.findViewById(R.id.car_right_rear)
        warningRearLeft = view.findViewById(R.id.warning_rear_left)
        warningRearCenter = view.findViewById(R.id.warning_rear_center)
        warningRearRight = view.findViewById(R.id.warning_rear_right)
        speedometer = view.findViewById(R.id.speedometer)
        rpmMeter = view.findViewById(R.id.rpm_meter)

        setRpm(1.5f)
        setSpeed(70.1f)

        setDoorVisibility("front_left", true)

        setWarningState(WarningPosition.REAR_CENTER, WarningState.BEWARE)
        setWarningState(WarningPosition.REAR_RIGHT, WarningState.CRITICAL)
        setWarningState(WarningPosition.REAR_LEFT, WarningState.BEWARE)

        return view
    }

    fun setWarningState(position: WarningPosition, state: WarningState) {
        val warningIcon = when (position) {
            WarningPosition.REAR_LEFT -> warningRearLeft
            WarningPosition.REAR_CENTER -> warningRearCenter
            WarningPosition.REAR_RIGHT ->  warningRearRight
            else -> null
        }

        if (state == WarningState.NORMAL) {
            warningIcon?.visibility = View.INVISIBLE
        }
        else if (state == WarningState.BEWARE) {
            warningIcon?.visibility = View.VISIBLE
        }
        else if (state == WarningState.CRITICAL) {
            warningIcon?.setImageResource(R.drawable.ic_critical)
            warningIcon?.visibility = View.VISIBLE
        }
    }

    fun setDoorVisibility(position: String, visible: Boolean) {
        when (position) {
            "front_left" -> carLeftImage.visibility = if (visible) View.VISIBLE else View.GONE
            "front_right" -> carRightImage.visibility = if (visible) View.VISIBLE else View.GONE
            "rear_left" -> carLeftRearImage.visibility = if (visible) View.VISIBLE else View.GONE
            "rear_right" -> carRightRearImage.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setSpeed(speed: Float) {
        speedometer.speedTo(speed)
    }

    fun setRpm(rpm: Float) {
        rpmMeter.speedTo(rpm)
    }
}