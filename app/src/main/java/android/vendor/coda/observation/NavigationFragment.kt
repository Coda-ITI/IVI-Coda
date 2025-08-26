package android.vendor.coda.observation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class NavigationFragment : Fragment() {
    private lateinit var mapView: MapView
    private lateinit var carMarker: Marker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navigation, container, false)
        mapView = view.findViewById(R.id.map_view) ?: throw IllegalStateException("MapView not found")

        // Set up map
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Initialize marker at dummy start point
        val startPoint = GeoPoint(30.0444, 31.2357)
        carMarker = Marker(mapView)
        carMarker.position = startPoint
        carMarker.icon = resources.getDrawable(R.drawable.ic_marker, null)
        carMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        carMarker.title = "My Car"
        mapView.overlays.add(carMarker)
        val mapController = mapView.controller
        mapController.setZoom(18.0) // reasonable zoom level; adjust to your needs
        mapController.setCenter(startPoint)

        val lat = 30.07
        val lon = 31.02
        val newPosition = GeoPoint(lat, lon)

        carMarker.position = newPosition

        mapController.animateTo(newPosition) // animate instead of setCenter
        mapView.invalidate() // force redraw
        Log.w("GPS-UPDATE", "Updated car position to: $lat, $lon")

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}