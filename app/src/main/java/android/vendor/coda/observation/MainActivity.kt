package android.vendor.coda.observation

import android.Manifest
import android.annotation.SuppressLint
import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import android.content.SharedPreferences
import android.graphics.Color
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.vendor.coda.observation.contracts.IDoorState
import android.vendor.coda.observation.contracts.IRPMDisplay
import android.vendor.coda.observation.contracts.ISpeedDisplay
import android.vendor.coda.observation.contracts.IUltrasonicDisplay
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() {
    private val carDataViewModel: CarDataViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkTheme: Boolean = true
    lateinit var observation : IObservationServiceIVIContract
    val TAG : String = "ServiceBinding"
    private val overlayPackage = "android.vendor.coda.observation.lightmode"
    private val targetPackage = "android.vendor.coda.observation"
    private val userId = 10

    private lateinit var mainCarFragment: MainCarFragment
    private lateinit var rpmDisplay: IRPMDisplay
    private lateinit var speedDisplay: ISpeedDisplay
    private lateinit var ultrasonicDisplay: IUltrasonicDisplay
    private lateinit var doorState: IDoorState

    private val VENDOR_EXTENSION_PROPERTY: Int = 0x21400105

    private var car: Car? = null
    private var carPropertyManager: CarPropertyManager? = null

//    private var carPropertyListener = object : CarPropertyManager.CarPropertyEventCallback {
//        override fun onChangeEvent(value: CarPropertyValue<Any>) {
//            Log.d(TAG, "Received on changed car property event")
//            Toast.makeText(this@MainActivity, "Hello", Toast.LENGTH_SHORT).show()
//            toggleTheme()
//        }
//
//        override fun onErrorEvent(propId: Int, zone: Int) {
//            Log.w(TAG, "Received error car property event, propId=$propId")
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isDarkTheme = sharedPreferences.getBoolean("dark_theme", true)

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        hideSystemBars()

        if (savedInstanceState == null) {
            mainCarFragment = MainCarFragment()
            rpmDisplay = mainCarFragment
            speedDisplay = mainCarFragment
            ultrasonicDisplay = mainCarFragment
            doorState = mainCarFragment

            supportFragmentManager.beginTransaction()
                .replace(R.id.car_info, mainCarFragment)
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.function_container, NavigationFragment())
                .commit()
        } else {
            mainCarFragment = supportFragmentManager.findFragmentById(R.id.car_info) as MainCarFragment
            rpmDisplay = mainCarFragment
            speedDisplay = mainCarFragment
            ultrasonicDisplay = mainCarFragment
            doorState = mainCarFragment
        }

        setupBottomBarButtons()

        updateThemeIcon()

        try {
            @SuppressLint("PrivateApi") val serviceManagerClass =
                Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod(
                "getService",
                String::class.java
            )

            val result = getServiceMethod.invoke(null, "android.vendor.coda.observation.IObservationServiceIVIContract/default")

            if (result != null) {
                val binder = result as IBinder
                observation = IObservationServiceIVIContract.Stub.asInterface(binder)
                Log.d(TAG, "Successfully bound to IObservationServiceIVIContract!")
            } else {
                Log.e(TAG, "Failed to get service binder.")
            }
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Class not found: " + e.message)
        } catch (e: NoSuchMethodException) {
            Log.e(TAG, "Method not found: " + e.message)
        } catch (e: InvocationTargetException) {
            Log.e(TAG, "Invocation target exception: " + e.message)
        } catch (e: IllegalAccessException) {
            Log.e(TAG, "Illegal access exception: " + e.message)
        }

        if (observation != null) {
            observation.registerRPMReadingsCallback(object : IRPMReadings.Stub() {
                override fun onRpmChanged(rpm: Int) {
                    Log.d(TAG, "RPMCallback: Received RPM: $rpm")
                    carDataViewModel.updateRPM(rpm.toFloat())
                }
            })

            observation.registerSpeedReadingsCallback(object : ISpeedReadings.Stub() {
                override fun onSpeedChanged(speed: Int) {
                    Log.d(TAG, "SpeedCallback: Received speed: $speed")
                    carDataViewModel.updateSpeed(speed.toFloat())
                }
            })

            observation.registerUltrasonic0ReadingsCallback(object : IUltrasonicReadings.Stub() {
                override fun onUltrasonicChanged(position: Int, reading: Float) {
                    Log.d(TAG, "UltrasonicCallback: Received position and reading: $position, $reading")
                    carDataViewModel.updateUltrasonicRL(reading)
                }
            })

            observation.registerUltrasonic1ReadingsCallback(object : IUltrasonicReadings.Stub() {
                override fun onUltrasonicChanged(position: Int, reading: Float) {
                    Log.d(TAG, "UltrasonicCallback: Received position and reading: $position, $reading")
                    carDataViewModel.updateUltrasonicRC(reading)
                }
            })

            observation.registerUltrasonic2ReadingsCallback(object : IUltrasonicReadings.Stub() {
                override fun onUltrasonicChanged(position: Int, reading: Float) {
                    Log.d(TAG, "UltrasonicCallback: Received position and reading: $position, $reading")
                    carDataViewModel.updateUltrasonicRC(reading)
                }
            })

            observation.registerUltrasonic3ReadingsCallback(object : IUltrasonicReadings.Stub() {
                override fun onUltrasonicChanged(position: Int, reading: Float) {
                    Log.d(TAG, "UltrasonicCallback: Received position and reading: $position, $reading")
                    carDataViewModel.updateUltrasonicRR(reading)
                }
            })

            observation.registerDoorStateFLReadingsCallback(object : IDoorStateReadings.Stub() {
                override fun onDoorStateChanged(position: Int, isOpen: Boolean) {
                    Log.d(TAG, "DoorStateCallback: Received position and state: $position, $isOpen")
                    carDataViewModel.updateDoorFL(isOpen)
                }
            })

            observation.registerDoorStateFRReadingsCallback(object : IDoorStateReadings.Stub() {
                override fun onDoorStateChanged(position: Int, isOpen: Boolean) {
                    Log.d(TAG, "DoorStateCallback: Received position and state: $position, $isOpen")
                    carDataViewModel.updateDoorFR(isOpen)
                }
            })

            observation.registerDoorStateRLReadingsCallback(object : IDoorStateReadings.Stub() {
                override fun onDoorStateChanged(position: Int, isOpen: Boolean) {
                    Log.d(TAG, "DoorStateCallback: Received position and state: $position, $isOpen")
                    carDataViewModel.updateDoorRL(isOpen)
                }
            })

            observation.registerDoorStateRRReadingsCallback(object : IDoorStateReadings.Stub() {
                override fun onDoorStateChanged(position: Int, isOpen: Boolean) {
                    Log.d(TAG, "DoorStateCallback: Received position and state: $position, $isOpen")
                    carDataViewModel.updateDoorRR(isOpen)
                }
            })

//            observation.changeSystemThemeToDark()
//            observation.changeSystemThemeToLight()

        } else {
            Log.e(TAG, "observation is null")
        }

        car = Car.createCar(this@MainActivity, null, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER) { car, ready ->
            if (ready) {
                carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
                Log.d(TAG, "CarPropertyManager initialized")

                // register callback for assistant VHAL property
//                carPropertyManager?.registerCallback(
//                    carPropertyListener,
//                    VENDOR_EXTENSION_PROPERTY,
//                    CarPropertyManager.SENSOR_RATE_ONCHANGE
//                )
            } else {
                Log.e(TAG, "Car service connection failed")
            }
        }
    }

    private fun setupBottomBarButtons() {
        val themeButton = findViewById<View>(R.id.settings_button)
        val drowsinessButton = findViewById<View>(R.id.map_button)
        val volumeButton = findViewById<View>(R.id.ambient_button)

        themeButton.setOnClickListener {
            toggleTheme()
            updateThemeIcon()
        }

        drowsinessButton.setOnClickListener {
            replaceFunctionFragment(NavigationFragment())
            updateButtonSelection(it)
        }

        volumeButton.setOnClickListener {
            replaceFunctionFragment(AmbientFragment())
            updateButtonSelection(it)
        }

        updateButtonSelection(drowsinessButton)
    }

    private fun setThemeToLightMode() {
        if (isDarkTheme) {
            toggleTheme()
        }
    }

    private fun setThemeToDarkTheme() {
        if (!isDarkTheme) {
            toggleTheme()
        }
    }

    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        sharedPreferences.edit { putBoolean("dark_theme", isDarkTheme) }

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
             observation.changeSystemThemeToDark()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
             observation.changeSystemThemeToLight()
        }

        toggleOverlayTheme()
    }

    private fun replaceFunctionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.function_container, fragment)
            .commit()
    }

    private fun updateButtonSelection(selectedView: View) {
        val buttons = listOf(
            findViewById<View>(R.id.map_button),
            findViewById<View>(R.id.ambient_button)
        )

        buttons.forEach { button ->
            val imageView = (button as? ViewGroup)?.getChildAt(0) as? ImageView
            imageView?.alpha = 0.5f
        }

        val selectedImageView = (selectedView as? ViewGroup)?.getChildAt(0) as? ImageView
        selectedImageView?.alpha = 1.0f
    }

    private fun hideSystemBars() {
        if (packageManager.hasSystemFeature("android.hardware.type.automotive")) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun toggleOverlayTheme() {
        setOverlayTheme(!isOverlayEnabled())
    }

    @RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
    @SuppressLint("UseCompatLoadingForDrawables")
    fun setOverlayTheme(enable: Boolean) {
        try {
            val isOverlayEnabled = isOverlayEnabled()
            Log.i(TAG, "Overlay $overlayPackage current state: enabled=$isOverlayEnabled, setting to enabled=$enable")

            if (isOverlayEnabled != enable) {
                val command = "cmd overlay ${if (enable) "enable" else "disable"} --user $userId $overlayPackage"
                val (success, output) = executeShellCommand(command)
                if (!success) {
                    throw IllegalStateException("Shell command failed: $output")
                }

                Log.i(TAG, "Overlay $overlayPackage set to enabled=$enable")
                updateThemeIcon()

                restartTargetApp(this, targetPackage)
            } else {
                Log.i(TAG, "Overlay $overlayPackage already in desired state: enabled=$enable")
            }

            Toast.makeText(this, "Theme ${if (enable) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied: Ensure app is a system app with CHANGE_OVERLAY_PACKAGES permission", e)
            Toast.makeText(this, "Permission denied: App must be a system app", Toast.LENGTH_LONG).show()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Overlay $overlayPackage not found or invalid: ${e.message}", e)
            Toast.makeText(this, "Overlay not found or invalid", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set overlay $overlayPackage: ${e.message}", e)
            Toast.makeText(this, "Failed to set theme", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun isOverlayEnabled(): Boolean {
        try {
            val (success, output) = executeShellCommand("cmd overlay list --user $userId")
            if (success) {
                val enabled = output.lines().any { it.contains("[x] $overlayPackage") }
                Log.i(TAG, "Overlay state check: enabled=$enabled, output=$output")
                return enabled
            } else {
                Log.w(TAG, "Failed to check overlay state: $output")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error checking overlay state: ${e.message}")
        }
        return false
    }

    private fun updateThemeIcon() {
        val themeButton = findViewById<LinearLayout>(R.id.settings_button)
        val themeIcon = themeButton.getChildAt(0) as ImageView

        if (isOverlayEnabled()) {
            themeIcon.setImageResource(R.drawable.ic_sun)
        } else {
            themeIcon.setImageResource(R.drawable.ic_dark)
        }

        themeIcon.alpha = 1.0f
    }

    private fun executeShellCommand(command: String): Pair<Boolean, String> {
        try {
            val process = Runtime.getRuntime().exec(command)
            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            val error = BufferedReader(InputStreamReader(process.errorStream)).use { it.readText() }
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Log.i(TAG, "Executed shell command: $command, output: $output")
                return Pair(true, output)
            } else {
                Log.e(TAG, "Shell command failed: $command, error: $error, exit code: $exitCode")
                return Pair(false, error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute shell command: ${e.message}", e)
            return Pair(false, e.message ?: "Unknown error")
        }
    }

    private fun restartTargetApp(context: Context, targetPackage: String) {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            activityManager.killBackgroundProcesses(targetPackage)
            Log.i(TAG, "Force-stopped $targetPackage")

            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(targetPackage)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                ContextCompat.startActivity(context, intent, null)
                Log.i(TAG, "Relaunched $targetPackage")
            } else {
                Log.w(TAG, "No launch intent found for $targetPackage")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restart $targetPackage: ${e.message}", e)
            Toast.makeText(context, "Failed to restart target app", Toast.LENGTH_LONG).show()
        }
    }
}