package com.g.barc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class MainActivity extends AppCompatActivity {

    Button button;
    ImageView image;
    TextView txtView;
    private CameraKitView cameraKitView;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        txtView = findViewById(R.id.txtContent);
        image = findViewById(R.id.img);

        cameraKitView = findViewById(R.id.camera);
        verifyStoragePermissions(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        String s = String.valueOf(System.currentTimeMillis());
                        File savedphoto = new File(Environment.getExternalStorageDirectory(), s + "photo.jpg");
                        try {
                            FileOutputStream outputStream = new FileOutputStream(savedphoto.getPath());
                            outputStream.write(bytes);
                            outputStream.close();

                            final Bitmap bit = BitmapFactory.decodeFile(savedphoto.getAbsolutePath());


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    image.setImageBitmap(bit);
                                }
                            });

                            FirebaseVisionImage img = FirebaseVisionImage.fromBitmap(bit);
                            FirebaseVisionBarcodeDetector detect = FirebaseVision.getInstance()
                                    .getVisionBarcodeDetector();


                            Task<List<FirebaseVisionBarcode>> result = detect.detectInImage(img)
                                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                            // Task completed successfully
                                            // ...

                                            for (FirebaseVisionBarcode barcode : barcodes) {
                                                Rect bounds = barcode.getBoundingBox();
                                                Point[] corners = barcode.getCornerPoints();

                                                String raw = barcode.getRawValue();
                                                System.out.println(raw);
                                                int ValueType = barcode.getValueType();
                                                System.out.println(ValueType);
                                                System.out.println(FirebaseVisionBarcode.TYPE_PRODUCT);


                                                switch (ValueType) {
                                                    case FirebaseVisionBarcode.TYPE_WIFI:
                                                        String ssid = barcode.getWifi().getSsid();
                                                        String password = barcode.getWifi().getPassword();
                                                        int type = barcode.getWifi().getEncryptionType();

                                                        Toast.makeText(MainActivity.this, ssid + " " + password + " " + type, Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case FirebaseVisionBarcode.TYPE_URL:
                                                        String title = barcode.getUrl().getTitle();
                                                        String url = barcode.getUrl().getUrl();

                                                        Toast.makeText(MainActivity.this, title + " " + url, Toast.LENGTH_SHORT).show();
                                                        break;

                                                    case FirebaseVisionBarcode.TYPE_PRODUCT:
                                                        String a = barcode.getDisplayValue();
                                                        Toast.makeText(MainActivity.this, a, Toast.LENGTH_SHORT).show();


                                                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                                                        String url2 = "https://api.upcitemdb.com/prod/trial/lookup?upc=" + a;
                                                        System.out.println(url2);



                                                        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url2, null,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        // response

                                                                        try {

                                                                            JSONArray items = response.getJSONArray("items");
                                                                            JSONObject jo = (JSONObject) items.get(0);

                                                                            System.out.println("he"+jo.getString("upc"));
                                                                            JSONArray offers = jo.getJSONArray("offers");
                                                                            JSONObject j = (JSONObject) offers.get(0);

                                                                            JSONArray image = jo.getJSONArray("images");
                                                                            String jsonObject = (String) image.get(0);

                                                                            if (response != null) {
                                                                                Intent intent = new Intent(MainActivity.this, Details.class);
                                                                                intent.putExtra("name", jo.getString("title"));
                                                                                intent.putExtra("brand_name", jo.getString("brand"));
                                                                                intent.putExtra("price", "$" + j.getString("price"));
                                                                                intent.putExtra("image", jsonObject);
                                                                                startActivity(intent);
                                                                            }

                                                                        } catch (Exception e) {
                                                                            e.getMessage();
                                                                        }

                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        // TODO Auto-generated method stub
                                                                        Log.d("ERROR", "error => " + error.toString());
                                                                    }
                                                                }
                                                        ) {
                                                            @Override
                                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                                //params.put("user-key", "5f718be08d069711b39c5f38c2b7ebb1");
//                                                                params.put("Accept", "application/json");

                                                                return new HashMap<String, String>();
                                                            }
                                                        };
                                                        queue.add(postRequest);

                                                }


                                            }

                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...

                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            System.out.println("hey" + e.getMessage());
                                        }
                                    });

                        } catch (IOException e) {
                            System.out.println("hey" + e.getMessage());
                        }
                    }
                });
            }
        });



    }

    @Override
    protected void onResume() {

        super.onResume();
        cameraKitView.onResume();

    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
