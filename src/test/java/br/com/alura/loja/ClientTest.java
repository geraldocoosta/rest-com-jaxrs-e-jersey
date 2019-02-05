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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;

public class ClientTest {

	private HttpServer server;
	private Client client;

	@Before
	public void before() {
		server = Servidor.startaServidor();
		/* Uma aula pra essas 3 linhas de código, xD dms */
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		client = ClientBuilder.newClient(config);
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

		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos").request().post(entity);
		assertEquals(201, response.getStatus());

		Carrinho carrinhoConfere = client.target(response.getLocation()).request().get(Carrinho.class);
		assertEquals(carrinho.getRua(), carrinhoConfere.getRua());
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
	public void testaAlteracaoDaQtdDoProdutoDoCarrinho() {
		WebTarget target = criandoTarget();

		Carrinho carrinho = target.path("carrinhos/1").request().get(Carrinho.class);

		Produto produto = carrinho.getProdutos().get(0);
		produto.setQuantidade(5);

		Entity<Produto> entity = Entity.entity(produto, MediaType.APPLICATION_XML);
		Response put = target.path("carrinhos/1/produtos/" + produto.getId() + "/quantidade").request().put(entity);
		assertEquals(200, put.getStatus());

		carrinho = target.path("carrinhos/1").request().get(Carrinho.class);
		produto = carrinho.getProdutos().get(0);
		assertEquals(5, produto.getQuantidade());
	}

	@Test
	public void testaOEndPointDeProjeto() {
		Carrinho carrinho = criandoTarget().path("/carrinhos/1").request().get(Carrinho.class);
		assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}

	@Test
	public void testandoOMetodoDeleteDosProjetos() {
		Response delete = criandoTarget().path("projetos/1").request().delete();
		assertEquals(200, delete.getStatus());
	}

	@Test
	public void testaOMetodoDeleteDoProdutoDoCarrinho() {
		Response delete = criandoTarget().path("/carrinhos/1/produtos/6237").request().delete();
		assertEquals(200, delete.getStatus());

		Carrinho carrinhoSemProduto = criandoTarget().path("/carrinhos/1").request().get(Carrinho.class);
		List<Produto> produtos = carrinhoSemProduto.getProdutos();
		assertEquals(1, produtos.size());
	}

	private String retornaXmlDoEndPoint(String string) {
		WebTarget target = criandoTarget();
		return target.path(string).request().get(String.class);
	}
}
