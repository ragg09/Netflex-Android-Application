package com.example.netflex_movie_api;

public class FilmGet_withActor {

    private int id;
    private String name;
    private  String note;
    private String image;


    public FilmGet_withActor(int id, String name, String note, String image) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getImage() {
        return image;
    }
}
