package br.com.alura.loja;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
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
		String conteudo = retornaXmlDoEndPoint("/projetos/1");
		Projeto projeto = new Gson().fromJson(conteudo, Projeto.class);
		assertEquals(2014, projeto.getAnoDeInicio());
	}
	
	@Test
	public void testaQueEstejaAdicionandoCarrinho() {
		WebTarget target = criandoTarget();
		
		Projeto projeto = new Projeto();
		projeto.setAnoDeInicio(2018);
		projeto.setNome("FITFIU");
		String json = new Gson().toJson(projeto);
		
		Entity<String> entity = Entity.entity(json, MediaType.APPLICATION_JSON);
		Response response = target.path("/projetos").request().post(entity);
		assertEquals("{\"status\":\"sucess\"}", response.readEntity(String.class));
	}

	private WebTarget criandoTarget() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		return target;
	}
	
	@Test
	public void testaQueEstejaAdicionandoProjeto() {
		
	}

	@Test
	public void testaOEndPointDeProjeto() {
		String conteudo = retornaXmlDoEndPoint("/carrinhos/1");
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}

	private String retornaXmlDoEndPoint(String string) {
		WebTarget target = criandoTarget();
		return target.path(string).request().get(String.class);
	}
}
