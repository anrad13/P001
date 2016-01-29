package ru.anrad.p001.core;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.anrad.p001.R;

public class DutyStateDialog extends DialogFragment {

    private DutyStateDialogListener listener;
    private Button bActive;
    private Button bArhive;
    private Button bTrash;


    public interface DutyStateDialogListener {
        void onSetDuty(Duty d);
        Duty onGetDuty();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DutyStateDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement DutyStateDialogListener");
        }

    }

    private Duty duty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_dutystate, container);

        bActive = (Button) v.findViewById(R.id.dialog_dutystate_to_active);
        bArhive = (Button) v.findViewById(R.id.dialog_dutystate_to_arhive);
        bTrash = (Button) v.findViewById(R.id.dialog_dutystate_to_trash);
        Button bCancel = (Button) v.findViewById(R.id.dialog_dutystate_cancel);

        bCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DutyStateDialog", "to cancel: ");
                        dismiss();
                    }
                }
        );

        bActive.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DutyStateDialog", "to active: ");
                        duty.setActive();
                        listener.onSetDuty(duty);
                        dismiss();
                    }
                }
        );
        bArhive.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DutyStateDialog", "to arhive: " );
                        duty.setArhive();
                        listener.onSetDuty(duty);
                        dismiss();
                    }
                }
        );
        bTrash.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("DutyStateDialog", "to trash: ");
                        duty.setTrash();
                        listener.onSetDuty(duty);
                        dismiss();
                    }
                }
        );

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        duty = listener.onGetDuty();
        // set visible button
        if (duty.isActive()) {
            bActive.setVisibility(View.GONE);
            bArhive.setVisibility(View.VISIBLE);
            bTrash.setVisibility(View.VISIBLE);
        }
        else if (duty.isArhive()) {
            bActive.setVisibility(View.VISIBLE);
            bArhive.setVisibility(View.GONE);
            bTrash.setVisibility(View.VISIBLE);
        }
        else if (duty.isTrash()) {
            bActive.setVisibility(View.VISIBLE);
            bArhive.setVisibility(View.VISIBLE);
            bTrash.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("DutyStateDialog", "Dialog 1: onDismiss");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d("DutyStateDialog", "Dialog 1: onCancel");
    }
}

