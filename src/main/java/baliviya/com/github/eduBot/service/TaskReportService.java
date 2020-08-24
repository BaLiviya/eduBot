package baliviya.com.github.eduBot.service;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.CategoryDao;
import baliviya.com.github.eduBot.dao.impl.TaskArchiveDao;
import baliviya.com.github.eduBot.dao.impl.UserDao;
import baliviya.com.github.eduBot.entity.custom.Task;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.util.Const;
import baliviya.com.github.eduBot.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TaskReportService {
	
	private DaoFactory      daoFactory      = DaoFactory.getInstance();
	private UserDao         userDao         = daoFactory.getUserDao();
	private TaskArchiveDao  taskArchiveDao  = daoFactory.getTaskArchiveDao();
	private CategoryDao     categoryDao     = daoFactory.getCategoryDao();
	private XSSFWorkbook    workbook        = new XSSFWorkbook();
	private XSSFCellStyle   style           = workbook.createCellStyle();
	private Language        currentLanguage = Language.ru;
	private Sheet           sheets;
	private Sheet           sheet;
	
	public void sendTaskReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int categoryId, int messagePrevReport){
		currentLanguage = LanguageService.getLanguage(chatId);
		try {
			sendReport(chatId, bot, dateBegin, dateEnd, categoryId, messagePrevReport);
		} catch (Exception e){
			log.error("Can't create/send report", e);
			try {
				bot.execute(new SendMessage(chatId, "Report doesn't create"));
			} catch (TelegramApiException telegramApiException) {
				log.error("Can't send message", telegramApiException);
			}
		}
	}
	
	private void sendReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int categoryId, int messagePrevReport) throws TelegramApiException, IOException {
		sheets                  = workbook.createSheet("предложения");
		sheet                   = workbook.getSheetAt(0);
		List<Task> tasks        = daoFactory.getTaskDao().getTasksByTime(dateBegin, dateEnd, categoryId);
		if (tasks == null || tasks.size() == 0) {
			bot.execute(new DeleteMessage(chatId, messagePrevReport));
			bot.execute(new SendMessage(chatId, "За выбранный период заявки отсутствуют"));
			return;
		}
		BorderStyle     thin        = BorderStyle.THIN;
		short           black       = IndexedColors.BLACK.getIndex();
		XSSFCellStyle   styleTitle  = setStyle(workbook, thin, black, style);
		int             rowIndex    = 0;
		createTitle(styleTitle, rowIndex, Arrays.asList("№;ФИО Ответственного;ФИО Заявителя;Текст обращение;Дата заявления;Текст ответа;Статус;Департамент".split(Const.SPLIT)));
		List<List<String>> info     = tasks.stream().map(x -> {
			List<String> list       = new ArrayList<>();
			list.add(String.valueOf(x.getId()));
			list.add(userDao.getUserByChatId(x.getEmployeeId()).getFullName());
			list.add(x.getPeopleName());
			list.add(x.getTaskText());
			list.add(String.valueOf(x.getDateBegin()));
			list.add(taskArchiveDao.getTaskArchive(x.getId()).getText());
			list.add(taskArchiveDao.getTaskArchive(x.getId()).getTaskStatus());
			list.add(categoryDao.getCategoryById(x.getCategoryId()).getName());
			return list;
		}).collect(Collectors.toList());
		addInfo(info, rowIndex);
		sendFile(chatId, bot, dateBegin, dateEnd);
	}
	
	private void sendFile(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd) throws IOException, TelegramApiException {
		String fileName = "Отчеты за: " + DateUtil.getDayDate(dateBegin) +" - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
		String path = "C:\\test\\" + fileName;
		path += new Date().getTime();
		try(FileOutputStream stream = new FileOutputStream(path)){
			workbook.write(stream);
		} catch (IOException e) {
			log.error("Can't send file error", e);
		}
		sendFile(chatId,bot,fileName,path);
	}
	
	private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
		File file = new File(path);
		try(FileInputStream fileInputStream = new FileInputStream(file)) {
			bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
		}
		file.delete();
	}
	
	private void addInfo(List<List<String>> tasks, int rowIndex) {
		int cellIndex;
		for (List<String> task : tasks){
			sheets.createRow(++rowIndex);
			insertToRow(rowIndex, task, style);
		}
		cellIndex = 0;
		sheets.autoSizeColumn(cellIndex++);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.autoSizeColumn(cellIndex++);
	}
	
	private XSSFCellStyle setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
		style.setBorderTop(thin);
		style.setBorderBottom(thin);
		style.setBorderRight(thin);
		style.setBorderLeft(thin);
		style.setTopBorderColor(black);
		style.setRightBorderColor(black);
		style.setBottomBorderColor(black);
		style.setLeftBorderColor(black);
		BorderStyle tittle          = BorderStyle.MEDIUM;
		XSSFCellStyle styleTitle    = workbook.createCellStyle();
		styleTitle.setWrapText(true);
		styleTitle.setAlignment(HorizontalAlignment.CENTER);
		styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
		styleTitle.setBorderTop(tittle);
		styleTitle.setBorderBottom(tittle);
		styleTitle.setBorderRight(tittle);
		styleTitle.setBorderLeft(tittle);
		styleTitle.setTopBorderColor(black);
		styleTitle.setRightBorderColor(black);
		styleTitle.setBottomBorderColor(black);
		styleTitle.setLeftBorderColor(black);
		style.setFillForegroundColor(new XSSFColor(new Color(0, 52, 94)));
		return styleTitle;
	}
	
	private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
		sheets.createRow(rowIndex);
		insertToRow(rowIndex, title, styleTitle);
	}
	
	private void insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
		int cellIndex = 0;
		for (String cellValue : cellValues){
			addCellValue(row, cellIndex++, cellValue, cellStyle);
		}
	}
	
	private void addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
		sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
		sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
	}
	
	private String getString(String nullable) {
		if (nullable == null) return "";
		return nullable;
	}
}
