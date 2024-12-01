package com.example.vetgan;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MedicionActivity extends AppCompatActivity {

    private Spinner spTipoAnimal;
    private Button button_mostrar;
    private TextView txtInfoAnimal;

    private String animalSeleccionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicion);

        spTipoAnimal = findViewById(R.id.spTipoAnimal);
        button_mostrar = findViewById(R.id.button_Mostrar);
        txtInfoAnimal = findViewById(R.id.txtInfoAnimal);

        //Opciones para el Spinner
        String[] tiposAnimales = {"Selecciona un tipo", "Vaca Seca", "Vaca Produccion", "Porcino Iniciación", "Borrego", "Caprino", "Gallina"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposAnimales);
        spTipoAnimal.setAdapter(adapter);

        button_mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = spTipoAnimal.getSelectedItemPosition();
                if (position == 0){
                    txtInfoAnimal.setText("Por favor, selecciona un Tipo de Animal");
                } else{
                    animalSeleccionado = tiposAnimales[position];
                    mostrarInformacionAnimal(animalSeleccionado);
                }
            }
        });
    }

    private void mostrarInformacionAnimal(String animal){
        String info = "";
        switch(animal){
            case "Vaca Seca":
                info = "90 a 100% de Forraje (0 a 10% de Concentrado)";
                break;
            case "Vaca Produccion":
                info = "40 a 45% de Forraje (55 a 60% de Concentrado)";
                break;
            case "Porcino Iniciación":
                info = "0,8 - 1,2 kg por cerdo";
                break;
            case "Borrego":
                info = "Informacion Borrego";
                break;
            case "Caprino":
                info = "Informacion Caprino";
                break;
            case "Gallina":
                info = "Informacion Gallina";
                break;
        }
        txtInfoAnimal.setText(info);
    }
}