package br.ufrj.ad.controller;

import java.util.ArrayList;

public class AcumuladorEstatistico
{
  private static final double VAR_MAX_TAP = 1000.000000001;
  private static final double VAR_MAX_TAM = 1000.000001;
  private static final double VAR_MAX_NCM = 1000.00000001;
  
  private static AcumuladorEstatistico instancia = null; 

  private ArrayList<Double> tapi;
  private ArrayList<Double> tap;
    
  private ArrayList<Double> tami;
  private ArrayList<Double> tam;
  
  private ArrayList<Double> ncmi;
  private ArrayList<Double> ncm;
  
  private ArrayList<Double> utilizacaoEthernet;
  
  private ArrayList< ArrayList<Double> > vazao; 
  
  private boolean transiente;
  
  
  public static AcumuladorEstatistico getInstancia()
  {
    return instancia;
  }
 
  public static AcumuladorEstatistico getInstancia(int numeroEstacoes)
  {
    if(instancia == null)
      instancia = new AcumuladorEstatistico(numeroEstacoes);
    
    return instancia;
  }
  
  private AcumuladorEstatistico() {}
  
  private AcumuladorEstatistico(int numeroEstacoes)
  {
    vazao = new ArrayList< ArrayList<Double> > ();
    for(int i = 0; i < numeroEstacoes; i++)
    {
       vazao.add(new ArrayList<Double>());
    }
    
    this.transiente = true;
    
    tapi = new ArrayList<Double>();
    tami = new ArrayList<Double>();
    ncmi = new ArrayList<Double>();
    
    tap = new ArrayList<Double>();
    tam = new ArrayList<Double>();
    ncm = new ArrayList<Double>();
    
    utilizacaoEthernet = new ArrayList<Double>();   
  }
  
  private void reinicia()
  {
    tap.clear();
    tam.clear();
    ncm.clear();
    
    utilizacaoEthernet.clear();
    
    
    for(ArrayList<Double> a : vazao)
    {
      a.clear();
    }
    
  }
  
  public void fimRodada(double utilizacao, ArrayList<Double> vazao)
  {
    if(transiente)
    {
      if(!verificaTransiencia())
      {
        transiente = false;
        System.out.println("Fim Fase Transiente");
      }
      reinicia();
      return;
    }
    
    
    if(tap.size()>0)
    {
      tapi.add(media(tap));
      tap.clear();
    }
    
    
    if(tam.size()>0)
    {
      tami.add(media(tam));
      tam.clear();
    }
    
    if(ncm.size()>0)
    {
      ncmi.add(media(ncm));
      ncm.clear();
    }
         
    utilizacaoEthernet.add(utilizacao);
    
    for(int i = 0; i < vazao.size(); i++)
    {
      this.vazao.get(i).add(vazao.get(i));
      
    }
  }
  
  public void addTap(double val)
  {
    tap.add(val);
  }
  
  public void addTam(double val)
  {
    tam.add(val);
  }
  
  public void addNcm(double val)
  {
    ncm.add(val);
  }
  
  private boolean verificaTransiencia()
  {
    double varTap = var(tap);
    double varTam = var(tam);
    double varNcm = var(ncm);
    
    /*
    System.out.println("Analize Transiencia:");
    System.out.println("    Var[Tap] = " + varTap + " - Valor Maximo = " + VAR_MAX_TAP);
    System.out.println("    Var[Tam] = " + varTam + " - Valor Maximo = " + VAR_MAX_TAM);
    System.out.println("    Var[Ncm] = " + varNcm + " - Valor Maximo = " + VAR_MAX_NCM);
 //   */
    
    if(varTap > VAR_MAX_TAP || varTam > VAR_MAX_TAM || varNcm > VAR_MAX_NCM)
      return true;
    
    return false;
  }
  
  private double var(ArrayList<Double> list)
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
  
  public boolean isTransiente()
  {
    return transiente;
  }
  
  public void extraiEstatistica()
  {
    System.out.println("Dados de Tapi:");
    System.out.println("   N        = " + tapi.size());
    System.out.println("   E[Tap]   = " + media(tapi));
    System.out.println("   VAR[Tap] = " + var(tapi));
    System.out.println("");
    
    System.out.println("Dados de Tami:");
    System.out.println("   N        = " + tami.size());
    System.out.println("   E[Tam]   = " + media(tami));
    System.out.println("   VAR[Tam] = " + var(tami));
    System.out.println("");
    
    System.out.println("Dados de Ncmi:");
    System.out.println("   N        = " + ncmi.size());
    System.out.println("   E[Ncmi]   = " + media(ncmi));
    System.out.println("   VAR[Ncmi] = " + var(ncmi));
    System.out.println("");
  }
}
