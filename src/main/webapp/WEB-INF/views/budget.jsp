<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<div class="container">

	<%@ include file="../common/register.jspf" %>
	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/budget.save?id=${budget.id}"
						method="post">

						<div class="form-group row">
							<div class="col-xs-3">
								<label for="ex1">Código</label> <input class="form-control"
									name="codBudget" type="text" value="${budget.codBudget}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Data Inicial</label> <input
									class="form-control" id="datepickerini" name="datIni"
									type="text" value="${budget.datIni}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Data Final</label> <input class="form-control"
									id="datepickerend" name="datEnd" type="text"
									value="${budget.datEnd}">
							</div>
							<div class="col-xs-1">
								<label for="ex1">Versão</label> <input class="form-control"
									name="version" type="text" value="${budget.version}">
							</div>
						</div>

						<input class="btn btn-success" type="submit" value="Salvar">
						<input class="btn btn-warning" type="reset"> <a
							class="btn btn-danger"
							style="display: ${budget.id == null ? 'none' : ''}"
							href="/controle-pessoal/budget.list">Cancelar</a>


					</form>

				</div>
			</div>
		</div>
	</div>

	<table class="table table-striped">

		<thead>
			<tr>
				<th>Código</th>
				<th>Versão</th>
				<th>Data Inicial</th>
				<th>Data Final</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${budgetList}" var="budget">
				<tr>
					<td><a href="/controle-pessoal/budget.item?id=${budget.id}">
							${budget.codBudget}</a></td>
					<td>${budget.version}</td>
					<td>${budget.datIni}</td>
					<td>${budget.datEnd}</td>

					<td><a class="btn btn-primary"
						href="/controle-pessoal/budget.update?id=${budget.id}">Alterar</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/budget.delete?id=${budget.id}">Delete</a>
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
		$("#datepickerini").datepicker();
		$("#datepickerend").datepicker();
	});
	function bodyLoadFunction() {
		registerLoadPageFunction(${budget.id});
	}
</script>