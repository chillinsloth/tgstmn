package umn.ac.id.ydkw01;

import android.net.Uri;

public class CustomModel {
    String portoName;
    Uri portoURI;

    public CustomModel() {
    }

    public CustomModel(String portoName, Uri portoURI) {
        this.portoName = portoName;
        this.portoURI = portoURI;
    }

    public String getPortoName() {
        return portoName;
    }

    public Uri getPortoURI() {
        return portoURI;
    }
}
