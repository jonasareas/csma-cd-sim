package br.ufrj.ad.model;

public class Computador
{

  private int codigo;
  
  private int distancia;
  
  private boolean meioEmColisao;
  
  private boolean meioOcupado;
  
  public Computador(int codigo, int distancia)
  {
    this.codigo = codigo;
    this.distancia = distancia;
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
  
}
