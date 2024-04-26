package com.passwordmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddPasswordDialog extends AppCompatDialogFragment {

    private EditText titleEditText;
    private EditText passwordEditText;
    private AddPasswordDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_password, null);

        titleEditText = view.findViewById(R.id.titleEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        builder.setView(view)
                .setTitle("Add Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel button clicked
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = titleEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        if (listener != null) {
                            listener.onPasswordAdded(title, password);
                        }
                    }
                });

        return builder.create();
    }

    public void setListener(AddPasswordDialogListener listener) {
        this.listener = listener;
    }

    public interface AddPasswordDialogListener {
        void onPasswordAdded(String title, String password);
    }
}
