package com.eurekatech.itlamath;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {
   // private static Context appContext;
   Parser2.Angulos tipoAngulo;

    TextView exp,res;
    String expresion="",expresion2="";
    ListView lvHist;
    Boolean FuncionSegundaActivada=true;
    private ArrayList<String> operaciones= new ArrayList<>();
    private ArrayAdapter<String> adaptador1;
    private int cambiodeAngulo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       exp=findViewById(R.id.txtoperacion);
        res=findViewById(R.id.txtResultado);
        lvHist=findViewById(R.id.lvHistorial);
        tipoAngulo = Parser2.Angulos.radian;



    }

    public  void calcular(View v){

        if (!exp.getText().toString().isEmpty()) {
            expresion2=expresion;
            String ExpresionAcalcular;
            ExpresionAcalcular=exp.getText().toString();

            if (ExpresionAcalcular.contains("!"))
                ExpresionAcalcular = factorizacion(ExpresionAcalcular);


            Parser2 expres = new Parser2(ExpresionAcalcular, this, tipoAngulo);

            BigDecimal df = expres.getValue(new BigDecimal("0"));
            res.setText(""+df);

            expresion=""+df;
            expresion2+="="+df;
            AgregarLista();

        }
    }

    public void borrarunCaracter(View v) {
        if (!expresion.isEmpty()) {
            expresion = expresion.substring(0, expresion.length() - 1);
            exp.setText(expresion);
        }

    }

    public void borraTodo(View v) {
        exp.setText("");
        expresion = "";
    }

    private void AgregarLista() {


        operaciones.add(expresion2);
        Collections.reverse(operaciones);
        adaptador1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operaciones);
        lvHist.setAdapter(adaptador1);
        expresion2 = "";
    }

    public void LimpiarLista(View v) {
        operaciones.clear();
        adaptador1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operaciones);
        lvHist.setAdapter(adaptador1);
    }

    public void hacerListaVisible(View v) {

        lvHist.setVisibility(View.VISIBLE);
    }

    public void hacerListaInVisible(View v) {

        lvHist.setVisibility(View.GONE);
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
        case "sec":
            expresion += "sct ";
        break;
        case "csc": expresion+="csc ";
        break;
        case "acos":
            expresion += "acos ";
            break;
        case "asen":
            expresion += "asin ";
            break;
        case "atan":
            expresion += "atan ";
            break;
        default: expresion +=boton.getText().toString().toLowerCase();

    }

          exp.setText(expresion);
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

    public void CambiarAngulo(View v) {
        TextView boton = findViewById(R.id.txtTipoAngulo);
        cambiodeAngulo++;
        if (cambiodeAngulo > 3) cambiodeAngulo = 1;
        switch (cambiodeAngulo) {
            case 1:
                tipoAngulo = Parser2.Angulos.radian;
                boton.setText("R");
                break;

            case 2:
                tipoAngulo = Parser2.Angulos.Degree;
                boton.setText("D");
                break;
            case 3:
                tipoAngulo = Parser2.Angulos.gradia;
                boton.setText("G");

        }


    }
    /*public  void pruebac(View v){

TextView pru=findViewById(R.id.txtprueba);
        Parser expres=new Parser(pru.getText().toString(),this);

        double df=   expres.getValue(2);
        pru.setText(""+df);

    }*/

    public void onSaveInstanceState(Bundle estado) {
        estado.putString("resultado", expresion);
        estado.putString("Operacion", exp.getText().toString());
        estado.putStringArrayList("listaOperaciones", operaciones);
        estado.putInt("angulo", cambiodeAngulo);
        super.onSaveInstanceState(estado);


    }

    public void onRestoreInstanceState(Bundle estado) {
        super.onRestoreInstanceState(estado);
        expresion = estado.getString("resultado");
        exp.setText(estado.getString("Operacion"));
        res.setText(expresion);
        cambiodeAngulo = estado.getInt("angulo") - 1;
        CambiarAngulo(null);

        operaciones = estado.getStringArrayList("listaOperaciones");

        adaptador1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, operaciones);
        lvHist.setAdapter(adaptador1);
    }

    private String factorizacion(String s) {
        boolean HaySimbolos = HaySimbolos(s);
        if (HaySimbolos) {

            int i;
            boolean salir = true;
            while ((i = s.indexOf("!")) >= 0) {
                int j = i;
                while (salir) {
                    j--;
                    String c = s.substring(j, j + 1);
                    /*mensaje(c);*/
                    switch (c) {

                        case "+":

                        case "-":

                        case "*":

                        case "/":
                            String cadReemp = s.substring(j + 1, i + 1);
                            String reemplazo = "fact " + s.substring(j + 1, i);
                           /* mensaje(cadReemp);
                            mensaje(reemplazo);*/
                            s = s.replace(cadReemp, reemplazo);
                            /* mensaje(s);*/


                            salir = false;


                    }

                }
                salir = true;
            }


        } else {

            String cadReemp = s;
            /* mensaje(s);*/
            String reemplazo = "fact " + s.substring(0, s.length() - 1);
            /* mensaje(reemplazo);*/
            s = s.replace(cadReemp, reemplazo);
            /* mensaje(s);*/


        }

        return s;
    }

    public void mensaje(String mensaje) {

        Toast men;
        men = makeText(this, "" + mensaje, LENGTH_LONG);

        men.setGravity(Gravity.CENTER, 0, 0);

        men.show();
    }

    public void memoria(View v) {
        expresion += res.getText().toString();
    }

    private boolean HaySimbolos(String s) {

        return s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/");
    }
}
