package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.ad.controller.variaveis.Exponencial;
import br.ufrj.ad.controller.variaveis.Geometrica;
import br.ufrj.ad.model.ConfiguracaoPc;
import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;
import br.ufrj.ad.model.Mensagem;
import br.ufrj.ad.util.BinaryBackoff;

public class Simulador
{
	public static enum TipoEvento {
		    ChegadaNaFila,
		    TentativaEnvio,
		    FimTransmissao,
		    MeioOcupado,
		    MeioLivre,
		    FimEspera,
		    FimReforco,
		    FimBackOff
	}
  
  private static final double PROPAGACAO_ELETRICA = 0.005; // Em microsegundos/metro
  private static final double TEMPO_ENTRE_TRANSMISSOES = 9.6; // Em microsegundos
  private static final double TEMPO_REFORCO_COLISAO = 3.2; // Em microsegundos
  
  private PriorityQueue<Evento> listaEventos;
  
  private List<Estacao> listaPCs = new ArrayList<Estacao>();
  
  public void iniciaSimulacao(double tempoSimulacao, ArrayList<ConfiguracaoPc> parametros) 
  {
    	
	  Estacao est;
	  for (ConfiguracaoPc config : parametros)
	  {
		  est = new Estacao(config.getCodigo(), config.getDistancia(), config.getP(), config.getA(), config.isDeterministico());
		  listaPCs.add(est);		  
	  }
		    
    listaEventos = new PriorityQueue<Evento>();
    
    for (Estacao estacao : listaPCs) 
    {
       if (estacao.getP() != 0)
       {
    	   programaChegadaNaFila(estacao.getCodigo(), Exponencial.geraAmostra(estacao.getA(), estacao.isDeterministico()));
       }
    }
    simula(tempoSimulacao);    
  }
  
