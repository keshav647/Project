package com.passwordmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class SavedPasswordsActivity extends AppCompatActivity implements AddPasswordDialog.AddPasswordDialogListener {

    private Button addPasswordButton;
    private PasswordAdapter passwordAdapter;
    private final List<PasswordItem> passwordList = new ArrayList<>();

    private static final String PREFS_NAME = "SavedPasswordsPrefs";
    public static final String PASSWORDS_KEY = "passwords_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_passwords);

        addPasswordButton = findViewById(R.id.addPasswordButton);
        addPasswordButton.setOnClickListener(v -> openAddPasswordDialog());

        RecyclerView passwordsRecyclerView = findViewById(R.id.passwordsRecyclerView);
        passwordAdapter = new PasswordAdapter(passwordList, getSharedPreferences(PREFS_NAME, MODE_PRIVATE), this);
        passwordsRecyclerView.setAdapter(passwordAdapter);
        passwordsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the saved passwords from SharedPreferences
        Set<String> savedPasswords = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getStringSet(PASSWORDS_KEY, null);
        if (savedPasswords != null && !savedPasswords.isEmpty()) {
            for (String password : savedPasswords) {
                String[] passwordParts = password.split(":");
                if (passwordParts.length == 2) {
                    passwordList.add(new PasswordItem(passwordParts[0], passwordParts[1]));
                }
            }
            passwordAdapter.notifyDataSetChanged();
        }
    }

    private void openAddPasswordDialog() {
        AddPasswordDialog dialog = new AddPasswordDialog();
        dialog.setListener(this);
        dialog.show(getSupportFragmentManager(), "AddPasswordDialog");
    }

    @Override
    public void onPasswordAdded(String title, String password) {
        addPassword(title, password);
        int newPosition = passwordList.size() - 1;
        passwordList.add(new PasswordItem(title, password));
        passwordAdapter.notifyItemInserted(newPosition);
        passwordAdapter.notifyDataSetChanged();
    }

    private static final int SHIFT_AMOUNT = 3; // Amount to shift characters

    public void addPassword(String title, String password) {
        // Store the obfuscated password in SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String obfuscatedPassword = obfuscatePassword(password);
        Set<String> savedPasswords = new HashSet<>(preferences.getStringSet(PASSWORDS_KEY, new HashSet<>()));
        savedPasswords.add(title + ":" + obfuscatedPassword);
        preferences.edit().putStringSet(PASSWORDS_KEY, savedPasswords).apply();
    }

    private String obfuscatePassword(String password) {
        StringBuilder sb = new StringBuilder();
        for (char c : password.toCharArray()) {
            sb.append((char) (c + SHIFT_AMOUNT));
        }
        return sb.toString();
    }
    public static class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder> {

        private final List<PasswordItem> passwordList;
        private SharedPreferences preferences;
        private Context context;

        public PasswordAdapter(List<PasswordItem> passwordList, SharedPreferences preferences, Context context) {
            this.passwordList = passwordList;
            this.preferences = preferences;
            this.context = context;
        }

        @NonNull
        @Override
        public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_saved_password, parent, false);
            return new PasswordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
            PasswordItem passwordItem = passwordList.get(position);
            holder.titleTextView.setText(passwordItem.getTitle());
            holder.passwordTextView.setText(passwordItem.getPassword());

            holder.copyButton.setOnClickListener(v -> {
                copyPasswordToClipboard(passwordItem.getPassword());
            });

            holder.deleteButton.setOnClickListener(v -> {
                deletePassword(position);
            });
        }

        @Override
        public int getItemCount() {
            return passwordList.size();
        }

        private void deletePassword(int position) {
            passwordList.remove(position);
            notifyItemRemoved(position);
            savePasswordsToSharedPreferences();
        }

        private void savePasswordsToSharedPreferences() {
            Set<String> savedPasswords = new HashSet<>();
            for (PasswordItem passwordItem : passwordList) {
                savedPasswords.add(passwordItem.getTitle() + ":" + passwordItem.getPassword());
            }
            preferences.edit().putStringSet(PASSWORDS_KEY, savedPasswords).apply();
        }

        private void copyPasswordToClipboard(String password) {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Generated Password", password);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        }

        public static class PasswordViewHolder extends RecyclerView.ViewHolder {

            public TextView titleTextView;
            public TextView passwordTextView;
            public ImageButton deleteButton;
            public Button copyButton;

            public PasswordViewHolder(View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.titleTextView);
                passwordTextView = itemView.findViewById(R.id.passwordTextView);
                deleteButton = itemView.findViewById(R.id.deleteButton);
                copyButton = itemView.findViewById(R.id.copyButton);
                deleteButton.setImageResource(android.R.drawable.ic_delete);
            }
        }
    }

    public static class PasswordItem {
        private final String title;
        private final String password;

        public PasswordItem(String title, String password) {
            this.title = title;
            this.password = password;
        }

        public String getTitle() {
            return title;
        }

        public String getPassword() {
            return password;
        }
    }
    private String analyzePasswordStrength(String password) {
        if (password.matches("[a-zA-Z]+")) {
            return "Weak";
        } else if (password.matches("[a-zA-Z0-9]+")) {
            return "Strong";
        } else {
            return "Very Strong";
        }
    }
}
