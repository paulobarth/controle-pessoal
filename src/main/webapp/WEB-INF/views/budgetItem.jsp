<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<div class="container">

	<p>
		<strong>${budget.codBudget} - ${budget.version}</strong>
	</p>

	<%@ include file="../common/register.jspf"%>
	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form
						action="/controle-pessoal/budgetItem.save?id=${budgetItem.id}"
						method="post">

						<div class="form-group row">
							<div class="col-xs-3">
								<label for="ex1">Item</label> <input class="form-control"
									name="codItem" type="text" value="${budgetItem.codItem}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Valor</label> <input class="form-control"
									id="datepickerini" name="valItem" type="number"
									value="${budgetItem.valItem}">
							</div>

							<div class="col-xs-2">
								<label for="ex2">Grupo</label> <select class="form-control"
									name="grpItem">
									<option value="Despesa"
										${budgetItem.grpItem == 'Despesa' ? 'selected="selected"' : ''}>
										Despesa</option>
									<option value="Receita"
										${budgetItem.grpItem == 'Receita' ? 'selected="selected"' : ''}>
										Receita</option>
									<option value="Investimento"
										${budgetItem.grpItem == 'Investimento' ? 'selected="selected"' : ''}>
										Investimento</option>
								</select>
							</div>

							<div class="col-xs-2">
								<label for="ex2">Tipo</label> <select class="form-control"
									name="type">
									<option value="Conta"
										${budgetItem.type == 'Conta' ? 'selected="selected"' : ''}>
										Conta</option>
									<option value="Conta Deb Aut"
										${budgetItem.type == 'Conta Deb Aut' ? 'selected="selected"' : ''}>
										Conta Deb Aut</option>
									<option value="Controle"
										${budgetItem.type == 'Controle' ? 'selected="selected"' : ''}>
										Controle</option>
								</select>
							</div>
							<div class="col-xs-2">
								<label for="ex2">Dia Vencto</label> <input class="form-control"
									name="dayVencto" type="text" value="${budgetItem.dayVencto}">
							</div>
						</div>

						<input class="btn btn-success" type="submit" value="Salvar">
						<input class="btn btn-warning" type="reset"> <a
							class="btn btn-danger"
							style="display: ${budgetItem.id == null ? 'none' : ''}"
							href="/controle-pessoal/budgetItem.list">Cancelar</a>

					</form>
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseFilterRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/budgetItem.filter" method="post">

						<div class="col-xs-3">
							<label for="ex3">Item</label> <input class="form-control"
								name="filterItem" type="text"
								value="${filterItem}">
						</div>

						<br>
						<div class="form-group col">
							<input class="btn btn-success" type="submit" value="Filtrar">
							<input class="btn btn-warning" type="reset">
						</div>
					</form>

				</div>
			</div>
		</div>
	</div>

			
	<table class="table table-striped">

		<thead>
			<th>Item</th>
			<th>Valor</th>
			<th>Grupo</th>
			<th>Tipo</th>
			<th>Dia Vencto</th>
			<th>Action</th>
		</thead>
		<tbody>
			<c:forEach items="${budgetItemList}" var="budgetItem">
				<tr>
					<td>${budgetItem.codItem}&nbsp;&nbsp;</td>
					<td>${budgetItem.valItem}&nbsp;&nbsp;</td>
					<td>${budgetItem.grpItem}&nbsp;&nbsp;</td>
					<td>${budgetItem.type}&nbsp;&nbsp;</td>
					<td>${budgetItem.dayVencto}&nbsp;&nbsp;</td>

					<td><a class="btn btn-primary"
						href="/controle-pessoal/budgetItem.update?id=${budgetItem.id}">Alterar</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/budgetItem.delete?id=${budgetItem.id}">Delete</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<%@ include file="../common/footer.jspf"%>

<script>
	function bodyLoadFunction() {
		filterLoadPageFunction(${filterCollapsed});
		registerLoadPageFunction(${budgetItem.id});
	}
</script>