package br.com.alura.loja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
	private Client client;

	@Before
	public void before() {
		server = Servidor.startaServidor();
		client = ClientBuilder.newClient();
		;
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

		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(156l, "Patinete", 25.5, 1));
		carrinho.setCidade("Gama");
		carrinho.setRua("Uma rua");
		String xml = carrinho.toXML();

		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos").request().post(entity);
		assertEquals(201, response.getStatus());

		String conteudo = client.target(response.getLocation()).request().get(String.class);
		Carrinho carrinhoConfere = (Carrinho) new XStream().fromXML(conteudo);
		assertTrue(carrinhoConfere.getRua().equals(carrinho.getRua()));
	}

	private WebTarget criandoTarget() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		return target;
	}

	@Test
	public void testaQueEstejaAdicionandoProjeto() {
		WebTarget target = criandoTarget();

		Projeto projeto = new Projeto();
		projeto.setAnoDeInicio(2018);
		projeto.setNome("FITFIU");
		String json = new Gson().toJson(projeto);

		Entity<String> entity = Entity.entity(json, MediaType.APPLICATION_JSON);
		Response response = target.path("/projetos").request().post(entity);
		assertEquals(201, response.getStatus());

		String jsonASerComparado = client.target(response.getLocation()).request().get(String.class);
		Projeto fromJson = new Gson().fromJson(jsonASerComparado, Projeto.class);
		assertTrue(projeto.getNome().equals(fromJson.getNome()));
	}

	@Test
	public void testaOEndPointDeProjeto() {
		String conteudo = retornaXmlDoEndPoint("/carrinhos/1");
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}

	@Test
	public void testaOMetodoDeleteDoProdutoDoCarrinho() {
		Response delete = criandoTarget().path("/carrinhos/1/produtos/6237").request().delete();
		assertEquals(200, delete.getStatus());

		String carrinhoSemProduto = criandoTarget().path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(carrinhoSemProduto);
		List<Produto> produtos = carrinho.getProdutos();
		assertEquals(1, produtos.size());
		System.out.println(carrinho);
	}

	private String retornaXmlDoEndPoint(String string) {
		WebTarget target = criandoTarget();
		return target.path(string).request().get(String.class);
	}
}
