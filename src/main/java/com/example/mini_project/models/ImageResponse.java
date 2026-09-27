package com.example.mini_project.models;

public class ImageResponse {

    private Integer id;
    private String name;
    private String type;

    public ImageResponse(
            Integer id,
            String name,
            String type
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}