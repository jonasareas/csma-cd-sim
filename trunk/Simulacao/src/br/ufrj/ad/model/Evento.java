package br.ufrj.ad.model;

import br.ufrj.ad.controller.Simulador.TipoEvento;


public class Evento implements Comparable<Evento>
{
  private double tempoExecucao;
  
  private TipoEvento tipoEvento;
  
  private int codigoEstacao;
  
  public Evento(TipoEvento tipoEvento, double tempoExecucao, int codigoEstacao) {
    this.tipoEvento = tipoEvento;
    this.tempoExecucao = tempoExecucao;
    this.codigoEstacao = codigoEstacao;
  }
  
  public double getTempoExecucao()
  {
    return tempoExecucao;
  }

  public void setTempoExecucao(double tempoExecucao)
  {
    this.tempoExecucao = tempoExecucao;
  }

  public TipoEvento getTipoEvento()
  {
    return tipoEvento;
  }

  public void setTipoEvento(TipoEvento tipoEvento)
  {
    this.tipoEvento = tipoEvento;
  }
  
  public int getCodigoEstacao()
  {
    return codigoEstacao;
  }

  public void setCodigoEstacao(int codigoEstacao)
  {
    this.codigoEstacao = codigoEstacao;
  }

  @Override
  public int compareTo(Evento e)
  {
	if (this.tempoExecucao < e.getTempoExecucao())
      return -1;
    else if (this.tempoExecucao > e.getTempoExecucao())
      return 1;
    return 0;
  }
  
  @Override public boolean equals(Object obj)
  {
	  if(obj == null)
		  return false;
	  
	  if (this == obj)
		  return true;
	  
	  if ( !(obj instanceof Evento) ) 
		  return false;
	  
	  Evento e = (Evento)obj;
	  
	  if(this.codigoEstacao == e.getCodigoEstacao() && this.tipoEvento == e.getTipoEvento())
		  return true;
	  
	  return false;
	  
  }  
}
