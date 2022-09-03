package com.example.netflex_movie_api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmActivity_Add extends AppCompatActivity implements FilmActivity_DialogFragment_ActorChooser.DialogListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener{
    ImageView iv_film_add, btnchoose_actor_film_add;
    Button btnsubmit_actor_film_add, btn_show_actor_sp;
    TextView tv_name_actor_film_add;
    TextView et_title_film_add, et_duration_film_add, et_story_film_add, et_date_film_add;
    ImageButton imageButton, btncapture_film_add, btnchoose_film_add ;
    Spinner spnr_genre_film_add, spnr_producer_film_add;
    EditText spn_tv_genre_film_add, spn_tv_producer_film_add;

    Bitmap bitmap;

    private String filmURLadd = "http://192.168.68.103:8000/api/film/";
    private String genreURL = "http://192.168.68.103:8000/api/genre/";
    private String producerURL = "http://192.168.68.103:8000/api/producer/";
    private URL myURL;

    private List<String> genre_list;
    private List<String> producer_list;
    private List<String> actor_list_data;


    SharedPreferences sp;
    private String accessToken;

    @Override
    public void actor_names(String names) {
        tv_name_actor_film_add.setText(names);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film__add);

        //GETTING ACCESS TOKEN USING SHARED PREFERENCE
        sp = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");
        //Toast.makeText(this, accessToken, Toast.LENGTH_SHORT).show();

        iv_film_add = (ImageView)findViewById(R.id.iv_film_add);
        btnchoose_actor_film_add = (ImageView)findViewById(R.id.btnchoose_actor_film_add);
        btnchoose_film_add = (ImageButton)findViewById(R.id.btnchoose_film_add);
        btncapture_film_add = (ImageButton)findViewById(R.id.btncapture_film_add);
        btnsubmit_actor_film_add = (Button)findViewById(R.id.btnsubmit_actor_film_add);

        tv_name_actor_film_add = (TextView)findViewById(R.id.tv_name_actor_film_add);

        et_title_film_add = (TextView)findViewById(R.id.et_title_film_add);
        et_duration_film_add = (TextView)findViewById(R.id.et_duration_film_add);
        et_story_film_add = (TextView)findViewById(R.id.et_story_film_add);
        et_date_film_add = (TextView)findViewById(R.id.et_date_film_add);

        imageButton = (ImageButton)findViewById(R.id.imageButton);

        spnr_genre_film_add = (Spinner)findViewById(R.id.spnr_genre_film_add);
        spnr_producer_film_add = (Spinner)findViewById(R.id.spnr_producer_film_add);

        spn_tv_genre_film_add = (EditText) findViewById(R.id.spn_tv_genre_film_add);
        spn_tv_producer_film_add = (EditText) findViewById(R.id.spn_tv_producer_film_add);

        getGenre();
        getProducer();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataePicker();
            }
        });

        //PANG TESTING LANG TO IDEDELTE KO DIN
