package umn.ac.id.ydkw01;

public class PortfolioModel {
    private String PortfolioUrl;
    private String Fullname;

    private PortfolioModel(){}

    private PortfolioModel(String PortfolioUrl, String Fullname){
        this.PortfolioUrl = PortfolioUrl;
        this.Fullname = Fullname;
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
