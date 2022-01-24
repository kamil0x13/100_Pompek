package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;

public class Podejscie implements Serializable {
    private LocalDate dataRozpoczecia;
    private LocalDate dataZakonczenia;
    private int numerPodejscia;
    private int wykonanePowtorzenia;
    private int poziom;

    public Podejscie(LocalDate dataRozpoczecia, LocalDate dataZakonczenia, int numerPodejscia, int wykonanePowtorzenia, int poziom) {
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.numerPodejscia = numerPodejscia;
        this.wykonanePowtorzenia = wykonanePowtorzenia;
        this.poziom = poziom;
    }

    public LocalDate getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public LocalDate getDataZakonczenia() {
        return dataZakonczenia;
    }

    public int getNumerPodejscia() {
        return numerPodejscia;
    }

    public int getWykonanePowtorzenia() {
        return wykonanePowtorzenia;
    }

    public int getPoziom() {
        return poziom;
    }

    @Override
    public String toString() {
        return "Podejscie{" +
                "dataRozpoczecia=" + dataRozpoczecia +
                ", dataZakonczenia=" + dataZakonczenia +
                ", numerPodejscia=" + numerPodejscia +
                ", wykonanePowtorzenia=" + wykonanePowtorzenia +
                ", poziom=" + poziom +
                '}';
    }

    public static int readIloscPodejsc(Context ctx){

        FileInputStream fis;
        try {
            fis = ctx.openFileInput("numerPodejscia.dat");

            ObjectInputStream oos = new ObjectInputStream(fis);
            int numer =  oos.readInt();
            oos.close();
            return numer;
        }catch (IOException e){
            e.printStackTrace();
            return 0;
        }
    }

    public static Podejscie[] readAllPodejscie(Context ctx){
        int iloscPodejsc = Podejscie.readIloscPodejsc(ctx);
        Podejscie[] podejscia = new Podejscie[iloscPodejsc];

        FileInputStream fis;
        for (int i=0;i<iloscPodejsc;i++){
            Object o;
            try {
                fis = ctx.openFileInput("podejscie"+(i+1)+".dat");

                ObjectInputStream ois = new ObjectInputStream(fis);

                o = ois.readObject();
                podejscia[i] = (Podejscie) o;
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
                return null;
            }
        }
        return podejscia;
    }
}
