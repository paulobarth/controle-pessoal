<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="../common/header.jspf"%>
<%@ include file="../common/navigation.jspf"%>

<style>
tr.None {
	color: black;
}

tr.Info {
	color: #4040bf;
	font-weight: bold;
}
</style>

<div class="container">

	<%@ include file="../common/filter.jspf"%>

	<div class="row">
		<div class="col">
			<div class="collapse multi-collapse" id="collapseFilterRegister">
				<div class="card card-body">

					<form action="/controle-pessoal/stocksOperation.reportFilter"
						method="post">
						<div class="col-xs-3">
							<label for="ex3">A��o</label> <select class="form-control"
								name="filterStockItem" id="filterStock">
								<option value=""
									${filterStockItem == '' ? 'selected="selected"' : ''}>
								</option>
								<c:forEach items="${stockList}" var="stockItem">
									<option value="${stockItem}"
										${stockItem == filterStockItem ? 'selected="selected"' : ''}>
										${stockItem}</option>
								</c:forEach>
							</select>
						</div>

						<div class="col-xs-3">
							<input type="checkbox" class="form-check-input"
								name="showOnlyOpenedItem" id="showOnlyOpened"> <label
								class="form-check-label" for="showOnlyOpened">Apenas em
								aberto</label>
						</div>

						<div class="col-xs-3">
							<label for="" class="form-control-label">A��o</label> <select
								class="form-control selectpicker" id="select-country"
								placeholder="" data-live-search="true">
								<option data-tokens="china">China</option>
								<option data-tokens="malayasia">Malayasia</option>
								<option data-tokens="singapore">Singapore</option>
							</select>
						</div>

						<br> <input class="btn btn-success" type="submit"
							value="Filtrar">
					</form>

				</div>
			</div>
		</div>
	</div>

	<label class="radio-inline"> <input type="radio"
		name="filterOptVision" value="div1" onchange="myFunction(this)"
		checked>Por A��o
	</label> <label class="radio-inline"> <input type="radio"
		name="filterOptVision" value="div2" onchange="myFunction(this)">Posi��o
		Atual
	</label> <label class="radio-inline"> <input type="radio"
		name="filterOptVision" value="div3" onchange="myFunction(this)">Vendas
		por m�s
	</label> <br>

	<hr>

	<div id="div1">
		<c:forEach items="${listSRPO}" var="SRPO">

			<table class="table caption-top">
				<caption>${SRPO.codStock}</caption>
				<thead class="table-light">
					<th scope="col" class="col-3">Tipo Opera��o</th>
					<th scope="col" class="col-2">A��o</th>
					<th scope="col" class="col-3"><div class="pull-right">Data
							Opera��o</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Quantidade</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Valor
							A��o</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Total</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Custo</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Total
							Custo</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Pre�o
							M�dio</div></th>
					<th scope="col" class="col-2"><div class="pull-right">LP</div></th>

				</thead>
				<tbody>
					<c:forEach items="${SRPO.stocksList}" var="stockOper">

						<tr class="${stockOper.typeOperation == 'R' ? 'Info' : 'None'}">

							<td>${stockOper.typeOperation}</td>
							<td>${stockOper.codStock}</td>
							<td>
								<div class="pull-right"
									style="display:${stockOper.typeOperation == 'R' ? 'none' : ''}">
									${stockOper.datOperation}</div>
							</td>
							<td><div class="pull-right">${stockOper.quantity}</div></td>
							<td>
								<div class="pull-right"
									style="display:${stockOper.typeOperation == 'R' ? 'none' : ''}">
									<fmt:formatNumber value="${stockOper.valStock}" type="number"
										minFractionDigits="2" />
								</div>
							</td>
							<td>
								<div class="pull-right"
									style="display:${stockOper.typeOperation == 'R' ? 'none' : ''}">
									<fmt:formatNumber value="${stockOper.totalOperation}"
										type="number" minFractionDigits="2" />
								</div>
							</td>
							<td>
								<div class="pull-right"
									style="display:${stockOper.typeOperation == 'R' ? 'none' : ''}">
									<fmt:formatNumber value="${stockOper.valCost}" type="number"
										minFractionDigits="2" />
								</div>
							</td>
							<td>
								<div class="pull-right"
									style="display:${stockOper.typeOperation == 'R' ? 'none' : ''}">
									<fmt:formatNumber value="${stockOper.totalOperCost}"
										type="number" minFractionDigits="2" />
								</div>
							</td>
							<td><div class="pull-right">
									<fmt:formatNumber value="${stockOper.medPrice}" type="number"
										minFractionDigits="4" />
								</div></td>
							<td><div class="pull-right">
									<fmt:formatNumber value="${stockOper.resultSell}" type="number"
										minFractionDigits="2" />
								</div></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<br>
		</c:forEach>
	</div>


	<div id="div2" hidden="true">

		<table class="table table-fit">
			<thead class="table-light">
				<th scope="col">A��o</th>
				<th scope="col"><div class="pull-right">Quantidade</div></th>
				<th scope="col"><div class="pull-right">Pre�o M�dio</div></th>
				<th scope="col"><div class="pull-right">Total Compra</div></th>
				<th scope="col"><div class="pull-right">Total Venda</div></th>
				<th scope="col"><div class="pull-right">LP Acum R$</div></th>
				<th scope="col"><div class="pull-right">LP Acum %</div></th>
			</thead>

			<c:forEach items="${listActualPosition}" var="actualPosition">

				<tr>
					<td>${actualPosition.codStock}</td>
					<td>
						<div class="pull-right">${actualPosition.actualQuantity}</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.medPrice}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.totalBuy}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.totalSell}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td
						class="${actualPosition.result < 0 ? 'table-danger' : actualPosition.result > 0 ? 'table-primary' : ''}">
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.totalResultSell}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td
						class="${actualPosition.result < 0 ? 'table-danger' : actualPosition.result > 0 ? 'table-primary' : ''}">
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.result}" type="number"
								minFractionDigits="2" />
							%
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>

	</div>

	<div id="div3" hidden="true">

		<table class="table table-fit table-hover">

			<thead class="table-light">
				<tr>
					<th>Per�odo</th>
					<th>Valor das Vendas</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${listMonthSales}" var="monthSales">r
					<tr class="${monthSales.exceeded ? 'table-danger' : ''}"
						data-toggle="collapse"
						data-target=".collapseItem${monthSales.period}"
						aria-expanded="true">

						<td>${monthSales.periodDescription}</td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${monthSales.value}" type="number"
									minFractionDigits="2" />
							</div>
						</td>
					</tr>


					<c:forEach items="${monthSales.salesPerStocks}"
						var="salesPerStocks">
						<tr
							class="table-light collapse multi-collapse collapseItem${monthSales.period}">
							<td>&nbsp&nbsp${salesPerStocks.codStock}<small
								class="text-muted"> (${salesPerStocks.dayOperation})</small>
							</td>
							<td>
								<div class="pull-right">
									<fmt:formatNumber value="${salesPerStocks.value}" type="number"
										minFractionDigits="2" />
									&nbsp&nbsp
							</td>
							</div>
						</tr>
					</c:forEach>
				</c:forEach>
			</tbody>
		</table>
	</div>

</div>

<%@ include file="../common/footer.jspf"%>

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
	$(function() {
		$("#filterDatMovementIni").datepicker({
			format : "mm-yyyy",
			viewMode : "months",
			minViewMode : "months"
		});
	})

	function myFunction(myRadio) {
		console.log(myRadio.value);
		for (i = 1; i <= 3; i++) {
			div = "div" + i;
			result = !(div == myRadio.value);
			document.getElementById(div).hidden = result;
		}
	}
</script>