<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<div class="container">

	<%@ include file="../common/register.jspf"%>
	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form
						action="/controle-pessoal/budgetShortcut.save?id=${budgetShortcut.id}"
						method="post">

						<div class="form-group row">
							<div class="col-xs-4">
								<label for="ex4">Atalhos</label> <input class="form-control"
									name="shortcut" type="text" value="${budgetShortcut.shortcut}">
							</div>
							<div class="col-xs-3">
								<label for="ex3">Item Orçamento</label> <select
									class="form-control" name=idBudgetItem>
									<c:forEach items="${budgetItemList}" var="budgetItem">
										<option value="${budgetItem.id}"
											${budgetItem.codItem == budgetShortcut.codItem ? 'selected="selected"' : ''}>
											${budgetItem.grpItem} - ${budgetItem.codItem}</option>
									</c:forEach>
								</select>
							</div>
							<div class="col-xs-1">
								<label for="ex1">Separador</label> <input class="form-control"
									name="splitter" type="text"
									value="${budgetShortcut.splitter == null ? '' : budgetShortcut.splitter}">
							</div>
						</div>

						<input class="btn btn-success" type="submit" value="Salvar">
						<input class="btn btn-warning" type="reset"> <a
							class="btn btn-danger"
							style="display: ${budgetShortcut.id == null ? 'none' : ''}"
							href="/controle-pessoal/budgetShortcut.list">Cancelar</a>

					</form>
				</div>
			</div>
		</div>
	</div>

	<table class="table table-striped">


		<thead>
			<th>Atalho</th>
			<th>Item Orçamento</th>
			<th>Separador</th>
			<th>Action</th>
		</thead>
		<tbody>
			<c:forEach items="${budgetShortcutList}" var="budgetShortcut">
				<tr>
					<td>${budgetShortcut.shortcut}&nbsp;&nbsp;</td>
					<td>${budgetShortcut.grpItem}-
						${budgetShortcut.codItem}&nbsp;&nbsp;</td>
					<td>${budgetShortcut.splitter}&nbsp;&nbsp;</td>

					<td><a class="btn btn-primary"
						href="/controle-pessoal/budgetShortcut.update?id=${budgetShortcut.id}">Alterar</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/budgetShortcut.delete?id=${budgetShortcut.id}">Delete</a>
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
		registerLoadPageFunction(${budgetShortcut.id});
	}
</script>
