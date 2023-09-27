/**
 * The Countries class represents a country in the scheduling system.
 * It contains information such as the country ID and name.
 * @author Tyler Bradshaw
 */
package models;

public class Countries {
    private Integer countryId;
    private String country;

    public Countries(Integer countryId, String country){
        this.countryId = countryId;
        this.country = country;
    }

    /**
     * @return the id
     */
    public Integer getCountryId() {
        return countryId;
    }

    /**
     * @param countryId the id to set
     */
    public void setCountryId(int countryId){
        this.countryId = countryId;
    }
    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }
    /**
     * @param country the id to set
     */
    public void setCountry(String country){
        this.country = country;
    }
}
