package com.lk.ecommerce.Sellers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.ecommerce.Buyers.ResetPasswordActivity;
import com.lk.ecommerce.Buyers.SettinsActivity;
import com.lk.ecommerce.R;

public class ContactUstoSeller extends AppCompatActivity {

    private TextView chatNum;
    private TextView callNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_usto_seller);

        chatNum = (TextView) findViewById(R.id.chat_num);
        callNum = (TextView) findViewById(R.id.call_num);


        chatNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openWhatsApp();
            }
        });
        callNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+94771765732";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });


    }

    private void openWhatsApp() {
        String smsNumber = "94771765732"; //without '+'
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi, i need to contact you");
            sendIntent.putExtra("jid", smsNumber + "@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch(Exception e) {
            Toast.makeText(this, "Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}