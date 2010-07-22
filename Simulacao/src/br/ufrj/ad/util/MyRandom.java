package br.ufrj.ad.util;

import java.util.Random;

public class MyRandom {
	
	private static Random gerador;             // Gerador do numero aleatorio
	
	private static MyRandom instancia = null;  // Instancia da classe MyRandom
	
	
	/*
	 * Construtor da Classe: Responsavel por inicializar o gerador de numeros aleatorios.
	 */
	private MyRandom(long seed)
	{
		gerador = new Random(seed);
	}
	
	/*
     * Metodo que inicializa uma instancia da Classe MyRandom com uma semente (seed) pre-determinada
     */
    public static MyRandom setSeed(long seed)  
    {
        if(instancia == null)
            instancia = new MyRandom(seed);
        return instancia;
    }
	
    /*
     * Metodo que o retorna o proximo numero aleatorio
     */
	public static double rand()
	{
		return gerador.nextDouble();
	}
}
