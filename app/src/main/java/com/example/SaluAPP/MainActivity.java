package com.example.SaluAPP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    AuthResult authResult;
    EditText MCorreoInicio, MContraseñaInicio;
    Button enviarButton;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    String userID;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion);

        MCorreoInicio = findViewById(R.id.CorreoInicio);
        MContraseñaInicio = findViewById(R.id.ContraseñaInicio);
        enviarButton = findViewById(R.id.enviarButton);
        progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Login
        progressBar.setVisibility(View.INVISIBLE);
        enviarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = MCorreoInicio.getText().toString().trim();
                String password = MContraseñaInicio.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    MCorreoInicio.setError("Ingrese su email");
                    return;
                }

                if (password.length() < 6) {
                    MContraseñaInicio.setError("La contraseña debe tener más de 6 caracteres");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    MContraseñaInicio.setError("Ingrese su contraseña");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Usuario entró con éxito", Toast.LENGTH_SHORT).show();
                        userID = firebaseAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);

                        documentReference.get().addOnSuccessListener(document -> {
                            // Obtener el valor del campo Status
                            String status = document.getString("Status");

                            if ("1".equals(status)) {
                                // Redirigir a la pantalla de baneado
                                startActivity(new Intent(getApplicationContext(), Baneado.class));
                                finish(); // Finaliza MainActivity para que no pueda volver
                            } else {
                                // Usuario activo, redirigir según su rol
                                String IdRoles = document.getString("IdRoles");
                                startActivity(new Intent(getApplicationContext(), Principal.class));
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        // Enviar a otros links
        TextView Registro = findViewById(R.id.RegistroInicio);
        TextView recuperarContra = findViewById(R.id.OlvidasteContra);

        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });

        // Recuperar contraseña
        recuperarContra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Cambia tu contraseña");
                passwordResetDialog.setMessage("Inserta tu email para resetear tu contraseña");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "El link de reseteo está en su correo", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "El link no fue enviado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "El link no fue enviado", Toast.LENGTH_SHORT).show();
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }
}
