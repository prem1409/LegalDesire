package com.example.msp.legaldesire.Admin_Module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.msp.legaldesire.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by MSP on 5/8/2017.
 */

public class Lawyer_Module2_Adapter extends ArrayAdapter<String> {
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Lawyers");
    ;


    public Lawyer_Module2_Adapter(Context context, ArrayList<String> name, ArrayList<String> email, ArrayList<String> address
            , ArrayList<Double> rating, ArrayList<String> type, ArrayList<Double> latitude, ArrayList<Double> longitude) {
        super(context, R.layout.lawyer_module2_adapter, name);
        this.context = context;
        this.name = name;
        this.email = email;
        this.address = address;
        this.rating = rating;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public class ViewHolder {
        EditText edit_name;
        EditText edit_email;
        EditText edit_address;
        EditText edit_rating;
        EditText edit_latitude;
        EditText edit_longitude;
        Spinner spinner;
        Button mEdit, mSave;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lawyer_module2_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.edit_name = (EditText) convertView.findViewById(R.id.edit_namefield2);
        holder.edit_email = (EditText) convertView.findViewById(R.id.edit_emailfield2);
        holder.edit_address = (EditText) convertView.findViewById(R.id.edit_addressfield2);
        holder.edit_rating = (EditText) convertView.findViewById(R.id.edit_ratingfield2);
        holder.edit_latitude = (EditText) convertView.findViewById(R.id.edit_latitudefield2);
        holder.edit_longitude = (EditText) convertView.findViewById(R.id.edit_longitudefield2);
        holder.spinner = (Spinner) convertView.findViewById(R.id.dialog_type2);
        holder.mEdit = (Button) convertView.findViewById(R.id.btn_edit);
        holder.mSave = (Button) convertView.findViewById(R.id.btn_save);

     /*   holder.edit_name.setEnabled(false);
        holder.edit_email.setEnabled(false);
        holder.edit_address.setEnabled(false);
        holder.edit_rating.setEnabled(false);
        holder.edit_latitude.setEnabled(false);
        holder.edit_longitude.setEnabled(false);
        holder.spinner.setEnabled(false);
        holder.mSave.setEnabled(false);  */

        holder.edit_name.setText(name.get(position));
        holder.edit_email.setText(email.get(position));
        holder.edit_address.setText(address.get(position));
        holder.edit_rating.setText(rating.get(position).toString());
        holder.edit_latitude.setText(latitude.get(position).toString());
        holder.edit_longitude.setText(longitude.get(position).toString());
        if (type.get(position).equals("---------"))
            holder.spinner.setSelection(0);

        else if (type.get(position).equals("Civil"))
            holder.spinner.setSelection(1);
        else if (type.get(position).equals("Corporate"))
            holder.spinner.setSelection(2);
        else if (type.get(position).equals("Criminal"))
            holder.spinner.setSelection(3);

        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.edit_name.setEnabled(true);
                holder.edit_email.setEnabled(true);
                holder.edit_address.setEnabled(true);
                holder.edit_rating.setEnabled(true);
                holder.edit_latitude.setEnabled(true);
                holder.edit_longitude.setEnabled(true);
                holder.spinner.setEnabled(true);
                holder.mSave.setEnabled(true);

            }
        });

        holder.mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.set(position, holder.edit_name.getText().toString());
                email.set(position, holder.edit_email.getText().toString());
                address.set(position, holder.edit_address.getText().toString());
                //  String str = holder.edit_rating.getText().toString();
                rating.set(position, Double.parseDouble(holder.edit_rating.getText().toString()));
                type.set(position, holder.spinner.getSelectedItem().toString());
                latitude.set(position, Double.parseDouble(holder.edit_latitude.getText().toString()));
                longitude.set(position, Double.parseDouble(holder.edit_longitude.getText().toString()));


              /*  holder.edit_name.setEnabled(false);
                holder.edit_email.setEnabled(false);
                holder.edit_address.setEnabled(false);
                holder.edit_rating.setEnabled(false);
                holder.edit_latitude.setEnabled(false);
                holder.edit_longitude.setEnabled(false);
                holder.spinner.setEnabled(false);
                holder.mSave.setEnabled(false);
                holder.mSave.setEnabled(false);*/
            }
        });

        return convertView;
    }
}

