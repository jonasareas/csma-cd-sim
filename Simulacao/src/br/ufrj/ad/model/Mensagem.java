package br.ufrj.ad.model;

public class Mensagem
{

  private static final int TAMANHO_QUADRO = 1000; // Em bytes
  
  private int tamanho;
  
  public int quantidadeQuadros() 
  {
    if (tamanho % TAMANHO_QUADRO != 0)
    {
      return (tamanho / TAMANHO_QUADRO) + 1;
    }
    return (tamanho / TAMANHO_QUADRO);
  }

  public int getTamanho()
  {
    return tamanho;
  }

  public void setTamanho(int tamanho)
  {
    this.tamanho = tamanho;
  }
  
}
