package br.com.alura.loja;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Projeto;

public class ClientTest {

	private HttpServer server;

	@Before
	public void before() {
		server = Servidor.startaServidor();
	}

	@After
	public void after() {
		server.stop();
	}

	@Test
	public void testaQueAConexaoComOServidorFunciona() {
		String conteudo = retornaXmlDoEndPoint("/projetos");
		Projeto projeto = (Projeto) new XStream().fromXML(conteudo);
		assertEquals(2014, projeto.getAnoDeInicio());
	}

	@Test
	public void testaOEndPointDeProjeto() {
		String conteudo = retornaXmlDoEndPoint("/carrinhos");
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}

	private String retornaXmlDoEndPoint(String string) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		return target.path(string).request().get(String.class);
	}
}
