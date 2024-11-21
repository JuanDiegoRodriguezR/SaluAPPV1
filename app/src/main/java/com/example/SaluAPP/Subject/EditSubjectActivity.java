package com.example.SaluAPP.Subject;

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
import com.example.SaluAPP.SubjectsMain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
public class EditSubjectActivity extends AppCompatActivity {

    // creando variables para nuestro campo de edición, base de datos de Firebase,
    // referencia de base de datos, SubjectRVModal y barra de progreso.
    private TextInputEditText SubjectNameEdt, SubjectDescEdt, SubjectImgEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SubjectRVModal SubjectRVModal;
    private ProgressBar loadingPB;
    // creando un string para nuestro ID de asignatura.
    private String SubjectID;

    private String mateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_subject);
        // inicializando todas nuestras variables en la línea de abajo.
        Button editSubjectBtn = findViewById(R.id.idBtnAddSubject);
        SubjectNameEdt = findViewById(R.id.idEdtSubjectName);
        SubjectDescEdt = findViewById(R.id.idEdtSubjectDescription);
        SubjectImgEdt = findViewById(R.id.idEdtSubjectImageLink);
        mateID = getIntent().getStringExtra("mateID");
        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();

        // en la línea de abajo obtenemos nuestra clase modal en la que hemos pasado.
        SubjectRVModal = getIntent().getParcelableExtra("subject");
        Button deleteSubjectBtn = findViewById(R.id.BtnDeleteSubject);

        if (SubjectRVModal != null) {
            // en la línea de abajo establecemos los datos en nuestro campo de edición desde nuestra clase modal.
            SubjectNameEdt.setText(SubjectRVModal.getSubjectName());
            SubjectImgEdt.setText(SubjectRVModal.getSubjectImg());
            SubjectDescEdt.setText(SubjectRVModal.getSubjectDescription());
            SubjectID = SubjectRVModal.getSubjectId();
        }

        // en la línea de abajo estamos inicializando nuestra referencia de base de datos y estamos agregando un hijo como nuestro ID de asignatura.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects").child(SubjectID);
       // databaseReference = firebaseDatabase.getReference("Subjects").child(SubjectID);
        // en la línea de abajo agregamos un listener de clic para nuestro botón de añadir asignatura.
        editSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPB.setVisibility(View.VISIBLE);
                String SubjectName = SubjectNameEdt.getText().toString().trim();
                String SubjectDesc = SubjectDescEdt.getText().toString().trim();
                String SubjectImg = SubjectImgEdt.getText().toString().trim();

                if (TextUtils.isEmpty(SubjectName)){
                    SubjectNameEdt.setError("Ingrese el nombre de asignatura para editar");
                    return;
                }

                if (TextUtils.isEmpty(SubjectImg)){
                    SubjectImgEdt.setError("Ingrese la URL de la imagen de la asignatura para editar");
                    return;
                }

                if (TextUtils.isEmpty(SubjectDesc)){
                    SubjectDescEdt.setError("Ingrese la descripción de la asignatura para editar");
                    return;
                }

                if (SubjectName.isEmpty() || SubjectDesc.isEmpty() || SubjectImg.isEmpty()) {
                    // Mostrar una alerta si algún campo está vacío.
                    Toast.makeText(EditSubjectActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    // Continuar con la lógica de actualización de la base de datos si todos los campos están completos.
                    Map<String, Object> map = new HashMap<>();
                    // Verifica si los datos se han cambiado antes de actualizar.
                    if (!SubjectName.equals(SubjectRVModal.getSubjectName())) {
                        map.put("subjectName", SubjectName);
                    }
                    if (!SubjectDesc.equals(SubjectRVModal.getSubjectDescription())) {
                        map.put("subjectDescription", SubjectDesc);
                    }
                    if (!SubjectImg.equals(SubjectRVModal.getSubjectImg())) {
                        map.put("subjectImg", SubjectImg);
                    }
                    map.put("subjectId", SubjectID);

                    // Actualiza solo los campos necesarios en la base de datos.
                    databaseReference.updateChildren(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingPB.setVisibility(View.GONE);
                                    Toast.makeText(EditSubjectActivity.this, "Recurso actualizado..", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditSubjectActivity.this, SubjectsMain.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditSubjectActivity.this, "Fallo al actualizar la asignatura..", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    // adding a click listener for our delete Subject button.
        deleteSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to delete a Subject.
                deleteSubject(SubjectID);
            }
        });

    }

    private void deleteSubject(String subjectId) {
        // Obten la referencia al nodo específico que quieres eliminar.
        // Llama al método removeValue() en la referencia para eliminar el nodo.
        databaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Mostrar un mensaje toast cuando el nodo se haya eliminado con éxito.
                        Toast.makeText(EditSubjectActivity.this, "Recurso eliminado..", Toast.LENGTH_SHORT).show();
                        // Abrir la actividad principal.
                        startActivity(new Intent(EditSubjectActivity.this, SubjectsMain.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el caso en que la eliminación del nodo falle.
                        Toast.makeText(EditSubjectActivity.this, "No se pudo eliminar el Recurso.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}