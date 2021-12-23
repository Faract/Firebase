package com.example.firebaseauth;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SIGN_IN = 1;

    GoogleSignInAccount account;

    TextView txtDisplayName;
    TextView txtEmail;

    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDisplayName = findViewById(R.id.txt_display_name);
        txtEmail = findViewById(R.id.txt_email);
        Button signOutButton = findViewById(R.id.sign_out_button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i, REQUEST_SIGN_IN);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Выход из аккаунта...", Toast.LENGTH_SHORT).show();
                client.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        account = null;
                        redrawUI();
                    }
                });
            }
        });

        account = GoogleSignIn.getLastSignedInAccount(this);
        redrawUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                account = task.getResult(ApiException.class);
                redrawUI();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void redrawUI() {
        if (account == null) {
            findViewById(R.id.account_holder).setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            txtDisplayName.setText(account.getDisplayName());
            txtEmail.setText(account.getEmail());
            findViewById(R.id.account_holder).setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        }
    }
}