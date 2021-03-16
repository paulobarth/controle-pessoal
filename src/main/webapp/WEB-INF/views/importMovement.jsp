<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>
`
<div class="container">

	<div class="alert alert-warning"
		role="alert">${importResultMessage}</div>

	<form action="/controle-pessoal/importMovement.import" method="post">

		<div class="row">

			<div class="col">

				<div class="col-md-8">
					<div class="custom-file">
						<label for="ex1">Arquivo</label> <input type="file"
							class="form-control" name="selectedFile" value="${selectedFile}">

					</div>
				</div>
			</div>
		</div>
		<br>
		<div class="row">
			<div class="col">

				<div class="col-xs-6">
					<label for="ex6">Origem</label> <select class="form-control"
						name="selectedOrigin">
						<c:forEach items="${originList}" var="origin">
							<option value="${origin}"
								${origin == selectedOrigin ? 'selected="selected"' : ''}>
								${origin}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="col">
				<div class="col-xs-3">
					<label for="ex2">Data Financeira (Cartão Crédito)</label> <input
						class="form-control" id="datepickerfin" name="datFinancial"
						type="text" value="${datFinancial}">
				</div>
			</div>
		</div>

		<br> <input class="btn btn-success" type="submit"
			value="Importar">

	</form>

	<table class="table table-striped">
		<thead>
			<tr>
				<th>Data Movimento</th>
				<th>Descricao</th>
				<th>Documento</th>
				<th>Valor</th>
			</tr>
		</thead>

		<tbody>
			<tr>
				<td>datMovement</td>
				<td>description</td>
				<td>documentNumber</td>
				<td>valMovement</td>
			</tr>
			<tr>
			</tr>
		</tbody>
	</table>

	<small id="emailHelp" class="form-text text-muted">Este layout
		deve serguir a seguinte sequência, mantendo a nomenclatura devida.</small>

</div>


<%@ include file="../common/footer.jspf"%>

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
	$(function() {
		$("#datepickerfin").datepicker();
	})
</script>