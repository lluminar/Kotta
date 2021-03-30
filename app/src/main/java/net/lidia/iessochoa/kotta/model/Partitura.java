package net.lidia.iessochoa.kotta.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Partitura {
    private int id;
    private Date fecha;
    private String nombre;
    private String instrumento;
    private String autor;
    private String categoria;
    private String pdf;

    public Partitura(int id, Date fecha, String nombre, String instrumento, String autor,
                     String categoria, String pdf) {
        this.id = id;
        this.fecha = fecha;
        this.nombre = nombre;
        this.instrumento = instrumento;
        this.autor = autor;
        this.categoria = categoria;
        this.pdf = pdf;
    }

    public Partitura(Date fecha, String nombre, String instrumento, String autor, String categoria, String pdf) {
        this.fecha = fecha;
        this.nombre = nombre;
        this.instrumento = instrumento;
        this.autor = autor;
        this.categoria = categoria;
        this.pdf = pdf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getFechaFormatoLocal() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(fecha);
    }
}
