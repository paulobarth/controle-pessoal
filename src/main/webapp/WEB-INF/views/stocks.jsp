<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<div class="container">

	<%@ include file="../common/register.jspf"%>
	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseEditRegister">
				<div class="card card-body">

					<form
						action="/controle-pessoal/stocks.save?id=${stocks.id}"
						method="post">

						<div class="form-group row">


							<div class="col-xs-1">
								<label for="ex1">Ação</label> <input class="form-control"
									name="codStock" type="text" value="${stocks.codStock}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Nome</label> <input class="form-control"
									name="name" type="text" value="${stocks.name}">
							</div>
							<div class="col-xs-2">
								<label for="ex2">Empresa</label> <input class="form-control"
									name="companyName" type="text" value="${stocks.companyName}">
							</div>

							<div class="col-xs-2">
								<label for="ex2">Carteira</label> <select
									class="form-control" name="codPortfolio">
									<option value=""
										${stocks.codPortfolio == '' ? 'selected="selected"' : ''}>
									</option>
									<option value="Rock Trade"
										${stocks.codPortfolio == 'Rock Trade' ? 'selected="selected"' : ''}>
										Rock Trade</option>
									<option value="Particular"
										${stocks.codPortfolio == 'Particular' ? 'selected="selected"' : ''}>
										Particular</option>
								</select>
							</div>
							<div class="col-xs-2">
								<label for="ex1">Preço</label> <input class="form-control"
									name="actualPrice" type="number" step="0.01"
									value="${stocks.actualPrice}">
							</div>
						</div>

						<input class="btn btn-success" type="submit" value="Salvar">
						<input class="btn btn-warning" type="reset"> <a
							class="btn btn-danger"
							style="display: ${stocks.id == null ? 'none' : ''}"
							href="/controle-pessoal/stocks.list">Cancelar</a>


					</form>

				</div>
			</div>
		</div>
	</div>

	<table class="table table-striped">

		<thead>
			<tr>
			<th>ID</th>
				<th>Ação</th>
				<th>Nome</th>
				<th>Empresa</th>
				<th>Carteira</th>
				<th>Preço</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${stocksList}" var="stocks">
				<tr>
					
					<td>${stocks.id}</td>
					<td>${stocks.codStock}</td>
					<td>${stocks.name}</td>
					<td>${stocks.companyName}</td>
					<td>${stocks.codPortfolio}</td>
					<td>${stocks.actualPrice}</td>

					<td><a class="btn btn-primary"
						href="/controle-pessoal/stocks.update?id=${stocks.id}">Alterar</a>
						<a class="btn btn-danger btn-xs"
						href="/controle-pessoal/stocks.delete?id=${stocks.id}">Delete</a>
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