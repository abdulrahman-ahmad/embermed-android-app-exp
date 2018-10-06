package com.biz4solutions.provider.utilities;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.biz4solutions.provider.R;
import com.biz4solutions.utilities.Constants;

import java.io.File;
import java.io.FileNotFoundException;

public class FileUtils {

    private static String nopath = "";

    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return nopath;
    }

    public static boolean isLargeSize(File file) {
        return Integer.parseInt(String.valueOf(file.length() / 1024)) < 2048;
    }

    public static String getFileExtension(Context context, File file) {
        String ext = null;
        if (file == null) {
            Toast.makeText(context, "Unknown file selected,Please try again.", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (file.getAbsolutePath().contains(".")) {
            ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            //  ext = ext.replace(".", "");
        }
        return ext;
    }

    public static boolean isValidFile(Context context, String ext) {
        return ext.equalsIgnoreCase(".pdf") || ext.equalsIgnoreCase(".jpeg") || ext.equalsIgnoreCase(".jpg") || ext.equalsIgnoreCase(".png");
    }

    public static String fileType(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        try {
            final String column = "_data";
            final String[] projection = {column};
            try (Cursor cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_invalid_selected_file, Toast.LENGTH_SHORT).show();
        }
        return nopath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static long downloadFile(Context context, String url, boolean isCprFile, String ext) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Ember_Docs");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        long downloadId = 0;
        if (URLUtil.isValidUrl(url) && (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url))) {
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);//(Uri.parse("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfytEjvVR-J7vZFAc5cA0qUsKuc_RcyKuLBLS8fRicDTHHY8jbqw"));
//            request.setDescription("Some description");
            if (isCprFile) {
                request.setTitle(Constants.CPR_FILE_NAME);
            } else {
                request.setTitle(Constants.MEDICAL_FILE_NAME);
            }
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (isCprFile)
                request.setDestinationInExternalPublicDir("/Ember_Docs", Constants.CPR_FILE_NAME + "." + ext);
            else {
                request.setDestinationInExternalPublicDir("/Ember_Docs", Constants.MEDICAL_FILE_NAME + "." + ext);
            }
            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadId = manager.enqueue(request);
            manager.getUriForDownloadedFile(downloadId);
            try {
                manager.openDownloadedFile(downloadId);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, R.string.error_invalid_file_link, Toast.LENGTH_SHORT).show();
        }
        return downloadId;
    }

    public static void deleteStoredFiles() {
//        File file = new File(Environment.getExternalStorageDirectory()
//                + "/Ember_Docs/" + Constants.CPR_FILE_NAME + FileTypes.pdf);
//        File file2 = new File(Environment.getExternalStorageDirectory()
//                + "/Ember_Docs/" + Constants.CPR_FILE_NAME + FileTypes.jpg);
//
//        File file3 = new File(Environment.getExternalStorageDirectory()
//                + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + FileTypes.pdf);
//        File file4 = new File(Environment.getExternalStorageDirectory()
//                + "/Ember_Docs/" + Constants.MEDICAL_FILE_NAME + FileTypes.jpg);
//        if (file.exists()) {
//            file.delete();
//        } else if (file2.exists()) {
//            file2.delete();
//        } else if (file3.exists()) {
//            file3.delete();
//        } else if (file4.exists()) {
//            file4.delete();
//        }

        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Ember_Docs");

        if (!direct.exists()) {
            direct.delete();
        }

    }
}