package com.example.netflex_movie_api;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorActivity_Add extends AppCompatActivity {
    private Bitmap bitmap;
    private ImageView imageView;
    private EditText etactor_name, etactor_note;

    private Button btnsubmit_actor;
    private ImageButton btnchoose_actor, btncapture_actor;

    private String actorURLadd = "http://192.168.68.103:8000/api/actor/";
    private URL myURL;


    SharedPreferences sp;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_add);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        imageView = (ImageView)findViewById(R.id.iv_actor);

        btnchoose_actor = (ImageButton)findViewById(R.id.btnchoose_actor);
        btncapture_actor = (ImageButton)findViewById(R.id.btncapture_actor);
        btnsubmit_actor = (Button)findViewById(R.id.btnsubmit_actor);

        etactor_name = (EditText)findViewById(R.id.etactor_name);
        etactor_note = (EditText)findViewById(R.id.etactor_note);



        //OPEN GALLERY  OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY
        btnchoose_actor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        //^^^OPEN GALLERY  OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY
        //==========================================================================================
        //ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD
        btnsubmit_actor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                    jsonItem.put("name", etactor_name.getText());
                    jsonItem.put("note", etactor_note.getText());
                    jsonItem.put("uploads", getStringImage(bitmap));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //checking url
                try {
                    myURL = new URL(actorURLadd);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,     //kung anong type of request
                        actorURLadd,                  //url na pupuntahan nung request
                        jsonItem,                   //data na ilalagay sa request
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                finish();
                                showToast("SAVED", R.drawable.ic_actor);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();
                            }
                        }
                ){
                    //ito na ung pag si-set ng authorization token
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", "Bearer "+accessToken);
                        return params;
                    }
                };

                //adding
                //sending the request
                requestQueue.add(jsonObjectRequest);
            }
        });
        //^^^ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD
        //==========================================================================================
        //Request for camera permission
        if(ContextCompat.checkSelfPermission(ActorActivity_Add.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((ActorActivity_Add.this),
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        //==========================================================================================
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA
        btncapture_actor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA

    }//END onCreate langya ang hirap hanapin


    //GETTING BITMAP STRING USING FILE CHOOSER
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //GETTING BITMAP STRING USING CAMERA CAMPTURE
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //FILE CHOOSER METHOD | GALLERY BY DEFAULT
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    //ON AVTIVITY RESULT NG FILE CHOOSER code:1 at IMAGE CAPTURE code:100
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Picasso.get().load(filePath).fit().centerCrop().into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), captureImage);
            //Toast.makeText(this, tempUri.toString(), Toast.LENGTH_SHORT).show();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), tempUri);
                Picasso.get().load(tempUri).fit().centerCrop().into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    //CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST
    public void showToast(String message, int icon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast, (ViewGroup) findViewById(R.id.toast_root));
        TextView toastText = layout.findViewById(R.id.toast_text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(message);
        toastImage.setImageResource(icon);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    //^^^CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAST      CUSTOM TOAS

}
