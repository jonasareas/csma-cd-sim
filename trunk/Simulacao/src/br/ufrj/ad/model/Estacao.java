package br.ufrj.ad.model;

import java.util.LinkedList;
import java.util.List;

public class Estacao
{

  private int codigo;
  
  private int distancia;
  
  private boolean meioEmColisao;
  
  private boolean meioOcupado;
  
  private List<Mensagem> listaMensagens;
  
  // Parâmetros da simulação
  
  private double p;

  private double a;

  private boolean deterministico;
  
  public Estacao(int codigo, int distancia, double p, double a, boolean ehDeterministico)
  {
    this.codigo = codigo;
    this.distancia = distancia;
    this.p = p;
    this.a = a;
    this.deterministico = ehDeterministico;
    this.listaMensagens = new LinkedList<Mensagem>();
  }
  
  public boolean enviaMensagem(Mensagem mensagem) {
    // TODO
    return true;
  }
  
  public int getCodigo()
  {
    return codigo;
  }

  public void setCodigo(int codigo)
  {
    this.codigo = codigo;
  }

  public int getDistancia()
  {
    return distancia;
  }

  public void setDistancia(int distancia)
  {
    this.distancia = distancia;
  }

  public boolean isMeioEmColisao()
  {
    return meioEmColisao;
  }

  public void setMeioEmColisao(boolean meioEmColisao)
  {
    this.meioEmColisao = meioEmColisao;
  }

  public boolean isMeioOcupado()
  {
    return meioOcupado;
  }

  public void setMeioOcupado(boolean meioOcupado)
  {
    this.meioOcupado = meioOcupado;
  }

  public double getP()
  {
    return p;
  }

  public void setP(double p)
  {
    this.p = p;
  }

  public double getA()
  {
    return a;
  }

  public void setA(double a)
  {
    this.a = a;
  }

  public boolean isDeterministico()
  {
    return deterministico;
  }

  public void setDeterministico(boolean deterministico)
  {
    this.deterministico = deterministico;
  }

  public List<Mensagem> getListaMensagens()
  {
    return listaMensagens;
  }

  public void setListaMensagens(List<Mensagem> listaMensagens)
  {
    this.listaMensagens = listaMensagens;
  }
  
  
  
}
