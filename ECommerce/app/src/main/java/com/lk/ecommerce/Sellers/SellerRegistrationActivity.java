package com.lk.ecommerce.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lk.ecommerce.Buyers.MainActivity;
import com.lk.ecommerce.R;
import com.lk.ecommerce.fcmHelper.FCmClient;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginButton;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private Button registerButton;
    private Button sellerContact;
    private Button sellerOthrSett;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    FCmClient fCmClient;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    private String FCMAToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        mAuth=FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        sellerLoginButton=findViewById(R.id.seller_already_have_account_btn);
        registerButton=findViewById(R.id.seller_register_btn);
        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        emailInput=findViewById(R.id.seller_email);
        passwordInput=findViewById(R.id.seller_password);
        addressInput=findViewById(R.id.seller_address);
        sellerContact=findViewById(R.id.seller_contact_us_btn);
        sellerOthrSett=findViewById(R.id.seller_other_settings);
        initFCM();
        awesomeValidation.addValidation(this, R.id.seller_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.seller_phone, "\\d{10,10}", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.seller_password, "^.{6,14}$", R.string.passworderror);


        sellerOthrSett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegistrationActivity.this, SellerBroadCast.class);
                startActivity(intent);
            }
        });
        sellerContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegistrationActivity.this, ContactUstoSeller.class);
                startActivity(intent);
            }
        });

        sellerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (awesomeValidation.validate()) {
                    registerSeller();

                }
            }
        });

    }

    private void registerSeller() {
        final String name=nameInput.getText().toString();
        final String phone=phoneInput.getText().toString();
       final String email=emailInput.getText().toString();
        String password=passwordInput.getText().toString();
        final String address=addressInput.getText().toString();

        if(!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")){
            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage("Please Wait, While we are checking the credientials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){
                          final DatabaseReference rootRef;
                          rootRef= FirebaseDatabase.getInstance().getReference();

                          String sid=mAuth.getCurrentUser().getUid();

                          HashMap<String,Object> sellerMap=new HashMap<>();
                          sellerMap.put("sid",sid);
                          sellerMap.put("phone",phone);
                          sellerMap.put("email",email);
                          sellerMap.put("address",address);
                          sellerMap.put("name",name);

                          rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  rootRef.child("Sellers").child(sid).child("token").setValue(FCMAToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void aVoid) {
                                         // fCmClient.execute(FCMAToken,"Dear Seller You are Registered Successfully","Shop");
                                          fCmClient.execute(FCMAToken, name+" You are Registered Successfully","Ruwan Super Center");
                                          loadingBar.dismiss();
                                      }
                                  });
                                  Toast.makeText(SellerRegistrationActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                                  Intent intent=new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                  startActivity(intent);

                              }
                          });


                      }else{
                          loadingBar.dismiss();
                          Toast.makeText(SellerRegistrationActivity.this, "Your email already exists or Password is too short ,Please use another one...", Toast.LENGTH_SHORT).show();
                      }
                }
            });

        }else{
            Toast.makeText(this, "Please complete the registration form", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        FCMAToken = task.getResult();


                        // Log and toast
                       // Log.d(TAG, FCMAToken.toString());

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fCmClient = new FCmClient();
    }


}