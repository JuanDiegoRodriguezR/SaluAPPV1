package com.example.SaluAPP;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.SaluAPP.Registrarse.Usuario;
import com.example.SaluAPP.Registrarse.Validaciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mCorreoUsuario, mContraseña, mContraseña_verificacion, mNombre_Usuario, mTelefonoUser;
    Button mRegistroBoton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;

    ProgressBar progressBar;

    CheckBox terminoscondiciones;
    String userID;

    Dialog MDialog;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        mCorreoUsuario = findViewById(R.id.CorreoUsuario);
        mContraseña = findViewById(R.id.Contraseña);
        mContraseña_verificacion = findViewById(R.id.Contraseña_verificacion);
        mNombre_Usuario = findViewById(R.id.Nombre_Usuario);
        mTelefonoUser = findViewById(R.id.TelefonoUser);
        mRegistroBoton = findViewById(R.id.RegistroBoton);
        terminoscondiciones = findViewById(R.id.terminoscondiciones);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.INVISIBLE);
        mRegistroBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mCorreoUsuario.getText().toString().trim();
                String password = mContraseña.getText().toString().trim();
                String fullname = mNombre_Usuario.getText().toString();
                String passwordverification = mContraseña_verificacion.getText().toString().trim();
                String number = mTelefonoUser.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mCorreoUsuario.setError("Su correo es requerido");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mContraseña.setError("Ingrese una contraseña");
                    return;
                }

                if (password.length() < 6) {
                    mContraseña.setError("La contraseña debe tener más de 6 caracteres");
                    return;
                }

                if (TextUtils.isEmpty(passwordverification)) {
                    mContraseña_verificacion.setError("Es requerido que confirme su contraseña");
                    return;
                }

                if (!password.equals(passwordverification)) {
                    mContraseña_verificacion.setError("Las contraseñas deben ser iguales");
                    return;
                }

                if (TextUtils.isEmpty(fullname)) {
                    mNombre_Usuario.setError("Su nombre de usuario es requerido");
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    mTelefonoUser.setError("Su teléfono es requerido");
                    return;
                }

                if (!number.matches("[0-9]+")) {
                    mTelefonoUser.setError("El registro debe ser numérico");
                    return;
                }

                if (number.length() < 10) {
                    mTelefonoUser.setError("Su teléfono tiene que tener 10 números");
                    return;
                }

                if (number.length() > 10) {
                    mTelefonoUser.setError("Su teléfono no puede tener más de 10 números");
                    return;
                }

                if (!terminoscondiciones.isChecked()) {
                    terminoscondiciones.setError("Acepte términos y condiciones");
                    Toast.makeText(Registro.this, "Acepte términos y condiciones si quiere continuar", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Enviar link de verificación
                            FirebaseUser usuario = firebaseAuth.getCurrentUser();
                            usuario.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Registro.this, "Verifique su email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email no fue enviado: " + e.getMessage());
                                }
                            });

                            Toast.makeText(Registro.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            // Se inserta la data
                            Map<String, Object> user = new HashMap<>();
                            user.put("UID", userID); // Almacena el UID
                            user.put("Correo", email);
                            user.put("Nombre_Apellido", fullname);
                            user.put("Telefono", number);
                            user.put("IdRoles", "0");
                            user.put("Status", "0");

                            // Ahora sí guarda la data
                            documentReference.set(user).addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "onSuccess: Perfil de usuario es creado para " + userID);
                            }).addOnFailureListener(e -> {
                                Log.d(TAG, "onFailure: " + e.toString());
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Registro.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        // ACCEDER A TÉRMINOS Y CONDICIONES
        myDialog = new Dialog(this);
        Button button = findViewById(R.id.Terminos);
        CheckBox terminoscondiciones2 = findViewById(R.id.terminoscondiciones);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.setContentView(R.layout.term_cond);
                myDialog.show();
            }
        });

        // Acceder al inicio de sesión
        Intent intent = getIntent();
        TextView Inicia = findViewById(R.id.iniciasesion);
        Inicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registro.this, MainActivity.class);
                startActivity(intent);
            }
        });

        MDialog = new Dialog(this);
        terminoscondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MDialog.setContentView(R.layout.term_cond);
                MDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
    }
}
