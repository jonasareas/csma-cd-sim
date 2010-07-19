package br.ufrj.ad.model;

public class Quadro
{

  private double tempoConsideradoTransmissao;

  private double tempoInicioTransmissao;

  private int    tamanhoQuadro;

  private int    colisoes;

  public Quadro(int tamanhoQuadro)
  {
    this.tamanhoQuadro = tamanhoQuadro;
    this.tempoConsideradoTransmissao = 0.0;
    this.tempoInicioTransmissao = 0.0;
  }

  public void setTamanhoQuadro(int tamanhoQuadro)
  {
    this.tamanhoQuadro = tamanhoQuadro;
  }

  public int getTamanhoQuadro()
  {
    return tamanhoQuadro;
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
    this.colisoes++;
  }

  public int getColisoes()
  {
    return colisoes;
  }

}
