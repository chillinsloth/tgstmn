package umn.ac.id.ydkw01;

public class MaterialModel {
    private String user_id;
    private String VideoTitle;
    private String MaterialUrl;
//    private String search;

    public MaterialModel(){}

    private MaterialModel(String MaterialUrl, String VideoTitle, String user_id){
        this.user_id = user_id;
        this.VideoTitle = VideoTitle;
        this.MaterialUrl = MaterialUrl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
