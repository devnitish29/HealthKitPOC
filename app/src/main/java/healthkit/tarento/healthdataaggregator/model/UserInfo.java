package healthkit.tarento.healthdataaggregator.model;

import java.io.Serializable;

public class UserInfo implements Serializable {

    String name;
    String gender;
    String weight;
    String height;
    String dob;
    String phKin1;
    String phKin2;
    String phDoc;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhKin1() {
        return phKin1;
    }

    public void setPhKin1(String phKin1) {
        this.phKin1 = phKin1;
    }

    public String getPhKin2() {
        return phKin2;
    }

    public void setPhKin2(String phKin2) {
        this.phKin2 = phKin2;
    }

    public String getPhDoc() {
        return phDoc;
    }

    public void setPhDoc(String phDoc) {
        this.phDoc = phDoc;
    }
}
