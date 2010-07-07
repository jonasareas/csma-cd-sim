package br.ufrj.ad.model;

public class ConfiguracaoPc
{
  private int     codigo;
  private double  distancia;
  private double  a;
  private double  p;
  private boolean deterministico;

  public ConfiguracaoPc(int codigo, double distancia, double p, double a, boolean deterministico)
  {
    this.codigo = codigo;
    this.a = a;
    this.p = p;
    this.distancia = distancia;
    this.deterministico = deterministico;
  }

  public int getCodigo()
  {
    return codigo;
  }

  public double getDistancia()
  {
    return distancia;
  }

  public double getA()
  {
    return a;
  }

  public double getP()
  {
    return p;
  }

  public boolean isDeterministico()
  {
    return deterministico;
  }
}
