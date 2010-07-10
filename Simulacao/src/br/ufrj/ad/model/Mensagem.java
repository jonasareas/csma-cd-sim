package br.ufrj.ad.model;

public class Mensagem
{
  public static final int TAMANHO_QUADRO = 1000; // Em bytes

  private int             quantidadeQuadros;    // Quantidade restante de quadros a serem enviados

  private int             totalQuadros;         // Total de quadros da mensagem

  private Quadro          quadro         = null;

  private double          tempoInicialAcesso;

  private int             totalColisoes;
  
  private int             codigoRodadaEntrada;

  public Mensagem(int tamanho,int codigoRodadaEntrada)
  {
    if (tamanho > 0)
    {
      this.totalQuadros = tamanho;
      this.quantidadeQuadros = tamanho - 1;
      quadro = new Quadro(TAMANHO_QUADRO);
    }
    this.codigoRodadaEntrada = codigoRodadaEntrada ;
    tempoInicialAcesso = 0.0;
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

  public void setTempoInicialAcesso(double tempoPrimeiroAcesso)
  {
    this.tempoInicialAcesso = tempoPrimeiroAcesso;
    quadro.setTempoInicialAcesso(tempoPrimeiroAcesso);
  }

  public double getTempoInicialAcesso()
  {
    return tempoInicialAcesso;
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

  public int getCodigoRodadaEntrada()
  {
    return codigoRodadaEntrada;
  }

}
