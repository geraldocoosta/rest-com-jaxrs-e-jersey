package br.com.alura.loja;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Servidor {

	public static void main(String[] args) throws IOException {
		HttpServer server = startaServidor();
		System.out.println("Servidor rodando");
		System.in.read();
		server.stop();
	}

	public static HttpServer startaServidor() {
		/* Para startar o server, � necess�rio essas 3 linhas */
		/*
		 * Resource config, configura��o do recurso, uri � a uri
		 * 
		 * Para esse curso, usamos a implementa��o Grizzly de um servidor http, que
		 * suporta servlet io e jax-rs
		 */
		ResourceConfig config = new ResourceConfig().packages("br.com.alura.loja");
		URI uri = URI.create("http://localhost:8080");
		return GrizzlyHttpServerFactory.createHttpServer(uri, config);
	}
}

