package com.biz4solutions.utilities;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * this class is used for firebase authentication util. sign up and sign in users to firebase auth.
 * Created by ketan on 27-12-2017.
 */

public class FirebaseAuthUtil {
    private static FirebaseAuthUtil firebaseAuthUtil;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    /**
     * this method gives the singleton instance of this class
     *
     * @return singleton instance of this class
     */
    public static FirebaseAuthUtil getInstance() {
        if (firebaseAuthUtil == null) {
            firebaseAuthUtil = new FirebaseAuthUtil();
        }
        return firebaseAuthUtil;
    }

    private FirebaseAuthUtil() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * this method is used to sign in old user with firebase authentication using email and password
     *
     * @param email    email address of the user
     * @param password password of the user
     */
    public void signInUser(@NonNull final String email, @NonNull final String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return;
        }
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //currentUser = mFirebaseAuth.getCurrentUser();
                            initDB();
                            //System.out.println("aa ------- currentUser=" + mFirebaseAuth.getCurrentUser());
                        } else {
                            // If sign in fails, display a message to the user.
                            //System.out.println("aa ------ signInWithEmailAndPassword error");
                            createUser(email, password);
                        }
                    }
                });
    }

    public boolean isFirebaseAuthValid() {
        return mFirebaseAuth != null && mFirebaseAuth.getCurrentUser() != null;
    }

    private void createUser(@NonNull final String email, @NonNull final String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return;
        }
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //currentUser = mFirebaseAuth.getCurrentUser();
                    initDB();
                    //System.out.println("aa ------- createUser currentUser=" + mFirebaseAuth.getCurrentUser());
                } /*else {
                    // If sign in fails, display a message to the user.
                    //System.out.println("aa ------ signInWithEmailAndPassword createUser error");
                }*/
            }
        });
    }

    public void initDB() {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeData(@NonNull final String firebaseDBRef, @NonNull final String firebaseDBKey, Map<String, Object> mapParams) {
        try {
            if (mDatabase != null) {
                mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).child(firebaseDBRef).child(firebaseDBKey).setValue(mapParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildEventListener(@NonNull final String firebaseDBRef, @NonNull final String firebaseDBKey, @NonNull ChildEventListener childEventListener) {
        if (mDatabase != null) {
            mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).child(firebaseDBRef).child(firebaseDBKey).addChildEventListener(childEventListener);
        }
    }

    public void removeEventListener(@NonNull ChildEventListener childEventListener) {
        if (mDatabase != null) {
            mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).removeEventListener(childEventListener);
        }
    }

    public void addValueEventListener(@NonNull final String firebaseDBRef, @NonNull final String firebaseDBKey, @NonNull ValueEventListener valueEventListener) {
        if (mDatabase != null) {
            mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).child(firebaseDBRef).child(firebaseDBKey).addValueEventListener(valueEventListener);
        }
    }

    public void addListenerForSingleValueEvent(@NonNull final String firebaseDBRef, @NonNull final String firebaseDBKey, @NonNull ValueEventListener valueEventListener) {
        if (mDatabase != null) {
            mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).child(firebaseDBRef).child(firebaseDBKey).addListenerForSingleValueEvent(valueEventListener);
        }
    }

    public void removeEventListener(@NonNull ValueEventListener valueEventListener) {
        if (mDatabase != null) {
            mDatabase.child(BuildConfig.FIREBACE_DB_ENVIROMENT).removeEventListener(valueEventListener);
        }
    }

    public void signOut() {
        try {
            mFirebaseAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}