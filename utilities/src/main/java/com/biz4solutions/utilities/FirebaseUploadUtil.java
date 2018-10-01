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

    public static void uploadImageToFirebase(String path, Uri uri, final FirebaseUploadInterface firebaseUploadInterface) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference ref = storage.getReference().child("/profileImages/users/" + path + "/profile.jpg");
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
                            firebaseUploadInterface.uploadSuccess(downloadUri.toString());
                        } else {
                            if (task.getException() != null) {
                                firebaseUploadInterface.uploadError(task.getException().getMessage());
                            }
                            // Handle failures
                        }
                    }
                });
    }

    public interface FirebaseUploadInterface {
        void uploadSuccess(String imageUrl);

        void uploadError(String exceptionMsg);
    }
}