package android.vendor.coda.observation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.vendor.coda.observation.contracts.IDoorState
import android.vendor.coda.observation.contracts.IRPMDisplay
import android.vendor.coda.observation.contracts.ISpeedDisplay
import android.vendor.coda.observation.contracts.IUltrasonicDisplay
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.edit
import java.lang.reflect.InvocationTargetException

class MainActivity : AppCompatActivity() {
    private val carDataViewModel: CarDataViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkTheme: Boolean = false
    lateinit var observation : IObservationServiceIVIContract
    val TAG : String = "ServiceBinding"

    // Add these properties to MainActivity
    private lateinit var mainCarFragment: MainCarFragment
    private lateinit var rpmDisplay: IRPMDisplay
    private lateinit var speedDisplay: ISpeedDisplay
    private lateinit var ultrasonicDisplay: IUltrasonicDisplay
    private lateinit var doorState: IDoorState

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)

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
            // Store interface references
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
            // Restore fragment instance on configuration change
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

            observation.changeSystemThemeToDark()
            observation.changeSystemThemeToLight()

        } else {
            Log.e(TAG, "observation is null")
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

    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        sharedPreferences.edit { putBoolean("dark_theme", isDarkTheme) }

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        recreate()
    }

    private fun updateThemeIcon() {
        val themeButton = findViewById<LinearLayout>(R.id.settings_button)
        val themeIcon = themeButton.getChildAt(0) as ImageView

        if (isDarkTheme) {
            themeIcon.setImageResource(R.drawable.ic_sun)
        } else {
            themeIcon.setImageResource(R.drawable.ic_dark)
        }

        themeIcon.alpha = 1.0f
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
}