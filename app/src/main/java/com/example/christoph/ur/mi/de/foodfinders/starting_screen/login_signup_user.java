package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class login_signup_user extends Activity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentdata();
    }

    //decides which layout to load: Login or Create Account
    private void getIntentdata() {
        String intentData = getIntent().getStringExtra("intentData");
        if (intentData.equals("login")) {
            setupLoginUI();
        } else {
            if (intentData.equals("signup")) {
                setupSignupUI();
            }
        }
    }

    private void setupSignupUI() {
        setContentView(R.layout.signup_layout);
        final EditText name = (EditText) findViewById(R.id.signup_name);
        final EditText email = (EditText) findViewById(R.id.signup_email);
        final EditText password = (EditText) findViewById(R.id.signup_password);
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringEmail = String.valueOf(email.getText());
                String stringPassword = String.valueOf(password.getText());
                String stringName = String.valueOf(name.getText());
                if (!stringPassword.isEmpty() && !stringEmail.isEmpty() && !stringName.isEmpty() && !stringPassword.isEmpty()) {
                    signup(stringName, stringEmail, stringPassword);
                } else {
                    //Password has to be 6 chars or more
                    TextView wrongData = (TextView) findViewById(R.id.signup_wrongData);
                    wrongData.setVisibility(View.VISIBLE);
                }
            }
        });

        TextView login = (TextView) findViewById(R.id.login_link_signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLoginUI();
            }
        });
    }

    private void signup(final String name, String email, String password) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        startProgress();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = task.getResult().getUser();
                    Task<Void> updateTask = user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build());
                    updateTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToastlogin(auth.getCurrentUser().getDisplayName());
                                finish();
                            }
                        }
                    });
                } else {
                    TextView wrongData = (TextView) findViewById(R.id.signup_wrongData);
                    wrongData.setText(getString(R.string.wrongInput_ger));
                    wrongData.setVisibility(View.VISIBLE);
                    progress.dismiss();
                }
            }
        });
    }

    private void setupLoginUI() {
        setContentView(R.layout.login_layout);
        final EditText email = (EditText) findViewById(R.id.login_email);
        final EditText password = (EditText) findViewById(R.id.login_password);
        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringEmail = String.valueOf(email.getText());
                String stringPassword = String.valueOf(password.getText());
                if (stringEmail.isEmpty() || stringPassword.isEmpty()) {
                    TextView wrongData = (TextView) findViewById(R.id.login_wrongData);
                    wrongData.setVisibility(View.VISIBLE);
                } else {
                    login(stringEmail, stringPassword);
                }
            }
        });
        TextView signup = (TextView) findViewById(R.id.signup_link_login);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSignupUI();
            }
        });
    }

    private void login(String email, String password) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        startProgress();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToastlogin(auth.getCurrentUser().getDisplayName());
                            finish();
                        }
                        if (!task.isSuccessful()) {
                            TextView wrongData = (TextView) findViewById(R.id.login_wrongData);
                            wrongData.setVisibility(View.VISIBLE);
                            progress.dismiss();
                        }
                    }
                });
    }

    private void showToastlogin(String user) {
        Context context = getApplicationContext();
        CharSequence text = "Hallo " + user + " !";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void startProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.loginProcess_ger));
        progress.setMessage(getString(R.string.loginInProcessCheck_ger));
        progress.show();
    }
}
