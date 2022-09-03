package com.example.netflex_movie_api;

public class ProducerGetAll {
    private int id;
    private String name;
    private  String email;
    private String website;


    public ProducerGetAll(int id, String name, String email, String website) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.website = website;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }
}
