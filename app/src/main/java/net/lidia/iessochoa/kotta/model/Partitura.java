package net.lidia.iessochoa.kotta.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Partitura {
    private String user;
    @ServerTimestamp
    private Date date;
    private String name;
    private String instrument;
    private String author;
    private String category;
    private String pdf;

    public Partitura() {
    }

    public Partitura(String user, String name, String instrument, String author, String category, String pdf) {
        this.user = user;
        this.name = name;
        this.instrument = instrument;
        this.author = author;
        this.category = category;
        this.pdf = pdf;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
