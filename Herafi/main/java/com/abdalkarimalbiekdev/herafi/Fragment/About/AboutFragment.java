package com.abdalkarimalbiekdev.herafi.Fragment.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdalkarimalbiekdev.herafi.R;
import com.abdalkarimalbiekdev.herafi.viewModel.AboutViewModel;
import com.abdalkarimalbiekdev.herafi.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private AboutViewModel aboutViewModel;
    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater , R.layout.fragment_about , container , false);
//        View root = inflater.inflate(R.layout.fragment_about, container, false);

        binding.txtPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("......................................................."));
                startActivity(intent);
            }
        });

        binding.txtTermsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("..................................................."));
                startActivity(intent);
            }
        });


        return binding.getRoot();

    }


}