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
							<label for="ex3">Ação</label> <select class="form-control"
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
							<label for="ex3">Apenas em aberto</label> <select
								class="form-control" name="filterShowOnlyOpened"
								id="filterShowOnlyOpened">
								<option value="false"
									${filterShowOnlyOpened == 'false' ? 'selected="selected"' : ''}>Não
								</option>
								<option value="true"
									${filterShowOnlyOpened == 'true' ? 'selected="selected"' : ''}>Sim
								</option>
							</select>
						</div>
						<div class="col-xs-2">
							<label for="ex2">Carteira</label> <select
								class="form-control" name="filterCodPortfolio"
								i="filterCodPortfolio">
								<option value=""
									${filterCodPortfolio == '' ? 'selected="selected"' : ''}>
								</option>
								<option value="Rock Trade"
									${filterCodPortfolio == 'Rock Trade' ? 'selected="selected"' : ''}>
									Rock Trade</option>
								<option value="Particular"
									${filterCodPortfolio == 'Particular' ? 'selected="selected"' : ''}>
									Particular</option>
							</select>
						</div>
						<div class="col-xs-3">
							<label for="ex3">Atualizar Preços</label> <select
								class="form-control" name="filterStockPrice"
								id="filterStockPrice">
								<option value="false" selected="selected">Não</option>
								<option value="true">Sim</option>
							</select>
						</div>

						<div class="col-xs-2">
							<label for="ex2">Ano para IR</label> <input
								class="form-control" id="datepickeroper" name="filterYearOperation"
								type="text" value="${filterYearOperation}">
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
		checked>Por Ação
	</label> <label class="radio-inline"> <input type="radio"
		name="filterOptVision" value="div2" onchange="myFunction(this)">Posição
		Atual
	</label> <label class="radio-inline"> <input type="radio"
		name="filterOptVision" value="div3" onchange="myFunction(this)">Vendas
		por mês
	</label> <br>

	<hr>

	<div id="div1">
		<c:forEach items="${listSRPO}" var="SRPO">

			<table class="table caption-top">
				<caption>${SRPO.codStock}</caption>
				<thead class="table-light">
					<th scope="col" class="col-3">Tipo Operação</th>
					<th scope="col" class="col-2">Ação</th>
					<th scope="col" class="col-3"><div class="pull-right">Data
							Operação</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Quantidade</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Valor
							Ação</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Total</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Custo</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Total
							Custo</div></th>
					<th scope="col" class="col-2"><div class="pull-right">Preço
							Médio</div></th>
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
							<td><div class="pull-right" color="#FF0000">
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

		Atualizado em: ${updatedAtInfo}
		<table class="table table-fit">
			<thead class="table-light">
				<th scope="col">Ação</th>
				<th scope="col"><div class="pull-right">Quantidade</div></th>
				<th scope="col"><div class="pull-right">Preço Médio</div></th>
				<th scope="col"><div class="pull-right">Total Compra</div></th>
				<th scope="col"><div class="pull-right">Total Venda</div></th>
				<th scope="col"><div class="pull-right">LP Acum</div></th>
				<th scope="col"><div class="pull-right">%</div></th>
				<th></th>
				<th scope="col"><div class="pull-right">Cotação Atual</div></th>
				<th scope="col"><div class="pull-right">Dif</div></th>
				<th scope="col"><div class="pull-right">LP Atual</div></th>
				<th scope="col"><div class="pull-right">%</div></th>
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
								type="number" minFractionDigits="2" maxFractionDigits="2" />
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
							<small class="text-muted"> <fmt:formatNumber
									value="${actualPosition.result}" type="percent"
									minFractionDigits="2" maxFractionDigits="2" />
							</small>
						</div>
					</td>
					<td></td>
					<td
						class="${actualPosition.actualQuantity > 0 ?
						actualPosition.actualPrice < actualPosition.medPrice ? 'table-danger' : 'table-success' : ''}">
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.actualPrice}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.actualPriceDif}"
								type="number" minFractionDigits="2" maxFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${actualPosition.totalActualResult}"
								type="number" minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<small class="text-muted"><fmt:formatNumber
									value="${actualPosition.actualResult}" type="percent"
									minFractionDigits="2" maxFractionDigits="2" /> </small>
						</div>
					</td>
				</tr>
			</c:forEach>
		</table>

		<table class="table table-fit">

			<tr>
				<td>Total Perda</td>
				<td>
					<div class="pull-right">
						<fmt:formatNumber value="${totalLoss}"
							type="number" minFractionDigits="2" maxFractionDigits="2" />
				</div>
			</tr>
			<tr>
				<td>Total Ganho</td>
				<td>
					<div class="pull-right">
						<fmt:formatNumber value="${totalGain}"
							type="number" minFractionDigits="2" maxFractionDigits="2" />
				</div>
			</tr>
			<tr>
				<td>Saldo/</td>
				<td>
					<div class="pull-right">
						<fmt:formatNumber value="${totalDifference}"
							type="number" minFractionDigits="2" maxFractionDigits="2" />
				</div>
			</tr>
			<tr>
				<td>Futuro</td>
				<td>
					<div class="pull-right">
						<fmt:formatNumber value="${totalFuture}"
							type="number" minFractionDigits="2" maxFractionDigits="2" />
				</div>
			</tr>

		</table>
	</div>

	<div id="div3" hidden="true">

		<table class="table table-fit table-hover">

			<thead class="table-light">
				<th scope="col">Período</th>
				<th scope="col">Valor das Vendas</th>
				<th scope="col"></th>
			</thead>  
			<tbody>
				<c:forEach items="${listMonthSales}" var="monthSales">
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
						<td>
							<a class="btn btn-danger btn-sm"
								style="display: ${monthSales.exceeded ? '' : 'none'}"
								href="/controle-pessoal/stocksOperation.taxCalculation?period=${monthSales.period}">Calc</a>
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
							<td>
								<div class="pull-right">
									<fmt:formatNumber value="${salesPerStocks.resultSell}" type="number"
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
		$("#datepickeroper").datepicker();
	});

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