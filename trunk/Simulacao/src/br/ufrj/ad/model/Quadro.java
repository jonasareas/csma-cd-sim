package br.ufrj.ad.model;

public class Quadro
{

  private double tempoConsideradoTransmissao;
  private double tempoInicioTransmissao;
  private int    numeroColisoes;

  public Quadro()
  {
    this.tempoConsideradoTransmissao = 0.0;
    this.tempoInicioTransmissao = 0.0;
  }

  public void setInicioTransmissao(double tempoInicioTransmissao)
  {
    this.tempoInicioTransmissao = tempoInicioTransmissao;
  }

  public double getTempoInicioTransmissao()
  {
    return tempoInicioTransmissao;
  }

  public void setTempoConsideradoTransmissao(double tempoConsideradoTransmissao)
  {
    this.tempoConsideradoTransmissao = tempoConsideradoTransmissao;
  }

  public double getTempoConsideradoTransmissao()
  {
    return tempoConsideradoTransmissao;
  }

  public void incrementaColisoes()
  {
    this.numeroColisoes++;
  }

  public int getNumeroColisoes()
  {
    return numeroColisoes;
  }

}
