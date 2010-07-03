package br.ufrj.ad.model;

public class Quadro {
	
	private double tempoEntradaServidor;
	
	private double tempoInicioServico;
	
	private int tamanhoQuadro;
	
	private int colisoes;
	
	public Quadro(int tamanhoQuadro)
	{
		this.tamanhoQuadro = tamanhoQuadro; 
		this.tempoEntradaServidor = 0.0;
		this.tempoInicioServico = 0.0;
	}

	public void setTamanhoQuadro(int tamanhoQuadro) {
		this.tamanhoQuadro = tamanhoQuadro;
	}

	public int getTamanhoQuadro() {
		return tamanhoQuadro;
	}

	public void setTempoInicioServico(double tempoInicioServico) {
		this.tempoInicioServico = tempoInicioServico;
	}

	public double getTempoInicioServico() {
		return tempoInicioServico;
	}

	public void setTempoEntradaServidor(double tempoEntradaServidor) {
		this.tempoEntradaServidor = tempoEntradaServidor;
	}

	public double getTempoEntradaServidor() {
		return tempoEntradaServidor;
	}

	public void incColisoes() {
		this.colisoes++;
	}

	public int getColisoes() {
		return colisoes;
	}

}
