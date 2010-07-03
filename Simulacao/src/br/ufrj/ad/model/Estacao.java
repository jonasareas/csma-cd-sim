package br.ufrj.ad.model;

import java.util.LinkedList;

public class Estacao
{

  private int codigo;
  
  private double distancia;
  
  private boolean meioEmColisao;
  
  private int meioOcupado; // indica quantos pcs estao ocupando o meio
  
  private boolean esperandoTempoSeguranca;
  
  private boolean esperandoBackoff;
  
  private boolean transmitindo;
  
  private boolean forcar;
  
  private LinkedList<Mensagem> listaMensagens;
  
  private double p;

  private double a;

  private boolean deterministico;
  
  private int quantidadeColisoes;
  
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
    this.quantidadeColisoes = 0;
    this.esperandoBackoff = false;
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

  public double getDistancia()
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

  public LinkedList<Mensagem> getListaMensagens()
  {
    return listaMensagens;
  }

  public void setListaMensagens(LinkedList<Mensagem> listaMensagens)
  {
    this.listaMensagens = new LinkedList<Mensagem>(listaMensagens);
  }
  
  public void addMensagem(Mensagem mem)
  {
	  listaMensagens.addLast(mem);
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
	public void pacoteEnviado()
	{
		Mensagem msg = listaMensagens.getFirst();
		
		if(msg.getQuantidadeQuadros() <= 1)
		{
			listaMensagens.removeFirst();
		}else
		{
			msg.setQuantidadeQuadros(msg.getQuantidadeQuadros() -1);
		}
		
		quantidadeColisoes = 0;
	}
	
	public int getTamanhoQuadro()
	{
		return Mensagem.TAMANHO_QUADRO;
	}

	public void setQuantidadeColisoes(int quantidadeColisoes) {
		this.quantidadeColisoes = quantidadeColisoes;
	}

	public int getQuantidadeColisoes() {
		return quantidadeColisoes;
	}
	
	public void descartaPacote()
	{
		Mensagem msg = listaMensagens.getFirst();
		
		if(msg.getQuantidadeQuadros() <= 1)
		{
			listaMensagens.removeFirst();
		}else
		{
			msg.setQuantidadeQuadros(msg.getQuantidadeQuadros() -1);
		}
		
		quantidadeColisoes = 0;
	}

	public void setEsperandoBackoff(boolean esperandoBackoff) {
		this.esperandoBackoff = esperandoBackoff;
	}

	public boolean isEsperandoBackoff() {
		return esperandoBackoff;
	}
  
}
