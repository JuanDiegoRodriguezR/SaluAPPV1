package com.example.SaluAPP.Units;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.SaluAPP.Comment;
import com.example.SaluAPP.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class CommentAdapter extends BaseAdapter {

    private final Context context;

    private final List<Comment> commentList;
    private OnLikeClickListener onLikeClickListener;

    private DatabaseReference commentsRef; // Agrega la referencia a la base de datos

    public CommentAdapter(Context context, List<Comment> commentList, DatabaseReference commentsRef) {
        this.context = context;
        this.commentList = commentList;
        this.commentsRef = commentsRef;
    }
    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        TextView userNameTextView = view.findViewById(R.id.userIdTextView);
        TextView likesCountTextView = view.findViewById(R.id.likesCountTextView);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        Button likeButton = view.findViewById(R.id.likeButton);
        ImageView profilePicture = view.findViewById(R.id.IdImagenUser);

        Comment comment = commentList.get(position);
        String profilePictureUrl = comment.getProfilePictureUrl();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(profilePicture);
            updateCommentInDatabase(comment);
        } else {

        }


        // Obtener los datos del usuario correspondientes al userId
        FirebaseFirestore.getInstance().collection("users").document(comment.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("Nombre_Apellido");
                        userNameTextView.setText(userName);
                    } else {
                        userNameTextView.setText("Usuario Desconocido");
                    }
                })
                .addOnFailureListener(e -> userNameTextView.setText("Error al obtener usuario"));

        messageTextView.setText(comment.getMessage());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        dateTextView.setText(dateFormat.format(comment.getDate()));
        likesCountTextView.setText(context.getString(R.string.likes_count, comment.getLikesCount()));

        likeButton.setOnClickListener(v -> {
            if (onLikeClickListener != null) {
                onLikeClickListener.onLikeClick(comment);
                // Actualizar el estado de "Me gusta" del comentario en la base de datos
                boolean updatedIsLiked = !comment.isLiked(); // Invertir el valor de isLiked
                comment.setLiked(updatedIsLiked);
                // Actualizar el botón de "Me gusta" en la vista
                likeButton.setBackgroundResource(updatedIsLiked ? R.drawable.ic_not_liked: R.drawable.ic_liked);
            }
        });
        Button deleteButton = view.findViewById(R.id.deleteButton);
        if (comment.getUserId().equals(getCurrentUserId())) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        deleteButton.setOnClickListener(v -> {
            // Verificar si el usuario es el autor del comentario
            if (comment.getUserId().equals(getCurrentUserId())) {
                deleteComment(comment);
            } else {
            }
        });
        Button editButton = view.findViewById(R.id.editButton);
        if (comment.getUserId().equals(getCurrentUserId())) {
            editButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(v -> {
            // Verificar si el usuario es el autor del comentario
            if (comment.getUserId().equals(getCurrentUserId())) {
                // Lógica para permitir al usuario editar el comentario
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Editar comentario");

                // Crear un EditText para que el usuario edite el comentario
                final EditText input = new EditText(context);
                input.setText(comment.getMessage()); // Establecer el texto actual del comentario
                builder.setView(input);

                // Agregar botones al cuadro de diálogo
                builder.setPositiveButton("Guardar", (dialog, which) -> {
                    String updatedMessage = input.getText().toString();
                    // Realizar la lógica para actualizar el comentario en la base de datos
                    comment.setMessage(updatedMessage);
                    updateCommentInDatabase(comment);
                });
                builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                // Mostrar el cuadro de diálogo
                builder.show();
            } else {
                // Notificar al usuario que no tiene permiso para editar el comentario
                Toast.makeText(context, "No tienes permiso para editar este comentario", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private void deleteComment(Comment comment) {
        commentsRef.child(comment.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
    public interface OnLikeClickListener {
        void onLikeClick(Comment comment);
    }
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }
    private void updateCommentInDatabase(Comment comment) {
        commentsRef.child(comment.getId()).setValue(comment)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }
}
