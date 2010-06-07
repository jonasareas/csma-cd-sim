package br.ufrj.ad.model;

import java.util.Arrays;
import java.util.List;

public class Evento
{

  private double entradaNaLista;
  
  private double saidaDaLista;
  
  public static final List<String> TIPOS_EVENTO = Arrays.asList(" /* TODO */ ");
  
  public double duracao() {
    return saidaDaLista - entradaNaLista;
  }

  public double getEntradaNaLista()
  {
    return entradaNaLista;
  }

  public void setEntradaNaLista(double entradaNaLista)
  {
    this.entradaNaLista = entradaNaLista;
  }

  public double getSaidaDaLista()
  {
    return saidaDaLista;
  }

  public void setSaidaDaLista(double saidaDaLista)
  {
    this.saidaDaLista = saidaDaLista;
  }
  
}
