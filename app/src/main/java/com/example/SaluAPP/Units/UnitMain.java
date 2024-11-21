package com.example.SaluAPP.Units;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.SaluAPP.MainActivity;
import com.example.SaluAPP.Profile.ProfileMain;
import com.example.SaluAPP.R;
import com.example.SaluAPP.perfil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UnitMain extends AppCompatActivity implements UnitRVAdapter.UnitClickInterface {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private ArrayList<UnitRVModal> unitRVModalArrayList;
    private UnitRVAdapter unitRVAdapter;
    private RelativeLayout homeRL;
    private String courseId,subjectId;

    private String mateID;

    Toolbar toolbar;

    Menu opcion1;



    FirebaseFirestore fStore;

    FirebaseAuth firebaseAuth;

    String userID;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_main);
        // inicializando todas nuestras variables.
        RecyclerView unitRV = findViewById(R.id.idRVUnits);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBUnitsLoading);
        // creando variables para fab, base de datos de Firebase,
        // barra de progreso, lista, adaptador, autenticación de Firebase,
        // vista de reciclaje y diseño relativo.
        FloatingActionButton addUnitFAB = findViewById(R.id.idFABAddUnit);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mateID = getIntent().getStringExtra("mateID");

        unitRVModalArrayList = new ArrayList<>();
        subjectId = getIntent().getStringExtra("subjectId");
        courseId = getIntent().getStringExtra("courseId");
        // en la línea de abajo estamos obteniendo la referencia de la base de datos.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");;
        //databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses").child(courseId).child("Units");
        // en la línea de abajo agregamos un listener de clic para nuestro botón de acción flotante.

        //Toolbar
        toolbar=findViewById(R.id.toolbar);
        opcion1 = findViewById(R.id.opcion1);
        setSupportActionBar(toolbar);
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        addUnitFAB.hide();

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo

            if(IdRoles.equals("0")){

            }


            if(IdRoles.equals("1")){
                addUnitFAB.show();
                addUnitFAB.setOnClickListener(v -> {
                    // abriendo una nueva actividad para añadir una unidad.
                    Intent i = new Intent(UnitMain.this, AddUnitsActivity.class);
                    i.putExtra("mateID",mateID);
                    i.putExtra("subjectId", subjectId);
                    i.putExtra("courseId", courseId);

                    // en la línea de abajo estamos abriendo nuestra actividad de AddUnitsActivity.
                    startActivity(i);
                });
            }
            if(IdRoles.equals("2")){
                addUnitFAB.show();
                addUnitFAB.setOnClickListener(v -> {
                    // abriendo una nueva actividad para añadir una unidad.
                    Intent i = new Intent(UnitMain.this, AddUnitsActivity.class);
                    i.putExtra("mateID",mateID);
                    i.putExtra("subjectId", subjectId);
                    i.putExtra("courseId", courseId);
                    // en la línea de abajo estamos abriendo nuestra actividad de AddUnitsActivity.
                    startActivity(i);
                });
            }
            if(IdRoles.equals("3")){

            }

        });


        // en la línea de abajo estamos inicializando nuestra clase de adaptador.
        unitRVAdapter = new UnitRVAdapter(unitRVModalArrayList, this, this);
        // configurando el gestor de diseño para la vista de reciclaje en la línea de abajo.
        unitRV.setLayoutManager(new LinearLayoutManager(this));
        // estableciendo el adaptador para la vista de reciclaje en la línea de abajo.
        unitRV.setAdapter(unitRVAdapter);
        // en la línea de abajo llamamos a un método para obtener unidades de la base de datos.
        getUnits();
    }


    private void getUnits() {
        // en la línea de abajo limpiamos nuestra lista.
        unitRVModalArrayList.clear();
        // en la línea de abajo estamos llamando al método de escucha de eventos de agregar hijo para leer los datos.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // en la línea de abajo estamos ocultando nuestra barra de progreso.
                if (unitRVModalArrayList != null) {
                    loadingPB.setVisibility(View.GONE);
                }
                // añadiendo instantánea a nuestra lista de arrays en la línea de abajo.
                unitRVModalArrayList.add(snapshot.getValue(UnitRVModal.class));
                // notificando a nuestro adaptador que los datos han cambiado.
                unitRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // este método se llama cuando se agrega un nuevo hijo
                // estamos notificando a nuestro adaptador y haciendo visible la barra de progreso
                loadingPB.setVisibility(View.GONE);
                unitRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // notificando a nuestro adaptador cuando se elimina un hijo.
                unitRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notificando a nuestro adaptador cuando se mueve un hijo.
                unitRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //buscar
    public void searchList(String text){
        ArrayList<UnitRVModal> searchList = new ArrayList<>();
        for (UnitRVModal dataClass: unitRVModalArrayList){
            if (dataClass.getUnitName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        unitRVAdapter.searchDataList(searchList);
    }

    //ver horita
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //SE DIFERENCIA EL TIPO DE TOOLBAR DEPENDIENDO DEL ROL DEL USUARIO
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        //buscar



        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo
            if(IdRoles.equals("1")){
                getMenuInflater().inflate(R.menu.menuprofesor,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchList(newText);
                        return true;
                    }
                });
            }
            if(IdRoles.equals("2")){
                getMenuInflater().inflate(R.menu.menu,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchList(newText);
                        return true;
                    }
                });

            }

            if(IdRoles.equals("3")){
                getMenuInflater().inflate(R.menu.menu,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchList(newText);
                        return true;
                    }
                });
            }

            if(IdRoles.equals("0")){
                getMenuInflater().inflate(R.menu.menuprofesor,menu);
                MenuItem searchItem = menu.findItem(R.id.buscar);
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchList(newText);
                        return true;
                    }
                });
            }
        });
        return true;
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

        }


        return true;
    }

    private void displayBottomSheet(UnitRVModal modal) {
        // en la línea de abajo estamos creando nuestro diálogo de hoja inferior.
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // en la línea de abajo estamos inflando nuestro archivo de diseño para nuestra hoja inferior.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout_unit, homeRL);
        // configurando la vista de contenido para la hoja inferior en la línea de abajo.
        bottomSheetDialog.setContentView(layout);
        // en la línea de abajo estamos estableciendo un cancelable
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        // llamando a un método para mostrar nuestra hoja inferior.
        bottomSheetDialog.show();
        // en la línea de abajo estamos creando variables para
        // nuestro text view e image view dentro de la hoja inferior
        // e inicializándolos con sus ids.
        TextView unitNameTV = layout.findViewById(R.id.UnitName);
        TextView unitDescTV = layout.findViewById(R.id.UnitDesc);
        ImageView unitIV = layout.findViewById(R.id.IVUnit);
        // en la línea de abajo estamos estableciendo datos en diferentes vistas.
        unitNameTV.setText(modal.getUnitName());
        unitDescTV.setText(modal.getUnitDescription());
        Picasso.get().load(modal.getUnitImg()).into(unitIV);
        Button viewBtn = layout.findViewById(R.id.ViewUnit);
        Button editBtn = layout.findViewById(R.id.EditUnit);

        // añadiendo un listener de clic para nuestro botón de edición.
        editBtn.setOnClickListener(v -> {
            // en la línea de abajo estamos abriendo nuestra actividad de EditUnitActivity.
            Intent i = new Intent(UnitMain.this, EditUnitsActivity.class);
            // en la línea de abajo estamos pasando nuestro modal de unidad.
            i.putExtra("mateID",mateID);
            i.putExtra("unit", modal);
            i.putExtra("subjectId", subjectId);
            i.putExtra("courseId", courseId);
            startActivity(i);
        });
        // añadiendo un listener de clic para nuestro botón de vista en la línea de abajo.
        viewBtn.setOnClickListener(v -> {
            Intent i = new Intent(UnitMain.this, VideoMain.class);
            String unitId = modal.getUnitId();
            i.putExtra("unit", modal);
            i.putExtra("mateID",mateID);
            i.putExtra("subjectId", subjectId);
            i.putExtra("courseId", courseId);
            i.putExtra("unitId", unitId);
            startActivity(i);
        });
    }

    @Override
    public void onUnitClick(int position) {
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo

            if(IdRoles.equals("0")){
                //Inicializamos el intent para pasar la actividad
                UnitRVModal modal = unitRVModalArrayList.get(position);
                //Inicializamos el intent para pasar la actividad
                String unitId = unitRVModalArrayList.get(position).getUnitId();
                Intent i = new Intent(UnitMain.this, VideoMain.class);
                i.putExtra("mateID",mateID);
                i.putExtra("subjectId", subjectId);
                i.putExtra("courseId", courseId);
                i.putExtra("unitId", unitId);
                i.putExtra("unit", modal);
                //Iniciamos la actividad
                startActivity(i);

            }

            if(IdRoles.equals("1")){
                if (unitRVModalArrayList != null && position < unitRVModalArrayList.size()) {
                    displayBottomSheet(unitRVModalArrayList.get(position));
                } else {
                    //Realiza las acciones correspondientes si no hay elementos en la lista o la posición está fuera de los límites.
                }
            }
            if(IdRoles.equals("2")){
                if (unitRVModalArrayList != null && position < unitRVModalArrayList.size()) {
                    displayBottomSheet(unitRVModalArrayList.get(position));
                } else {
                    //Realiza las acciones correspondientes si no hay elementos en la lista o la posición está fuera de los límites.
                }
            }
            if(IdRoles.equals("3")){
                if (unitRVModalArrayList != null && position < unitRVModalArrayList.size()) {
                    displayBottomSheet(unitRVModalArrayList.get(position));
                } else {
                    //Realiza las acciones correspondientes si no hay elementos en la lista o la posición está fuera de los límites.
                }

            }
        });



    }

}
