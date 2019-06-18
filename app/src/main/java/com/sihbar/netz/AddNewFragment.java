package com.sihbar.netz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AddNewFragment extends Fragment {

    EditText txtHandle;
    String link;
    String handle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflater/View
        View view = inflater.inflate(R.layout.fragment_add_new_link, null);

        //Handle
        txtHandle = (EditText) view.findViewById(R.id.txtHandle);

        //Dropdown
        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.social_media_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Button
        Button btnConfirm = view.findViewById(R.id.btnConfirm);


        //Spinner selection
        //aka
        //Dropdown selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                link = spinner.getSelectedItem().toString();
                link = link.toLowerCase();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            //NAO APAGAR
            //If este método for apagado = crash
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddHandle();
            }
        });

        return view;
    }

    public void AddHandle(){

        handle = txtHandle.getText().toString();
        Intent intent = new Intent(getActivity(), SaveNewLink.class);
        intent.putExtra("app", link);
        intent.putExtra("handle", handle);
        startActivity(intent);
    }


}