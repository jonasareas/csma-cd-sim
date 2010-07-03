package br.ufrj.ad.model;

import java.util.LinkedList;

public class Estacao
{

   // Parametros da estacao
	
  private int codigo;
  
  private double distancia;
  
  private double p;

  private double a;

  private boolean deterministico;
  
  // Variaveis de controle
  
  private boolean meioEmColisao;
  
  private int meioOcupado; // indica quantos pcs estao ocupando o meio
  
  private boolean esperandoTempoSeguranca;
  
  private boolean esperandoBackoff;
  
  private boolean transmitindo;
  
  private boolean forcar;
  
  private LinkedList<Mensagem> listaMensagens;
  
  private int quadrosTransmitidos;
  
  private double tempoInicioTentativa;
  
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
    this.forcar = false;
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
	  if(listaMensagens.isEmpty())
		  return null;
	  
	  return listaMensagens.getFirst();
  }
  
  public boolean isFilaVazia()
  {
	return listaMensagens.isEmpty();  
  }

  public void setEsperandoTempoSeguranca(boolean esperandoTempoSeguranca) {
	this.esperandoTempoSeguranca = esperandoTempoSeguranca;
  }

  public boolean isEsperandoTempoSeguranca() {
	return esperandoTempoSeguranca;
  }

	public void setTransmitindo(boolean transmitindo) {
		this.transmitindo = transmitindo;
	}
	
	public boolean isTransmitindo() {
		return transmitindo;
	}
	
	public void setForcar(boolean forcar) {
		this.forcar = forcar;
	}
	
	public boolean isForcar() {
		return forcar;
	} 
	public void pacoteEnviado(double tempoFimServico)
	{
		quadrosTransmitidos++;
		
		//informacao pedida
		double utilizaoEthernet = tempoFimServico - this.tempoInicioTentativa;
		this.tempoInicioTentativa = 0.0;
		
		Mensagem msg = listaMensagens.getFirst();
		
		double inicio = msg.getQuadro().getTempoInicioServico();
		double fim = msg.getTempoPrimeiroAcesso();
		//informacao pedida
		double tap = inicio - fim;		
		
		if(!msg.fimServicoQuadro())
		{
			//informacao pedida
			double tam = fim - msg.getTempoPrimeiroAcesso();
			double ncm = msg.colisoesPorQuadro();
			listaMensagens.removeFirst();
		}
	}
	
	public int getTamanhoQuadro()
	{
		return listaMensagens.getFirst().getQuadro().getTamanhoQuadro();
	}

	public void incQuantidadeColisoes() {
		listaMensagens.getFirst().getQuadro().incColisoes();
	}

	public int getQuantidadeColisoes() {
		return listaMensagens.getFirst().getQuadro().getColisoes();
	}
	
	public void descartaPacote(double tempoFimTentativa)
	{
		double utilizaoEthernet = tempoFimTentativa - this.tempoInicioTentativa;
		this.tempoInicioTentativa = 0.0;
		
		Mensagem msg = listaMensagens.getFirst();
		
		// Nao contabiliza o TAp nem o TAm 
		
		if(!msg.fimServicoQuadro())
		{
			//informacao pedida
			double ncm = msg.colisoesPorQuadro();
			listaMensagens.removeFirst();
		}
	}

	public void setEsperandoBackoff(boolean esperandoBackoff) {
		this.esperandoBackoff = esperandoBackoff;
	}

	public boolean isEsperandoBackoff() {
		return esperandoBackoff;
	}

	public int getQuadrosTransmitidos() {
		return quadrosTransmitidos;
	}

	public void setTempoInicioTentativa(double tempoInicioTentativa) {
		this.tempoInicioTentativa = tempoInicioTentativa;
	}

	public double getTempoInicioTentativa() {
		return tempoInicioTentativa;
	}
}
