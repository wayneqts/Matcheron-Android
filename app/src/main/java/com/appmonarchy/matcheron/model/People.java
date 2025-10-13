package com.appmonarchy.matcheron.model;

import java.io.Serializable;

public class People implements Serializable {
    String id, fName, lName, gender, seeking, age, height, weight, status, country, state, phone, email, religion, goal, pairing, profession, img1, img2, img3, bio, originCountry, created, pw, phoneStt;

    public People(String id, String fName, String lName, String gender, String seeking, String age, String height, String weight, String status, String country, String state, String phone, String email, String religion,
                  String goal, String pairing, String profession, String img1, String img2, String img3, String bio, String originCountry, String created, String phoneStt) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.gender = gender;
        this.seeking = seeking;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.status = status;
        this.country = country;
        this.state = state;
        this.phone = phone;
        this.email = email;
        this.religion = religion;
        this.goal = goal;
        this.pairing = pairing;
        this.profession = profession;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.bio = bio;
        this.originCountry = originCountry;
        this.created = created;
        this.phoneStt = phoneStt;
    }

    public String getPhoneStt() {
        return phoneStt;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getCreated() {
        return created;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public String getId() {
        return id;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getGender() {
        return gender;
    }

    public String getSeeking() {
        return seeking;
    }

    public String getAge() {
        return age;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getReligion() {
        return religion;
    }

    public String getGoal() {
        return goal;
    }

    public String getPairing() {
        return pairing;
    }

    public String getProfession() {
        return profession;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public String getImg3() {
        return img3;
    }

    public String getBio() {
        return bio;
    }
}
