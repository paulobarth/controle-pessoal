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

	<table class="table table-fit table-hover">

		<thead class="table-light">
			<th scope="col">Ano</th>
			<th scope="col">Mês</th>
			<th scope="col">Vendas do Mês</th>
			<th scope="col">Vendas com Lucro</th>
			<th scope="col">Lucro</th>
			<th scope="col">Deduções</th>
			<th scope="col">Base IR</th>
			<th scope="col">Imposto a pagar</th>
			<th scope="col">Status</th>
		</thead>  
		<tbody>
			<c:forEach items="${stocksReportTaxList}" var="stocksReportTax">
				<tr data-toggle="collapse"
					data-target=".collapseItem${stocksReportTax.stocksTax.month}"
					aria-expanded="true">

					<td>${stocksReportTax.stocksTax.year}</td>
					<td>${stocksReportTax.stocksTax.month}</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.totalSell}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.profitSell}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.resultSell}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.valTotalDeduction}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.resultWithDeduction}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>
						<div class="pull-right">
							<fmt:formatNumber value="${stocksReportTax.stocksTax.valTotalPayment}" type="number"
								minFractionDigits="2" />
						</div>
					</td>
					<td>${stocksReportTax.stocksTax.statusTax}
					&nbsp&nbsp
					<a class="btn btn-danger btn-sm"
						style="display: ${stocksReportTax.stocksTax.statusTax == 'Pendente' ? '' : 'none'}"
						href="/controle-pessoal/stocksTax.taxCancel?id=${stocksReportTax.stocksTax.id}">Estornar</a>
					&nbsp&nbsp
					<a class="btn btn-primary btn-sm"
						style="display: ${stocksReportTax.stocksTax.statusTax == 'Pendente' ? '' : 'none'}"
						href="/controle-pessoal/stocksTax.taxPayment?id=${stocksReportTax.stocksTax.id}">Pagar</a>
					
					</td>
				</tr>

				<c:forEach items="${stocksReportTax.stocksSellList}"
					var="stocksSell">
					<tr
						class="table-success collapse multi-collapse collapseItem${stocksReportTax.stocksTax.month}">
						<td>&nbsp&nbsp${stocksSell.codStock}</td>
						<td>${stocksSell.datSettlement}</td>
						<td></td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${stocksSell.quantity * stocksSell.valStock}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${stocksSell.valResultSell}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</c:forEach>
				

				<c:forEach items="${stocksReportTax.stocksDeductionList}"
					var="stocksDeduction">
					<tr
						class="table-danger collapse multi-collapse collapseItem${stocksReportTax.stocksTax.month}">
						<td>&nbsp&nbsp${stocksDeduction.codStock}</td>
						<td>${stocksDeduction.datSettlement}</td>
						<td></td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${stocksDeduction.quantity * stocksDeduction.valStock}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${stocksDeduction.valResultSell}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td>
							<div class="pull-right">
								<fmt:formatNumber value="${stocksDeduction.valTotalIRLossConsumed}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table>
	<a class="btn btn-primary"
		style="display: ${showCalcutationButton ? '' : 'none'}"
		href="/controle-pessoal/stocksTax.taxCalculation?period=${period}">Calcular</a>


</div>

<%@ include file="../common/footer.jspf"%>

<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<link rel="stylesheet" href="/resources/demos/style.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
</script>