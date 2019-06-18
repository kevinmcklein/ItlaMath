package com.eurekatech.itlamath;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
   // private static Context appContext;

    TextView exp,res;
    String expresion="",expresion2="";
    ListView lvHist;
    Boolean FuncionSegundaActivada=true;
    private ArrayList<String> operaciones= new ArrayList<>();
    private ArrayAdapter<String> adaptador1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       exp=findViewById(R.id.txtoperacion);
        res=findViewById(R.id.txtResultado);
        lvHist=findViewById(R.id.lvHistorial);



    }

    public  void calcular(View v){
        expresion2=expresion;
        String ExpresionAcalcular;
        ExpresionAcalcular=exp.getText().toString();



        Parser expres=new Parser(ExpresionAcalcular,this);

        double df=   expres.getValue(5,5,5);
        res.setText(""+df);

        expresion=""+df;
        expresion2+="="+df;
        AgregarLista();

    }

public  void EntradaExpresion(View v){
        int id=v.getId();
    Button boton=findViewById(id);

    switch (boton.getText().toString().toLowerCase()){
        case "x": expresion+="*";
        break;
        case "sen":expresion+="sin ";
        break;
        case "cos":expresion+="cos ";
        break;
        case "tan":expresion+="tan ";
        break;
        case "log":expresion+="log ";
        break;
        case "ln": expresion+="ln ";
        break;
        case "cot": expresion+="cot ";
        break;
        case "sec": expresion+="sec ";
        break;
        case "csc": expresion+="csc ";
        break;

        default: expresion +=boton.getText().toString().toLowerCase();

    }

          exp.setText(expresion);
}

public void borrarunCaracter(View v){
        if (!expresion.isEmpty()){
            expresion=expresion.substring(0,expresion.length()-1);
            exp.setText(expresion);
        }

}

public void borraTodo(View v){
        exp.setText("");
        expresion="";
}

private  void AgregarLista(){


    operaciones.add(expresion2);
    Collections.reverse(operaciones);
    adaptador1= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operaciones);
    lvHist.setAdapter(adaptador1);
    expresion2="";
}

public void  LimpiarLista(View v){
    operaciones.clear();
    adaptador1= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operaciones);
    lvHist.setAdapter(adaptador1);
}

public void hacerListaVisible(View v){

    lvHist.setVisibility(View.VISIBLE);
}

    public void hacerListaInVisible(View v){

        lvHist.setVisibility(View.GONE);
    }



    public  void activarDescativarSegundaFuncion(View v){

    if (FuncionSegundaActivada){
        Button boton;
        boton=findViewById(R.id.bsen);
        boton.setText(getText( R.string.csc));
        boton=findViewById(R.id.bcos);
        boton.setText(getText( R.string.sec));
        boton=findViewById(R.id.btan);
        boton.setText(getText(R.string.cot));
        boton=findViewById(R.id.bln);
        boton.setText(getText( R.string.asen));
        boton=findViewById(R.id.blog);
        boton.setText(getText( R.string.acos));
        boton=findViewById(R.id.bfact);
        boton.setText(getText( R.string.atan));
        FuncionSegundaActivada=false;
    }else{
        Button boton;
        boton=findViewById(R.id.bsen);
        boton.setText(getText( R.string.sen));
        boton=findViewById(R.id.bcos);
        boton.setText(getText( R.string.cos));
        boton=findViewById(R.id.btan);
        boton.setText(getText( R.string.tan));
        boton=findViewById(R.id.bln);
        boton.setText(getText( R.string.ln));
        boton=findViewById(R.id.blog);
        boton.setText(getText( R.string.log));
        boton=findViewById(R.id.bfact);
        boton.setText(getText( R.string.fact));
        FuncionSegundaActivada=true ;

    }



    }
    /*public  void pruebac(View v){

TextView pru=findViewById(R.id.txtprueba);
        Parser expres=new Parser(pru.getText().toString(),this);

        double df=   expres.getValue(2);
        pru.setText(""+df);

    }*/
}
