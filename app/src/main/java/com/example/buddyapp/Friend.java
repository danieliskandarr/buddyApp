package com.example.buddyapp;

public class Friend {
    private int id;
    private String name;
    private String gender;
    private String hpNo;
    private String email;
    // Address broken down as required
    private String address1; // house/lot/floor/building
    private String address2; // number/street/district
    private String address3; // postcode/locality
    private String address4; // state

    public Friend(int id, String name, String gender, String hpNo, String email,
                  String address1, String address2, String address3, String address4) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.hpNo = hpNo;
        this.email = email;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getHpNo() { return hpNo; }
    public String getEmail() { return email; }
    public String getAddress1() { return address1; }
    public String getAddress2() { return address2; }
    public String getAddress3() { return address3; }
    public String getAddress4() { return address4; }

    // Helper for displaying full address in a list if needed
    public String getFullAddress() {
        return address1 + ", " + address2 + ", " + address3 + ", " + address4;
    }
}