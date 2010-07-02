package br.ufrj.ad.util;

import java.util.Random;

public class MyRandom {
	
	private static Random gerador;
	
	private static MyRandom instancia = null;
	
	public static MyRandom getMyRandom(long seed)
	{
		if(instancia == null)
			instancia = new MyRandom(seed);
		return instancia;
	}
	
	private MyRandom(long seed)
	{
		gerador = new Random(seed);
	}
	
	public static double next()
	{
		return gerador.nextDouble();
	}
}
