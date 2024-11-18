package com.example.vetgan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        editTextLoginEmail = findViewById(R.id.edText_login_email);
        editTextLoginPwd = findViewById(R.id.edText_login_pwd);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        //MOSTRAR OCULTAR CONTRASEÑA CON ICON OJO
        ImageView imageViewShowHidePwd = findViewById(R.id.imgV_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //si la contraseña es visible se oculta
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //cambio de icono
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                } else{
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Login Usuario
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Porfavor, ingresa tu correo", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Correo Obligatorio");
                    editTextLoginEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Porfavor, re-ingresa tu correo", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Correo Válido Obligatorio");
                    editTextLoginEmail.requestFocus();
                } else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Porfavor, ingresa tu clave", Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Clave Obligatoria");
                    editTextLoginPwd.requestFocus();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }
            }
        });

    }

    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Obtiene el usuario actual
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();
                    //Comprueba si el email esta verificado
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();

                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }
                } else{
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("El Usuario no existe o ya no es válido. Registrese Nuevamente");
                        editTextLoginEmail.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Datos no válidos. Compruebe y vuelva a ingresar");
                        editTextLoginEmail.requestFocus();
                    } catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //Configuracion de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Correo no verificado");
        builder.setMessage("Porfavor verifica tu correo antes de continuar");

        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //Creacion AlertDialog
        AlertDialog alertDialog = builder.create();
        //Mostrar AlertDialog
        alertDialog.show();
    }

    //Comprueba si el usuario ya ha iniciado sesión
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Ya has iniciado sesion!", Toast.LENGTH_SHORT);

            //Inicia la actividad principal
            startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
            finish(); //Cierra LoginActivity
        }
        else{
            Toast.makeText(LoginActivity.this, "Puedes iniciar sesión!", Toast.LENGTH_SHORT);
        }
    }
}