package umn.ac.id.ydkw01;

public class MaterialModel {
    private String Fullname;
    private String VideoTitle;
    private String MaterialUrl;
//    private String search;

    public MaterialModel(){}

    private MaterialModel(String MaterialUrl, String VideoTitle, String Fullname){
        this.Fullname = Fullname;
        this.VideoTitle = VideoTitle;
        this.MaterialUrl = MaterialUrl;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        this.Fullname = fullname;
    }

    public String getVideoTitle() {
        return VideoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.VideoTitle = videoTitle;
    }

    public String getMaterialUrl() {
        return MaterialUrl;
    }

    public void setMaterialUrl(String materialUrl) {
        this.MaterialUrl = materialUrl;
    }

//    public String getSearch() {
//        return search;
//    }
//
//    public void setSearch(String search) {
//        this.search = search;
//    }
}
