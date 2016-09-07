package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.google.android.gms.nearby.connection.dev.Strategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by juli on 05.09.16.
 */

public class login_signup_user extends Activity {

    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        getIntentdata();

    }

    //decides which layout to load: Login or Create Account
    private void getIntentdata() {
            String intentData = getIntent().getStringExtra("intentData");
            if(intentData.equals("login")){
                setupLoginUI();
            }else{
             if(intentData.equals("signup")){
                 setupSignupUI();

             }
            }
    }

    private void setupSignupUI() {
        setContentView(R.layout.signup_layout);
       final EditText name  = (EditText) findViewById(R.id.signup_name);
       final EditText email  = (EditText) findViewById(R.id.signup_email);
        final EditText password  = (EditText) findViewById(R.id.signup_password);
        Button signupButton =(Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(password.getText().length()>=6){
                   signup(name.getText().toString(),email.getText().toString(),password.getText().toString());
               } else{
                   //passwort muss mindestens 6 Zeichenhaben
                   TextView wrongData= (TextView) findViewById(R.id.login_wrongData);
                   wrongData.setText("Ihr Passwort muss mindestens 6 Zeichen haben!!!");
                   wrongData.setVisibility(View.VISIBLE);

               }
            }
        });

        TextView login=(TextView) findViewById(R.id.login_link_signup);
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
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = task.getResult().getUser();
                    Task<Void> updateTask = user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build());
                    updateTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("FirebaseSignup",task.isSuccessful()+"user login");
                            if(task.isSuccessful()){finish();
                                Log.d("firebaselogin","Anzeigename des Users"+auth.getCurrentUser().getDisplayName()+"  UserUID"+auth.getCurrentUser().getUid());
                            }
                            if (!task.isSuccessful()) {
                                //Falsche Eingabe
                                Log.d("firebaselogin", "signInWithEmail:failed"+ task.getException());
                                TextView wrongData= (TextView) findViewById(R.id.signup_wrongData);
                                wrongData.setVisibility(View.VISIBLE);
                                progress.dismiss();

                            }
                        }
                    });
                            }
                    }
        });
    }

    private void setupLoginUI(){
        setContentView(R.layout.login_layout);
        final EditText email  = (EditText) findViewById(R.id.login_email);
        final EditText password  = (EditText) findViewById(R.id.login_password);
        Button login =(Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(email.getText().toString(),password.getText().toString());
            }
        });

        TextView signup=(TextView) findViewById(R.id.signup_link_login);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSignupUI();
            }
        });
    }

    private void login(String email, String password) {
        final FirebaseAuth auth= FirebaseAuth.getInstance();
        startProgress();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("firebaselogin", "signInWithEmail:onComplete:" + task.isSuccessful());
                        //TODO loading screen anzeigen
                        if(task.isSuccessful()){
                            Log.d("firebaselogin","Anzeigename des Users"+auth.getCurrentUser().getDisplayName()+"  UserUID"+auth.getCurrentUser().getUid());
                            finish();
                        }

                        if (!task.isSuccessful()) {
                            //Falsche Eingabe
                            Log.d("firebaselogin", "signInWithEmail:failed"+ task.getException());
                            TextView wrongData= (TextView) findViewById(R.id.login_wrongData);
                            wrongData.setVisibility(View.VISIBLE);
                            progress.dismiss();
                        }
                    }
                });
    }

    private void startProgress() {
        progress = new ProgressDialog(this);
        progress.setTitle("Anmeldung");
        progress.setMessage("Ihre Anmeldedaten werden überprüft");
        progress.show();
    }
}
