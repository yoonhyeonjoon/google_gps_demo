/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

/**
 * This shows how to use setTag/getTag on API objects.
 */
class TagsDemoActivity : AppCompatActivity(),
        GoogleMap.OnCircleClickListener,
        GoogleMap.OnGroundOverlayClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnPolylineClickListener {

    private lateinit var map: GoogleMap

    private lateinit var tagText: TextView

    private val places = mapOf(
            "BRISBANE" to LatLng(-27.47093, 153.0235),
            "MELBOURNE" to LatLng(-37.81319, 144.96298),
            "DARWIN" to LatLng(-12.4634, 130.8456),
            "SYDNEY" to LatLng(-33.87365, 151.20689),
            "ADELAIDE" to LatLng(-34.92873, 138.59995),
            "PERTH" to LatLng(-31.952854, 115.857342),
            "ALICE_SPRINGS" to LatLng(-24.6980, 133.8807),
            "HOBART" to LatLng(-42.8823388, 147.311042)
    )

    /**
     * Class to store a tag to attach to a map object to keep track of
     * how many times it has been clicked
     */
    private class CustomTag(private val description: String) {
        private var clickCount: Int = 0

        fun incrementClickCount() {
            clickCount++
        }

        override fun toString() = "The $description has been clicked $clickCount times."

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tags_demo)

        tagText = findViewById(R.id.tag_text)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        // return early if the map was not initialised properly
        map = googleMap ?: return

        // Add a circle, a ground overlay, a marker, a polygon and a polyline to the googleMap.
        addObjectsToMap()

        with(map.uiSettings) {
            // Turn off the map toolbar.
            isMapToolbarEnabled = false

            // Disable interaction with the map - other than clicking.
            isZoomControlsEnabled = false
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }

        with(map) {
            // Set listeners for click events.  See the bottom of this class for their behavior.
            setOnCircleClickListener(this@TagsDemoActivity)
            setOnGroundOverlayClickListener(this@TagsDemoActivity)
            setOnMarkerClickListener(this@TagsDemoActivity)
            setOnPolygonClickListener(this@TagsDemoActivity)
            setOnPolylineClickListener(this@TagsDemoActivity)

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription(getString(R.string.tags_demo_map_description))

            // include all places we have markers for in the initial view of the map
            val boundsBuilder = LatLngBounds.Builder()
            places.keys.map { boundsBuilder.include(places.getValue(it)) }
            // Move the camera to view all listed locations
            moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))

        }
    }

    private fun addObjectsToMap() {
        with(map) {
            // A circle centered on Adelaide.
            addCircle(CircleOptions().apply {
                center(places.getValue("ADELAIDE"))
                radius(500000.0)
                fillColor(Color.argb(150, 66, 173, 244))
                strokeColor(Color.rgb(66, 173, 244))
                clickable(true)
            }).run {
                // add a tag to the circle to count clicks
                //tag = String("Adelaide circle")
                tag = "hello"
            }

            // A ground overlay at Sydney.
            addGroundOverlay(GroundOverlayOptions().apply {
                image(BitmapDescriptorFactory.fromResource(R.drawable.harbour_bridge))
                position(places.getValue("SYDNEY"), 700000f)
                clickable(true)
            }).run {
                // add a tag to the overlay to count clicks
                tag = CustomTag("Sydney ground overlay")
            }

            // A marker at Hobart.
            addMarker(MarkerOptions().apply {
                position(places.getValue("HOBART"))
            }).run {
                // add a tag to the marker to count clicks
                tag = CustomTag("Hobart marker")
            }

            // A polygon centered at Darwin.
            addPolygon(PolygonOptions().apply{
                add(LatLng(places.getValue("DARWIN").latitude + 3,
                        places.getValue("DARWIN").longitude - 3),
                        LatLng(places.getValue("DARWIN").latitude + 3,
                                places.getValue("DARWIN").longitude + 3),
                        LatLng(places.getValue("DARWIN").latitude - 3,
                                places.getValue("DARWIN").longitude + 3),
                        LatLng(places.getValue("DARWIN").latitude - 3,
                                places.getValue("DARWIN").longitude - 3))
                fillColor(Color.argb(150, 34, 173, 24))
                strokeColor(Color.rgb(34, 173, 24))
                clickable(true)
            }).run {
                // add a tag to the marker to count clicks
                tag = CustomTag("Darwin polygon")
            }

            // A polyline from Perth to Brisbane.
            addPolyline(PolylineOptions().apply{
                add(places.getValue("PERTH"), places.getValue("BRISBANE"))
                color(Color.rgb(103, 24, 173))
                width(30f)
                clickable(true)
            }).run {
                // add a tag to the polyline to count clicks
                tag = CustomTag("Perth to Brisbane polyline")
            }
        }
    }

    // Click event listeners.
    private fun onClick(tag: CustomTag) {
        tag.incrementClickCount()
        tagText.text = tag.toString()
    }

    override fun onCircleClick(circle: Circle) {
        onClick(circle.tag as? CustomTag ?: return)
    }

    override fun onGroundOverlayClick(groundOverlay: GroundOverlay) {
        onClick(groundOverlay.tag as? CustomTag ?: return)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        onClick(marker.tag as? CustomTag ?: return false)
        // We return true to indicate that we have consumed the event and that we do not wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true
    }

    override fun onPolygonClick(polygon: Polygon) {
        onClick(polygon.tag as? CustomTag ?: return)
    }

    override fun onPolylineClick(polyline: Polyline) {
        onClick(polyline.tag as? CustomTag ?: return)
    }

}