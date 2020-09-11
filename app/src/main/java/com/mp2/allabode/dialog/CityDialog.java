package com.mp2.allabode.dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.databse.FlatEntity;

import java.util.ArrayList;
import java.util.List;


public class CityDialog extends DialogFragment {

    public CityDialog(OnCityChanged onCityChanged, List<String> list) {
        this.onCityChanged = onCityChanged;
        this.list = list;
    }

    private List<String> list;
    private TextView txtBack,txtSave;
    private Spinner spnCity;
    private OnCityChanged onCityChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_dialog, container, false);

        txtBack = view.findViewById(R.id.txtCityBack);
        txtSave = view.findViewById(R.id.txtCitySave);
        spnCity = view.findViewById(R.id.spnCity);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        spnCity.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.my_spinner_text,list));
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spnCity.getSelectedItemPosition() == 0){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.spinner_light));
                }else{
                    ((TextView)view).setTextColor(getResources().getColor(R.color.spinner_dark));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = spnCity.getSelectedItem().toString();
                if(spnCity.getSelectedItemPosition() == 0){
                    makeErrorToast("Select City",null,"");
                    spnCity.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else{
                    onCityChanged.onCityChanged(city);
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface OnCityChanged{
        void onCityChanged(String city);
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast, null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            editText.clearFocus();
        }
    }
}
