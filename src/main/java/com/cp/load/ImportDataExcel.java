package com.cp.load;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.print.DocFlavor.STRING;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.cp.fwk.data.DataManager;
import com.cp.fwk.util.GeneralFunctions;
import com.cp.fwk.util.model.QueryParameter;
import com.cp.fwk.util.query.QueryTypeCondition;
import com.cp.fwk.util.query.QueryTypeFilter;
import com.cp.model.Movement;

public class ImportDataExcel {

	private static final String DEBITO	= "Debito";
	private static final String CREDITO	= "Credito";
	private static final String NUMERIC = "Numeric";

	public static String importMovementDebitSantander(String file, String origin) throws IOException {

		double totalDebito   = 0.00;
		double totalCredito  = 0.00;
		double planTotalDebit = 0.00;
		double planTotalCredit = 0.00;
		boolean startSaving = false;
		boolean isTotalLine = false;
		String storeAfterLine = "Data";
		Movement movement;

		FileInputStream inputStream = new FileInputStream(new File(file));

		Workbook workbook = new HSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		List<Movement> lMovement = new ArrayList<Movement>();

		lineBlock:
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();

			System.out.println("Linha - " + nextRow.getRowNum());

			Iterator<Cell> cellIterator = nextRow.cellIterator();

			movement = new Movement();

			cellBlock:
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				System.out.println("Coluna - " + cell.getColumnIndex());

//				Controle de início e fim da gravação dos dados
				if (cell.getColumnIndex() == 0) {
					if (!startSaving && cell.getStringCellValue().contains(storeAfterLine)) {
						startSaving = true;
						continue lineBlock;
					}
					if (!isTotalLine && cell.getStringCellValue().contains("TOTAL")) {
						isTotalLine = true;
						continue cellBlock;
					}
				}
				
				if (isTotalLine) {
					switch (cell.getColumnIndex()) {
					case 4: //Valor Crédito
						planTotalCredit = Double.parseDouble(getCellContent(cell, NUMERIC));
						break;
					case 5: //Valor Débito
						planTotalDebit = Double.parseDouble(getCellContent(cell, NUMERIC));
						break lineBlock;				
					default:
						break;
					}
					continue cellBlock;
				}
				
				if (!startSaving) {
					continue lineBlock;
				}

				switch (cell.getColumnIndex()) {
				case 0: //Data
					System.out.println("Data = " + cell.getStringCellValue());
					movement.setDatMovement(GeneralFunctions.convertDateBRToUS(cell.getStringCellValue()));
					movement.setDatFinancial(movement.getDatMovement());
				 	break;
				case 1: //Descricao
					if (cell.getStringCellValue().contains("SALDO ANTERIOR")) {
						continue lineBlock;
					}
					movement.setDescription(cell.getStringCellValue());
					System.out.print("Descrição - " + movement.getDescription());
					break;
				case 2: //Documento
					movement.setDocumentNumber(getCellContent(cell));
					break;
				case 4: //Valor Crédito
				case 5: //Valor Débito
					String valMovement = getCellContent(cell, NUMERIC);
					if (!valMovement.isEmpty()) {
						System.out.println("Valor String = " + valMovement);
						movement.setValMovement(valMovement);
						if (movement.getValMovement() < 0) {
							movement.setTypeMovement("Despesa");
							movement.setValMovement(movement.getValMovement() * -1);
							totalDebito += movement.getValMovement();
						} else if (movement.getValMovement() > 0) {
							movement.setTypeMovement("Receita");
							totalCredito += movement.getValMovement();
						}
					}
					break;
			
			 default:
				 break;
				}
			}

			System.out.println("   #####      TOTAL - " + totalCredito);
			System.out.println("");
			movement.setValTotal(movement.getValMovement());
			movement.setOrigin(origin);
			
