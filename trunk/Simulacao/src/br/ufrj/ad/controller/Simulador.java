package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import br.ufrj.ad.model.ConfiguracaoPc;
import br.ufrj.ad.model.Estacao;
import br.ufrj.ad.model.Evento;
import br.ufrj.ad.model.Mensagem;
import br.ufrj.ad.model.Quadro;
import br.ufrj.ad.parametros.ParametroA;
import br.ufrj.ad.parametros.ParametroP;
import br.ufrj.ad.util.BinaryBackoff;

public class Simulador
{
  public static enum TipoEvento //TODO Temos que caracterizar bem caa uma destas
  {
    ChegadaNaFila, 
    TentativaEnvio, 
    FimTransmissao, 
    MeioOcupado, 
    MeioLivre, 
    FimEspera, 
    FimReforco, 
    FimBackOff
  }

  private static final double   PROPAGACAO_ELETRICA      = 0.000005;    // Em milisegundos/metro
  private static final double   TEMPO_ENTRE_TRANSMISSOES = 0.0096;      // Em milisegundos
  private static final double   TEMPO_REFORCO_COLISAO    = 0.0032;      // Em milisegundos

  private PriorityQueue<Evento> listaEventos;

  private List<Estacao>         listaPCs                 = new ArrayList<Estacao>();

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
        programaChegadaNaFila(ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico()), estacao.getCodigo()); 
      }
    }
    simula(tempoSimulacao);
  }

  private void programaChegadaNaFila(double tempoExecucao, int codigoEstacao)
  {
    Evento evento = new Evento(TipoEvento.ChegadaNaFila, tempoExecucao, codigoEstacao);
    listaEventos.add(evento);
  }

  private void simula(double tempoSimulacao)
  {
    Evento evento;
    do
    {
      evento = listaEventos.remove();
      verificaTipoEvento(evento);
    }
    while (evento.getTempoExecucao() < tempoSimulacao); 
  }

  private Estacao buscaEstacao(int codEstacao)
  {
    for (Estacao estacao : listaPCs)
    {
      if (estacao.getCodigo() == codEstacao)
        return estacao;
    }
    return null;
  }

  private void verificaTipoEvento(Evento evento)
  {
    switch (evento.getTipoEvento())
    {
      case ChegadaNaFila:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Chegada na Fila de " + evento.getCodigoEstacao());
        processaChegadaNaFila(evento);
        break;
      case TentativaEnvio:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Tentativa de Envio de " + evento.getCodigoEstacao());
        processaTentativaEnvio(evento);
        break;
      case FimTransmissao:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de transmissao de " + evento.getCodigoEstacao());
        processaFimTransmissao(evento);
        break;
      case FimEspera:
        System.out.println(Double.toString(evento.getTempoExecucao()) + ": Fim de espera de " + evento.getCodigoEstacao());
        processaFimEspera(evento);
        break;
      case MeioLivre:
        System.out.println(evento.getTempoExecucao() + ": Meio Livre em " + evento.getCodigoEstacao());
        processaMeioLivre(evento);
        break;
      case MeioOcupado:
        System.out.println(evento.getTempoExecucao() + ": Meio ocupado em " + evento.getCodigoEstacao());
        processaMeioOcupado(evento);
        break;
      case FimReforco:
        System.out.println(evento.getTempoExecucao() + ": Fim de reforco de " + evento.getCodigoEstacao());
        processaFimReforco(evento);
        break;
      case FimBackOff:
        System.out.println(evento.getTempoExecucao() + ": Fim de backoff de " + evento.getCodigoEstacao());
        processaFimBackOff(evento);
        break;

    }
  }

  private void processaChegadaNaFila(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    // Programa próxima chegada
    double proximoTempo = evento.getTempoExecucao() + ParametroA.geraParametro(estacao.getA(), estacao.isDeterministico());
    programaChegadaNaFila(proximoTempo, estacao.getCodigo());

    // Realiza a chegada na fila
    Mensagem mensagem = new Mensagem(ParametroP.geraParametro(estacao.getP()));
    if (estacao.isFilaVazia())
    {
      Evento novoEvento = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novoEvento);
    }
    estacao.addMensagem(mensagem);

  }

  private void processaTentativaEnvio(Evento evento) //TODO: Esse método tá meio obscuro pra mim. =S
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    if (estacao.isFilaVazia()) 
    {
      estacao.setForcar(false); //TODO Nao entendi. O que é isso?
      System.out.println("Fila vazia na estacao " +  estacao.getCodigo());
      return;
    }

    if (estacao.getTempoInicioTentativa() == 0.0) //TODO Nao entendi. O que é isso?
      estacao.setTempoInicioTentativa(evento.getTempoExecucao());

    Mensagem msg = estacao.getMensagem();
    Quadro qd = msg.getQuadro();

    if (msg.getTempoPrimeiroAcesso() == 0.0)
    {
      msg.setTempoPrimeiroAcesso(evento.getTempoExecucao());
    }
    if (qd.getTempoEntradaServidor() == 0.0)
    {
      qd.setTempoEntradaServidor(evento.getTempoExecucao());
    }

    if (estacao.getMeioOcupado() == 0)
    {
      if (!estacao.isEsperandoTempoSeguranca())
      {
        if (qd.getTempoInicioServico() == 0.0) //TODO Nao entendi. O que é isso?
        {
          qd.setTempoInicioServico(evento.getTempoExecucao());
        }
        estacao.setForcar(false);
        for (Estacao e : listaPCs)
        {
          if (e.getCodigo() != estacao.getCodigo()) //TODO A própria estação recebe o sinal também, não?
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
        tempoDeFim += estacao.getTamanhoQuadro() * 8 / 10.0; // TODO: O que significa a conta?

        Evento fimTransmissao = new Evento(TipoEvento.FimTransmissao, tempoDeFim, estacao.getCodigo());
        listaEventos.add(fimTransmissao);
      }
      else
      {
        estacao.setForcar(true);
      }
    }
    else
    {
      if (estacao.isForcar())
      {
        if (qd.getTempoInicioServico() == 0.0)
          qd.setTempoInicioServico(evento.getTempoExecucao());

        estacao.setForcar(false);
        for (Estacao e : listaPCs)
        {
          if (e.getCodigo() != estacao.getCodigo()) //TODO Vamos colocar isso num método separado. Código repetido (varias e varias vezes =S)!
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
      }
      else
      {
        return;
      }
    }
  }

  private void processaFimTransmissao(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    for (Estacao e : listaPCs)
    {
      if (e.getCodigo() != estacao.getCodigo())
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

    estacao.pacoteEnviado(evento.getTempoExecucao());

    Evento transmissaoCompleta = new Evento(TipoEvento.FimEspera, evento.getTempoExecucao() + TEMPO_ENTRE_TRANSMISSOES, estacao.getCodigo());
    listaEventos.add(transmissaoCompleta);

  }

  private void processaFimEspera(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoTempoSeguranca(false);

    if (!estacao.isFilaVazia())
    {
      Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
      listaEventos.add(novaTentativa);
      // processaTentativaEnvio(evento);
    }
  }

  private void processaMeioLivre(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setMeioOcupado(estacao.getMeioOcupado() - 1);

    if (estacao.isTransmitindo())
      return;

    if (estacao.getMeioOcupado() == 0)
    {
      if (estacao.isEsperandoTempoSeguranca())
      {
        estacao.setForcar(true);
      }
      else
      {
        if (!estacao.isEsperandoBackoff())
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

    if (!estacao.isTransmitindo() || estacao.isMeioEmColisao())
      return;

    listaEventos.remove(new Evento(TipoEvento.FimTransmissao, 0, estacao.getCodigo()));

    estacao.setMeioEmColisao(true);

    Evento fimReforco = new Evento(TipoEvento.FimReforco, evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO, estacao.getCodigo());
    listaEventos.add(fimReforco);

  }

  private void processaFimReforco(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    for (Estacao e : listaPCs)
    {
      if (e.getCodigo() != estacao.getCodigo())
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

    if (tempoEspera < 0)
      estacao.descartaPacote(evento.getTempoExecucao());
    else
    {
      tempoEspera += evento.getTempoExecucao() + TEMPO_REFORCO_COLISAO;

      Evento esperaBackOff = new Evento(TipoEvento.FimBackOff, tempoEspera, estacao.getCodigo());
      listaEventos.add(esperaBackOff);

      estacao.setEsperandoBackoff(true);
    }

    estacao.incQuantidadeColisoes();
  }

  private void processaFimBackOff(Evento evento)
  {
    Estacao estacao = buscaEstacao(evento.getCodigoEstacao());

    estacao.setEsperandoBackoff(false);

    Evento novaTentativa = new Evento(TipoEvento.TentativaEnvio, evento.getTempoExecucao(), estacao.getCodigo());
    listaEventos.add(novaTentativa);
  }
}
