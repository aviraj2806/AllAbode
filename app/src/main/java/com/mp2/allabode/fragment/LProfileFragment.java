package com.mp2.allabode.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.ValueIterator;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.activity.AuthActivity;
import com.mp2.allabode.adapter.RequestAdapter;
import com.mp2.allabode.adapter.RequestInterface;
import com.mp2.allabode.databse.FlatDatabase;
import com.mp2.allabode.databse.FlatEntity;
import com.mp2.allabode.databse.RequestDatabase;
import com.mp2.allabode.databse.RequestEntity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class LProfileFragment extends Fragment implements RequestInterface {

    public LProfileFragment() {
        // Required empty public constructor
    }

    private TextView txtName,txtEmail,txtMobile,txtFlats,txtRequests;
    private ImageView imgLP,imgLogout;
    private SharedPreferences sharedPreferences;
    private NestedScrollView nsvLP;
    private LinearLayout llNo;
    private RecyclerView recyclerView;
    List<RequestEntity> myReq;
    RequestAdapter requestAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_l_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        txtName = view.findViewById(R.id.txtLPName);
        txtEmail = view.findViewById(R.id.txtLPEmail);
        txtMobile = view.findViewById(R.id.txtLPMobile);
        txtFlats = view.findViewById(R.id.txtLPFlats);
        txtRequests = view.findViewById(R.id.txtLPRequests);
        imgLP = view.findViewById(R.id.imgLP);
        imgLogout = view.findViewById(R.id.imgLPOut);
        nsvLP = view.findViewById(R.id.nsvLP);
        llNo = view.findViewById(R.id.llLPNoReq);
        recyclerView = view.findViewById(R.id.recyclerLPReq);

        recyclerView.setVisibility(View.GONE);
        llNo.setVisibility(View.GONE);
        nsvLP.setVisibility(View.GONE);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putBoolean("isLoggedIn",false).apply();
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                getActivity().finish();
            }
        });

        txtName.setText(getSharedPrefData("name"));
        txtEmail.setText(getSharedPrefData("email"));
        txtMobile.setText(getSharedPrefData("mobile"));

        List<FlatEntity> getFlat = Room.databaseBuilder(getActivity(), FlatDatabase.class,"flat")
                .allowMainThreadQueries().build().flatDao().getFlatByOwnerMobile(getSharedPrefData("mobile"));

        List<RequestEntity> getRequest = Room.databaseBuilder(getActivity(), RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().getRequestByOwner(getSharedPrefData("mobile"));

        if(getRequest.isEmpty()){
            llNo.setVisibility(View.VISIBLE);
        }else{
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
             myReq = Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                    .allowMainThreadQueries().build().requestDao().getRequestByOwner(getSharedPrefData("mobile"));
            requestAdapter = new RequestAdapter(getActivity(),myReq,LProfileFragment.this);

            recyclerView.setAdapter(requestAdapter);
            recyclerView.setLayoutManager(layoutManager);
        }

        txtFlats.setText("Total Property : "+getFlat.size());
        txtRequests.setText("Total Request : "+getRequest.size());


        Picasso.get().load(getSharedPrefData("image")).error(R.drawable.avatar).into(imgLP, new Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                nsvLP.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                nsvLP.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_in));
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                nsvLP.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                nsvLP.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_in));
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public String getSharedPrefData(String key){
        return sharedPreferences.getString(key,"");
    }

    @Override
    public void onAccept(@NotNull RequestEntity requestEntity) {
        Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().updateStatus(1,requestEntity.getTimeStamp());
        myReq = Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().getRequestByOwner(getSharedPrefData("mobile"));
        requestAdapter = new RequestAdapter(getActivity(),myReq,LProfileFragment.this);
        recyclerView.setAdapter(requestAdapter);

        makeErrorToast("Request Accepted!",null,"");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(requestEntity.getStudent(),null,"Hola,\nYour request have been accepted by the owner.",null,null);

    }

    @Override
    public void onReject(@NotNull RequestEntity requestEntity) {
        Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().updateStatus(2,requestEntity.getTimeStamp());
        myReq = Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().getRequestByOwner(getSharedPrefData("mobile"));
        requestAdapter = new RequestAdapter(getActivity(),myReq,LProfileFragment.this);
        recyclerView.setAdapter(requestAdapter);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(requestEntity.getStudent(),null,"Your request have been rejected by the owner.\nPlease try with some other property.",null,null);
        makeErrorToast("Request Rejected!",null,"");
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
}
