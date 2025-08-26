package android.vendor.coda.observation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment

class MainCarFragment : Fragment() {

    private lateinit var carImage: ImageView
    private lateinit var carLeftImage: ImageView
    private lateinit var carRightImage: ImageView
    private lateinit var leftButton: MaterialButton
    private lateinit var rightButton: MaterialButton
    private lateinit var offButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_car, container, false)

        // Find views
        carImage = view.findViewById(R.id.car)
        carLeftImage = view.findViewById(R.id.car_left)
        carRightImage = view.findViewById(R.id.car_right)
        leftButton = view.findViewById(R.id.button_left)
        rightButton = view.findViewById(R.id.button_right)
        offButton = view.findViewById(R.id.car_doors)

        // Left button logic
        leftButton.setOnClickListener {
            carLeftImage.visibility = View.VISIBLE
        }

        // Right button logic
        rightButton.setOnClickListener {
            carRightImage.visibility = View.VISIBLE
        }

        // Off button logic
        offButton.setOnClickListener {
            carLeftImage.visibility = View.GONE
            carRightImage.visibility = View.GONE
        }

        return view
    }
}
