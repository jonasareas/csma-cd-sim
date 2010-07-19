package br.ufrj.ad.model;

public class Mensagem
{
  public static final int TAMANHO_QUADRO = 1000; // Em bytes

  private int             quantidadeQuadros;     // Quantidade restante de quadros a serem enviados

  private int             totalQuadros;          // Total de quadros da mensagem

  private Quadro          quadro         = null;

  private double          tempoConsideradaTransmissao;

  private int             totalColisoes;
  
  private int             codigoRodadaEntrada; //TODO: Pra que precisa disso aqui?

  public Mensagem(int tamanho,int codigoRodadaEntrada)
  {
    if (tamanho > 0)
    {
      this.totalQuadros = tamanho;
      this.quantidadeQuadros = tamanho - 1;
      quadro = new Quadro(TAMANHO_QUADRO);
    }
    this.codigoRodadaEntrada = codigoRodadaEntrada ;
    tempoConsideradaTransmissao = 0.0;
    totalColisoes = 0;
  }

  public Quadro getQuadro()
  {
    return quadro;
  }

  // Finaliza o serviço de um quadro e indica se ainda há quadros a serem transmitidos.
  public boolean fimServicoMensagem()
  {
    if (this.quantidadeQuadros > 0)
    {
      this.quantidadeQuadros--;
      this.totalColisoes += quadro.getColisoes();
      quadro = new Quadro(TAMANHO_QUADRO);
      return false;
    }

    quadro = null;
    return true;

  }

  public int getQuantidadeQuadros()
  {
    return quantidadeQuadros;
  }

  public void setTempoConsideradaTransmissao(double tempoConsideradaTransmissao)
  {
    this.tempoConsideradaTransmissao = tempoConsideradaTransmissao;
    // quadro.setTempoConsideradoTransmissao(tempoConsideradaTransmissao); TODO: Pode tirar?
  }

  public double getTempoConsideradaTransmissao()
  {
    return tempoConsideradaTransmissao;
  }

  public int getTotalQuadros() 
  { 
    return totalQuadros; 
  }
  
  public int getTotalColisoes() 
  { 
    return totalColisoes; 
  }

  // Informacao pedida
  public double colisoesPorQuadro()
  {
    return (double) (totalColisoes) / (double) (totalQuadros);
  }

  public int getCodigoRodadaEntrada()
  {
    return codigoRodadaEntrada;
  }

}
