package com.star.criminalintent;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.UUID;

public class DateTimeSelectFragment extends DialogFragment {

    public static final String EXTRA_CRIME_ID = "com.star.criminalintent.crime_id";

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private Date mDate;

    private Button mDateButton;
    private Button mTimeButton;

    public static DateTimeSelectFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        DateTimeSelectFragment fragment = new DateTimeSelectFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() != null) {
            Intent i = new Intent();

            mDate = mCrime.getDate();

            i.putExtra(DatePickerFragment.EXTRA_DATE, mDate);

            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        }

    }

    public void updateDate() {
        mDateButton.setText(mCrime.getFormatedDate());
    }

    public void updateTime() {
        mTimeButton.setText(mCrime.getFormatedTime());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);

        mCrime = CrimeLab.getCrimeLab(getActivity()).getCrime(crimeId);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_select_time_date, null);

        mDateButton = (Button) v.findViewById(R.id.select_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());

                dialog.setTargetFragment(DateTimeSelectFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.select_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());

                dialog.setTargetFragment(DateTimeSelectFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.select_time_date_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DATE) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
            } else if (requestCode == REQUEST_TIME) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateTime();
            }
        }
    }
}
