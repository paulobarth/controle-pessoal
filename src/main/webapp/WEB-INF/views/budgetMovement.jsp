<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<style>
td.Despesa {
	color: red;
}

td.Receita {
	color: blue;
}

td.Investimento {
	color: green;
}

td.None {
	color: black;
}

td.Info {
	color: #4040bf;
}

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

	<%@ include file="../common/filter.jspf"%>

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseFilterRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/budgetMovement.filter"
						method="post">

						<div class="col-xs-2">
							<label for="ex2">Per�odo Inicial</label> <input class="form-control"
								id="filterDatMovementIni" name="filterDatMovementIni"
								type="text" value="${filterDatMovementIni}">
						</div>
						<div class="col-xs-2">
							<label for="ex2">Per�odo Final</label> <input class="form-control"
								id="filterDatMovementEnd" name="filterDatMovementEnd"
								type="text" value="${filterDatMovementEnd}">
						</div>
						<br> <input class="btn btn-success" type="submit"
							value="Filtrar">
					</form>

				</div>
			</div>
		</div>
	</div>


	<table class="table table-hover tableFixHead">
		<thead>
			<th scope="col" class="col-3">Grupo</th>
			<th scope="col" class="col-3">Item Or�amento</th>
			<th scope="col" class="col-3"><div class="pull-right">Valor
					Previsto</div></th>
			<c:forEach items="${monthList}" var="month">
				<th scope="col" class="col-3"><div class="pull-right">${month}&nbsp;&nbsp;</div></th>
			</c:forEach>
		</thead>
		<tbody>
			<c:forEach items="${budgetMovList}" var="budgetMov">

				<tr data-toggle="collapse"
					data-target=".collapseItem${budgetMov.id}" aria-expanded="true">

					<td>${budgetMov.grpItem}</td>
					<td>${budgetMov.codItem}</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${budgetMov.valItem}" type="number"
								minFractionDigits="2" />
						</div>
					</td>

					<c:forEach items="${budgetMov.valMovement}" var="valMov">
						<td
							class="${budgetMov.valItem < valMov ? budgetMov.grpItem : 'None'}">
							<div class="pull-right">
								<fmt:formatNumber value="${valMov}" type="number"
									minFractionDigits="2" />
							</div>
						</td>
					</c:forEach>
				</tr>

				<c:forEach items="${budgetMov.listMovement}" var="movementDetail">

					<tr class="collapse multi-collapse collapseItem${budgetMov.id}">

						<td colspan="3" class="Info">
							<div>
								<small><em>&nbsp;&nbsp;${movementDetail.description}</em></small>
							</div>
						</td>

						<c:forEach items="${movementDetail.listValue}" var="valMovDetail">
							<td class="Info">
								<div class="pull-right">
									<small class="text-muted"
										style="display:${valMovDetail.day == 0 ? 'none' : ''}">
										(${valMovDetail.day})</small> <em><fmt:formatNumber
											value="${valMovDetail.valMovement}" type="number"
											minFractionDigits="2" /></em>
								</div>
							</td>
						</c:forEach>
					</tr>
				</c:forEach>

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
	function bodyLoadFunction() {
		filterLoadPageFunction(${filterCollapsed});
	}
	$(function() {
		$("#filterDatMovementIni").datepicker({
			format : "mm-yyyy",
			viewMode : "months",
			minViewMode : "months"
		});
		$("#filterDatMovementEnd").datepicker({
			format : "mm-yyyy",
			viewMode : "months",
			minViewMode : "months"
		});
	})
</script>