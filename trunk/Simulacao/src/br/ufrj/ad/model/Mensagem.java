package br.ufrj.ad.model;

public class Mensagem
{
  public static final int TAMANHO_QUADRO = 1000; // Em bytes

  private int             quantidadeQuadros;    // Quantidade restante de quadros a serem enviados

  private int             totalQuadros;         // Total de quadros da mensagem

  private Quadro          quadro         = null;

  private double          tempoPrimeiroAcesso;

  private int             totalColisoes;

  public Mensagem(int tamanho)
  {
    if (tamanho > 0)
    {
      this.totalQuadros = tamanho;
      this.quantidadeQuadros = tamanho - 1;
      quadro = new Quadro(TAMANHO_QUADRO);
    }
    tempoPrimeiroAcesso = 0.0;
    totalColisoes = 0;
  }

  public Quadro getQuadro()
  {
    return quadro;
  }

  public boolean fimServicoQuadro()
  {
    if (this.quantidadeQuadros > 0)
    {
      this.quantidadeQuadros--;
      this.totalColisoes += quadro.getColisoes();
      quadro = new Quadro(TAMANHO_QUADRO);
      return true;
    }

    quadro = null;
    return false;

  }

  public int getQuantidadeQuadros()
  {
    return quantidadeQuadros;
  }

  public void setTempoPrimeiroAcesso(double tempoPrimeiroAcesso)
  {
    this.tempoPrimeiroAcesso = tempoPrimeiroAcesso;
    quadro.setTempoEntradaServidor(tempoPrimeiroAcesso);
  }

  public double getTempoPrimeiroAcesso()
  {
    return tempoPrimeiroAcesso;
  }

  /*
   * public int getTotalQuadros() { return totalQuadros; } public int
   * getTotalColisoes() { return totalColisoes; }
   */

  // informacao pedida
  public double colisoesPorQuadro()
  {
    return (double) (totalColisoes) / (double) (totalQuadros);
  }

}
