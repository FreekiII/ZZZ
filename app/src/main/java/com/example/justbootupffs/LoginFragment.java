package com.example.justbootupffs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private View view;
    private EditText textPassword, textEmail;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        init();
        return view;
    }

    private void init() {
        textPassword = view.findViewById(R.id.editTextPassword);
        textEmail = view.findViewById(R.id.editTextEmail);
    }
}