package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TreningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TreningFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //------------------------ZMIENNE---------------
    PlanTreningowy planTreningowy;

    public TreningFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TreningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TreningFragment newInstance(String param1, String param2) {
        TreningFragment fragment = new TreningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Wczytywanie z bazy danych
        planTreningowy = PlanTreningowy.readMe(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(planTreningowy == null){
            return inflater.inflate(R.layout.fragment_max_powtorzen, container, false);
        }

        if(planTreningowy.sprawdzDate(getContext())){
            if(!planTreningowy.isAktualny()){
                return inflater.inflate(R.layout.fragment_max_powtorzen, container, false);
            }
        }

        if(planTreningowy.isZaczeto()){
            return inflater.inflate(R.layout.fragment_wykonywanie_treningu, container, false);
        }
        return inflater.inflate(R.layout.fragment_trening, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (planTreningowy == null || !planTreningowy.isAktualny()) {

            MaterialButton maxPowtorzenButton = getView().findViewById(R.id.maxPowtorzenButton);

            maxPowtorzenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextInputEditText liczbaPowtorzenEditText = getView().findViewById(R.id.maxPowtorzenEditText);

                    if((liczbaPowtorzenEditText.getText().toString().equals("")) || (Integer.parseInt(liczbaPowtorzenEditText.getText().toString()) < 3) || (Integer.parseInt(liczbaPowtorzenEditText.getText().toString()) > 100)){
                        Toast.makeText(getContext(), "Podaj liczbe z zakresu 3-100", Toast.LENGTH_SHORT).show();
                    } else {
                        planTreningowy = new PlanTreningowy(Integer.parseInt(liczbaPowtorzenEditText.getText().toString()), getContext());
                        getFragmentManager().beginTransaction().replace(R.id.container, new TreningFragment()).commit();
                    }
                }
            });
        }else if (planTreningowy.isZaczeto()){

            TextView textView = getView().findViewById(R.id.titleTextView);
            textView.setText("Seria " + (planTreningowy.getSeria()+1));

            textView = getView().findViewById(R.id.ilePowtorzenTextView);
            textView.setText(""+planTreningowy.ilePowtorzen());

            MaterialButton materialButton = getView().findViewById(R.id.wykonalemButton);
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planTreningowy.nastepnaSeria(true, getContext());
                    getFragmentManager().beginTransaction().replace(R.id.container, new PrzerwaFragment()).commit();
                }
            });

            materialButton = getView().findViewById(R.id.nieDalemRadyButton);
            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planTreningowy.nastepnaSeria(false, getContext());
                    getFragmentManager().beginTransaction().replace(R.id.container, new PrzerwaFragment()).commit();
                }
            });

        } else {
            //Ustawianie widoku


            TextView textView = getView().findViewById(R.id.dataTextView);

            LocalDate data = LocalDate.now();
            textView.setText((data.getDayOfMonth() <10 ? "0" + data.getDayOfMonth() : data.getDayOfMonth()) + "." + (data.getMonthValue() < 9 ? "0" + data.getMonthValue() : data.getMonthValue() ) + "." + data.getYear());

            textView = getView().findViewById(R.id.cytatTextView);
            textView.setText("Jeśli potrafisz o czymś marzyć, to potrafisz także tego dokonać.");

            textView = getView().findViewById(R.id.wykonaneTextView);
            textView.setText("Wykonałeś już " + planTreningowy.getWykonanePowtorzenia() + " pompek");

            textView = getView().findViewById(R.id.dzisiajTextView);
            if(planTreningowy.isWykonano()){
                textView.setText("Dzisiaj wykonałeś już terning, oby tak dalej.");
            }else if (planTreningowy.isOdpoczynek()){
                textView.setText("Dzisaiaj odpoczynek, wróć jutro:)");
            } else{
                textView.setText("Masz trening do wykonania, nie ma na co czekać.");
                MaterialButton materialButton = getView().findViewById(R.id.zaczynamyButton);
                materialButton.setVisibility(View.VISIBLE);
                materialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        planTreningowy.rozpocznijTrening(getContext());
                        getFragmentManager().beginTransaction().replace(R.id.container, new TreningFragment()).commit();
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(planTreningowy != null && planTreningowy.sprawdzDate(getContext())){
          getFragmentManager().beginTransaction().replace(R.id.container, new TreningFragment()).commit();
        }
    }


}