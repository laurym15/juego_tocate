package com.lvmo.tocatef2.model;

import java.util.Date;

public class Jugada {
    private String jugadorUnoID;
    private String jugadorDosID;
    private String  ganadorID;
    private String  abandonoID;
    private String invitacionID;
    private int tocate;
    private int  puntosjugadorUnoID;
    private int  puntosjugadorDosID;
    private Date create;

    public Jugada() {
    }

    public Jugada(String jugadorUnoID,String invitacionID) {
        this.jugadorUnoID = jugadorUnoID;
        this.invitacionID = invitacionID;
        this.jugadorDosID ="";
        this.create = new Date();
        this.ganadorID="";
        this.abandonoID="";
        this.tocate=0;
        this.puntosjugadorDosID=0;
        this.puntosjugadorUnoID=0;
    }

    public String getJugadorUnoID() {
        return jugadorUnoID;
    }

    public void setJugadorUnoID(String jugadorUnoID) {
        this.jugadorUnoID = jugadorUnoID;
    }

    public String getJugadorDosID() {
        return jugadorDosID;
    }

    public void setJugadorDosID(String jugadorDosID) {
        this.jugadorDosID = jugadorDosID;
    }

    public String getInvitacionID() { return invitacionID;  }

    public void setInvitacionID(String invitacionID) { this.invitacionID = invitacionID; }

    public int getPuntosjugadorUnoID() {
        return puntosjugadorUnoID;
    }

    public void setPuntosjugadorUnoID(int puntosjugadorUnoID) {
        this.puntosjugadorUnoID = puntosjugadorUnoID;
    }

    public int getPuntosjugadorDosID() {
        return puntosjugadorDosID;
    }

    public void setPuntosjugadorDosID(int puntosjugadorDosID) {
        this.puntosjugadorDosID = puntosjugadorDosID;
    }

    public String getGanadorID() {
        return ganadorID;
    }

    public void setGanadorID(String ganadorID) {
        this.ganadorID = ganadorID;
    }

    public String getAbandonoID() {
        return abandonoID;
    }

    public void setAbandonoID(String abandonoID) {
        this.abandonoID = abandonoID;
    }


    public int getTocate() {
        return tocate;
    }

    public void setTocate(int tocate) {
        this.tocate = tocate;
    }

    public Date getCreate() {
        return create;
    }

    public void setCreate(Date create) {
        this.create = create;
    }
}
