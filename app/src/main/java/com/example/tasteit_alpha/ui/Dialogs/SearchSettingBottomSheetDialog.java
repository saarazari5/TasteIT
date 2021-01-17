package com.example.tasteit_alpha.ui.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tasteit_alpha.Activities.MainActivity;
import com.example.tasteit_alpha.R;
import com.example.tasteit_alpha.ui.Adapters.IndicatorSeekBarAdapter;
import com.example.tasteit_alpha.ui.home.StateChosenObserver;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.warkiz.widget.IndicatorSeekBar;

import static com.example.tasteit_alpha.Utils.AppUtils.DISTANCE_TAG;
import static com.example.tasteit_alpha.Utils.AppUtils.STATE_TAG;
import static com.example.tasteit_alpha.ui.home.HomeFragment.QueryStates;

public class SearchSettingBottomSheetDialog extends BottomSheetDialogFragment {
    private QueryStates queryState;
    private StateChosenObserver observer;
    private int distance;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        observer = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.bottom_sheet_home_settings , container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL , R.style.BottomSheetDialogStyle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchDataFromFatherFragment();
    }

    private void fetchDataFromFatherFragment() {
        Bundle bundle = getArguments();
        if(getArguments()==null) onDestroy();
        int distancePreference = bundle.getInt(DISTANCE_TAG);
        distance=distancePreference;

        String state = bundle.getString(STATE_TAG);
        RadioGroup rg = requireView().findViewById(R.id.radioGroup);
        if(state.equals(QueryStates.DISTANCE.name())){
            ((RadioButton)rg.getChildAt(0)).setChecked(true);
            queryState=QueryStates.DISTANCE;
        }else {
            ((RadioButton)rg.getChildAt(1)).setChecked(true);
            queryState=QueryStates.LOCALITY;
        }


        IndicatorSeekBar indicatorSeekBar = requireView().findViewById(R.id.indicatorSeekBar);
        indicatorSeekBar.setProgress(distancePreference);
        indicatorSeekBar.setOnSeekChangeListener(new IndicatorSeekBarAdapter() {
            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                distance=seekBar.getProgress();
            }
        });


        rg.setOnCheckedChangeListener((radioGroup, id) -> {
            RadioButton radioButton = radioGroup.findViewById(id);
            switch (radioGroup.indexOfChild(radioButton)){
                case 0 : {
                    queryState=QueryStates.DISTANCE;
                }
                case 1 : {
                    queryState=QueryStates.LOCALITY;
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
       observer.onChose(queryState,distance);
    }
}





