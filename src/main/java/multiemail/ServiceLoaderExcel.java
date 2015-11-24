package multiemail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceLoaderExcel extends Service<ObservableList<ObservableList<String>>> {

	final static Logger logger = Logger.getLogger(ServiceLoaderExcel.class);
	
	private File inputFile;

	public ServiceLoaderExcel(File inputFile) {
		this.inputFile = inputFile;
	}

	@Override
	protected Task<ObservableList<ObservableList<String>>> createTask() {
		return new Task<ObservableList<ObservableList<String>>>() {

			@Override
			protected ObservableList<ObservableList<String>> call() throws Exception {
				ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
				
				try (InputStream is = new FileInputStream(inputFile)){
					Workbook wb = getWorkbook(is);
					Sheet sheet = wb.getSheetAt(0);
					int lastRowNum = sheet.getLastRowNum();
					int i = 0;
					updateProgress(i, lastRowNum);
					updateProgress(-1, lastRowNum);
//					for (; i < lastRowNum; i++) {
					Iterator<Row> rows = sheet.rowIterator();
					while(rows.hasNext()){
						Row row = rows.next();//sheet.getRow(i);
//						updateProgress(i, lastRowNum);
						String name = "", telephone = "", email = "";
						try{
							name = row.getCell(0).getStringCellValue();
						}catch(Exception ex){}
						try {
							Cell cell = row.getCell(1);
							if(cell!=null){
//								telephone = new BigInteger(cell.getNumericCellValue()).toString();
								telephone = ((long)cell.getNumericCellValue())+"";
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
						try { 
							email = row.getCell(2).getStringCellValue();
						} catch (Exception ex) {}
						ObservableList<String> auxRow = FXCollections.observableArrayList(name, telephone, email);
						data.add(auxRow);
					}
					updateMessage("Finishd OK ");
					logger.debug("Data loaded: " + data.size());
				} catch (Exception e) {
					logger.error("An error ocurred during the extraction of data", e);
					updateMessage("A problem happen during the load of the file");
				}
				return data;
			}

			private Workbook getWorkbook(InputStream is) throws FileNotFoundException, IOException, InvalidFormatException {
				String file = inputFile.getAbsolutePath().toLowerCase();
				if (file.endsWith(".xls")) {
					return new HSSFWorkbook(is);
				} else if (file.endsWith(".xlsx")) {
					return new XSSFWorkbook(is);
				}
				return null;
			}
		};
	}

}
