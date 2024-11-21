package com.example.SaluAPP.Subject;

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
import com.example.SaluAPP.SubjectsMain;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddSubjectActivity extends AppCompatActivity {
    private Button addSubjectBtn;
    private TextInputEditText subjectNameEdt, subjectDescEdt, subjectImgEdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ProgressBar loadingPB;
    private String subjectID;

    private String mateID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        // Inicializando todas nuestras variables
        addSubjectBtn = findViewById(R.id.BtnAddSubject);
        subjectNameEdt = findViewById(R.id.idEdtSubjectName);
        subjectDescEdt = findViewById(R.id.idEdtSubjectDescription);
        subjectImgEdt = findViewById(R.id.idEdtSubjectImageLink);
        mateID = getIntent().getStringExtra("mateID");

        loadingPB = findViewById(R.id.idPBLoading);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // En la siguiente línea estamos creando nuestra referencia de base de datos.
        databaseReference = firebaseDatabase.getReference("Materias").child(mateID).child("Subjects");
        //databaseReference = firebaseDatabase.getReference("Subjects");
        // Agregando un listener de clic para nuestro botón de añadir asignatura.
        addSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtener datos de nuestros campos de texto.
                String subjectName = subjectNameEdt.getText().toString().trim();
                String subjectDesc = subjectDescEdt.getText().toString().trim();
                String subjectImg = subjectImgEdt.getText().toString().trim();

                if (TextUtils.isEmpty(subjectName)){
                    subjectNameEdt.setError("Ingrese el nombre de asignatura");
                    return;
                }

                if (TextUtils.isEmpty(subjectImg)){
                    subjectImgEdt.setError("Ingrese URL de la imagen de la asignatura");
                    return;
                }

                if (TextUtils.isEmpty(subjectDesc)){
                    subjectDescEdt.setError("Ingrese la descripción de la asignatura");
                    return;
                }

                if (subjectName.isEmpty() || subjectDesc.isEmpty() || subjectImg.isEmpty()) {
                    // Mostrar una alerta si algún campo está vacío.
                    Toast.makeText(AddSubjectActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    loadingPB.setVisibility(View.GONE);
                } else {
                    if (android.util.Patterns.WEB_URL.matcher(subjectImg).matches()) {
                        loadingPB.setVisibility(View.VISIBLE);
                        // Continuar con la lógica de agregar a la base de datos si todos los campos están completos.
                        subjectID = databaseReference.push().getKey();
                        // Resto del código para agregar a la base de datos...
                        SubjectRVModal subjectRVModal = new SubjectRVModal(subjectID, subjectName, subjectDesc, subjectImg);
                        // En la siguiente línea estamos llamando a un evento de agregado de valor
                        // para pasar datos a la base de datos de Firebase.
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // En la siguiente línea estamos estableciendo datos en nuestra base de datos de Firebase.
                                databaseReference.child(subjectID).setValue(subjectRVModal);
                                // Mostrando un mensaje emergente (toast)..
                                Toast.makeText(AddSubjectActivity.this, "¡Recurso añadido!", Toast.LENGTH_SHORT).show();
                                // Iniciando una actividad principal.
                                startActivity(new Intent(AddSubjectActivity.this, SubjectsMain.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Mostrando un mensaje de error en la siguiente línea.
                                Toast.makeText(AddSubjectActivity.this, "No se pudo agregar el Recurso..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        // La cadena no es una URL válida
                        Toast.makeText(AddSubjectActivity.this, "La url de la imagen no es valida.", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
    }

}