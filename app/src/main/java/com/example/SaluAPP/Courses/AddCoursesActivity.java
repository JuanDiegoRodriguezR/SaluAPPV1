package com.example.SaluAPP.Courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;




import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.SaluAPP.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCoursesActivity extends AppCompatActivity {
    private Button addCourseBtn;
    private TextInputEditText courseNameEdt, courseDescEdt, courseImgEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private String courseID;
    private String subjectId;

    private String mateID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_courses);
        // Inicializando todas nuestras variables
        addCourseBtn = findViewById(R.id.BtnAddCourse);
        courseNameEdt = findViewById(R.id.idEdtCourseName);
        courseDescEdt = findViewById(R.id.idEdtCourseDescription);
        courseImgEdt = findViewById(R.id.idEdtCourseImageLink);
        loadingPB = findViewById(R.id.idPBCourseLoading);
        mateID = getIntent().getStringExtra("mateID");
        firebaseDatabase = FirebaseDatabase.getInstance();
        subjectId = getIntent().getStringExtra("subjectId");
        // En la siguiente línea estamos creando nuestra referencia de base de datos.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses");
        //databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses");

        // Agregando un listener de clic para nuestro botón de añadir asignatura.
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtener datos de nuestros campos de texto.
                String courseName = courseNameEdt.getText().toString().trim();
                String courseDesc = courseDescEdt.getText().toString().trim();
                String courseImg = courseImgEdt.getText().toString().trim();

                if (TextUtils.isEmpty(courseName)){
                    courseNameEdt.setError("Ingrese el nombre del curso");
                    return;
                }

                if (TextUtils.isEmpty(courseImg)){
                    courseImgEdt.setError("Ingrese la URL de la imagen del curso");
                    return;
                }

                if (TextUtils.isEmpty(courseDesc)){
                    courseDescEdt.setError("Ingrese la descripción del curso");
                    return;
                }


                if (courseName.isEmpty() || courseDesc.isEmpty() || courseImg.isEmpty()) {
                    // Mostrar una alerta si algún campo está vacío.
                    Toast.makeText(AddCoursesActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {

                    if (android.util.Patterns.WEB_URL.matcher(courseImg).matches()) {
                        loadingPB.setVisibility(View.VISIBLE);
                        loadingPB.setVisibility(View.VISIBLE);
                        // Continuar con la lógica de agregar a la base de datos si todos los campos están completos.
                        String courseID = databaseReference.push().getKey(); // Generar un ID único para el curso
                        if (courseID != null) {
                            CourseRVModal courseRVModal = new CourseRVModal(courseID, courseName, courseDesc, courseImg);
                            databaseReference.child(courseID).setValue(courseRVModal)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingPB.setVisibility(View.GONE);
                                            Toast.makeText(AddCoursesActivity.this, "¡Modulo añadido!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddCoursesActivity.this, CoursesMain.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddCoursesActivity.this, "No se pudo agregar el Modulo.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(AddCoursesActivity.this, "No se pudo obtener un ID para el Modulo.", Toast.LENGTH_SHORT).show();
                            loadingPB.setVisibility(View.GONE);
                        }
                    }else {
                        // La cadena no es una URL válida
                        Toast.makeText(AddCoursesActivity.this, "La url de la imagen no es valida.", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });


    }
}