//        btn_show_actor_sp = (Button)findViewById(R.id.btn_show_actor_sp);
//        btn_show_actor_sp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Getting Actors name from shared preference, this this it is a single string with commas
//                //but Ill use split to separate each name
//                String[] names = sp.getString("Actors_to_movie","").split("\\,");
//
//                //creating ang arralist, intended to be populated by names
//                actor_list_data = new ArrayList<String>();
//                //looping throurg names and append it to the array
//                for(String name: names){
//                    //Toast.makeText(FilmActivity_Add.this, name, Toast.LENGTH_SHORT).show();
//                    actor_list_data.add(name.toUpperCase() + " ");
//                }
//                Toast.makeText(FilmActivity_Add.this, sp.getString("Actors_to_movie_id",""), Toast.LENGTH_SHORT).show();
//                Toast.makeText(FilmActivity_Add.this, actor_list_data.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
        //=========================================================================================
        //ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD
        btnsubmit_actor_film_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovie();
                //DELETEING SHARED PREFERENCE VALUE FOR ACTOR NAME AND ID
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Actors_to_movie_id", "");
                editor.putString("Actors_to_movie", "");
                editor.commit();
            }
        });
        //ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD   ADD

        //OPEN GALLERY  OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY
        btnchoose_film_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        //^^^OPEN GALLERY  OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY    OPEN GALLERY
        //Request for camera permission
        if(ContextCompat.checkSelfPermission(FilmActivity_Add.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((FilmActivity_Add.this),
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        //==========================================================================================
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA
        btncapture_film_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
        //OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA       OPEN CAMERA
        //==========================================================================================
        //OPEN ACTOR CHOOSER    OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER
        btnchoose_actor_film_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_name_actor_film_add.setText("Please insert actor");
                //DELETEING SHARED PREFERENCE VALUE FOR ACTOR NAME AND ID
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Actors_to_movie_id", "");
                editor.putString("Actors_to_movie", "");
                editor.commit();

                FilmActivity_DialogFragment_ActorChooser filmActivity_dialogFragment_actorChooser = new FilmActivity_DialogFragment_ActorChooser();
                filmActivity_dialogFragment_actorChooser.show(getSupportFragmentManager(), "CHOOSE ACTOR");
            }
        });
        //^^^OPEN ACTOR CHOOSER    OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER  OPEN ACTOR CHOOSER

    }//END onCreate

    private void showDataePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = year+"-"+month+"-"+dayOfMonth;
        et_date_film_add.setText(date);
    }

    public void getGenre(){
        try {
            myURL = new URL(genreURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Log.i("url","url"+ myURL);
        //RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        // stringRequest=new StringRequest(Request.Method.GET,
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                genreURL,
                null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                String token = accessToken;
                try {
                    genre_list = new ArrayList<String>();
                    JSONArray array=response.getJSONArray("data");
                    for (int i=0; i<array.length(); i++ ){
                        JSONObject ob=array.getJSONObject(i);
                        genre_list.add(ob.getString("genre"));
                    }

                    ArrayAdapter<String> genre_adapter = new ArrayAdapter<String>(FilmActivity_Add.this, android.R.layout.simple_spinner_item, genre_list);
                    genre_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnr_genre_film_add.setAdapter(genre_adapter);
                    spnr_genre_film_add.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) FilmActivity_Add.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);

    }

    public void getProducer(){
        try {
            myURL = new URL(producerURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //Log.i("url","url"+ myURL);
        //RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        // stringRequest=new StringRequest(Request.Method.GET,
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                producerURL,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String token = accessToken;
                        try {
                            producer_list = new ArrayList<String>();
                            JSONArray array=response.getJSONArray("data");
                            for (int i=0; i<array.length(); i++ ){
                                JSONObject ob=array.getJSONObject(i);
                                producer_list.add(ob.getString("name"));
                            }

                            ArrayAdapter<String> producer_adapter = new ArrayAdapter<String>(FilmActivity_Add.this, android.R.layout.simple_spinner_item, producer_list);
                            producer_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnr_producer_film_add.setAdapter(producer_adapter);
                            spnr_producer_film_add.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) FilmActivity_Add.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ accessToken);
                return params;
            }


        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonRequest);

    }

    public void addMovie(){
        //creating json object
        JSONObject jsonItem = new JSONObject();
        //putting datum/data to the newly created json object
        try {
            jsonItem.put("title", et_title_film_add.getText());
            jsonItem.put("story", et_story_film_add.getText());
            jsonItem.put("duration", et_duration_film_add.getText());
            jsonItem.put("date", et_date_film_add.getText());
            jsonItem.put("uploads", getStringImage(bitmap));
            jsonItem.put("actor_ids", sp.getString("Actors_to_movie_id",""));

            jsonItem.put("producer", spn_tv_producer_film_add.getText());

            jsonItem.put("genre", spn_tv_genre_film_add.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //checking url
        try {
            myURL = new URL(filmURLadd);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //initialize a new request queue instance
        //to prevent multiple request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,     //kung anong type of request
                filmURLadd,                  //url na pupuntahan nung request
                jsonItem,                   //data na ilalagay sa request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                        showToast("MOVIE SAVED", R.drawable.ic_movie_add);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spnr_genre_film_add = (Spinner)parent;
        Spinner spnr_producer_film_add = (Spinner)parent;

        if(spnr_genre_film_add.getId() == R.id.spnr_genre_film_add){
            String text = parent.getItemAtPosition(position).toString();
            spn_tv_genre_film_add.setText(text);
        }

        if(spnr_producer_film_add.getId() == R.id.spnr_producer_film_add){
            String text2 = parent.getItemAtPosition(position).toString();
            spn_tv_producer_film_add.setText(text2);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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
                Picasso.get().load(filePath).fit().centerCrop().into(iv_film_add);
                //Toast.makeText(FilmActivity_Add.this, bitmap.toString(), Toast.LENGTH_SHORT).show();
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
                Picasso.get().load(tempUri).fit().centerCrop().into(iv_film_add);
                //Toast.makeText(FilmActivity_Add.this, bitmap.toString(), Toast.LENGTH_SHORT).show();
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