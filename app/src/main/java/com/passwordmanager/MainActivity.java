package com.passwordmanager;

import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements PasswordParametersDialog.PasswordParametersDialogListener {

    private Toolbar toolbar;
    private Random random;
    private List<String> passwords;
    private TextView generatedPasswordTextView;

    private static final String PREFS_NAME = "GeneratedPasswordsPrefs";
    private static final String PASSWORDS_KEY = "generatedPasswords";
    private static final int REQUEST_CODE_AUTHENTICATION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        random = new Random();
        passwords = getGeneratedPasswords();

        Button generateButton = findViewById(R.id.generateButton);
        generatedPasswordTextView = findViewById(R.id.generatedPasswordTextView);
        Button copyButton = findViewById(R.id.copyButton);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordParametersDialog();
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyPasswordToClipboard();
            }
        });

        findViewById(R.id.savedPasswordsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger screen lock authentication when accessing Saved Passwords
                triggerScreenLockAuthentication();
            }
        });
    }

    // Method to trigger screen lock authentication
    private void triggerScreenLockAuthentication() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isDeviceSecure()) {
            Intent authIntent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            startActivityForResult(authIntent, REQUEST_CODE_AUTHENTICATION);
        } else {
            // Device is not secure or screen lock is not set up
            // Handle accordingly (e.g., show error message)
            Toast.makeText(MainActivity.this, "Screen lock is not set up", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTHENTICATION) {
            if (resultCode == RESULT_OK) {
                // Authentication successful, grant access to "Saved Passwords" content
                showSavedPasswords();
            } else {
                // Authentication failed or user canceled
                // Handle accordingly (e.g., show error message)
                Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_credits) {
            showCredits();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<String> getGeneratedPasswords() {
        // Retrieve the generated passwords from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedPasswords = preferences.getStringSet(PASSWORDS_KEY, null);
        if (savedPasswords != null && !savedPasswords.isEmpty()) {
            return new ArrayList<>(savedPasswords);
        } else {
            // Generate new passwords if none are saved
            List<String> generatedPasswords = generatePasswords(10000);
            saveGeneratedPasswords(generatedPasswords);
            return generatedPasswords;
        }
    }

    private void saveGeneratedPasswords(List<String> generatedPasswords) {
        // Store the generated passwords in SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> passwordsSet = new HashSet<>(generatedPasswords);
        editor.putStringSet(PASSWORDS_KEY, passwordsSet);
        editor.apply();
    }

    private List<String> generatePasswords(int count) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generatePassword());
        }
        return passwords;
    }

    private String generatePassword() {
        // Return a randomly generated password
        StringBuilder password = new StringBuilder();
        int length = random.nextInt(10) + 8; // Random length between 8 and 17
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private void showSavedPasswords() {
        // Start the SavedPasswordsActivity
        Intent intent = new Intent(MainActivity.this, SavedPasswordsActivity.class);
        startActivity(intent);
    }

    private void showCredits() {
        String credits = "KESHAV BUDHIRAJA-23BCC80003,SUJAL SHARMA -22BCC70121,CHOGYAL WANGDI - 22BCC70154\n\n\n";

        // Inflate the custom layout for the toast
        View toastView = getLayoutInflater().inflate(R.layout.toast_custom, null);

        // Set the credits text
        TextView creditsTextView = toastView.findViewById(R.id.creditsTextView);
        creditsTextView.setText(credits);

        //showing the toast
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();
    }

    private void copyPasswordToClipboard() {
        String password = generatedPasswordTextView.getText().toString().trim();
        if (!password.isEmpty()) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Generated Password", password);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPasswordParametersDialog() {
        PasswordParametersDialog dialog = new PasswordParametersDialog();
        dialog.show(getSupportFragmentManager(), "password_parameters_dialog");
    }

    @Override
    public void onParametersSelected(int length, boolean includeUppercase, boolean includeLowercase, boolean includeDigits) {
        // Generated password with parameter selection option
        String generatedPassword = generateCustomPassword(length, includeUppercase, includeLowercase, includeDigits);
        generatedPasswordTextView.setText(generatedPassword);

        // Analyze the generated password and display its strength
        String passwordStrength = analyzePasswordStrength(generatedPassword);
        Toast.makeText(this, "Password Strength: " + passwordStrength, Toast.LENGTH_SHORT).show();

        generatedPasswordTextView.setVisibility(View.VISIBLE);
        Button copyButton = findViewById(R.id.copyButton);
        copyButton.setVisibility(View.VISIBLE);
    }

    private String generateCustomPassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeDigits) {
        StringBuilder password = new StringBuilder();
        String characters = "";
        if (includeLowercase) {
            characters += "abcdefghijklmnopqrstuvwxyz";
        }
        if (includeUppercase) {
            characters += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }
        if (includeDigits) {
            characters += "1234567890!@#$%^&*()";
        }



        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }
    private String analyzePasswordStrength(String password) {
        if (password.matches("[a-z]+")) {
            return "Weak";
        } else if (password.matches("[a-zA-Z]+")) {
            return "Strong";
        } else {
            return "Very Strong";
        }
    }
}

