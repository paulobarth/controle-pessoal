<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<div class="container">

	<%@ include file="../common/register.jspf"%>
	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form
						action="/controle-pessoal/stocksOperation.save?id=${stocksOperation.id}"
						method="post">

						<div class="form-group row">

							<div class="col-xs-2">
								<label for="ex2">Tipo Opera��o</label> <select
									class="form-control" name="typeOperation">
									<option value=""
										${stocksOperation.typeOperation == '' ? 'selected="selected"' : ''}>
									</option>
									<option value="Compra"
										${stocksOperation.typeOperation == 'Compra' ? 'selected="selected"' : ''}>
										Compra</option>
									<option value="Venda"
										${stocksOperation.typeOperation == 'Venda' ? 'selected="selected"' : ''}>
										Venda</option>
								</select>
							</div>

							<div class="col-xs-1">
								<label for="ex1">A��o</label> <input class="form-control"
									name="codStock" type="text" value="${stocksOperation.codStock}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Data Opera��o</label> <input
									class="form-control" id="datepickeroper" name="datOperation"
									type="text" value="${stocksOperation.datOperation}">
							</div>
							<div class="col-xs-2">
								<label for="ex1">Quantidade</label> <input class="form-control"
									name="quantity" type="number"
									value="${stocksOperation.quantity}">
							</div>
							<div class="col-xs-2">
								<label for="ex1">Valor A��o</label> <input class="form-control"
									name="valStock" type="number" step="0.01"
									value="${stocksOperation.valStock}">
							</div>
							<div class="col-xs-2">
								<label for="ex1">Valor Total Custo</label> <input
									class="form-control" name="valCost" type="number" step="0.01"
									value="${stocksOperation.valCost}">
							</div>

						</div>

						<input class="btn btn-success" type="submit" value="Salvar">
						<input class="btn btn-warning" type="reset"> <a
							class="btn btn-danger"
							style="display: ${stocksOperation.id == null ? 'none' : ''}"
							href="/controle-pessoal/stocksOperation.list">Cancelar</a>


					</form>

				</div>
			</div>
		</div>
	</div>

	<table class="table table-striped">

		<thead>
			<tr>
			<th>ID</th>
				<th>Tipo Opera��o</th>
				<th>A��o</th>
				<th>Data Opera��o</th>
				<th>Quantidade</th>
				<th>Valor Cota</th>
				<th>Custo Opera��o</th>
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

					<td><a class="btn btn-primary"
						href="/controle-pessoal/stocksOperation.update?id=${stocksOperation.id}">Alterar</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/stocksOperation.delete?id=${stocksOperation.id}">Delete</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="../common/footer.jspf"%>


<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
	$(function() {
		$("#datepickeroper").datepicker();
	});
	function bodyLoadFunction() {
		registerLoadPageFunction(${stocksOperation.id});
	}
</script>