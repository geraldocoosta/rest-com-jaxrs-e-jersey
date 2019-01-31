package br.com.alura.loja;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.Test;

public class ClientTest {

	@Test
	public void testaQueAConexaoComOServidorFunciona() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://www.mocky.io/");
		String conteudo = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
		System.out.println(conteudo);
		assertTrue(conteudo.contains("Rua Vergueiro 3185"));
	}
}
