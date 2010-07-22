package br.ufrj.ad.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrj.ad.model.Estacao;

public class AcumuladorEstatistico
{
  private static AcumuladorEstatistico instancia = null; 
 
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tapi;  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> tami;  
  public HashMap<Integer, AcumuladorEstatistico.EstruturaEstatistica> ncmi;  
  
  private ArrayList<String> mensagens = new ArrayList<String>();
 
  public static AcumuladorEstatistico getInstancia()
  {
    if(instancia == null)
      instancia = new AcumuladorEstatistico();
    return instancia;
  }
   
  public AcumuladorEstatistico()
  {
    tapi = new HashMap<Integer, EstruturaEstatistica>();
    tami = new HashMap<Integer, EstruturaEstatistica>();
    ncmi = new HashMap<Integer, EstruturaEstatistica>();
  }
  
  public void addEstacao(int codigoEstacao)
  {
    tapi.put(codigoEstacao, new EstruturaEstatistica());
    tami.put(codigoEstacao, new EstruturaEstatistica());
    ncmi.put(codigoEstacao, new EstruturaEstatistica());
  }
  
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
  private double variancia(ArrayList<Double> list)
  {
    if(list.size() <= 1)
      return -1;
    
    double med = media(list);
    double sum = 0.0;
    for(Double d : list)
    {
      sum += (d - med)*(d - med); 
    }
    
    return sum/(list.size()-1);
  }
*/

/*
  private double media(ArrayList<Double> list)
  {
    if(list.size() <= 0 )
      return -1;
    
    double sum = 0.0;
    for(Double d: list)
    {
      sum += d;
    }
    return sum/list.size();
  }
*/
  
  public void extraiEstatistica(List<Estacao> listaEstacoes, double tempoSimulacao)
  {
    Double utilizacao = 0.0;
    
    for (Estacao estacao : listaEstacoes)
    {
      //mensagens.add("\nESTATISTICAS DA ESTACAO " + estacao.getCodigo() + "\n");
      /*
      mensagens.add("E[TAp] da estacao " + estacao.getCodigo() + " = " + tapi.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("E[TAm] da estacao " + estacao.getCodigo() + " = " + tami.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("E[NCm] da estacao " + estacao.getCodigo() + " = " + ncmi.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add("Vazao da estacao " + estacao.getCodigo() + " = " + estacao.getQuadrosTransmitidos()/tempoSimulacao + "\n");
      */
      mensagens.add(tapi.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add(tami.get(estacao.getCodigo()).getMedia() + "\n");
      mensagens.add(ncmi.get(estacao.getCodigo()).getMedia() + "\n");
      if (estacao.isAnaliseUtilizacaoEthernet())
        utilizacao = estacao.getTempoUtilizacaoEthernet()/tempoSimulacao;
      /*
      System.out.println("VAR(Tap) da estacao " + estacao.getCodigo() + " = " + tapi.get(estacao.getCodigo()).getVariancia() + "\n");
      System.out.println("VAR(Tam) da estacao " + estacao.getCodigo() + " = " + tami.get(estacao.getCodigo()).getVariancia() + "\n");
      System.out.println("VAR(Ncm) da estacao " + estacao.getCodigo() + " = " + ncmi.get(estacao.getCodigo()).getVariancia() + "\n");
      */
      System.out.println(tapi.get(estacao.getCodigo()).getVariancia());
      System.out.println(tami.get(estacao.getCodigo()).getVariancia());
      System.out.println(ncmi.get(estacao.getCodigo()).getVariancia());
      
    }
    for (Estacao estacao : listaEstacoes)
    {
      mensagens.add("Vazao da estacao " + estacao.getCodigo() + " = " + estacao.getQuadrosTransmitidos()/tempoSimulacao + "\n");
    }
    
    mensagens.add("\nESTATISTICAS DA ETHERNET\n");
    mensagens.add("Utilizacao = " + utilizacao + " ( " + Math.floor((utilizacao * 100) * 10000)/10000  + " % do tempo total de simulacao)\n");
    
  }
  
  public void novaAmostraTap(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tapi.get(codigoEstacao);
    estrutura.novaAmostra(amostra);
  }
  
  public void novaAmostraTam(double amostra, int codigoEstacao)
  {
    EstruturaEstatistica estrutura = tami.get(codigoEstacao);
    estrutura.novaAmostra(amostra);    
  }
  
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

  private class EstruturaEstatistica
  {
    //ArrayList<Double> valorPorRodada;
    double acumuladorFinal;
    double acumuladorFinal2;
    int numeroRodadas;
    
    double acumuladorRodada;
    int numeroAmostrasRodada;
    
    public EstruturaEstatistica() {
      //valorPorRodada = new ArrayList<Double>();
      
      acumuladorFinal = 0.0;
      acumuladorFinal2 = 0.0;
      numeroRodadas = 0;
      acumuladorRodada = 0.0;
      numeroAmostrasRodada = 0;
    }
    
    public void novaAmostra(Double amostra)
    {
      numeroAmostrasRodada++;
      acumuladorRodada += amostra;
    }
    
    public void fimRodada()
    {
      if (numeroAmostrasRodada > 0)
      {
        //valorPorRodada.add(acumuladorRodada/numeroAmostrasRodada);
        acumuladorFinal +=  acumuladorRodada/numeroAmostrasRodada;
        acumuladorFinal2 += (acumuladorRodada/numeroAmostrasRodada)*(acumuladorRodada/numeroAmostrasRodada);
        numeroRodadas++;
        
        acumuladorRodada = 0.0;
        numeroAmostrasRodada = 0;
      }
    }
    
    public double getMedia()
    {
      if(numeroRodadas>0)
        return  acumuladorFinal/numeroRodadas;
      else
        return -1;
    }
    
///*
    public double getVariancia()
    {
      if(numeroRodadas > 1)
      {
        return acumuladorFinal2/(numeroRodadas-1) - (acumuladorFinal*acumuladorFinal)/(numeroRodadas*(numeroRodadas - 1));
      }else
        return -1;
    }
//*/
    
/*
    public ArrayList<Double> getValorPorRodada()
    {
      return valorPorRodada;
    }
 */
  }
}
