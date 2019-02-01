package br.com.alura.loja.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import br.com.alura.loja.dao.ProjetoDAO;
import br.com.alura.loja.modelo.Projeto;

/*Essa classe representa a parte além do dominio da aplicação
 * referente ao /projetos da uri localhost:8080/projetos*/

/*Esse path que diz que ao acessar a uri projeto, algo será 
 * retornado, ou persistido, sei lá*/
@Path("projetos")
public class ProjetoResource {

	/*
	 * Explicação rapida sobre Path, GET, Produces .
	 * 
	 * Path quer dizer que teremos um caminho na url variavel que corresponde um
	 * parametro, uma identificação de um recurso do carrinho
	 * 
	 * @GET fala o método que estamos utilizando
	 * 
	 * @Produces, o que aquele método produz, XML, JSON?
	 */
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String busca(@PathParam("id") long id) {
		/*
		 * Essa anotação esquisita dentro do parametro do método quer dizer
		 * "pega aquele parametro que está no caminho, na url, aquele com nome id e joga aqui"
		 */
		return new ProjetoDAO().busca(id).toJson();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response adiciona(String json) {
		Projeto project = new Gson().fromJson(json, Projeto.class);
		new ProjetoDAO().adiciona(project);
		URI uri = URI.create("http://localhost:8080/projetos/" + project.getId());
		return Response.created(uri).build();
	}
}
