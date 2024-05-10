package com.hari.solutionhub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hari.solutionhub.Model.Post;
import com.hari.solutionhub.Model.User;
import com.hari.solutionhub.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context context;
    public List<Post>postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.questions_retrieved_layout,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = postList.get(position);
        if (post.getQuestionImage() == null){
            holder.qsImage.setVisibility(View.GONE);
        }
        holder.qsImage.setVisibility(View.VISIBLE);

        Glide.with(context).load(post.getQuestionImage()).into(holder.qsImage);
        holder.expandableTextView.setText(post.getQuestion());
        holder.topicTextView.setText(post.getTopic());
        holder.askedOnTextview.setText(post.getDate());
        publisherInformation(holder.publisher_profile,holder.asked_by_textview,post.getPublisher());

    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView publisher_profile;
        public TextView asked_by_textview,Likes,Dislikes,Comments;
        public ImageView qsImage,more,like,disLike,comment,save;
        public TextView topicTextView,askedOnTextview;
        private ExpandableTextView expandableTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile = itemView.findViewById(R.id.publisher_profile);
            asked_by_textview = itemView.findViewById(R.id.asked_by_textview);
            Likes = itemView.findViewById(R.id.likes);
           Dislikes = itemView.findViewById(R.id.dislikes);
            Comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);
            like = itemView.findViewById(R.id.like);
            disLike = itemView.findViewById(R.id.dislike);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            qsImage = itemView.findViewById(R.id.qsImage);
            topicTextView = itemView.findViewById(R.id.topicTextview);
           askedOnTextview = itemView.findViewById(R.id.askedOnTextview);
            expandableTextView = itemView.findViewById(R.id.expandable_text_view);


        }
    }
    private void publisherInformation(CircleImageView publisher_profile, TextView askedBy, String userId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getProfileImageUrl()).into(publisher_profile);
                askedBy.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}
