package com.deveficiente.blblioteca.emprestimo;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.util.Assert;

import com.deveficiente.blblioteca.novainstancia.Instancia;
import com.deveficiente.blblioteca.novousuario.Usuario;

@Entity
public class Emprestimo implements Comparable<Emprestimo> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	//1
	private @NotNull @Valid Usuario usuario;
	@ManyToOne
	//1
	private @NotNull @Valid Instancia instanciaSelecionada;
	private @Positive int tempo;	
	private Instant instanteDevolucao;
	private Instant instanteEmprestimo = Instant.now();;
	
	@Deprecated
	public Emprestimo() {
	}

	public Emprestimo(@NotNull @Valid Usuario usuario,
			@NotNull @Valid Instancia instanciaSelecionada, @Positive int tempo) {
				Assert.isTrue(instanciaSelecionada.aceita(usuario),"Olha, você está construindo um emprestimo com instancia que nao aceita o usuario. Será que você verificou corretamente antes?");
				this.usuario = usuario;
				this.instanciaSelecionada = instanciaSelecionada;
				this.tempo = tempo;
	}
	
	public boolean expirado() {
		return this.instanteEmprestimo
				.plus(tempo, ChronoUnit.DAYS)
				.compareTo(Instant.now()) < 0;
	}

	public Long getId() {
		Assert.state(Objects.nonNull(id), "Será que você esqueceu de persistir o emprestimo");
		return id;
	}

	public boolean foiDevolvido() {
		return Objects.nonNull(instanteDevolucao);
	}

	@Override
	public int compareTo(Emprestimo outro) {
		return this.instanteEmprestimo.compareTo(outro.instanteEmprestimo);
	}

}
