package com.santos.spring_jpa.procedures;

/**
 * Funcoes Java expostas ao H2 via CREATE ALIAS — o equivalente do H2 a uma
 * stored procedure (ele nao tem PL/SQL proprio). O teste registra o alias e
 * o repositorio chama com @Procedure.
 */
public final class H2Functions {

	private H2Functions() {
	}

	public static double plusTax(double amount) {
		return amount * 1.2;
	}
}
