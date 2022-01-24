package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;

public class PlanTreningowy implements Serializable {

    //TODO obsługa wyjątków związanych z pamięcią

    // --------------------ZMIENNE------------------

    //
    private int wykonanePowtorzenia; //suma wykonanych pompek
    private int poziom; //na podstwanie poziomu okreslana jest ilosc powturzen w serji
    private int progres; //od tego zalezy zmiana poziomu trudnosci
    private boolean aktualny; //przyjmuje wartosc false gdy nalezy usunac obiekt

    //
    private LocalDate dataStanu; //!! sprawdzic po zminimalizowanu apki
    private LocalDate dataRozpoczecia;
    private LocalDate dataZakonczenia;
    private int numerPodejscia;

    //
    private boolean odpoczynek;
    private boolean zaczeto;
    private int seria;
    private int[] ilePowtorzenArr;
    private boolean nieZaliczono;
    private boolean wykonano;

    // ----------- KONSTRUKTOR ----------------------
    public PlanTreningowy(int powtorzenia, Context ctx) {
        this.dataStanu = LocalDate.now();
        this.dataRozpoczecia = LocalDate.now();
        this.aktualny = true;

        //dane konfiguracyjne
        this.poziom = powtorzenia;
        this.progres = 0;

        //dane podejscia
        this.wykonanePowtorzenia = 0;
        this.odpoczynek = false;
        this.zaczeto = false;
        this.wykonano = false;
        this.nieZaliczono = false;
        this.seria = 0;

        this.ilePowtorzenArr = this.getIlePowtorzenArr();

        //NUMER PODEJSCIE ----- ODCZYTAJ Z PLIKU
        this.numerPodejscia = this.readNumerPodejscia(ctx);

        //Zapis do bazy danych
        this.saveMe(ctx);
    }

    //---------------- FUNKCJE PUBLICZNE ---------------

    public void rozpocznijTrening(Context ctx){
        this.zaczeto = true;
        this.saveMe(ctx);
    }

    public void nastepnaSeria(boolean zaliczono, Context ctx){
        if(!this.nieZaliczono && !zaliczono){
            this.nieZaliczono = true;
        }
        if(zaliczono){
            this.wykonanePowtorzenia += this.ilePowtorzenArr[this.seria];
        }
        if(this.seria >= 4){
            this.wykonano = true;
            this.zaczeto = false;
            this.saveMe(ctx);
        }else {
            this.seria += 1;
            this.saveMe(ctx);
        }
    }

    // zwraca prawde jesli zmienił się dzień
    public boolean sprawdzDate(Context ctx){
        if(!this.dataStanu.equals(LocalDate.now())){
            this.kolejnyDzien(ctx);
            return true;
        }else {
            return false;
        }
    }

    //GET-------------------------------------

    public int getWykonanePowtorzenia() { return wykonanePowtorzenia; }

    public int ilePowtorzen(){
        return this.ilePowtorzenArr[this.seria];
    }

    public boolean isOdpoczynek() { return odpoczynek; }

    public boolean isWykonano() { return wykonano; }

    public int getSeria() { return seria; }

    public boolean isZaczeto() { return zaczeto; }

    public boolean isAktualny(){return this.aktualny;}

    //------------- FUNKCJE PRYWATNE ------------------------------
//SPRAWD DATE NA REMUSE __________
    private void kolejnyDzien(Context ctx){
        if((this.dataStanu.getMonthValue() == LocalDate.now().getMonthValue())){
            if(this.dataStanu.plusDays(1).equals(LocalDate.now())){
                //Zmiana o 1 dzien
                if(this.odpoczynek || this.wykonano){
                    if(!this.odpoczynek){
                        this.aktualizujProgres();
                    }else {
                        this.ilePowtorzenArr = this.getIlePowtorzenArr();
                    }
                    this.odpoczynek = !this.odpoczynek;
                    this.zaczeto = false;
                    this.wykonano = false;
                    this.nieZaliczono = false;
                    this.seria = 0;
                    this.dataStanu = LocalDate.now();
                    this.saveMe(ctx);
                }else {
                    this.savePodejscie(ctx);
                }
            } else if(this.dataStanu.plusDays(2).equals(LocalDate.now())){
                //Zmiana o 2 dni
                    if(this.wykonano){
                        this.zaczeto = false;
                        this.wykonano = false;
                        this.nieZaliczono = false;
                        this.seria = 0;
                        this.ilePowtorzenArr = this.getIlePowtorzenArr();
                        this.aktualizujProgres();
                        this.dataStanu = LocalDate.now();
                        this.saveMe(ctx);
                    }else {
                        this.savePodejscie(ctx);
                    }
            }else {
                this.savePodejscie(ctx);
            }
        }else {
            this.savePodejscie(ctx);
        }
    }

    private void aktualizujProgres(){
        if(!this.nieZaliczono){
            this.progres = this.progres < 3 ? this.progres++ : 3;
        }else {
            if(this.progres > 0){
                this.progres = 0;
            }else {
                if(this.progres == -2){
                    //TODO Propozycja zmiany poziomu
                }else {
                    this.progres--;
                }
            }
        }
        this.poziom += this.progres;
    }

    private int[] getIlePowtorzenArr(){
        int[] arr = {(int) (0.3*this.poziom),(int) (0.5*this.poziom), (int) (0.5*this.poziom), (int) (0.4*this.poziom), (int) (0.7*this.poziom) };
        return arr;
    }

    //------------------- SYSTEM DANYCH -------------------------------

    public boolean saveMe(Context ctx){
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("planTreningowy.dat", Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return true;
        }
    }

    public boolean setMeNull(Context ctx){
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("planTreningowy.dat", Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(null);
            oos.close();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return true;
        }
    }

    public static PlanTreningowy readMe(Context ctx){
        PlanTreningowy planTreningowy = null;

        FileInputStream fis;

        try {
            fis = ctx.openFileInput("planTreningowy.dat");

            ObjectInputStream ois = new ObjectInputStream(fis);

            Object o;
            if((o = ois.readObject()) == null){
                return null;
            }
            planTreningowy = (PlanTreningowy) o;
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
        return planTreningowy;
    }

    private int readNumerPodejscia(Context ctx){
        FileInputStream fis;
        try {
                fis = ctx.openFileInput("numerPodejscia.dat");

                ObjectInputStream oos = new ObjectInputStream(fis);
                int numer =  oos.readInt();
                numer++;
                oos.close();
                return numer;
        }catch (IOException e){
            e.printStackTrace();
            return 1;
        }
    }

    private boolean saveNumerPodejscia(Context ctx){
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("numerPodejscia.dat", Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeInt(this.numerPodejscia);
            oos.close();
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return true;
        }
    }

    private boolean savePodejscie(Context ctx){
        Podejscie podejscie = new Podejscie(this.dataRozpoczecia,LocalDate.now(),this.numerPodejscia,this.wykonanePowtorzenia,this.poziom);
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput("podejscie"+this.numerPodejscia+".dat", Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(podejscie);
            oos.close();
            this.aktualny = false;
            this.saveNumerPodejscia(ctx);
            this.setMeNull(ctx);
            return false;
        }catch (IOException e){
            e.printStackTrace();
            return true;
        }
    }
}
