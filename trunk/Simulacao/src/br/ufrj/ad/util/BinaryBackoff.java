package br.ufrj.ad.util;


public class BinaryBackoff
{
  private static final int SLOT_TEMPO = 51200; // em nanosegundos
  private static final int COLISOES_MAXIMO = 10;
  private static final int COLISOES_DESCARTE = 16;
  
  // Em nanosegundos
  public static double geraAtraso(int numeroColisoes)
  {
    if (numeroColisoes > COLISOES_DESCARTE)
    {
      return -1;
    } else {
      int k = Math.min(numeroColisoes, COLISOES_MAXIMO);
      return (int) (MyRandom.next() * (Math.pow(2, k))) * (SLOT_TEMPO); 
      // O fato de truncarmos para inteiro elimina a necessidade de subtrairmos 1 do resultado
    }
  }

}
