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

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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

		double primeiroSaldo = 0.00;
		double totalDebito   = 0.00;
		double totalCredito  = 0.00;
		double ultimoSaldo   = 0.00;		
		boolean startSaving = false;
		String storeAfterLine = "Data";
		String datFinancial;
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

//				Controle de início e fim da gravação dos dados
				if (cell.getColumnIndex() == 0) {
					if (cell.getStringCellValue().contains(storeAfterLine)) {
						startSaving = true;
						continue lineBlock;
					}
					if (cell.getStringCellValue().contains("TOTAL")) {
						break lineBlock;
					}
				}
				
				if (!startSaving) {
					continue lineBlock;
				}

				switch (cell.getColumnIndex()) {
				case 0: //Data
					movement.setDatMovement(GeneralFunctions.convertDateBRToUS(cell.getStringCellValue()));
					movement.setDatFinancial(movement.getDatMovement());
				 	break;
				case 1: //Descricao
					if (cell.getStringCellValue().contains("SALDO ANTERIOR")) {
						for (int i=0; i <= 4; i++) {
							cell = cellIterator.next();
						}
						primeiroSaldo = Double.parseDouble(getCellContent(cell, NUMERIC));;
						continue lineBlock;
					}
					movement.setDescription(cell.getStringCellValue());
					break;
				case 2: //Documento
					movement.setDocumentNumber(cell.getStringCellValue());
					break;
				case 4: //Valor Crédito
				case 5: //Valor Débito
					if (!isCellEmpty(cell)) {
						movement.setValMovement(getCellContent(cell, NUMERIC));
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
				case 6:
					ultimoSaldo = Double.parseDouble(getCellContent(cell, NUMERIC));
					break;
			
			 default:
				 break;
				}
			}

//			System.out.println("aaa - " + movement.getDescription());
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
        
        Double total = primeiroSaldo + totalCredito - totalDebito - ultimoSaldo;
        
		String fim = "Finalizado - Saldo anterior: " + primeiroSaldo + " | Saldo Final: " + ultimoSaldo +
				" | Total Debito: " + totalDebito + " | Total Credito: " + totalCredito;
        fim += " | Conferência: " + total;

        if (total < -0.02 || total > 0.02) {
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

	private static boolean isCellEmpty(Cell cell) {
		return cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty();
	}


	private static String getCellContent (Cell cell) {
		
		String content = "";
		
		//String teste = cell.getCellType().toString();
		
        //Reading example
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
        
        return content;
	}

	private static String getCellContent (Cell cell, String specialFormat) {
		String content = getCellContent(cell);
		switch (specialFormat) {
		case NUMERIC:
			return content.replace(".", "").replace(",", ".");

		default:
			break;
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
