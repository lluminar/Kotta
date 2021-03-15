package net.lidia.iessochoa.kotta.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;


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
    private Date fecha;
    @NonNull
    private String nombre;
    @NonNull
    private String instrumento;
    @NonNull
    private String autor;
    @NonNull
    private String Categoria;
    @NonNull
    private String pdf;

    protected Partitura(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        instrumento = in.readString();
        autor = in.readString();
        Categoria = in.readString();
        pdf = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
        dest.writeString(instrumento);
        dest.writeString(autor);
        dest.writeString(Categoria);
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
