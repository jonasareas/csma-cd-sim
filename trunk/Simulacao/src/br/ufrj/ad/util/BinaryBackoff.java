package br.ufrj.ad.util;

public class BinaryBackoff
{
  private static final double SLOT_TEMPO        = 0.0512; // em milessegundos
  private static final int    COLISOES_MAXIMO   = 10;
  private static final int    COLISOES_DESCARTE = 16;

  /*
   * Metodo responsavel por gerar o atraso em milessegundos atraves do algoritmo de BinaryBackoff
   */
  public static double geraAtraso(int numeroColisoes)
  {
    if (numeroColisoes > COLISOES_DESCARTE)
    {
      return -1;
    }
    else
    {
      int k = Math.min(numeroColisoes, COLISOES_MAXIMO);
      double tempo = (int) (MyRandom.rand() * (Math.pow(2, k))) * (SLOT_TEMPO);
      return Math.floor(tempo * 10000)/10000;
    }
  }

}
