package com.pank.pankapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.pank.pankapp.R;

public class DashboardFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        this.initView();
        this.setOnCLickListener();

        return this.view;
    }

    private void initView() {

    }

    private void setOnCLickListener() {

    }


}
