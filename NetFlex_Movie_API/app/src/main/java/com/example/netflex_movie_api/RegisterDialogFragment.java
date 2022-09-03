package com.example.netflex_movie_api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDialogFragment extends AppCompatDialogFragment {
    private EditText etname_reg, etemail_reg, etpassword_reg, etpassword_confirm_reg;
    private Button btnregister_confirm;

    private String RegisterURL = "http://192.168.68.103:8000/api/auth/register";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_register, null);

        etname_reg = view.findViewById(R.id.etname_reg);
        etemail_reg = view.findViewById(R.id.etemail_reg);
        etpassword_reg = view.findViewById(R.id.etpassword_reg);
        etpassword_confirm_reg = view.findViewById(R.id.etpassword_confirm_reg);
        btnregister_confirm = (Button) view.findViewById(R.id.btnregister_confirm);

        builder.setView(view)
                .setTitle("REGISTER ACCOUNT")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        btnregister_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating json object
                JSONObject jsonItem = new JSONObject();

                //putting datum/data to the newly created json object
                try {
                    jsonItem.put("name", etname_reg.getText());
                    jsonItem.put("email", etemail_reg.getText());
                    jsonItem.put("password", etpassword_reg.getText());
                    jsonItem.put("password_confirmation", etpassword_confirm_reg.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //initialize a new request queue instance
                //to prevent multiple request
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,    //kung anong type of request
                        RegisterURL,               //url na pupuntahan nung request
                        jsonItem,               //data na ilalagay sa request
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String message = response.getString("message");
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                    etname_reg.setText("");
                                    etemail_reg.setText("");
                                    etpassword_reg.setText("");
                                    etpassword_confirm_reg.setText("");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        (error)->{
                            Toast.makeText(getActivity().getApplicationContext(), "MAY MALING NANGYAYARE!!", Toast.LENGTH_LONG).show();

                        }
                );

                //adding
                //sending the request
                requestQueue.add(jsonObjectRequest);

            }
        });

        return  builder.create();
    }
}
