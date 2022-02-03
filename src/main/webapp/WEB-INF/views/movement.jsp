
<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<style>
.tableFixHead {
	overflow-y: auto;
	height: 50px;
}

.tableFixHead thead th {
	position: sticky;
	top: 0;
}

table {
	border-collapse: collapse;
	width: 100%;
}

th, td {
	padding: 8px 16px;
}

th {
	background: #f2f2f2;
}
</style>

<div class="container-fluid">

	<%@ include file="../common/register.jspf"%>
	<!-- <a class="btn btn-sm" href="/controle-pessoal/movement.shortchut">
		Aplicar Atalho</a> -->

	<li class="btn btn-sm dropdown">
		<a class="dropdown-toggle"
			data-toggle="dropdown" href="#" role="button" aria-haspopup="true"
			aria-expanded="false" onmouseover="aatalhoMenu()" id="atMenu">
				Aplicar Atalho &nbsp; <span class="glyphicon glyphicon-menu-down"></span>
		</a>
		<div class="dropdown-menu">
			<a class="btn btn-sm" href="/controle-pessoal/movement.shortgen">
				Aplicar Geral</a> <br> <a class="btn btn-sm"
				href="/controle-pessoal/movement.shortrest"> Aplicar Restrito</a> <br>

			<a class="btn btn-sm" data-toggle="collapse" id="shortCutNewButton"
				href="#collapseShortCutNew"
				onmouseover="ashortCutNewButtonClickFunction()" role="button"
				aria-expanded="false" aria-controls="collapseShortCutNew">
				Aplicar Definido &nbsp; <span class="glyphicon glyphicon-edit"></span>
			</a>
		</div>
	</li>
		
	<a class="btn btn-sm" href="/controle-pessoal/importMovement.option">
		Importar Movimentos</a>

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/movement.save?id=${movement.id}"
						method="post">

						<div class="form-group row">
							<div class="col-xs-3">
								<label for="ex1">Descrição</label> <input class="form-control"
									name="description" type="text" value="${movement.description}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Data Movimento</label> <input
									class="form-control" id="datepickermov" name="datMovement"
									type="text" value="${movement.datMovement}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Data Financeira</label> <input
									class="form-control" id="datepickerfin" name="datFinancial"
									type="text" value="${movement.datFinancial}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Origem</label> <input class="form-control"
									name="origin" type="text" value="${movement.origin}">
							</div>
						</div>
						<div class="form-group row">
							<div class="col-xs-2">
								<label for="ex2">Valor</label> <input class="form-control"
									name="valMovement" type="number" step="0.01"
									value="${movement.valMovement}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Tipo Movimento</label> <select
									class="form-control" name="typeMovement">
									<option value="Despesa"
										${movement.typeMovement == 'Despesa' ? 'selected="selected"' : ''}>
										Despesa</option>
									<option value="Receita"
										${movement.typeMovement == 'Receita' ? 'selected="selected"' : ''}>
										Receita</option>
								</select>
							</div>
							<div class="col-xs-3">
								<label for="ex3">Item Orçamento</label> <select
									class="form-control" name="idBudgetItem">
									<c:forEach items="${budgetItemList}" var="budgetItem">
										<option value="${budgetItem.id}"
											${budgetItem.codItem == movement.codItem ? 'selected="selected"' : ''}>
											${budgetItem.grpItem} - ${budgetItem.codItem}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-xs-2">
								<label for="ex2">Rateio</label> <select class="form-control"
									name="splitted">
									<option value="0"
										${budgetItem.splitted == '0' ? 'selected="selected"' : ''}>
										False</option>
									<option value="1"
										${budgetItem.splitted == '1' ? 'selected="selected"' : ''}>
										True</option>
								</select>
							</div>
							<div class="col-xs-2">
								<label for="ex2">Valor Total</label> <input class="form-control"
									name="valTotal" type="number" step="0.01"
									value="${movement.valTotal}">
							</div>
						</div>
						<div class="form-group row">
							<input class="btn btn-success" type="submit" value="Salvar">
							<input class="btn btn-warning" type="reset"> <a
								class="btn btn-danger"
								style="display: ${movement.id == null ? 'none' : ''}"
								href="/controle-pessoal/movement.cancel">Cancelar </a>
						</div>
					</form>

				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseFilterRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/movement.filter" method="post">

						<div class="col-xs-1">
							<label for="ex1">Data Inicial</label> <input class="form-control"
								id="datepickerfilterini" name="filterDatMovementIni"
								type="text" value="${filterDatMovementIni}">
						</div>
						<div class="col-xs-1">
							<label for="ex1">Data Final</label> <input class="form-control"
								id="datepickerfilterend" name="filterDatMovementEnd"
								type="text" value="${filterDatMovementEnd}">
						</div>
						<div class="col-xs-3">
							<label for="ex3">Descrição</label> <input class="form-control"
								name="filterDescription" type="text"
								value="${filterDescription}">
						</div>

						<div class="col-xs-2">
							<label for="ex2">Tipo Movimento</label> <select
								class="form-control" name="filterTypeMovement">
								<option value=""
									${filterTypeMovement == '' ? 'selected="selected"' : ''}>
								</option>
								<option value="Despesa"
									${filterTypeMovement == 'Despesa' ? 'selected="selected"' : ''}>
									Despesa</option>
								<option value="Receita"
									${filterTypeMovement == 'Receita' ? 'selected="selected"' : ''}>
									Receita</option>
							</select>
						</div>
						<div class="col-xs-3">
							<label for="ex3">Origem</label> <select class="form-control"
								name="filterOrigin">
								<option value=""
									${filterOrigin == '' ? 'selected="selected"' : ''}></option>
								<c:forEach items="${originList}" var="origin">
									<option value="${origin}"
										${origin == filterOrigin ? 'selected="selected"' : ''}>
										${origin}</option>
								</c:forEach>
							</select>
						</div>
						<div class="col-xs-3">
							<label for="ex3">Item Orçamento</label> <select
								class="form-control" name="filterBudgetItem" id="filterBudget">
								<option value=""
									${filterBudgetItem == '' ? 'selected="selected"' : ''}>
								</option>
								<option value="undefined"
									${filterBudgetItem == 'undefined' ? 'selected="selected"' : ''}>
									A definir</option>
								<c:forEach items="${budgetItemList}" var="budgetItem">
									<option value="${budgetItem.codItem}"
										${budgetItem.codItem == filterBudgetItem ? 'selected="selected"' : ''}>
										${budgetItem.grpItem} - ${budgetItem.codItem}</option>
								</c:forEach>
							</select>
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

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseShortCutNew">
				<div class="card card-body">

					<form action="/controle-pessoal/movement.shortnew" method="post">

						<div class="col-xs-3">
							<label for="ex3">Item Orçamento</label> <select
								class="form-control" name="shortCutNewBudgetItem">
								<option value="">
								</option>
								<option value="clean">
									-- Limpar</option>
								<c:forEach items="${budgetItemList}" var="budgetItem">
									<option value="${budgetItem.grpItem}-${budgetItem.codItem}">
										${budgetItem.grpItem} - ${budgetItem.codItem}</option>
								</c:forEach>
							</select>
						</div>
						<br>
						<div class="form-group col">
							<input class="btn btn-success" type="submit" value="Aplicar">
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<form action="/controle-pessoal/movement.saveItem"
						method="post">
		<div class="form-group col"
			style="display: ${filterBudgetItem == 'undefined' ? '' : 'none'}">
			<br>
			<input class="btn btn-success" type="submit" value="Salvar">
		</div>

	<table class="table tableFixHead">

		<thead>
			<!--<th>ID</th>-->
			<th>Descrição</th>
			<th>Dat Movimento</th>
			<th>Data Financeira</th>
			<!--<th>Origem</th>-->
			<th>Valor</th>
			<th>Tipo Movimento</th>
			<th>Item Orçamento</th>
			<th>Rateio</th>
			<th>Valor Total</th>
			<th style="display: ${filterBudgetItem == 'undefined' ? '' : 'none'}">Item</th>
			<th>Actions</th>
		</thead>
		<tbody>
			<c:forEach items="${movementList}" var="movement">
				<tr>
					<!--<td>${movement.id}&nbsp;&nbsp;</td>-->
					<td>${movement.description}&nbsp;&nbsp;</td>
					<td>${movement.datMovement}&nbsp;&nbsp;</td>
					<td>${movement.datFinancial}&nbsp;&nbsp;</td>
					<!--<td>${movement.origin}&nbsp;&nbsp;</td>-->
					<td>${movement.valMovement}&nbsp;&nbsp;</td>
					<td>${movement.typeMovement}&nbsp;&nbsp;</td>
					<td>${movement.grpItem}-${movement.codItem}&nbsp;&nbsp;</td>
					<td>${movement.splitted == '0' ? 'False' : 'True'}&nbsp;&nbsp;</td>
					<td>${movement.valTotal}&nbsp;&nbsp;</td>
					<td style="display: ${filterBudgetItem == 'undefined' ? '' : 'none'}">
						<div class="col-sm">
								<select style="display: ${filterBudgetItem == 'undefined' ? '' : 'none'}"
									class="form-control" name="movementId=${movement.id}">
								<option value="0">A definir</option>
									<c:forEach items="${budgetItemList}" var="budgetItem">
										<option value="${budgetItem.grpItem}-${budgetItem.codItem}">
											${budgetItem.grpItem} - ${budgetItem.codItem}</option>
									</c:forEach>
								</select>
							</div>					
					&nbsp;&nbsp;</td>

					<td><a class="btn btn-primary btn-xs"
						href="/controle-pessoal/movement.update?id=${movement.id}">A</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/movement.delete?id=${movement.id}">D</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
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
		$("#datepickermov").datepicker();
		$("#datepickerfin").datepicker();
		$("#datepickerfilterini").datepicker();
		$("#datepickerfilterend").datepicker();
	})
	function bodyLoadFunction() {
		registerLoadPageFunction(${movement.id});
		filterLoadPageFunction(${filterCollapsed});
	}
	function atalhoMenu() {
			document.getElementById("atMenu").click();
	}
	function shortCutNewButtonClickFunction() {
		document.getElementById("shortCutNewButton").click();
	}
	
</script>