  private void programaChegadaNaFila(int codigoEstacao, double tempoExecucao)
  {
    Evento evento = new Evento(TipoEvento.ChegadaNaFila, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }
  
  private void simula(double tempoSimulacao)
  {
    Evento evento;
    do {
      evento = listaEventos.remove();
      verificaTipoEvento(evento);
    } while(/*evento.getTempoExecucao() < tempoSimulacao*/ !listaEventos.isEmpty());
  }
  
  private Estacao buscaEstacao(int codEstacao)
  {
	  for(Estacao e : listaPCs)
	  {
		  if(e.getCodigo() == codEstacao)
			  return e;
	  }
	  return null;
  }
  
  private void verificaTipoEvento(Evento evento)
  {
    switch(evento.getTipoEvento()) {
      case ChegadaNaFila :
    	//System.out.println(Double.toString(evento.getTempoExecucao()) + ": Chegada na Fila de " + evento.getCodigoEstacao());  
        processaChegadaNaFila(evento);
        break;
      case TentativaEnvio :
    	System.out.println(Double.toString(evento.getTempoExecucao()) + ": Tentativa de Envio de " + evento.getCodigoEstacao());
    	processaTentativaEnvio(evento);
    	break;
      case FimTransmissao :
    	System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de transmissao de " + evento.getCodigoEstacao());
    	processaFimTransmissao(evento);
    	break;
      case FimEspera :
    	//System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de espera de " + evento.getCodigoEstacao());
    	processaFimEspera(evento);
    	break;
      case MeioLivre :
    	//System.out.println(evento.getTempoExecucao() + ": Meio Livre em " + evento.getCodigoEstacao());
    	processaMeioLivre(evento);
    	break;
      case MeioOcupado :
    	//System.out.println(evento.getTempoExecucao() + ": Meio ocupado em " + evento.getCodigoEstacao());
    	processaMeioOcupado(evento);
    	break;
      case FimReforco :
    	//System.out.println(evento.getTempoExecucao() + ": Fim de reforco de " + evento.getCodigoEstacao());
    	processaFimReforco(evento);
    	break;
      case FimBackOff :
    	System.out.println(evento.getTempoExecucao() + ": Fim de backOff de " + evento.getCodigoEstacao());
    	processaFimBackOff(evento);
    	break;
        
    }
  }
  
  private void processaChegadaNaFila(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  double proximoTempo = evento.getTempoExecucao() + Exponencial.geraAmostra(estacao.getA(), estacao.isDeterministico());
	  programaChegadaNaFila(estacao.getCodigo(), proximoTempo );
	  
	  Mensagem mensagem = new Mensagem(Geometrica.geraAmostra(estacao.getP()));
	  if(estacao.isFilaVazia())
	  {
		  Evento novoEvento = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
		  listaEventos.add(novoEvento);
	  }
	  estacao.addMensagem(mensagem);
	
  }
  
  private void processaTentativaEnvio(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  if(estacao.isFilaVazia())
	  {
		  estacao.setForcar(false);
		  System.out.println("Fila Vazia");
		  return;
	  }
	  
	  if(estacao.getMeioOcupado() == 0)
	  {
		  if(!estacao.isEsperandoTempoSeguranca()) 
		  {
			  estacao.setForcar(false);
			  for(Estacao e : listaPCs)
			  {
				  if(e.getCodigo() != estacao.getCodigo())
				  {
					  double distanciaTotal = e.getDistancia() + estacao.getDistancia();
					  double tempoInicioPercepecao = evento.getTempoExecucao();
						  
					  tempoInicioPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;
						  
					  Evento inicioPercepcao = new Evento(TipoEvento.MeioOcupado, tempoInicioPercepecao, e.getCodigo());
					  listaEventos.add(inicioPercepcao);
				  }
			  }
				  
			  estacao.setTransmitindo(true);
			  double tempoDeFim = evento.getTempoExecucao();
			  tempoDeFim += estacao.getTamanhoQuadro()*8/10.0;
				  
			  Evento fimTransmicao = new Evento(TipoEvento.FimTransmissao, tempoDeFim, estacao.getCodigo());
			  listaEventos.add(fimTransmicao);
		  
		  }else
		  {
			  estacao.setForcar(true);
		  }
	  }else
	  {
		  if(estacao.isForcar())
		  {
			  estacao.setForcar(false);
			  for(Estacao e : listaPCs)
			  {
				  if(e.getCodigo() != estacao.getCodigo())
				  {
					  double distanciaTotal = e.getDistancia() + estacao.getDistancia();
					  double tempoInicioPercepecao = evento.getTempoExecucao();
						  
					  tempoInicioPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;
						  
					  Evento inicioPercepcao = new Evento(TipoEvento.MeioOcupado, tempoInicioPercepecao, e.getCodigo());
					  listaEventos.add(inicioPercepcao);
				  }
			  }
				  
			  estacao.setTransmitindo(true);
			  estacao.setMeioEmColisao(true);
			  double tempoDeFim = evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO;
				  
			  Evento fimTransmicao = new Evento(TipoEvento.FimReforco, tempoDeFim, estacao.getCodigo());
			  listaEventos.add(fimTransmicao);
		  }else
		  {
			  return;
		  }
	  }
  }

  private void processaFimTransmissao(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  for(Estacao e: listaPCs)
	  {
		  if(e.getCodigo() != estacao.getCodigo())
		  {
			  double distanciaTotal = e.getDistancia() + estacao.getDistancia();
			  double tempoFimPercepecao = evento.getTempoExecucao();
			  
			  tempoFimPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;
			  
			  Evento inicioPercepcao = new Evento(TipoEvento.MeioLivre, tempoFimPercepecao, e.getCodigo());
			  listaEventos.add(inicioPercepcao);
		  }
	  }
	  
	  estacao.setEsperandoTempoSeguranca(true);
	  estacao.setTransmitindo(false);
	  estacao.setForcar(false);
	  estacao.setMeioEmColisao(false);
	  
	  estacao.pacoteEnviado();
	  
	  
	  Evento transmissaoCompleta = new Evento(TipoEvento.FimEspera,
			  								  evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES,
			  								  estacao.getCodigo());
	  listaEventos.add(transmissaoCompleta);
	  
	  
  }
  
  private void processaFimEspera(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  estacao.setEsperandoTempoSeguranca(false);
	  
	  if(!estacao.isFilaVazia())
	  {
		  Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
		  listaEventos.add(novaTentativa);
		  //processaTentativaEnvio(evento);
	  }
  }
  
  private void processaMeioLivre(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  estacao.setMeioOcupado(estacao.getMeioOcupado() - 1);
	  
	  if(estacao.isTransmitindo())
		  return;
	  
	  if(estacao.getMeioOcupado() == 0)
	  {
		  if(estacao.isEsperandoTempoSeguranca())
		  {
			  estacao.setForcar(true);
		  }else
		  {
			  if(!estacao.isEsperandoBackoff())
			  {
				  Evento fimEspera = new Evento(TipoEvento.FimEspera, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, evento.getCodigoEstacao());
				  listaEventos.add(fimEspera);
			  }
		  }
	  }  
  }
  
  private void processaMeioOcupado(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  estacao.setMeioOcupado(estacao.getMeioOcupado() + 1);
	  
	  if(!estacao.isTransmitindo() || estacao.isMeioEmColisao())
		  return;
	  
	  listaEventos.remove(new Evento(TipoEvento.FimTransmissao, 0, estacao.getCodigo()));
	  
	  estacao.setMeioEmColisao(true);
	  
	  Evento fimReforco = new Evento(TipoEvento.FimReforco, evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO, estacao.getCodigo());
	  listaEventos.add(fimReforco);
	  
  }
  
  private void processaFimReforco(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  for(Estacao e: listaPCs)
	  {
		  if(e.getCodigo() != estacao.getCodigo())
		  {
			  double distanciaTotal = e.getDistancia() + estacao.getDistancia();
			  double tempoFimPercepecao = evento.getTempoExecucao();
			  
			  tempoFimPercepecao += PROPAGACAO_ELETRICA * distanciaTotal;
			  
			  Evento inicioPercepcao = new Evento(TipoEvento.MeioLivre, tempoFimPercepecao, e.getCodigo());
			  listaEventos.add(inicioPercepcao);
		  }
	  }
	  
	  estacao.setEsperandoTempoSeguranca(false);
	  estacao.setTransmitindo(false);
	  estacao.setForcar(false);
	  estacao.setMeioEmColisao(false);
	  
	  double tempoEspera = BinaryBackoff.geraAtraso(estacao.getQuantidadeColisoes());
	  
	  if(tempoEspera < 0)
		  estacao.descartaPacote();
	  else
	  {
		  tempoEspera += evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO;
		  
		  Evento esperaBackOff = new Evento(TipoEvento.FimBackOff, tempoEspera, estacao.getCodigo());
		  listaEventos.add(esperaBackOff);
		  
		  estacao.setEsperandoBackoff(true);
	  }
	  
	  estacao.setQuantidadeColisoes(estacao.getQuantidadeColisoes() + 1);
  }
  
  private void processaFimBackOff(Evento evento)
  {
	  Estacao estacao = buscaEstacao(evento.getCodigoEstacao());
	  
	  estacao.setEsperandoBackoff(false);
	  
	  Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
	  listaEventos.add(novaTentativa);
  }
}
