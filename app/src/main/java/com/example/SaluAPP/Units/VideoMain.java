package com.example.SaluAPP.Units;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SaluAPP.Comment;
import com.example.SaluAPP.MainActivity;
import com.example.SaluAPP.Profile.ProfileMain;
import com.example.SaluAPP.R;
import com.example.SaluAPP.perfil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoMain extends AppCompatActivity implements CommentAdapter.OnLikeClickListener{


    ExoPlayer player;
    private WebView webView;

    Toolbar toolbar;

    Menu opcion1,buscar;

    String userID,url;

    FirebaseFirestore fStore;

    FirebaseAuth firebaseAuth;

    TextView UnitName, UnitDesc, UnitRcmd, Descripcion, Recomendacion;

    UnitRVModal UnitRVModal;
    private DatabaseReference commentsRef;
    private String courseId,subjectId,unitId;

    private String mateID;
    private ListView commentsListView;
    private List<Comment> commentList;
    private EditText commentEditText;
    private Button sendCommentButton;
    private CommentAdapter commentAdapter;
    StorageReference storageReference;

    ImageView profilePicture;
    String videoUrl="https://www.dailymotion.com/video/x8pbwm8";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);
        firebaseAuth = FirebaseAuth.getInstance();

        fStore= FirebaseFirestore.getInstance();

        //toolbar
        toolbar=findViewById(R.id.toolbar);
        opcion1 = findViewById(R.id.opcion1);

        setSupportActionBar(toolbar);
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        //cargar imagen w


        // exoplayer descartado




        //obtener videos y texto
        UnitRVModal = getIntent().getParcelableExtra("unit");



        UnitName = findViewById(R.id.TituloUnidad);
        UnitDesc = findViewById(R.id.DescripcionUnidad);
        UnitRcmd= findViewById(R.id.RecomendacionUnidad);
        Descripcion = findViewById(R.id.Descripcion);
        Recomendacion = findViewById(R.id.Recomendacion);


        if (UnitRVModal != null) {
            UnitName.setText(UnitRVModal.getUnitName());
            url = UnitRVModal.getUnitVid();
            Descripcion.setText(UnitRVModal.getUnitDescription());
            Recomendacion.setText(UnitRVModal.getUnitRcmd());
        }



       /* StyledPlayerView playerView = findViewById(R.id.player_view);
        player = new ExoPlayer.Builder(VideoMain.this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);*/
        webView = findViewById(R.id.webView);

        // video
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        // Load the Dailymotion video URL
        String dailymotionUrl ="https://www.youtube.com/embed/" + url + "?autoplay=1&rel=0";
        webView.loadUrl(dailymotionUrl);


        mateID = getIntent().getStringExtra("mateID");
        subjectId = getIntent().getStringExtra("subjectId");
        courseId = getIntent().getStringExtra("courseId");
        unitId = getIntent().getStringExtra("unitId");

        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        // Inicializar la base de datos de Firebase
        commentsRef = FirebaseDatabase.getInstance().getReference("Materias").child(mateID).
                child("Subjects").child(subjectId).child("Courses").child(courseId).child("Units").child(unitId).child("Comments");

        // Inicializar vistas
        commentsListView = findViewById(R.id.commentsListView);
        commentEditText = findViewById(R.id.commentEditText);
        sendCommentButton = findViewById(R.id.sendCommentButton);

        // Inicializar la lista de comentarios y el adaptador
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, commentsRef);

        commentsListView.setAdapter(commentAdapter);
        commentAdapter.setOnLikeClickListener(this); // Configurar el listener para el botón de "Me gusta"

        // Cargar comentarios de la base de datos
        loadComments();
        // Manejar evento de clic en el botón de enviar comentario
        sendCommentButton.setOnClickListener(v -> {
            String message = commentEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                addComment(message);
                commentEditText.setText("");
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
                getMenuInflater().inflate(R.menu.menuprofesor,menu);
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
    // Cargar comentarios desde la base de datos
    private void loadComments() {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    // Llamar a loadProfilePicture para cargar la imagen de perfil del usuario correspondiente
                    loadProfilePicture(comment.getUserId(), comment);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
            }
        });

        // Configurar el listener para el botón de "Me gusta"
        commentAdapter.setOnLikeClickListener(this);
    }

    // Método para cargar la imagen de perfil del usuario que ha hecho el comentario
    private void loadProfilePicture(String userId, Comment comment) {
        // Obtener la referencia de almacenamiento de la imagen de perfil del usuario
        StorageReference profileRef = storageReference.child("users/"+ userId + "/profile.jpg");

        // Obtener la URL de descarga de la imagen de perfil
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Cargar la imagen de perfil en el ImageView correspondiente
                if (profilePicture != null) {
                    Picasso.get().load(uri).into(profilePicture);
                }
                // Guardar la URL de la imagen de perfil en el objeto Comment
                comment.setProfilePictureUrl(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Imprimir el mensaje de error
                Log.e("LoadProfilePicture", "Error al cargar la imagen: " + e.getMessage());
            }
        });
    }



    // Agregar un nuevo comentario a la base de datos
    private void addComment(String message) {
        String commentId = commentsRef.push().getKey();
        if (commentId != null) {
            Comment comment = new Comment(commentId, message, getCurrentUserId());
            comment.setDate(new Date()); // Establece la fecha actual
            commentsRef.child(commentId).setValue(comment);
        }
    }


    // Obtener el ID del usuario actualmente autenticado
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    // Método para manejar la funcionalidad de "Me gusta" en los comentarios
    @Override
    public void onLikeClick(Comment comment) {
        String currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            if (comment.getLikedUserIds() != null && !comment.getLikedUserIds().contains(currentUserId)) {
                comment.incrementLikes();
                comment.getLikedUserIds().add(currentUserId);
                comment.setLiked(true);

            } else {
                comment.decrementLikes();
                comment.getLikedUserIds().remove(currentUserId);
                comment.setLiked(false);
            }
            updateCommentInDatabase(comment);
            commentAdapter.notifyDataSetChanged();
        }
    }



    private void updateCommentInDatabase(Comment comment) {
        DatabaseReference commentRef = commentsRef.child(comment.getId());
        commentRef.child("likesCount").setValue(comment.getLikesCount());
        commentRef.child("likedUserIds").setValue(comment.getLikedUserIds());
        commentRef.child("isLiked").setValue(comment.isLiked()); // Actualizar el estado de "isLiked" en la base de datos
    }


}