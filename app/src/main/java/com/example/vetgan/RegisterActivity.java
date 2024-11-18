package com.example.vetgan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterName, editTextRegisterEmail, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private static final String TAG= "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Toast.makeText(RegisterActivity.this, "Registrese Ahora", Toast.LENGTH_LONG).show();

        progressBar                = findViewById(R.id.progressBar);
        editTextRegisterName       = findViewById(R.id.editText_register_name);
        editTextRegisterEmail      = findViewById(R.id.editText_register_email);
        editTextRegisterPwd        = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = editTextRegisterName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();

                if(TextUtils.isEmpty(textName)){
                    Toast.makeText(RegisterActivity.this, "Porfavor, Ingrese su Nombre Completo", Toast.LENGTH_LONG).show();
                    editTextRegisterName.setError("Nombre Completo Obligatorio");
                    editTextRegisterName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Porfavor, Ingrese su Correo", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Correo Obligatorio");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this, "Porfavor, re-ingrese su Correo", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Correo Válido Obligatorio");
                    editTextRegisterEmail.requestFocus();
                } else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Porfavor, Ingrese su Clave", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Clave Obligatoria");
                    editTextRegisterPwd.requestFocus();
                } else if(textPwd.length() < 6){
                    Toast.makeText(RegisterActivity.this, "La Clave debe tener 6 Digitos como Minimo", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Clave Débil");
                    editTextRegisterPwd.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Confirma tu clave", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Clave Obligatoria");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if(!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Ingrese la misma clave", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Clave Obligatoria");
                    editTextRegisterConfirmPwd.requestFocus();
                    //LIMPIAR CONTRASEÑA INGRESADA
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    usuarioRegistrado(textName, textEmail, textPwd, textConfirmPwd);
                }
            }
        });

    }

    private void usuarioRegistrado(String textName, String textEmail, String textPwd, String textConfirmPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Usuario Registrado Correctamente", Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            firebaseUser.sendEmailVerification();

                            /*
                            Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish(); */
                        } else{
                            try{
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e){
                                editTextRegisterPwd.setError("Tu contraseña es demasiado débil. Utilice una combinación de alfabetos, números y caracteres especiales.");
                                editTextRegisterPwd.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterEmail.setError("Su correo electrónico no es válido o ya está en uso");
                                editTextRegisterEmail.requestFocus();
                            } catch(FirebaseAuthUserCollisionException e){
                                editTextRegisterEmail.setError("El usuario ya está registrado con este correo electrónico. Usa otro correo electrónico");
                                editTextRegisterEmail.requestFocus();
                            } catch(Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}