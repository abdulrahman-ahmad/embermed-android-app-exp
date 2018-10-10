package com.biz4solutions.data;


import android.support.annotation.StringDef;

@StringDef({FileTypes.pdf, FileTypes.jpeg, FileTypes.jpg, FileTypes.png,FileTypes.mimeImage,FileTypes.mimePdf})
public @interface FileTypes {
    String pdf = ".pdf";
    String jpeg = ".jpeg";
    String jpg = ".jpg";
    String png = ".png";
    String mimePdf = "application/pdf";
    String mimeImage = "image/*";
}
