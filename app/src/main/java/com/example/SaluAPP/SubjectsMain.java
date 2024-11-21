package com.example.SaluAPP;

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
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.SaluAPP.Courses.CoursesMain;
import com.example.SaluAPP.Profile.ProfileMain;
import com.example.SaluAPP.Subject.AddSubjectActivity;
import com.example.SaluAPP.Subject.EditSubjectActivity;
import com.example.SaluAPP.Subject.SubjectRVAdapter;
import com.example.SaluAPP.Subject.SubjectRVModal;
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

public class SubjectsMain extends AppCompatActivity implements SubjectRVAdapter.SubjectClickInterface {

    // creando variables para fab, base de datos de Firebase,
    // barra de progreso, lista, adaptador, autenticación de Firebase,
    // vista de reciclaje y diseño relativo.
    private FloatingActionButton addSubjectFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView subjectRV;
    Toolbar toolbar;

    Menu opcion1;

    private ProgressBar loadingPB;
    private ArrayList<SubjectRVModal> subjectRVModalArrayList;
    private SubjectRVAdapter subjectRVAdapter;
    private RelativeLayout homeRL;
    SearchView searchView;
    private String mateID;

    FirebaseFirestore fStore;

    FirebaseAuth firebaseAuth;

    String userID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cursos);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        // inicializando todas nuestras variables.
        subjectRV = findViewById(R.id.idRVSubjects);
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBLoading);
        addSubjectFAB = findViewById(R.id.idFABAddSubject);
        mateID = getIntent().getStringExtra("imageViewId");
        firebaseDatabase = FirebaseDatabase.getInstance();
        subjectRVModalArrayList = new ArrayList<>();
        // en la línea de abajo estamos obteniendo la referencia de la base de datos.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects");
       // databaseReference = firebaseDatabase.getReference("Subjects");
        // en la línea de abajo agregamos un listener de clic para nuestro botón de acción flotante.

        addSubjectFAB.hide();


        //buscar
      /*  searchView = findViewById(R.id.search);

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
        });*/


        //agregar toolbar
        toolbar=findViewById(R.id.toolbar);
        opcion1 = findViewById(R.id.opcion1);
        setSupportActionBar(toolbar);
        // Cambiar el color de los iconos del menú a blanco
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }


        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo

            if(IdRoles.equals("0")){

            }

            if(IdRoles.equals("1")){
                addSubjectFAB.show();
                addSubjectFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // abriendo una nueva actividad para añadir una asignatura.
                        Intent i = new Intent(SubjectsMain.this, AddSubjectActivity.class);
                        i.putExtra("mateID",mateID);
                        startActivity(i);

                    }
                });
            }
            if(IdRoles.equals("2")){
                addSubjectFAB.show();
                addSubjectFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // abriendo una nueva actividad para añadir una asignatura.
                        Intent i = new Intent(SubjectsMain.this, AddSubjectActivity.class);
                        i.putExtra("mateID",mateID);
                        startActivity(i);
                    }
                });
            }
            if(IdRoles.equals("3")){

            }
        });


        // en la línea de abajo estamos inicializando nuestra clase de adaptador.
        subjectRVAdapter = new SubjectRVAdapter(subjectRVModalArrayList, this, this::onSubjectClick);
        // configurando el gestor de diseño para la vista de reciclaje en la línea de abajo.
        subjectRV.setLayoutManager(new LinearLayoutManager(this));
        // estableciendo el adaptador para la vista de reciclaje en la línea de abajo.
        subjectRV.setAdapter(subjectRVAdapter);
        // en la línea de abajo llamamos a un método para obtener asignaturas de la base de datos.
        getSubjects();
    }


    private void getSubjects() {
        // en la línea de abajo limpiamos nuestra lista.
        subjectRVModalArrayList.clear();
        // en la línea de abajo estamos llamando al método de escucha de eventos de agregar hijo para leer los datos.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // en la línea de abajo estamos ocultando nuestra barra de progreso.
                loadingPB.setVisibility(View.GONE);
                // añadiendo instantánea a nuestra lista de arrays en la línea de abajo.
                subjectRVModalArrayList.add(snapshot.getValue(SubjectRVModal.class));
                // notificando a nuestro adaptador que los datos han cambiado.
                subjectRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // este método se llama cuando se agrega un nuevo hijo
                // estamos notificando a nuestro adaptador y haciendo visible la barra de progreso
                loadingPB.setVisibility(View.GONE);
                subjectRVAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // notificando a nuestro adaptador cuando se elimina un hijo.
                subjectRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notificando a nuestro adaptador cuando se mueve un hijo.
                subjectRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //buscar
    public void searchList(String text){
        ArrayList<SubjectRVModal> searchList = new ArrayList<>();
        for (SubjectRVModal dataClass: subjectRVModalArrayList){
            if (dataClass.getSubjectName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        subjectRVAdapter.searchDataList(searchList);
    }

    //ver horita
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //SE DIFERENCIA EL TIPO DE TOOLBAR DEPENDIENDO DEL ROL DEL USUARIO
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
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


    private void displayBottomSheet(SubjectRVModal modal) {
        // en la línea de abajo estamos creando nuestro diálogo de hoja inferior.
        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // en la línea de abajo estamos inflando nuestro archivo de diseño para nuestra hoja inferior.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRL);
        // configurando la vista de contenido para la hoja inferior en la línea de abajo.
        bottomSheetTeachersDialog.setContentView(layout);
        // en la línea de abajo estamos estableciendo un cancelable
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        // llamando a un método para mostrar nuestra hoja inferior.
        bottomSheetTeachersDialog.show();
        // en la línea de abajo estamos creando variables para
        // nuestro text view e image view dentro de la hoja inferior
        // e inicializándolos con sus ids.
        TextView subjectNameTV = layout.findViewById(R.id.SubjectName);
        TextView subjectDescTV = layout.findViewById(R.id.SubjectDesc);
        ImageView subjectIV = layout.findViewById(R.id.IVSubject);
        // en la línea de abajo estamos estableciendo datos en diferentes vistas.
        subjectNameTV.setText(modal.getSubjectName());
        subjectDescTV.setText(modal.getSubjectDescription());
        Picasso.get().load(modal.getSubjectImg()).into(subjectIV);
        Button viewBtn = layout.findViewById(R.id.VIewSubject);
        Button editBtn = layout.findViewById(R.id.EditSubject);

        // añadiendo un listener de clic para nuestro botón de edición.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // en la línea de abajo estamos abriendo nuestra actividad de EditsubjectActivity.
                Intent i = new Intent(SubjectsMain.this, EditSubjectActivity.class);
                // en la línea de abajo estamos pasando nuestro modal de asignatura.
                i.putExtra("mateID",mateID);
                i.putExtra("subject", modal);
                startActivity(i);
            }
        });
        // añadiendo un listener de clic para nuestro botón de vista en la línea de abajo.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inicializamos el intent para pasar la actividad
                Intent i = new Intent(SubjectsMain.this, CoursesMain.class);
                //Obtenemos el ID del sujeto
                String subjectId = modal.getSubjectId();
                i.putExtra("mateID",mateID);
                //Pasamos el ID como dato extra al intent
                i.putExtra("subjectId", subjectId);
                //Iniciamos la actividad
                startActivity(i);
            }
        });
    }

    @Override
    public void onSubjectClick(int position) {
        //SE DIFERENCIA EL TIPO DE TOOLBAR DEPENDIENDO DEL ROL DEL USUARIO
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo
            if(IdRoles.equals("0")){
                //Inicializamos el intent para pasar la actividad
                String subjectId = subjectRVModalArrayList.get(position).getSubjectId();
                Intent i = new Intent(SubjectsMain.this, CoursesMain.class);
                //Obtenemos el ID del sujeto;
                //Pasamos el ID como dato extra al intent
                i.putExtra("mateID",mateID);
                i.putExtra("subjectId", subjectId);
                //Iniciamos la actividad
                startActivity(i);
            }
            if(IdRoles.equals("1")){
                displayBottomSheet(subjectRVModalArrayList.get(position));
            }
            if(IdRoles.equals("2")){
                displayBottomSheet(subjectRVModalArrayList.get(position));
            }
            if(IdRoles.equals("3")){
                displayBottomSheet(subjectRVModalArrayList.get(position));
            }
        });


    }

}