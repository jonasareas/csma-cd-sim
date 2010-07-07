package br.ufrj.ad.model;

import java.util.LinkedList;

public class Estacao
{
  // Parametros da estacao

  private int                  codigo;

  private double               distancia;

  private double               p;

  private double               a;

  private boolean              deterministico;

  // Variaveis de controle

  private boolean              meioEmColisao;

  private int                  meioOcupado;            // indica quantos pcs estao ocupando o meio

  private boolean              esperandoTempoSeguranca;

  private boolean              esperandoBackoff;

  private boolean              transmitindo;

  private boolean              forcarEnvio;

  private LinkedList<Mensagem> listaMensagens;

  private int                  quadrosTransmitidos;

  private double               tempoInicioTentativa;

  public Estacao(int codigo, double distancia, double p, double a, boolean ehDeterministico)
  {
    this.codigo = codigo;
    this.distancia = distancia;
    this.p = p;
    this.a = a;
    this.deterministico = ehDeterministico;
    this.listaMensagens = new LinkedList<Mensagem>();
    this.meioEmColisao = false;
    this.meioOcupado = 0;
    this.esperandoTempoSeguranca = false;
    this.transmitindo = false;
    this.forcarEnvio = false;
    this.esperandoBackoff = false;
    this.quadrosTransmitidos = 0;
  }

  public int getCodigo()
  {
    return codigo;
  }

  public double getDistancia()
  {
    return distancia;
  }

  public boolean isMeioEmColisao()
  {
    return meioEmColisao;
  }

  public void setMeioEmColisao(boolean meioEmColisao)
  {
    this.meioEmColisao = meioEmColisao;
  }

  public int getMeioOcupado()
  {
    return meioOcupado;
  }

  public void setMeioOcupado(int meioOcupado)
  {
    this.meioOcupado = meioOcupado;
  }

  public double getP()
  {
    return p;
  }

  public double getA()
  {
    return a;
  }

  public boolean isDeterministico()
  {
    return deterministico;
  }

  public void addMensagem(Mensagem mem)
  {
    listaMensagens.addLast(mem);
  }

  public Mensagem getMensagem()
  {
    if (listaMensagens.isEmpty())
      return null;

    return listaMensagens.getFirst();
  }

  public boolean isFilaVazia()
  {
    return listaMensagens.isEmpty();
  }

  public void setEsperandoTempoSeguranca(boolean esperandoTempoSeguranca)
  {
    this.esperandoTempoSeguranca = esperandoTempoSeguranca;
  }

  public boolean isEsperandoTempoSeguranca()
  {
    return esperandoTempoSeguranca;
  }

  public void setTransmitindo(boolean transmitindo)
  {
    this.transmitindo = transmitindo;
  }

  public boolean isTransmitindo()
  {
    return transmitindo;
  }

  public void setForcarEnvio(boolean forcarEnvio)
  {
    this.forcarEnvio = forcarEnvio;
  }

  public boolean isForcarEnvio()
  {
    return forcarEnvio;
  }

  public void pacoteEnviado(double tempoFimServico)
  {
    quadrosTransmitidos++;

    // informacao pedida
    double utilizaoEthernet = tempoFimServico - this.tempoInicioTentativa;
    this.tempoInicioTentativa = 0.0;

    Mensagem msg = listaMensagens.getFirst();

    double inicio = msg.getQuadro().getTempoInicioEnvio();
    double fim = msg.getTempoPrimeiroAcesso();
    
    // informacao pedida
    double tap = inicio - fim;

    if (!msg.fimServicoQuadro())
    {
      // informacao pedida
      double tam = inicio - msg.getTempoPrimeiroAcesso();
      double ncm = msg.colisoesPorQuadro();
      listaMensagens.removeFirst();
    }
  }

  public int getTamanhoQuadro()
  {
    return listaMensagens.getFirst().getQuadro().getTamanhoQuadro();
  }

  public void incrementaQuantidadeColisoes()
  {
    listaMensagens.getFirst().getQuadro().incrementaColisoes();
  }

  public int getQuantidadeColisoes()
  {
    return listaMensagens.getFirst().getQuadro().getColisoes();
  }

  public void descartaPacote(double tempoFimTentativa)
  {
    double utilizacaoEthernet = tempoFimTentativa - this.tempoInicioTentativa; //EH SOH PARA O PC 1
    this.tempoInicioTentativa = 0.0;

    Mensagem msg = listaMensagens.getFirst();

    // Nao contabiliza o TAp nem o TAm

    if (!msg.fimServicoQuadro())
    {
      // informacao pedida
      double ncm = msg.colisoesPorQuadro();
      listaMensagens.removeFirst();
    }
  }

  public void setEsperandoBackoff(boolean esperandoBackoff)
  {
    this.esperandoBackoff = esperandoBackoff;
  }

  public boolean isEsperandoBackoff()
  {
    return esperandoBackoff;
  }

  public int getQuadrosTransmitidos()
  {
    return quadrosTransmitidos;
  }

  public void setTempoInicioTentativa(double tempoInicioTentativa)
  {
    this.tempoInicioTentativa = tempoInicioTentativa;
  }

  public double getTempoInicioTentativa()
  {
    return tempoInicioTentativa;
  }
}
