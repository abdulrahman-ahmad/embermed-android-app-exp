package com.biz4solutions.utilities;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class FirebaseUploadUtil {

    public static void uploadImageToFirebase(String userId, Uri uri, final FirebaseUploadInterface firebaseUploadInterface) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference ref = storage.getReference()
                .child(BuildConfig.FIREBACE_DB_ENVIROMENT)
                .child("users")
                .child(userId)
                .child("profileImages")
                .child("profile.jpg");
        ref.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            firebaseUploadInterface.uploadSuccess(downloadUri.toString(), 101);
                        } else {
                            if (task.getException() != null) {
                                firebaseUploadInterface.uploadError(task.getException().getMessage(), 101);
                            }
                            // Handle failures
                        }
                    }
                });
    }

    public static void uploadMultipleFileToFirebase(String uid, String fileName, Uri uri, final int fileCode, final FirebaseUploadInterface firebaseUploadInterface) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference ref = storage.getReference()
                .child(BuildConfig.FIREBACE_DB_ENVIROMENT)
                .child("users")
                .child(uid)
                .child("documents")
                .child(fileName);
        ref.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            firebaseUploadInterface.uploadSuccess(downloadUri.toString(), fileCode);
                        } else {
                            if (task.getException() != null) {
                                firebaseUploadInterface.uploadError(task.getException().getMessage(), fileCode);
                            }
                            // Handle failures
                        }
                    }
                });
    }

    public interface FirebaseUploadInterface {
        void uploadSuccess(String imageUrl, int fileCode);

        void uploadError(String exceptionMsg, int fileCode);
    }
}