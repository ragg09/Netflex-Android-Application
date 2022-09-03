package com.example.netflex_movie_api;

public class ActorGet_withMovie {
    private int movie_id;
    private String title;
    private String image;
    private String producer_name;
    private String producer_website;

    public ActorGet_withMovie(int movie_id, String title, String image, String producer_name, String producer_website) {
        this.movie_id = movie_id;
        this.title = title;
        this.image = image;
        this.producer_name = producer_name;
        this.producer_website = producer_website;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getTitle(){
        return title;
    }

    public String getImage(){
        return image;
    }

    public String getProducer_name(){
        return producer_name;
    }

    public String getProducer_website(){
        return producer_website;
    }


}
