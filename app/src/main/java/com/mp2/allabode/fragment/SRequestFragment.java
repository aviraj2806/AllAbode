package com.mp2.allabode.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.adapter.PropertyAdapter;
import com.mp2.allabode.databse.FlatDatabase;
import com.mp2.allabode.databse.FlatEntity;
import com.mp2.allabode.databse.RequestDatabase;
import com.mp2.allabode.databse.RequestEntity;
import com.mp2.allabode.dialog.CityDialog;
import com.mp2.allabode.dialog.ExpandImageDialog;
import com.mp2.allabode.dialog.LFirstDialog;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SRequestFragment extends Fragment implements  PropertyAdapter.OnStudentRequest, CityDialog.OnCityChanged {

    public SRequestFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    PropertyAdapter propertyAdapter;
    SharedPreferences sharedPreferences;
    List<FlatEntity> myFlat = new ArrayList<>();
    TextView txtCity;
    LinearLayout llNo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_request, container, false);

        recyclerView = view.findViewById(R.id.recyclerStudentProperty);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        txtCity = view.findViewById(R.id.txtSRCity);
        llNo = view.findViewById(R.id.llNoReq);

        llNo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        myFlat = Room.databaseBuilder(getActivity(), FlatDatabase.class, "flat")
                .allowMainThreadQueries().build().flatDao().getAllFlat();

        if(myFlat.isEmpty()){
            llNo.setVisibility(View.VISIBLE);
        }else {
            propertyAdapter = new PropertyAdapter(getActivity(), myFlat, SRequestFragment.this);

            recyclerView.setAdapter(propertyAdapter);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in));
            recyclerView.setVisibility(View.VISIBLE);
        }
        txtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> cityList = new ArrayList<>();
                cityList.add("Select City");
                List<String> allFlat = Room.databaseBuilder(getActivity(),FlatDatabase.class,"flat")
                        .allowMainThreadQueries().build().flatDao().getAllCity();
                cityList.addAll(allFlat);

                DialogFragment dialogFragment = new CityDialog(SRequestFragment.this,cityList);
                dialogFragment.setCancelable(false);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"CityDialog");
            }
        });

        return view;
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast, null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    public void onStudentRequest(@NotNull final FlatEntity flatEntity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Are you sure?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyYY-MM-dd hh:mm:ss");
                String timeStamp = simpleDateFormat.format(calendar.getTime());

                RequestEntity requestEntity = new RequestEntity(timeStamp,sharedPreferences.getString("mobile",""),
                        flatEntity.getOwnerMobile(),0,flatEntity.getTimeStamp());
                Room.databaseBuilder(getActivity(), RequestDatabase.class,"request")
                        .allowMainThreadQueries().build().requestDao().insertRequest(requestEntity);
                makeErrorToast("Request Sent!",null,"");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sharedPreferences.getString("mobile",""),null,
                        "Your request have been successfully sent.\nThe Owner will reach you sopn.\nThank You for using All Abode",null,null);
                myFlat.remove(flatEntity);
                propertyAdapter.notifyDataSetChanged();

                if(myFlat.isEmpty()){
                    llNo.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.fadein));
                    llNo.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public void onCityChanged(String city) {
        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_out));
        recyclerView.setVisibility(View.GONE);

        myFlat = Room.databaseBuilder(getActivity(), FlatDatabase.class, "flat")
                .allowMainThreadQueries().build().flatDao().getFlatByCity(city);

        if(!myFlat.isEmpty() && llNo.getVisibility() == View.VISIBLE){
            llNo.setVisibility(View.GONE);
        }


        propertyAdapter = new PropertyAdapter(getActivity(),myFlat,SRequestFragment.this);
        recyclerView.setAdapter(propertyAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_in));
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onImageExpand(@NotNull String url) {
        DialogFragment dialogFragment = new ExpandImageDialog(url);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"ImageExpand");
    }
}
