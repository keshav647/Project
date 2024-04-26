package com.passwordmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class PasswordParametersDialog extends AppCompatDialogFragment {

    private EditText editTextLength;
    private CheckBox checkBoxUppercase;
    private CheckBox checkBoxLowercase;
    private CheckBox checkBoxDigits;
    private PasswordParametersDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password_parameters, null);

        editTextLength = dialogView.findViewById(R.id.editText_length);
        checkBoxUppercase = dialogView.findViewById(R.id.checkBox_uppercase);
        checkBoxLowercase = dialogView.findViewById(R.id.checkBox_lowercase);
        checkBoxDigits = dialogView.findViewById(R.id.checkBox_digits);

        builder.setView(dialogView)
                .setTitle("Password Parameters")
                .setPositiveButton("Generate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int length = Integer.parseInt(editTextLength.getText().toString());
                        boolean includeUppercase = checkBoxUppercase.isChecked();
                        boolean includeLowercase = checkBoxLowercase.isChecked();
                        boolean includeDigits = checkBoxDigits.isChecked();

                        if (!includeUppercase && !includeLowercase && !includeDigits) {
                            // If none of the checkboxes are selected, include both uppercase and lowercase by default
                            includeUppercase = true;
                            includeLowercase = true;
                        }

                        listener.onParametersSelected(length, includeUppercase, includeLowercase, includeDigits);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (PasswordParametersDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement PasswordParametersDialogListener");
        }
    }

    public interface PasswordParametersDialogListener {
        void onParametersSelected(int length, boolean includeUppercase, boolean includeLowercase, boolean includeDigits);
    }
}
