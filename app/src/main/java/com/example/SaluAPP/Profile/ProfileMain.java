package com.example.SaluAPP.Profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.SaluAPP.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileMain extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> userArrayList;
    private UserAdapter userAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId;
    private String currentUserRole;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofilemain);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        // Inicializa FirebaseAuth y obtiene el UID actual
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        currentUserRole = "";

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<>();

        // Cargar el rol del usuario actual desde Firestore
        loadCurrentUserRole();
    }

    private void loadCurrentUserRole() {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser != null) {
                            currentUserRole = currentUser.getIdRoles();

                            // Inicializa el adaptador con el rol actual
                            userAdapter = new UserAdapter(ProfileMain.this, userArrayList, currentUserId, currentUserRole);
                            recyclerView.setAdapter(userAdapter);

                            // Comienza a escuchar cambios en la colección de usuarios
                            EventChangeListener();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore Error", "Error getting current user role: " + e.getMessage()));
    }

    private void EventChangeListener() {
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            userArrayList.add(dc.getDocument().toObject(User.class));
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
}