package com.project.quickreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button call,map,status,ecg;
    private TextView bTemp,heartbeat,eTemp;
    private ImageView tempOK,heartbeatOK,ecgimage;

    private StorageReference mStorageRef=FirebaseStorage.getInstance().getReference("ecg/boom.png");
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    //mStorageRef = FirebaseStorage.getInstance().getReference().child("tiles");
    private String st,bt,bb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        call=findViewById(R.id.callButton);
        map=findViewById(R.id.mapButton);
        status=findViewById(R.id.getStatusButton);
        ecg=findViewById(R.id.getEcgButton);

        bTemp=findViewById(R.id.tempvalue);
        heartbeat=findViewById(R.id.heartRateValue);
        eTemp=findViewById(R.id.aroundTempValue);

        tempOK=findViewById(R.id.tempok);
        heartbeatOK=findViewById(R.id.Rateok);
        ecgimage=findViewById(R.id.image);


        call.setOnClickListener(this);
        map.setOnClickListener(this);
        ecg.setOnClickListener(this);
        status.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.callButton:
                Toast.makeText(this,"calling...",Toast.LENGTH_SHORT).show();
                String ph="tel:9620519930";
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(ph));
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
                } else startActivity(callIntent);
                break;
            case R.id.mapButton:
                Toast.makeText(this,"mapping...",Toast.LENGTH_SHORT).show();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=hospital");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            case R.id.getEcgButton:
                Toast.makeText(this,"downloading...",Toast.LENGTH_SHORT).show();
                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("uri",""+uri);
                        Picasso.get().load(uri).fit().centerCrop().into(ecgimage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("fail","not able to capture");

                    }
                });

                //Toast.makeText(this,"downloading...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.getStatusButton:
                Toast.makeText(this,"getting status....!",Toast.LENGTH_SHORT).show();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String,Object> value = (Map<String, Object>) dataSnapshot.getValue();
                        st=value.get("sTemp").toString();
                        bt=value.get("bTemp").toString();
                        bb=value.get("beatRate").toString();
                        Log.d("msg",""+value);
                        bTemp.setText(bt);
                        eTemp.setText(st);
                        heartbeat.setText(bb);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("msg", "Failed to read value.", error.toException());
                    }
                });

                if (Double.valueOf(bTemp.getText().toString())>97.0){

                    tempOK.setImageResource(R.drawable.wrong);
                }
                else tempOK.setImageResource(R.drawable.feedback);
                if(Integer.valueOf(heartbeat.getText().toString())>120 || Integer.valueOf(heartbeat.getText().toString())<70){

                    heartbeatOK.setImageResource(R.drawable.wrong);
                }else
                    heartbeatOK.setImageResource(R.drawable.feedback);
                break;

        }

    }
}