			if (isMovementUnique(movement)) {
				lMovement.add(movement);
			}
		}

		workbook.close();
        inputStream.close();
        
        totalCredito = GeneralFunctions.truncDouble(totalCredito, 2);
        totalDebito = GeneralFunctions.truncDouble(totalDebito, 2);

        String fim = "PLANILHA x COUNT CR e DB = " + planTotalCredit + " x " + totalCredito + "  e  " +
        				planTotalDebit + " x " + totalDebito;
        if ((Math.abs(planTotalCredit - totalCredito) >= 0.02) ||
    		(Math.abs(planTotalDebit + totalDebito) >= 0.02)) {
        	fim += " | Erro";
        } else {
        	fim += " | OK";
        	createMovementByList(lMovement);
        }
        
		return fim;
	}

	public static String importMovementCreditSantander(String file, String origin, String datFinancial) throws IOException {

		Double totalFatura = 0.00;
		String newDatMovement;
		Movement movement;

		FileInputStream inputStream = new FileInputStream(new File(file));

		Workbook workbook = new HSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		List<Movement> lMovement = new ArrayList<Movement>();

		lineBlock:
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			
//			System.out.println("Linha - " + nextRow.getRowNum());

			Iterator<Cell> cellIterator = nextRow.cellIterator();

			movement = new Movement();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

//				System.out.println("Coluna - " + cell.getColumnIndex());

				switch (cell.getColumnIndex()) {
				case 0: //Data
					try {
						Date aaa = cell.getDateCellValue();

						Calendar calendar = new GregorianCalendar();
						calendar.setTime(aaa);
						int year = calendar.get(Calendar.YEAR);
						int month = calendar.get(Calendar.MONTH) + 1;
						int day = calendar.get(Calendar.DAY_OF_MONTH);
						if (year < 2000) {
							continue lineBlock;
						}
						movement.setDatMovement(
								String.valueOf(month) + "/" +
										String.valueOf(day) + "/" +
										String.valueOf(year));
					} catch (Exception e) {
						continue lineBlock;
					}
				 	break;
				case 1: //Descricao
					movement.setDescription(cell.getStringCellValue());
					if (movement.getDescription().contains("DEB. AUTOM. DE FATURA")) {
						continue lineBlock;
					}
					break;
				case 3: //Valor
					movement.setValMovement(getCellContent(cell));
					totalFatura += movement.getValMovement();
					if (movement.getValMovement() < 0) {
						movement.setTypeMovement("Receita");
						movement.setValMovement(movement.getValMovement() * -1);
					} else if (movement.getValMovement() > 0) {
						movement.setTypeMovement("Despesa");
					}
					break;
			
			 default:
				 break;
				}
			}

//			System.out.println("aaa - " + movement.getDescription());
			newDatMovement = calculateDatFinancial(movement.getDescription(), movement.getDatMovement());
			if (!newDatMovement.isEmpty()) {
				movement.setDatMovement(newDatMovement);
			}
			movement.setDatFinancial(datFinancial);
			movement.setValTotal(movement.getValMovement());
			movement.setOrigin(origin);

			if (isMovementUnique(movement)) {
				lMovement.add(movement);
			}
		}

		workbook.close();
        inputStream.close();
        
        createMovementByList(lMovement);
        
		String fim = "Finalizado -  Total fatura: " + String.valueOf(totalFatura);
        
		return fim;
	}

	private static void createMovementByList(List<Movement> lMovement) {
		List<Movement> newMovList = new ArrayList<Movement>();
    	for (Movement mov : lMovement) {
    		newMovList.clear();
    		newMovList.add(mov);
    		DataManager.insert(Movement.class, newMovList);
    	}
	}

	private static String getCellContent (Cell cell, String specialFormat) {
		String content = getCellContent(cell);
		if (cell.getCellType().equals(CellType.STRING) && specialFormat.equals(NUMERIC)) {
			return content.replace(".", "").replace(",", ".");			
		}
		return content;
	}

	private static String getCellContent (Cell cell) {
		
		String content = "";
		
		//String teste = cell.getCellType().toString();
		
        //Reading example
		try {
	        switch (cell.getCellType()) {
	            case STRING:
	            	content = cell.getStringCellValue();
	                break;
	            case BOOLEAN:
	            	content = String.valueOf(cell.getBooleanCellValue());
	                break;
	            case NUMERIC:
	            	content = String.valueOf(cell.getNumericCellValue());
	                break;
				default:
					break;
	        }
		} catch (Exception e) {
			content = cell.getStringCellValue();
			System.out.println("Exception: " + e.getMessage());
		}
        
        return content;
	}

	private static String calculateDatFinancial(String description, String datMovement) {

		String newDate = "";

		int initPos = description.indexOf("(") + 1;
		if (initPos > 0) {

			int months = Integer.parseInt(description.substring(initPos, initPos + 2));

			if (months > 1) {

				months--;

				String[] splitDate = datMovement.split("/");

				int newMonth = Integer.parseInt(splitDate[0]) + months;

				if (newMonth > 12) {
					newDate = String.valueOf(newMonth - 12) + "/" + splitDate[1] + "/"
							+ String.valueOf(Integer.parseInt(splitDate[2]) + 1);
				} else {
					newDate = newMonth + "/" + splitDate[1] + "/" + splitDate[2];
				}
			}
		}

		return newDate;
	}

	private static boolean isMovementUnique(Movement movement) {

		QueryParameter qp = new QueryParameter();

		qp.addSingleParameter("description", QueryTypeFilter.EQUAL, movement.getDescription(), QueryTypeCondition.AND);
		qp.addSingleParameter("datFinancial", QueryTypeFilter.EQUAL,
				GeneralFunctions.stringDatetoSql(movement.getDatFinancial()), QueryTypeCondition.AND);
		qp.addSingleParameter("valMovement", QueryTypeFilter.EQUAL, String.valueOf(movement.getValMovement()),
				QueryTypeCondition.AND);

		return DataManager.isDataUnique(Movement[].class, qp);
	}
}
