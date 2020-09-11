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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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


public class SProfileFragment extends Fragment implements RequestInterface {

    public SProfileFragment() {
        // Required empty public constructor
    }

    private TextView txtName,txtEmail,txtMobile,txtUni,txtAge;
    private ImageView imgSP,imgLogout;
    private SharedPreferences sharedPreferences;
    private NestedScrollView nsvSP;
    private LinearLayout llNo;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_s_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        txtName = view.findViewById(R.id.txtSPName);
        txtEmail = view.findViewById(R.id.txtSPEmail);
        txtMobile = view.findViewById(R.id.txtSPMobile);
        txtUni = view.findViewById(R.id.txtSPUniversity);
        imgSP = view.findViewById(R.id.imgSP);
        imgLogout = view.findViewById(R.id.imgSPOut);
        nsvSP = view.findViewById(R.id.nsvSP);
        llNo = view.findViewById(R.id.llSPNoReq);
        recyclerView = view.findViewById(R.id.recyclerSPReq);

        llNo.setVisibility(View.GONE);
        nsvSP.setVisibility(View.GONE);
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
        txtUni.setText("University : "+getSharedPrefData("uni"));

        List<RequestEntity> getRequest = Room.databaseBuilder(getActivity(), RequestDatabase.class,"request")
                .allowMainThreadQueries().build().requestDao().getRequestByStudent(getSharedPrefData("mobile"));

        if(getRequest.isEmpty()){
            llNo.setVisibility(View.VISIBLE);
        }else{
            List<RequestEntity> myReq = Room.databaseBuilder(getActivity(),RequestDatabase.class,"request")
                    .allowMainThreadQueries().build().requestDao().getRequestByStudent(sharedPreferences.getString("mobile",""));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            RequestAdapter requestAdapter = new RequestAdapter(getActivity(),myReq,SProfileFragment.this);
            recyclerView.setVisibility(View.GONE);
            recyclerView.setAdapter(requestAdapter);
            recyclerView.setLayoutManager(layoutManager);

        }


        Picasso.get().load(getSharedPrefData("image")).error(R.drawable.avatar).into(imgSP, new Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                nsvSP.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                nsvSP.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                nsvSP.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                nsvSP.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onReject(@NotNull RequestEntity requestEntity) {

    }
}
