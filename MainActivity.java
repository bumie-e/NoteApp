package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView UserPic;
    FloatingActionButton btnAddNotes;
    ListView listView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<Member> list;
    ArrayAdapter<Member> adapter;
    TextView UserName, UserEmail;
    Member user;
    FirebaseFirestore firestore;
    String userId;
    List<Member> contents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listV);
        UserName = (TextView) findViewById(R.id.user_UserName);
        UserEmail = (TextView) findViewById(R.id.user_email);
        UserPic = (ImageView) findViewById(R.id.user_pic);


        contents = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        //
        user = new Member();

        /*
        try{
            userId = firebaseAuth.getCurrentUser().getUid();

            DocumentReference documentReference = firestore.collection("User").document(userId);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    UserEmail.setText(value.getString("email"));
                    UserName.setText(value.getString("UserName"));
                }
            });
        } catch (Exception e) {
            UserEmail.setText("Couldn't retrive your email");
            UserName.setText("Couldn't retrive your User Name");
        }*/

        list = new ArrayList<>();
        adapter = new ArrayAdapter<Member>(this, R.layout.view_notes,R.id.tvContent, list);
        databaseReference = firebaseDatabase.getReference( "Member");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    user = ds.getValue(Member.class);
                    list.add(user);
                }
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        btnAddNotes = findViewById(R.id.fab);
        listView = findViewById(R.id.listView);
        contents = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member member = contents.get(position);

                Intent i = new Intent(getApplicationContext(), EditNotes.class);
                i.putExtra("NOTESTITLE", member.getTitle());
                i.putExtra("NOTESCONTENT", member.getContent());
                startActivity(i);
            }
        });

        //setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_allNotes);
        // hide or show items

       // Menu menu = navigationView.getMenu();
       // menu.findItem(R.id.nav_logout).setVisible(false);

        //imChangeLin.setVisibility(View.INVISIBLE);


        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddNotes.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_allNotes:
                break;
            case R.id.nav_impNotes:
                Intent intent3 = new Intent(MainActivity.this, ImportantNotes.class);
                startActivity(intent3);
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_addpic:
                Intent intent1 = new Intent(MainActivity.this, Profile.class);
                startActivity(intent1);
                break;
            case R.id.nav_logOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(getApplicationContext(), frag_login.class);
                startActivity(intent2);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}