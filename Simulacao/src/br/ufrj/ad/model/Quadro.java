package br.ufrj.ad.model;

public class Quadro
{

  private double tempoConsideradoTransmissao;       // Tempo inicial do Quadro para o Tap(i)
  
  private double tempoInicioTransmissao;            // Tempo inicial de transmissao de um Quadro
  
  private int    numeroColisoes;                    // Numero de colisoes de um Quadro
  
  /*
   * Construtor da Classe: Responsavel por inicializar um Quadro com seus tempos iniciais de transmissao iguais a zero.
   */
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
