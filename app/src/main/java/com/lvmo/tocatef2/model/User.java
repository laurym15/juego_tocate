package com.lvmo.tocatef2.model;

public class User{
    private String name;
    private String foto;
    private int gameDone;
    private int points;
    private String Online;

    public User() {
    }

    public User(String name, int points, int gameDone, String foto) {
        this.name = name;
        this.foto = foto;
        this.gameDone = gameDone;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getGameDone() {
        return gameDone;
    }

    public void setGameDone(int gameDone) {
        this.gameDone = gameDone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getOnline() {return Online;    }

    public void setOnline(String online) { Online = online;  }
}