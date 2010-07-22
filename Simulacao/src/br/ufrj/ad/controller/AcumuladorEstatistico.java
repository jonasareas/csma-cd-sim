package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrj.ad.model.Estacao;

public class AcumuladorEstatistico
{
  private static AcumuladorEstatistico instancia = null; 
 
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tapi; // Tempo de acesso de um quadro na estacao i  
  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tami; // Tempo de acesso de uma mensagem na estacao i
  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> ncmi; // Numero de colisoes por quadro na estacao i
  
  private ArrayList<String> mensagens = new ArrayList<String>();    // lista de logs a serem impressos na tela.
 
  /*
   * Retorna a instancia da Classe Acumulador Estatistico
   */
  public static AcumuladorEstatistico getInstancia()
  {
    if(instancia == null)
      instancia = new AcumuladorEstatistico();
    return instancia;
  }
  
  /*
   * Construtor da Classe: Responsavel por inicial o Acumulador Estatistico, inicializando o tapi, tami e ncmi
   */
  public AcumuladorEstatistico()
  {
    tapi = new HashMap<Integer, EstruturaEstatistica>();
    tami = new HashMap<Integer, EstruturaEstatistica>();
    ncmi = new HashMap<Integer, EstruturaEstatistica>();
  }
  
  /*
   * Metodo que adiciona uma nova estacao nas estatisticas
   */
  public void addEstacao(int codigoEstacao)
  {
    tapi.put(codigoEstacao, new EstruturaEstatistica());
    tami.put(codigoEstacao, new EstruturaEstatistica());
    ncmi.put(codigoEstacao, new EstruturaEstatistica());
  }
  
  /*
   * Metodo que chama o metodo interno fimRodada de cada Estacao 
   */
  public void fimRodada()
  {
     for (int codigoEstacao : tapi.keySet())
     {
       tapi.get(codigoEstacao).fimRodada();
       tami.get(codigoEstacao).fimRodada();
       ncmi.get(codigoEstacao).fimRodada();
     }
  }
 
  /*
   * Metodo que recolhe as estatisticas de cada Estacao e adiciona na estrutura que será escrita na tela.
   */
  public void extraiEstatistica(List<Estacao> listaEstacoes, double tempoSimulacao)
  {
    Double utilizacao = 0.0;
    
    for (Estacao estacao : listaEstacoes)
    {
      mensagens.add("\nESTATISTICAS DA ESTACAO " + estacao.getCodigo() + "\n");
      
      mensagens.add("E[TAp] da estacao " + estacao.getCodigo() + " = " + tapi.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("E[TAm] da estacao " + estacao.getCodigo() + " = " + tami.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("E[NCm] da estacao " + estacao.getCodigo() + " = " + ncmi.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("Vazao da estacao " + estacao.getCodigo() + " = " + estacao.getQuadrosTransmitidos()/tempoSimulacao + "\n");

      if (estacao.isAnaliseUtilizacaoEthernet())
        utilizacao = estacao.getTempoUtilizacaoEthernet()/tempoSimulacao;
      
    }
    mensagens.add("\nESTATISTICAS DA ETHERNET\n");
    mensagens.add("Utilizacao = " + utilizacao + " ( " + Math.floor((utilizacao * 100) * 10000)/10000  + " % do tempo total de simulacao)\n");
    
  }
  
  /*
   * Metodo que adiciona uma nova amostra Tap
   */
  public void novaAmostraTap(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tapi.get(codigoEstacao);
    estrutura.novaAmostra(amostra);
  }
  
  /*
   * Metodo que adiciona uma nova amostra Tam
   */
  public void novaAmostraTam(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tami.get(codigoEstacao);
    estrutura.novaAmostra(amostra);    
  }
  
  /*
   * Metodo que adiciona uma nova amostra Ncm
   */
  public void novaAmostraNcm(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = ncmi.get(codigoEstacao);
    estrutura.novaAmostra(amostra);    
  }
  
  public ArrayList<String> getMensagens()
  {
    return mensagens;
  }

  public void setMensagens(ArrayList<String> mensagens)
  {
    this.mensagens = mensagens;
  }
  
  public void clearMensagens()
  {
    this.mensagens.clear();
  }

  // --------------------------------------------------------------- x -----------------------------------------------------------------------
  /*
   * Classe Interna: Define a estrutura das estatisticas
   */
  private class EstruturaEstatistica
  {
    double acumuladorFinal;
    double acumuladorFinal2;
    int numeroRodadas;
    
    double acumuladorRodada;
    int numeroAmostrasRodada;
    
    /*
     * Construtor da Classe Interna: Nesta classe serao calculados os valores de cada uma das grandezas
     */
    public EstruturaEstatistica() 
    {
      acumuladorFinal = 0.0;
      acumuladorFinal2 = 0.0;
      numeroRodadas = 0;
      acumuladorRodada = 0.0;
      numeroAmostrasRodada = 0;
    }
    
    /*
     * Metodo que adiciona uma nova amostra ao valor já acumulado
     */
    public void novaAmostra(Double amostra)
    {
      numeroAmostrasRodada++;
      acumuladorRodada += amostra;
    }
    
    /*
     * Metodo que finaliza uma rodada fazendo os devidos calculos
     */
    public void fimRodada()
    {
      if (numeroAmostrasRodada > 0)
      {
        acumuladorFinal +=  acumuladorRodada/numeroAmostrasRodada;
        acumuladorFinal2 += (acumuladorRodada/numeroAmostrasRodada)*(acumuladorRodada/numeroAmostrasRodada);
        numeroRodadas++;
        
        acumuladorRodada = 0.0;
        numeroAmostrasRodada = 0;
      }
    }
    
    /*
     * Metodo que retorna a media dos valores da grandeza acumulada
     */    
    public double getMedia()
    {
      if(numeroRodadas>0)
        return  acumuladorFinal/numeroRodadas;
      else
        return -1;
    }
    
    /*
     * Metodo que retorna a variancia dos valores da grandeza acumulada. Foi utilizado apenas para obtencao do intervalo de confianca.
     */    
    @SuppressWarnings("unused")
    public double getVariancia()
    {
      if(numeroRodadas > 1)
      {
        return acumuladorFinal2/(numeroRodadas-1) - (acumuladorFinal*acumuladorFinal)/(numeroRodadas*(numeroRodadas - 1));
      }else
        return -1;
    }
  }
}
