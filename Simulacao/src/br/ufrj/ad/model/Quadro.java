package br.ufrj.ad.model;

public class Quadro
{

  private double tempoInicialAcesso;

  private double tempoFinalAcesso;

  private int    tamanhoQuadro;

  private int    colisoes;

  public Quadro(int tamanhoQuadro)
  {
    this.tamanhoQuadro = tamanhoQuadro;
    this.tempoInicialAcesso = 0.0;
    this.tempoFinalAcesso = 0.0;
  }

  public void setTamanhoQuadro(int tamanhoQuadro)
  {
    this.tamanhoQuadro = tamanhoQuadro;
  }

  public int getTamanhoQuadro()
  {
    return tamanhoQuadro;
  }

  public void setFinalAcesso(double tempoInicioServico)
  {
    this.tempoFinalAcesso = tempoInicioServico;
  }

  public double getTempoFinalAcesso()
  {
    return tempoFinalAcesso;
  }

  public void setTempoInicialAcesso(double tempoEntradaServidor)
  {
    this.tempoInicialAcesso = tempoEntradaServidor;
  }

  public double getTempoInicialAcesso()
  {
    return tempoInicialAcesso;
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
