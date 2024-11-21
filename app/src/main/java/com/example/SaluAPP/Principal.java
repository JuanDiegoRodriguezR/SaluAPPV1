package com.example.SaluAPP;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SaluAPP.Profile.ProfileMain;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Principal extends AppCompatActivity {


    Toolbar toolbar;

    Menu opcion1;

    FirebaseFirestore fStore;

    FirebaseAuth firebaseAuth;

    String userID;

    TextView UserName;

    ImageView Matematicas,Ingles,Literatura,Ciencias,profilePicture;

    StorageReference storageReference;

    String[] textViewIds;

    DatabaseReference databaseReference;

    FirebaseDatabase firebaseDatabase;

    // SharedPreferences para almacenar los IDs
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        firebaseAuth = FirebaseAuth.getInstance();

        fStore= FirebaseFirestore.getInstance();


        //toolbar
        toolbar=findViewById(R.id.toolbar);
        opcion1 = findViewById(R.id.opcion1);
        setSupportActionBar(toolbar);
        UserName = findViewById(R.id.UserName);
        profilePicture = findViewById(R.id.ImagenUser);

        storageReference = FirebaseStorage.getInstance().getReference();
        //obtenerImagenDePerfil
        StorageReference profileRef = storageReference.child("users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePicture);
            }
        });


        // Obtener la instancia de SharedPreferences
        sharedPreferences = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);

        // Inicializar Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Materias");

        // Crear TextViews
        ImageView[] imageViews = new ImageView[]{
                findViewById(R.id.Matematicas),
                findViewById(R.id.Ingles),
                findViewById(R.id.Literatura),
                findViewById(R.id.Ciencias)
        };

        // IDs fijos del 1 al 5
        String[] textViewIds = new String[]{"1", "2", "3", "4"};

        for (int i = 0; i < imageViews.length; i++) {
            // Asignar el ID al TextView
            imageViews[i].setTag(textViewIds[i]);
        }


        // Agregar los listeners a los TextViews
        for (int i = 0; i < imageViews.length; i++) {
            int finalI = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Inicia la otra actividad
                    Intent intent = new Intent(Principal.this, SubjectsMain.class);
                    intent.putExtra("imageViewId", textViewIds[finalI]);
                    startActivity(intent);
                }
            });
        }

        //Obtener nombre de usuario:

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                UserName.setText(documentSnapshot.getString("Nombre_Apellido"));
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //SE DIFERENCIA EL TIPO DE TOOLBAR DEPENDIENDO DEL ROL DEL USUARIO
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo

            if(IdRoles.equals("0")){
                getMenuInflater().inflate(R.menu.menu_usuario,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                searchItem.setVisible(false);
            }
            if(IdRoles.equals("1")){
                getMenuInflater().inflate(R.menu.menu_usuario,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                searchItem.setVisible(false);
            }
            if(IdRoles.equals("2")){
                getMenuInflater().inflate(R.menu.menu,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                searchItem.setVisible(false);
            }
            if(IdRoles.equals("3")){
                getMenuInflater().inflate(R.menu.menu,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                searchItem.setVisible(false);
            }
        });
        return true;
    }


    public void logout(View view) {

    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.opcion1) {
            startActivity(new Intent(getApplicationContext(), perfil.class));
        } else if (id == R.id.opcion2) {
            startActivity(new Intent(getApplicationContext(), ProfileMain.class));

        }else if (id == R.id.opcion3) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }else if (id == R.id.buscar) {
            Toast.makeText(this,"Oprimiste Buscar",Toast.LENGTH_SHORT).show();
            //  startActivity(new Intent(getApplicationContext(),Principal.class));
        }


        if (id == R.id.opcion1_usuario) {
            startActivity(new Intent(getApplicationContext(), perfil.class));
        } else if (id == R.id.opcion2_usuario) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }else if (id == R.id.buscar) {

        }


        if (id == R.id.opcion1_profesor) {
            startActivity(new Intent(getApplicationContext(), perfil.class));
        }else if (id == R.id.opcion2_profesor) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }else if (id == R.id.buscar) {
            Toast.makeText(this,"Oprimiste Buscar",Toast.LENGTH_SHORT).show();
            //  startActivity(new Intent(getApplicationContext(),Principal.class));
        }


        return true;
    }




}