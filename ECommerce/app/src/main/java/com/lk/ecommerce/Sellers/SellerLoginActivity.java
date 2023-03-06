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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lk.ecommerce.R;

public class SellerLoginActivity extends AppCompatActivity {
    private Button loginSellerBtn;
    private EditText emailInput,passwordInput;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        mAuth=FirebaseAuth.getInstance();

        emailInput=findViewById(R.id.seller_login_email);
        passwordInput=findViewById(R.id.seller_login_password);
        loginSellerBtn=findViewById(R.id.seller_login_btn);
        loadingBar = new ProgressDialog(this);

        awesomeValidation.addValidation(this, R.id.seller_login_email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.seller_login_password, "^.{6,14}$", R.string.passworderror);

        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (awesomeValidation.validate()) {
                    loginSeller();

                }
            }
        });
    }

    private void loginSeller() {
        final String email=emailInput.getText().toString();
        final String password=passwordInput.getText().toString();

        if(!email.equals("") && !password.equals("")) {
            loadingBar.setTitle("Seller Account Login");
            loadingBar.setMessage("Please Wait, While we are checking the credientials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()){
                         Intent intent=new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(intent);
                         finish();

                     }else{
                         loadingBar.dismiss();
                         Toast.makeText(SellerLoginActivity.this, "Your email or password invalid. Please use another one...", Toast.LENGTH_SHORT).show();
                     }
                }
            });


        }else{
            Toast.makeText(this, "Please complete the Login form", Toast.LENGTH_SHORT).show();
        }

    }
    @Override     //This error will happen if your activity has been destroyed but you dialog is still showing. So you have to add these code in your activity's.
    public void onDestroy() {
        super.onDestroy();
        if (loadingBar != null) {
            loadingBar.dismiss();
            loadingBar = null;
        }
    }
}