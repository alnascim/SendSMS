package br.com.bright;

public class clItemAgenda {

    String strNome ;
    String strTelefone ;
    String strAg_data ;
    String strAg_hora ;
    String strId_agenda ;


    public clItemAgenda(){}

    public clItemAgenda(String strNome, String strTelefone, String strAg_data, String strAg_hora, String strId_agenda){
        this.strNome = strNome;
        this.strTelefone = strTelefone;
        this.strAg_data = strAg_data;
        this.strAg_hora= strAg_hora;
        this.strId_agenda = strId_agenda;
    }

}
