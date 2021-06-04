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
				</tr>

				<c:forEach items="${stocksReportTax.stocksSellList}"
					var="stocksSell">
					<tr
						class="table-light collapse multi-collapse collapseItem${stocksReportTax.stocksTax.month}">
						<td>&nbsp&nbspLucro - ${stocksSell.codStock}</td>
						<td>&nbsp&nbsp${stocksSell.datOperation}</td>
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
					</tr>
				</c:forEach>
				

				<c:forEach items="${stocksReportTax.stocksDeductionList}"
					var="stocksDeduction">
					<tr
						class="table-light collapse multi-collapse collapseItem${stocksReportTax.stocksTax.month}">
						<td>&nbsp&nbspPrej - ${stocksDeduction.codStock}</td>
						<td>&nbsp&nbsp${stocksDeduction.datOperation}</td>
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
								<fmt:formatNumber value="${stocksDeduction.valIRLossConsumed}" type="number"
									minFractionDigits="2" />
								&nbsp&nbsp
							</div>
						</td>
						<td></td>
						<td></td>
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
</script>