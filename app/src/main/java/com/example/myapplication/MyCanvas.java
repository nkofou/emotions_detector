package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class MyCanvas extends View {
    Paint paint;
    String emotion;
    private GestureDetector gestureDetector;
    private Context context;
    private LocationManager mlocManager;
    private LocationListener mlocListener;
    private com.example.myapplication.MainActivity activity;

    private com.example.myapplication.DatabaseConnection db;

    public MyCanvas(Context context, com.example.myapplication.MainActivity activity) {
        super(context);
        this.activity = activity;
        this.context = context;

        paint = new Paint();
        paint.setTypeface(Typeface.create("Arial", Typeface.ITALIC));
        paint.setTextSize(60);

        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,mlocListener);
        db=new com.example.myapplication.DatabaseConnection();
        gestureDetector = new GestureDetector(context, new GestureListener(db));

    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(100);
        //canvas.drawRect(new Rect(0,100,200,300),paint);
        //canvas.drawCircle(540,900,500,paint);
        paint.setColor(Color.GREEN);
        canvas.drawArc(70, 300, 1040, 1400, 0, 180, true, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawArc(70, 300, 1040, 1400, 0, -180, true, paint);
        paint.setColor(Color.RED);
        canvas.drawArc(70, 300, 1040, 1400, 0, -90, true, paint);
        paint.setColor(Color.BLUE);
        canvas.drawArc(70, 300, 1040, 1400, 0, 90, true, paint);
        paint.setColor(Color.BLACK);
        paint.setFontFeatureSettings("Arial black 14");
        canvas.drawText("blue sadness ", 100, 2000, paint);
        canvas.drawText("red anger ", 100, 2050, paint);
        canvas.drawText("green fear ", 100, 2100, paint);
        canvas.drawText("yellow happiness", 100, 2150, paint);

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private com.example.myapplication.DatabaseConnection db;
        public GestureListener(com.example.myapplication.DatabaseConnection db) {
            this.db=db;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            emotion = "none";
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                return true;
            }
            Location location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Geocoder gcd = new Geocoder(context,
                    Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);


            } catch (IOException ee) {
                ee.printStackTrace();
                System.out.println("gps failed");
            }

                String straddress =(addresses!=null) ? addresses.get(0).getAddressLine(0):""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = (addresses!=null) ?addresses.get(0).getLocality():"";
                String state = (addresses!=null) ?addresses.get(0).getAdminArea():"";
                String country = (addresses!=null) ?addresses.get(0).getCountryName():"";
                String postalCode = (addresses!=null) ?addresses.get(0).getPostalCode():"";
                String knownName = (addresses!=null) ?addresses.get(0).getFeatureName():""; // Only if available else return NULL
            if(addresses!=null)
                System.out.println(addresses.toString());
            Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("-> at Location ");
            if(x>555 && y<854 ){
                System.out.println("red");
                alertDialog.setTitle("anger -> at Location ");
                emotion="anger";
            }
            if(x<555 && y<854 ){
                System.out.println("yellow");
                alertDialog.setTitle("happiness -> at Location ");
                emotion="happiness";
            }
            if(x>555 && y>854 ){
                System.out.println("blue");
                alertDialog.setTitle("sadness -> at Location ");
                emotion="sadness";
            }
            if(x<555 && y>854 ){
                System.out.println("green");
                alertDialog.setTitle("fear -> at Location ");
                emotion="fear";
            }

            if(location!=null ){
                if(addresses!=null){
                alertDialog.setMessage(state); //+ " "+location.getLongitude() +" "+location.getLatitude());}
            }
            }
            else{
                alertDialog.setMessage("please enable your location");
            }
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

                if(emotion!=null) {


                    try {


                        db.insertEmotion(emotion, x, y, location.getLongitude(), location.getLatitude(), activity.getX(), activity.getZ(), String.valueOf(activity.getId()));


                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);

                    }

                }


            return true;
        }
    }



        public class MyLocationListener implements LocationListener  {
        private Context context;

            public MyLocationListener(Context context) {
                super();
                this.context = context;
            }

            @Override
            public void onLocationChanged(Location loc) {
                /*
                loc.getLatitude();
                loc.getLongitude();

                Geocoder gcd = new Geocoder(context,
                        Locale.getDefault());
                List<Address> mAddresses = null;
                try {
                    mAddresses = gcd.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 1);

                } catch (IOException e) {

                }

                String cityName = (mAddresses != null) ? mAddresses.get(0)
                        .getLocality() : TimeZone.getDefault().getID();
                String countryName = (mAddresses != null) ? mAddresses.get(0)
                        .getCountryName() : Locale.getDefault().getDisplayCountry()
                        .toString();
                */


            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(context, "Gps Disabled",
                        Toast.LENGTH_SHORT).show();
                Log.d("gps","disabled");

            }



            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context, "Gps Enabled",
                        Toast.LENGTH_SHORT).show();
                Log.d("gps","enabled");

            }


        }


}
