package android.vendor.coda.observation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.LinearLayout
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var isDarkTheme: Boolean = false

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
            supportFragmentManager.beginTransaction()
                .replace(R.id.car_info, MainCarFragment())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.function_container, NavigationFragment())
                .commit()
        }

        setupBottomBarButtons()

        updateThemeIcon()
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
            imageView?.alpha = 0.5f // Dim unselected buttons
        }

        // Highlight selected button
        val selectedImageView = (selectedView as? ViewGroup)?.getChildAt(0) as? ImageView
        selectedImageView?.alpha = 1.0f // Full opacity for selected button
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