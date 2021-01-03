package com.deveficiente.blblioteca.novolivro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.ISBN.Type;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import com.deveficiente.blblioteca.emprestimo.Emprestimo;
import com.deveficiente.blblioteca.novainstancia.Instancia;
import com.deveficiente.blblioteca.novousuario.Usuario;

@Entity
//6
public class Livro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private @NotBlank String titulo;
	private @NotNull @Positive BigDecimal preco;
	@Column(unique = true)
	private @NotBlank @ISBN(type = Type.ISBN_10) String isbn;
	@OneToMany(mappedBy = "livro")
	//1
	private List<Instancia> instancias = new ArrayList<>();

	@Deprecated
	public Livro() {

	}

	public Livro(@NotBlank String titulo, @NotNull @Positive BigDecimal preco,
			@NotBlank @ISBN(type = Type.ISBN_10) String isbn) {
		this.titulo = titulo;
		this.preco = preco;
		this.isbn = isbn;
	}

	public Long getId() {
		Assert.state(id != null,
				"Não rola chamar o getId do livro com o id nulo. Será que você já persistiu?");
		return id;
	}

	//2
	//1
	public boolean aceitaSerEmprestado(Usuario usuario) {
		//1
		return instancias.stream()
				.anyMatch(instancia -> instancia.aceita(usuario));
	}

	public Emprestimo criaEmprestimo(@NotNull @Valid Usuario usuario,
			@Positive int tempo) {
		Assert.state(this.aceitaSerEmprestado(usuario),"Você está gerar um emprestimo de um livro que não aceita ser emprestado para o usuario "+usuario.getId());
		Assert.state(this.estaDisponivelParaEmprestimo(),"O livro precisa estar disponível para empréstimo para ser emprestado");
		Assert.state(usuario.aindaPodeSolicitarEmprestimo(),"Este usuário já está no limite de empréstimos");
		
		//1
		Instancia instanciaSelecionada = instancias.stream()
			.filter(instancia -> instancia.disponivel(usuario))
			.findFirst().get();
		
		Assert.state(instanciaSelecionada.disponivelParaEmprestimo(),"Olha, o seu código não deveria tentar criar um emprestimo para uma instancia que não está disponível");
		
		//1
		return instanciaSelecionada.criaEmprestimo(usuario,tempo);
	}

	public boolean estaDisponivelParaEmprestimo() {//1
		return instancias.stream().anyMatch(instancia -> instancia.disponivelParaEmprestimo());
	}

}
