package android.vendor.coda.observation

import android.R.attr.visible
import android.os.Bundle
import android.vendor.coda.observation.contracts.IDoorState
import android.vendor.coda.observation.contracts.IRPMDisplay
import android.vendor.coda.observation.contracts.ISpeedDisplay
import android.vendor.coda.observation.contracts.IUltrasonicDisplay
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class MainCarFragment : Fragment(), IRPMDisplay, ISpeedDisplay, IUltrasonicDisplay, IDoorState {

    private val carDataViewModel: CarDataViewModel by activityViewModels()
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

    enum class DoorPosition {
        FRONT_LEFT,
        FRONT_RIGHT,
        REAR_LEFT,
        REAR_RIGHT
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

//        setRpm(1.5f)
//        setSpeed(70.1f)

//        setDoorVisibility("front_left", true)

//        setWarningState(WarningPosition.REAR_CENTER, WarningState.BEWARE)
//        setWarningState(WarningPosition.REAR_RIGHT, WarningState.CRITICAL)
//        setWarningState(WarningPosition.REAR_LEFT, WarningState.BEWARE)

        setupObservers()

        return view
    }

    private fun setupObservers() {
        carDataViewModel.rpm.observe(viewLifecycleOwner, Observer { rpm ->
            displayRPM(rpm)
        })

        carDataViewModel.speed.observe(viewLifecycleOwner, Observer { speed ->
            displaySpeed(speed)
        })

        carDataViewModel.ultrasonicRL.observe(viewLifecycleOwner, Observer { reading ->
            displayUltrasonicRL(reading)
        })

        carDataViewModel.ultrasonicRC.observe(viewLifecycleOwner, Observer { reading ->
            displayUltrasonicRC(reading)
        })

        carDataViewModel.ultrasonicRR.observe(viewLifecycleOwner, Observer { reading ->
            displayUltrasonicRR(reading)
        })

        carDataViewModel.doorFL.observe(viewLifecycleOwner, Observer { isOpen ->
            setFLDoorState(isOpen)
        })

        carDataViewModel.doorFR.observe(viewLifecycleOwner, Observer { isOpen ->
            setFRDoorState(isOpen)
        })

        carDataViewModel.doorRL.observe(viewLifecycleOwner, Observer { isOpen ->
            setRLDoorState(isOpen)
        })

        carDataViewModel.doorRR.observe(viewLifecycleOwner, Observer { isOpen ->
            setRRDoorState(isOpen)
        })
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

    fun setDoorVisibility(position: DoorPosition, visible: Boolean) {
        when (position) {
            DoorPosition.FRONT_LEFT -> carLeftImage.visibility = if (visible) View.VISIBLE else View.GONE
            DoorPosition.FRONT_RIGHT -> carRightImage.visibility = if (visible) View.VISIBLE else View.GONE
            DoorPosition.REAR_LEFT -> carLeftRearImage.visibility = if (visible) View.VISIBLE else View.GONE
            DoorPosition.REAR_RIGHT -> carRightRearImage.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    override fun displayRPM(reading: Float) {
        rpmMeter.speedTo(reading)
    }

    override fun displaySpeed(reading: Float) {
        speedometer.speedTo(reading)
    }

    override fun displayUltrasonicRC(reading: Float) {
        if (reading < 30 && reading > 15) {
            setWarningState(WarningPosition.REAR_CENTER, WarningState.BEWARE)
        }
        else if (reading < 15) {
            setWarningState(WarningPosition.REAR_CENTER, WarningState.CRITICAL)
        }
        else {
            setWarningState(WarningPosition.REAR_CENTER, WarningState.NORMAL)
        }
    }

    override fun displayUltrasonicRL(reading: Float) {
        if (reading < 30 && reading > 15) {
            setWarningState(WarningPosition.REAR_LEFT, WarningState.BEWARE)
        }
        else if (reading < 15) {
            setWarningState(WarningPosition.REAR_LEFT, WarningState.CRITICAL)
        }
        else {
            setWarningState(WarningPosition.REAR_LEFT, WarningState.NORMAL)
        }
    }

    override fun displayUltrasonicRR(reading: Float) {
        if (reading < 30 && reading > 15) {
            setWarningState(WarningPosition.REAR_RIGHT, WarningState.BEWARE)
        }
        else if (reading < 15) {
            setWarningState(WarningPosition.REAR_RIGHT, WarningState.CRITICAL)
        }
        else {
            setWarningState(WarningPosition.REAR_RIGHT, WarningState.NORMAL)
        }
    }

    override fun setFLDoorState(isOpen: Boolean) {
        setDoorVisibility(DoorPosition.FRONT_LEFT, isOpen)
    }

    override fun setFRDoorState(isOpen: Boolean) {
        setDoorVisibility(DoorPosition.FRONT_RIGHT, isOpen)
    }

    override fun setRLDoorState(isOpen: Boolean) {
        setDoorVisibility(DoorPosition.REAR_LEFT, isOpen)
    }

    override fun setRRDoorState(isOpen: Boolean) {
        setDoorVisibility(DoorPosition.REAR_RIGHT, isOpen)
    }
}