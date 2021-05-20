package umn.ac.id.ydkw01;

public class PortfolioModel {
    private String user_id;
    private String PortfolioUrl;
    private String Fullname;

    private PortfolioModel(){}

    private PortfolioModel(String PortfolioUrl, String Fullname, String user_id){
        this.user_id = user_id;
        this.PortfolioUrl = PortfolioUrl;
        this.Fullname = Fullname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPortfolioUrl() {
        return PortfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.PortfolioUrl = portfolioUrl;
    }

    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        this.Fullname = fullname;
    }
}
