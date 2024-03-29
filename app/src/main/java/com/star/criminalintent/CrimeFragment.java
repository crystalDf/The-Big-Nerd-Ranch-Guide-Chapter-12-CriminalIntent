package com.star.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSelectButton;
    private CheckBox mSolvedCheckBox;

    public static final String EXTRA_CRIME_ID = "com.star.criminalintent.crime_id";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final String DIALOG_SELECT = "select";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_SELECT = 2;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    public void updateDate() {
        mDateButton.setText(mCrime.getFormatedDate());
    }

    public void updateTime() {
        mTimeButton.setText(mCrime.getFormatedTime());
    }

    public void updateDateAndTime() {
        mSelectButton.setText(mCrime.getFormatedDate() + " " + mCrime.getFormatedTime());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
//        mDateButton.setText(mCrime.getFormatedDate());
        updateDate();
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        mSelectButton = (Button) v.findViewById(R.id.select);
        updateDateAndTime();
        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DateTimeSelectFragment dialog = DateTimeSelectFragment.newInstance(mCrime.getId());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_SELECT);
                dialog.show(fm, DIALOG_SELECT);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DATE) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
//            mDateButton.setText(mCrime.getFormatedDate());
                updateDate();
            } else if (requestCode == REQUEST_TIME) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateTime();
            } else if (requestCode == REQUEST_SELECT) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                updateTime();
                updateDateAndTime();
            }
        }
    }
}
