package br.ufrj.ad.controller;

import javax.swing.text.StyledEditorKit.BoldAction;

public class BinaryBackoff
{
  private static final int TEMPO_SLOT = 51200; // em nanosegundos
  private static final int COLISOES_MAXIMO = 10;
  private static final int COLISOES_DESCARTE = 16;
  
  private int colisoes;
  
  public boolean descartaPacote()
  {
    if(colisoes > COLISOES_DESCARTE)
        return true;
    return false;    
  }

  private int minimoColisoes()
  {
    return Math.min(colisoes, COLISOES_MAXIMO);
  }
  
  public int geraAtraso()
  {
    return (int)(Math.random()* minimoColisoes());
  }
  
  public int getColisoes()
  {
    return colisoes;
  }

  public void setColisoes(int colisoes)
  {
    this.colisoes = colisoes;
  }

  
  

}
