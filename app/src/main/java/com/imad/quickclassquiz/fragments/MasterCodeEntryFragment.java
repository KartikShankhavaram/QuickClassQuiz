package com.imad.quickclassquiz.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imad.quickclassquiz.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MasterCodeEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasterCodeEntryFragment extends Fragment {

    public MasterCodeEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MasterCodeEntryFragment.
     */
    public static MasterCodeEntryFragment newInstance() {
        MasterCodeEntryFragment fragment = new MasterCodeEntryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_master_code_entry, container, false);
    }

}
