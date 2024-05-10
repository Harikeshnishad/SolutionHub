package com.hari.solutionhub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hari.solutionhub.Adapters.PostAdapter;
import com.hari.solutionhub.Model.Post;
import com.hari.solutionhub.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    FloatingActionButton fab;
    private ProgressBar progressBar;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private CircleImageView navHeaderImage;
    private TextView nav_headerEmail, nav_headerName;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.home_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SolutionHub");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress_cercular);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AskActivity.class);
                startActivity(intent);
            }
        });
        nav_headerEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        nav_headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        navHeaderImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance()
                .getCurrentUser()).getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nav_headerName.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                nav_headerEmail.setText(Objects.requireNonNull(snapshot.child("email").getValue()).toString());

                String imageUrl = Objects.requireNonNull(snapshot.child("profileImageUrl").getValue()).toString();
                Glide.with(HomeActivity.this).load(imageUrl).into(navHeaderImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(HomeActivity.this, postList);
        recyclerView.setAdapter(postAdapter);
        readQuestionPosts();


    }

    private void readQuestionPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_Finance) {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_sport) {
            Intent intents = new Intent(HomeActivity.this, CategoryActivity.class);
            intents.putExtra("title", "Sports");
            startActivity(intents);

        } else if (itemId == R.id.nav_Food) {
            Intent intentF = new Intent(HomeActivity.this, CategoryActivity.class);
            intentF.putExtra("title", "food");
            startActivity(intentF);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

     @Override
    public void onBackPressed() {
       if (drawerLayout.isDrawerOpen(GravityCompat.START)){
           drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }
}