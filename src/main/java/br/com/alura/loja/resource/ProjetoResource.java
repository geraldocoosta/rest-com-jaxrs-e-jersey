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

/*Essa classe representa a parte al�m do dominio da aplica��o
 * referente ao /projetos da uri localhost:8080/projetos*/

/*Esse path que diz que ao acessar a uri projeto, algo ser� 
 * retornado, ou persistido, sei l�*/
@Path("projetos")
public class ProjetoResource {

	/*
	 * Explica��o rapida sobre Path, GET, Produces .
	 * 
	 * Path quer dizer que teremos um caminho na url variavel que corresponde um
	 * parametro, uma identifica��o de um recurso do carrinho
	 * 
	 * @GET fala o m�todo que estamos utilizando
	 * 
	 * @Produces, o que aquele m�todo produz, XML, JSON?
	 */
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String busca(@PathParam("id") long id) {
		/*
		 * Essa anota��o esquisita dentro do parametro do m�todo quer dizer
		 * "pega aquele parametro que est� no caminho, na url, aquele com nome id e joga aqui"
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
