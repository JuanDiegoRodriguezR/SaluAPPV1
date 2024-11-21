package com.example.SaluAPP;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class perfil extends AppCompatActivity {

    FirebaseFirestore fStore;

    private final static int GALLEY_INTENT_CODE =1023;

    FirebaseAuth firebaseAuth;

    String userID;

    TextView Musuario,Mtelefono,Mcorreo,Mrol,MVerificacion;

    ImageView profilePicture;

    ImageButton mEDITAR;

    Button MVerificar;

    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);
        firebaseAuth = FirebaseAuth.getInstance();

        fStore= FirebaseFirestore.getInstance();
        Musuario = findViewById(R.id.UserName);
        Mrol= findViewById(R.id.ROL);
        Mtelefono= findViewById(R.id.Telefono);
        mEDITAR= findViewById(R.id.EDITAR);
        Mcorreo= findViewById(R.id.CORREOUSER);
        MVerificar= findViewById(R.id.BotonConfirmar);
        MVerificacion = findViewById(R.id.VERIFICACION);
        profilePicture = findViewById(R.id.fotousuario);

        //obtener imagen de perfil
        storageReference = FirebaseStorage.getInstance().getReference();
        //obtener imagen de cada usuario
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getUid()+"/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
        });


        //Mandar a la estructura de editar usuario
        mEDITAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getApplicationContext(), editarperfil.class);
                i.putExtra("fullName", Musuario.getText().toString());
                i.putExtra("Number", Mtelefono.getText().toString());
                startActivity(i);
            }
        });

        //Obtener datos de usuario:


        FirebaseUser usuario = firebaseAuth.getCurrentUser();


        if(usuario.isEmailVerified()){
            MVerificar.setVisibility(View.INVISIBLE);
            MVerificacion.setText("Verificado");
        }else{
            MVerificar.setVisibility(View.VISIBLE);
            MVerificacion.setText("Sin verificar");
        }

        MVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(perfil.this, "Verifique su email", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "onFailure:Email no fue enviado" + e.getMessage());
                    }
                });
            }
        });

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                Musuario.setText(documentSnapshot.getString("Nombre_Apellido"));
                documentReference.get().addOnSuccessListener(document -> {
                    // Obtener el valor del campo
                    String IdRoles = document.getString("IdRoles");

                    // Verificar el valor del campo
                    if(IdRoles.equals("0")){
                        Mrol.setText("Usuario");
                    }

                    if(IdRoles.equals("1")){
                        Mrol.setText("Profesional");
                    }
                    if(IdRoles.equals("2")){
                        Mrol.setText("Administrador");
                    }
                    if(IdRoles.equals("3")){
                        Mrol.setText("Moderador");
                    }
                });
                Mtelefono.setText(documentSnapshot.getString("Telefono"));
                Mcorreo.setText(documentSnapshot.getString("Correo"));
            }
        });



    }
}