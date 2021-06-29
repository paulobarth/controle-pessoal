<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>
`
<div class="container">

	<h4>Lista de operações sem custo:</h4>
	<br>

	<table class="table table-striped">

		<thead>
			<tr>
			<th>ID</th>
				<th>Tipo Operação</th>
				<th>Ação</th>
				<th>Data Operação</th>
				<th>Quantidade</th>
				<th>Valor Cota</th>
				<th>Custo Operação</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${stocksOperationList}" var="stocksOperation">
				<tr>
					
					<td>${stocksOperation.id}</td>
					<td>${stocksOperation.typeOperation}</td>
					<td>${stocksOperation.codStock}</td>
					<td>${stocksOperation.datOperation}</td>
					<td>${stocksOperation.quantity}</td>
					<td>${stocksOperation.valStock}</td>
					<td>${stocksOperation.valCost}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<br>

	<form action="/controle-pessoal/stocksOperation.costsCalculation" method="post">

		<div class="row">
			<div class="col">
				<div class="col-xs-3">
					<label for="ex2">Data da Operação</label> <input
						class="form-control" id="datepickerstock" name="datOperation"
						type="text">
				</div>
			</div>
		</div>
		<br>
		<div class="row">

			<div class="col">

				<div class="col-xs-2">
					<label for="ex1">Taxa 1</label> <input class="form-control"
						name="taxa1" type="number" step="0.01" value="0">
				</div>
			</div>
		</div>
		<br>
		<div class="row">

			<div class="col">

				<div class="col-xs-2">
					<label for="ex1">Taxa 2</label> <input class="form-control"
						name="taxa2" type="number" step="0.01" value="0">
				</div>
			</div>
		</div>
		<br>
		<div class="row">

			<div class="col">

				<div class="col-xs-2">
					<label for="ex1">Taxa 3</label> <input class="form-control"
						name="taxa3" type="number" step="0.01" value="0">
				</div>
			</div>
		</div>
		<br>
		<div class="row">

			<div class="col">

				<div class="col-xs-3">
					<label for="ex1">IRRF (Apenas para Venda)</label> <input class="form-control"
						name="taxaIRRF" type="number" step="0.01" value="0">
				</div>
			</div>
		</div>
		<br>
		<div class="row">
			<div class="col">
				<div class="col-xs-3">
					<label for="ex2">Data Liquidação</label> <input
						class="form-control" id="datepickerliq" name="datSettlement"
						type="text">
				</div>
			</div>
		</div>
		<br> <input class="btn btn-success" type="submit"
			value="Aplicar">

	</form>
</div>

<%@ include file="../common/footer.jspf"%>

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
	$(function() {
		$("#datepickerstock").datepicker();
		$("#datepickerliq").datepicker();
	})
</script>