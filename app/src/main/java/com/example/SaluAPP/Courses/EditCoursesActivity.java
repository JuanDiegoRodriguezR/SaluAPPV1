package com.example.SaluAPP.Courses;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.SaluAPP.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditCoursesActivity extends AppCompatActivity {

    private TextInputEditText CourseNameEdt, CourseDescEdt, CourseImgEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    CourseRVModal CourseRVModal;
    private ProgressBar loadingPB;
    private String CourseID;
    private String subjectId;

    private String mateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_courses);
        Button editCourseBtn = findViewById(R.id.idBtnAddCourse);
        CourseNameEdt = findViewById(R.id.idEdtCourseName);
        mateID = getIntent().getStringExtra("mateID");
        CourseDescEdt = findViewById(R.id.idEdtCourseDescription);
        CourseImgEdt = findViewById(R.id.idEdtCourseImageLink);
        loadingPB = findViewById(R.id.idPBCourseLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        CourseRVModal = getIntent().getParcelableExtra("course");
        subjectId = getIntent().getStringExtra("subjectId");
        Button deleteCourseBtn = findViewById(R.id.BtnDeleteCourse);

        if (CourseRVModal != null) {
            CourseNameEdt.setText(CourseRVModal.getCourseName());
            CourseImgEdt.setText(CourseRVModal.getCourseImg());
            CourseDescEdt.setText(CourseRVModal.getCourseDescription());
            CourseID = CourseRVModal.getCourseId();
        }

        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(subjectId).child("Courses");
      //  databaseReference = firebaseDatabase.getReference("Subjects").child(subjectId).child("Courses");
        editCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);

                String CourseName = CourseNameEdt.getText().toString().trim();
                String CourseDesc = CourseDescEdt.getText().toString().trim();
                String CourseImg = CourseImgEdt.getText().toString().trim();

                if (TextUtils.isEmpty(CourseName)){
                    CourseNameEdt.setError("Ingrese el nombre del curso para editar");
                    return;
                }

                if (TextUtils.isEmpty(CourseDesc)){
                    CourseDescEdt.setError("Ingrese la URL de la imagen del curso para editar");
                    return;
                }

                if (TextUtils.isEmpty(CourseImg)){
                    CourseImgEdt.setError("Ingrese la descripción del curso para editar");
                    return;
                }

                if (CourseName.isEmpty() || CourseDesc.isEmpty() || CourseImg.isEmpty()) {
                    Toast.makeText(EditCoursesActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    if (!CourseName.equals(CourseRVModal.getCourseName())) {
                        map.put("courseName", CourseName);
                    }
                    if (!CourseDesc.equals(CourseRVModal.getCourseDescription())) {
                        map.put("courseDescription", CourseDesc);
                    }
                    if (!CourseImg.equals(CourseRVModal.getCourseImg())) {
                        map.put("courseImg", CourseImg);
                    }
                    map.put("courseId", CourseID);

                    // Verifica si CourseRVModal no es nulo antes de actualizar los datos
                    if (CourseRVModal != null) {
                        // Actualiza los datos del curso en la base de datos
                        databaseReference.child(CourseID).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loadingPB.setVisibility(View.GONE);
                                        Toast.makeText(EditCoursesActivity.this, "Modulo actualizado..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditCoursesActivity.this, CoursesMain.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditCoursesActivity.this, "Fallo al actualizar el Modulo..", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(EditCoursesActivity.this, "Fallo al cargar los datos del Modulo.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CourseRVModal != null) {
                    deleteCourse(CourseID);
                } else {
                    Toast.makeText(EditCoursesActivity.this, "No se pudo eliminar el Modulo. Modulo no encontrado.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void deleteCourse(String courseId) {
        DatabaseReference courseRef = databaseReference.child(courseId);
        courseRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditCoursesActivity.this, "Modulo eliminado..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditCoursesActivity.this, CoursesMain.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditCoursesActivity.this, "No se pudo eliminar el Modulo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
