package com.lk.ecommerce.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lk.ecommerce.R;
import com.lk.ecommerce.directionsLib.FetchURL;

public class MapsFragment extends Fragment {


        private static final int LOACTION_PERMISSION = 100;
        private static final String TAG ="MapFragment";
        FusedLocationProviderClient fusedLocationProviderClient;
        GoogleMap CurrentgoogleMap;
        private Polyline currentPolyline;
        private OnMapReadyCallback callback = new OnMapReadyCallback() {


            @Override
            public void onMapReady(GoogleMap googleMap) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsFragment.super.getContext());
                CurrentgoogleMap = googleMap;

                UpdateCustomerLocation();


            }
        };

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {//call back eka enne me method ekata
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == LOACTION_PERMISSION) {
                if (permissions.length > 0) {
                    UpdateCustomerLocation();

                }

            }

        }

        private void UpdateCustomerLocation() {


            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //permsiions natnm me if eka thule permisions request kirnna oni
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOACTION_PERMISSION);
                return;
            }


            Task<Location> task = fusedLocationProviderClient.getLastLocation();// me line eka run wenna nm permissions oni
            task.addOnSuccessListener(new OnSuccessListener<Location>() {

                private LatLng customerLocation;
                private LatLng dropLocation;

                @Override
                public void onSuccess(Location location) {
                    if(location!=null){
                        Toast.makeText(MapsFragment.super.getContext(),
                                "location:"+location.getLatitude()+" "+location.getLongitude(),Toast.LENGTH_SHORT).show();

                        customerLocation =new LatLng(location.getLatitude(),location.getLongitude());
                        dropLocation =new LatLng(location.getLatitude(),location.getLongitude());

                        BitmapDescriptor ico_curret=getBitMapDesc(getActivity(),R.drawable.ic_tracking);
                        BitmapDescriptor ico_dest=getBitMapDesc(getActivity(),R.drawable.ic_walkto);
                        // BitmapDescriptor ico_curret=BitmapDescriptorFactory.fromResource(R.drawable.ic_tracking);
                        // BitmapDescriptor ico_dest=BitmapDescriptorFactory.fromResource(R.drawable.ic_walkto);
                        LatLng kohuwala=new LatLng(6.9122,79.8829);



                        MarkerOptions currentLocation=new MarkerOptions().icon(ico_curret).
                                draggable(false).position(customerLocation).title("You are here");//marker thygnna map eka uda
                        MarkerOptions destinationLocation=new MarkerOptions().icon(ico_dest).
                                draggable(false).position(kohuwala).title("Shop is here");//""


                        CurrentgoogleMap.addMarker(currentLocation); //add marker
                        CurrentgoogleMap.addMarker(destinationLocation);//camera focus
                        CurrentgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));
                        CurrentgoogleMap.moveCamera(CameraUpdateFactory.zoomTo(12));//zoom relavant location

                        setPolyline(kohuwala,customerLocation);

                        CurrentgoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                                Log.d(TAG,"marker drag start");
                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                                Log.d(TAG,"marker drag");
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                Log.d(TAG,"marker drag end");
                                dropLocation=marker.getPosition();//latitiude and longitude obj eka set krgnnawa
                                // CurrentgoogleMap.addPolyline(new PolylineOptions().add(customerLocation,dropLocation));
 //url and method
                            }
                        });

                    }else{
                        Toast.makeText(MapsFragment.super.getContext(),
                                "location not found!",Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MapsFragment.super.getContext(),"error "+e.getMessage() ,Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void setPolyline (LatLng customer,LatLng my){
            new FetchURL() {
                @Override
                public void onTaskDone(Object... values) {
                    if(currentPolyline!=null){
                        currentPolyline.remove();
                    }
                    currentPolyline=CurrentgoogleMap.addPolyline((PolylineOptions) values[0]);
                }
            }.execute(getUrl(customer,my,"driving"),"driving");
        }

        private BitmapDescriptor getBitMapDesc(FragmentActivity activity, int ic_tracking) {
            Drawable LAYER_1= ContextCompat.getDrawable(activity,ic_tracking);
            LAYER_1.setBounds(0,0,LAYER_1.getIntrinsicWidth(),LAYER_1.getIntrinsicHeight());
            Bitmap bitmap=Bitmap.createBitmap(LAYER_1.getIntrinsicWidth(),LAYER_1.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bitmap);
            LAYER_1.draw(canvas);// canvas eka draw wenwa
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_maps, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }
        }

        private String getUrl(LatLng origin, LatLng dest, String directionMode) {
            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            // Mode
            String mode = "mode=" + directionMode;
            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest + "&" + mode;
            // Output format
            String output = "json";
            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
            Log.d(TAG,"URL:"+url);
            return url;
        }
}