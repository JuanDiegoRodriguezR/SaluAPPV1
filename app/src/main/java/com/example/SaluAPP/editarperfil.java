package com.example.SaluAPP;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class editarperfil extends AppCompatActivity {
    FirebaseFirestore fStore;

    private final static int GALLEY_INTENT_CODE =1023;

    FirebaseAuth firebaseAuth;

    FirebaseUser user;

    String userID;

    EditText Mnombre,Mtelefono;


    ImageView profilePicture;

    Button MBotonFoto,Mguardar;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        //
        Mnombre = findViewById(R.id.nombre);
        Mtelefono = findViewById(R.id.telefono);
        Mguardar= findViewById(R.id.guardar);

        //obtener imagen de cada usuario
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
        });

        profilePicture = findViewById(R.id.fotoperfil);
        MBotonFoto = findViewById(R.id.botonfoto);


        MBotonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                Intent openGalleryIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


        //Obtener info del usuario

        Intent data= getIntent();

        String fullName= data.getStringExtra("fullName");
        String Number= data.getStringExtra("Number");
        Mnombre.setText(fullName);
        Mtelefono.setText(Number);

        //editar info de usuario
        Mguardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String fullname = Mnombre.getText().toString();
                String number= Mtelefono.getText().toString();


                if (TextUtils.isEmpty(fullname)){
                    Mnombre.setError("Su nombre de usuario es requerido para editar");
                    return;
                }

                if (TextUtils.isEmpty(number)){
                    Mtelefono.setError("Su telefono es requerido para editar");
                    return;
                }

                if (!number.matches("[0-9]+")){
                    Mtelefono.setError("El registro debe ser numerico");
                    return;
                }

                if (number.length() < 10){
                    Mtelefono.setError("Su telefono tiene que tener 10 numeros");
                    return;
                }

                if (number.length() > 10){
                    Mtelefono.setError("Su telefono no puede tener mas de 10 numeros");
                    return;
                }

                DocumentReference docRef= fStore.collection("users").document(user.getUid());
                Map<String,Object> edited = new HashMap<>();
                edited.put("Nombre_Apellido",Mnombre.getText().toString());
                edited.put("Telefono",Mtelefono.getText().toString());
                docRef.update(edited);
                Toast.makeText(editarperfil.this, "Los datos han sido actualizados", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Principal.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000){
            if (resultCode== Activity.RESULT_OK){
                Uri imageUri= data.getData();
                //profilePicture.setImageURI(imageUri);
                uploadImageToFireBase(imageUri);
            }
        }

    }

    private void uploadImageToFireBase(Uri imageUri){
        //Subir imagen al storage de FireBase
        final StorageReference fileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(editarperfil.this, "Imagen subida", Toast.LENGTH_SHORT).show();
                //obtener url de la imagen
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editarperfil.this, "Fallo en subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
