package com.example.SaluAPP.Courses;

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
import com.example.SaluAPP.Units.UnitMain;
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

public class CoursesMain extends AppCompatActivity implements CourseRVAdapter.CourseClickInterface {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private ArrayList<CourseRVModal> courseRVModalArrayList;
    private CourseRVAdapter courseRVAdapter;
    private RelativeLayout homeRL;
    private String subjectId;

    FirebaseFirestore fStore;

    FirebaseAuth firebaseAuth;

    String userID;

    Toolbar toolbar;

    Menu opcion1;

    private String mateID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_main);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        // inicializando todas nuestras variables.
        RecyclerView courseRV = findViewById(R.id.idRVCourses);
        mateID = getIntent().getStringExtra("mateID");
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBCoursesLoading);
        // creando variables para fab, base de datos de Firebase,
        // barra de progreso, lista, adaptador, autenticación de Firebase,
        // vista de reciclaje y diseño relativo.
        FloatingActionButton addCourseFAB = findViewById(R.id.idFABAddCourse);
        firebaseDatabase = FirebaseDatabase.getInstance();
        courseRVModalArrayList = new ArrayList<>();
        subjectId = getIntent().getStringExtra("subjectId");
        // en la línea de abajo estamos obteniendo la referencia de la base de datos.

        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses");
        //databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses");
        // en la línea de abajo agregamos un listener de clic para nuestro botón de acción flotante.


        //agregar toolbar
        toolbar=findViewById(R.id.toolbar);
        opcion1 = findViewById(R.id.opcion1);
        setSupportActionBar(toolbar);
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        addCourseFAB.hide();

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            // Verificar el valor del campo

            if(IdRoles.equals("0")){

            }

            if(IdRoles.equals("1")){
                addCourseFAB.show();
                addCourseFAB.setOnClickListener(v -> {
                    // abriendo una nueva actividad para añadir un curso.
                    Intent i = new Intent(CoursesMain.this, AddCoursesActivity.class);
                    i.putExtra("mateID",mateID);
                    i.putExtra("subjectId", subjectId);
                    // en la línea de abajo estamos abriendo nuestra actividad de EditsubjectActivity.
                    startActivity(i);
                });
            }
            if(IdRoles.equals("2")){
                addCourseFAB.show();
                addCourseFAB.setOnClickListener(v -> {
                    // abriendo una nueva actividad para añadir un curso.
                    Intent i = new Intent(CoursesMain.this, AddCoursesActivity.class);
                    i.putExtra("mateID",mateID);
                    i.putExtra("subjectId", subjectId);
                    // en la línea de abajo estamos abriendo nuestra actividad de EditsubjectActivity.
                    startActivity(i);
                });

            }
            if(IdRoles.equals("3")){

            }
        });


        // en la línea de abajo estamos inicializando nuestra clase de adaptador.
        courseRVAdapter = new CourseRVAdapter(courseRVModalArrayList, this, this);
        // configurando el gestor de diseño para la vista de reciclaje en la línea de abajo.
        courseRV.setLayoutManager(new LinearLayoutManager(this));
        // estableciendo el adaptador para la vista de reciclaje en la línea de abajo.
        courseRV.setAdapter(courseRVAdapter);
        // en la línea de abajo llamamos a un método para obtener cursos de la base de datos.
        getCourses();
    }


    private void getCourses() {
// en la línea de abajo limpiamos nuestra lista.
        courseRVModalArrayList.clear();
// en la línea de abajo estamos llamando al método de escucha de eventos de agregar hijo para leer los datos.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
// en la línea de abajo estamos ocultando nuestra barra de progreso.
                if (courseRVModalArrayList != null) {
                    loadingPB.setVisibility(View.GONE);
                }
// añadiendo instantánea a nuestra lista de arrays en la línea de abajo.
                courseRVModalArrayList.add(snapshot.getValue(CourseRVModal.class));
// notificando a nuestro adaptador que los datos han cambiado.
                courseRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // este método se llama cuando se agrega un nuevo hijo
                // estamos notificando a nuestro adaptador y haciendo visible la barra de progreso
                loadingPB.setVisibility(View.GONE);
                courseRVAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // notificando a nuestro adaptador cuando se elimina un hijo.
                courseRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notificando a nuestro adaptador cuando se mueve un hijo.
                courseRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //buscar
    public void searchList(String text){
        ArrayList<CourseRVModal> searchList = new ArrayList<>();
        for (CourseRVModal dataClass: courseRVModalArrayList){
            if (dataClass.getCourseName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        courseRVAdapter.searchDataList(searchList);
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



    private void displayBottomSheet(CourseRVModal modal) {
        // en la línea de abajo estamos creando nuestro diálogo de hoja inferior.
        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // en la línea de abajo estamos inflando nuestro archivo de diseño para nuestra hoja inferior.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout_course, homeRL);
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
        TextView courseNameTV = layout.findViewById(R.id.CourseName);
        TextView courseDescTV = layout.findViewById(R.id.CourseDesc);
        ImageView courseIV = layout.findViewById(R.id.IVCourse);
        // en la línea de abajo estamos estableciendo datos en diferentes vistas.
        courseNameTV.setText(modal.getCourseName());
        courseDescTV.setText(modal.getCourseDescription());
        Picasso.get().load(modal.getCourseImg()).into(courseIV);
        Button viewBtn = layout.findViewById(R.id.ViewCourse);
        Button editBtn = layout.findViewById(R.id.EditCourse);

        // añadiendo un listener de clic para nuestro botón de edición.
        editBtn.setOnClickListener(v -> {
            // en la línea de abajo estamos abriendo nuestra actividad de EditCourseActivity.
            Intent i = new Intent(CoursesMain.this, EditCoursesActivity.class);
            // en la línea de abajo estamos pasando nuestro modal de curso.
            i.putExtra("mateID",mateID);
            i.putExtra("course", modal);
            i.putExtra("subjectId", subjectId);
            startActivity(i);
        });
        // añadiendo un listener de clic para nuestro botón de vista en la línea de abajo.
        viewBtn.setOnClickListener(v -> {
            //Inicializamos el intent para pasar a la actividad
            Intent i = new Intent(CoursesMain.this, UnitMain.class);
            String courseId = modal.getCourseId();
            //Pasamos el ID como dato extra al intent
            i.putExtra("mateID",mateID);
            i.putExtra("subjectId", subjectId);
            i.putExtra("courseId", courseId);
            //Iniciamos la actividad
            startActivity(i);
        });
    }

    @Override
    public void onCourseClick(int position) {
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.get().addOnSuccessListener(document -> {
            // Obtener el valor del campo
            String IdRoles = document.getString("IdRoles");

            if(IdRoles.equals("0")){
                //Inicializamos el intent para pasar la actividad
                String courseId = courseRVModalArrayList.get(position).getCourseId();
                Intent i = new Intent(CoursesMain.this, UnitMain.class);
                //Obtenemos el ID del sujeto;
                //Pasamos el ID como dato extra al intent
                i.putExtra("mateID",mateID);
                i.putExtra("subjectId", subjectId);
                i.putExtra("courseId", courseId);
                //Iniciamos la actividad
                startActivity(i);
            }

            if(IdRoles.equals("1")){
                if (courseRVModalArrayList != null && position < courseRVModalArrayList.size()) {
                    displayBottomSheet(courseRVModalArrayList.get(position));
                } else {
                }
            }

            if(IdRoles.equals("2")){
                if (courseRVModalArrayList != null && position < courseRVModalArrayList.size()) {
                    displayBottomSheet(courseRVModalArrayList.get(position));
                } else {
                }
            }

            // Verificar el valor del campo
            if(IdRoles.equals("3")){
                if (courseRVModalArrayList != null && position < courseRVModalArrayList.size()) {
                    displayBottomSheet(courseRVModalArrayList.get(position));
                } else {
                }
            }



        });




    }

}
