package capitals;


public class Capital {

    private String name;
    private String country;
    private String currency;

    public Capital(String name, String country, String currency) {
        this.name = name;
        this.country = country;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
