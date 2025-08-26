package android.vendor.coda.observation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Load the default fragments
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.car_info, MainCarFragment())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.function_container, NavigationFragment())
                .commit()
        }

        // Set up bottom bar button click listeners
        setupBottomBarButtons()
    }

    private fun setupBottomBarButtons() {
        val engineButton = findViewById<View>(R.id.settings_button)
        val drowsinessButton = findViewById<View>(R.id.map_button)
        val volumeButton = findViewById<View>(R.id.ambient_button)

        engineButton.setOnClickListener {
            replaceFunctionFragment(SettingsFragment())
            updateButtonSelection(it)
        }

        drowsinessButton.setOnClickListener {
            replaceFunctionFragment(NavigationFragment())
            updateButtonSelection(it)
        }

        volumeButton.setOnClickListener {
            replaceFunctionFragment(AmbientFragment())
            updateButtonSelection(it)
        }

        // Set initial selection (NavigationFragment is default)
        updateButtonSelection(drowsinessButton)
    }

    private fun replaceFunctionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.function_container, fragment)
            .commit()
    }

    private fun updateButtonSelection(selectedView: View) {
        // Reset all button selections
        val buttons = listOf(
            findViewById<View>(R.id.settings_button),
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
}