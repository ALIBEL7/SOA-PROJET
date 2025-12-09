package com.hotel.room; // adapte au besoin

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;


@XmlRootElement(name = "Chambre")
@XmlAccessorType(XmlAccessType.FIELD)
public class Chambre {

    private int num;        // PK
    private String type;    // A1, A2, Suite...
    private double prix;
    private boolean etat;   // true = dispo

    public Chambre() {
    }

    public Chambre(int num, String type, double prix, boolean etat) {
        this.num = num;
        this.type = type;
        this.prix = prix;
        this.etat = etat;
    }

    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isEtat() {
        return etat;
    }
    public void setEtat(boolean etat) {
        this.etat = etat;
    }
}
