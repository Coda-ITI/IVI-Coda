package android.vendor.coda.observation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.content.Context
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay

class NavigationFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var carMarker: Marker

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        mapView = view.findViewById(R.id.map_view) ?: throw IllegalStateException("MapView not found")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)
        mapView.minZoomLevel = 5.0
        mapView.maxZoomLevel = 20.0
        mapView.setUseDataConnection(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)

        val startPoint = GeoPoint(30.0444, 31.2357)

        carMarker = Marker(mapView).apply {
            position = startPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker)
            title = "My Car"
        }

        mapView.overlays.add(carMarker)
        mapController.setCenter(startPoint)

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                updateMarkerPosition(p, "Updated Location")
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }
        mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))

        simulatePositionUpdate()
    }

    private fun updateMarkerPosition(geoPoint: GeoPoint, title: String) {
        carMarker.position = geoPoint
        carMarker.title = title
        mapView.controller.animateTo(geoPoint)
        mapView.invalidate()
        Log.d("Navigation", "Marker updated at: $geoPoint")
    }

    private fun simulatePositionUpdate() {
        val lat = 30.07
        val lon = 31.02
        val newPosition = GeoPoint(lat, lon)
        updateMarkerPosition(newPosition, "Updated Position")
        Log.w("GPS-UPDATE", "Updated car position to: $lat, $lon")
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }
}