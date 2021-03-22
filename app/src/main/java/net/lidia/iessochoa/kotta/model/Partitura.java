package net.lidia.iessochoa.kotta.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = Partitura.TABLE_NAME,
        indices = {@Index(value = {Partitura.FECHA},unique = true)})
public class Partitura implements Parcelable {
    public static final String TABLE_NAME="partitura";
    public static final String ID= BaseColumns._ID;
    public static final String FECHA="fecha";
    public static final String NOMBRE="nombre";
    public static final String INSTRUMENTO="instrumento";
    public static final String AUTOR="autor";
    public static final String CATEGORIA="categoria";
    public static final String PDF="pdf";

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name=ID)
    private int id;
    @NonNull
    @ColumnInfo(name = FECHA)
    private Date fecha;
    @NonNull
    @ColumnInfo(name = NOMBRE)
    private String nombre;
    @NonNull
    @ColumnInfo(name = INSTRUMENTO)
    private String instrumento;
    @NonNull
    @ColumnInfo(name = AUTOR)
    private String autor;
    @NonNull
    @ColumnInfo(name = CATEGORIA)
    private String categoria;
    @NonNull
    @ColumnInfo(name = PDF)
    private String pdf;

    @Ignore
    public Partitura(int id, @NonNull Date fecha, @NonNull String nombre, @NonNull String instrumento,
                     @NonNull String autor, @NonNull String categoria, @NonNull String pdf) {
        this.id = id;
        this.fecha = fecha;
        this.nombre = nombre;
        this.instrumento = instrumento;
        this.autor = autor;
        this.categoria = categoria;
        this.pdf = pdf;
    }

    public Partitura(@NonNull Date fecha, @NonNull String nombre, @NonNull String instrumento,
                     @NonNull String autor, @NonNull String categoria, @NonNull String pdf) {
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

    @NonNull
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(@NonNull Date fecha) {
        this.fecha = fecha;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(@NonNull String instrumento) {
        this.instrumento = instrumento;
    }

    @NonNull
    public String getAutor() {
        return autor;
    }

    public void setAutor(@NonNull String autor) {
        this.autor = autor;
    }

    @NonNull
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(@NonNull String categoria) {
        this.categoria = categoria;
    }

    @NonNull
    public String getPdf() {
        return pdf;
    }

    public void setPdf(@NonNull String pdf) {
        this.pdf = pdf;
    }

    public String getFechaFormatoLocal() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(fecha);
    }

    protected Partitura(Parcel in) {
        id = in.readInt();
        fecha = (Date) in.readSerializable();
        nombre = in.readString();
        instrumento = in.readString();
        autor = in.readString();
        categoria = in.readString();
        pdf = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeSerializable(this.fecha);
        dest.writeString(nombre);
        dest.writeString(instrumento);
        dest.writeString(autor);
        dest.writeString(categoria);
        dest.writeString(pdf);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Partitura> CREATOR = new Creator<Partitura>() {
        @Override
        public Partitura createFromParcel(Parcel in) {
            return new Partitura(in);
        }

        @Override
        public Partitura[] newArray(int size) {
            return new Partitura[size];
        }
    };
}
