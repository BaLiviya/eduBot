package baliviya.com.github.eduBot.service;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.UserDao;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.entity.standart.User;
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
public class UsersReportService {
	private Language            currentLanguage    = Language.ru;
	private DaoFactory          daoFactory  = DaoFactory.getInstance();
	private UserDao             userDao     = daoFactory.getUserDao();
	private XSSFWorkbook        workbook    = new XSSFWorkbook();
	private XSSFCellStyle       style       = workbook.createCellStyle();
	private Sheet               sheets;
	private Sheet               sheet;
	
	public void sendUserReport(Long chatId, DefaultAbsSender bot, int preview) {
		currentLanguage = LanguageService.getLanguage(chatId);
		try {
			sendReport(chatId,bot,preview);
		} catch (Exception e) {
			log.error("Can't create/send report", e);
			try {
				bot.execute(new SendMessage(chatId, "Report doesn't create"));
			} catch (TelegramApiException telegramApiException) {
				log.error("Can't send message",telegramApiException);
			}
		}
	}
	
	private void sendReport(Long chatId, DefaultAbsSender bot, int preview) throws TelegramApiException, IOException {
		sheets                      = workbook.createSheet("Предложения");
		sheet                       = workbook.getSheetAt(0);
		List<User> users            = userDao.getAll();
		if (users == null || users.size() == 0) {
			bot.execute(new DeleteMessage(chatId, preview));
			bot.execute(new SendMessage(chatId, "Список пользователей пуст"));
			return;
		}
		BorderStyle         thin        = BorderStyle.THIN;
		short               black       = IndexedColors.BLACK.getIndex();
		XSSFCellStyle       styleTitle  = setStyle(workbook, thin, black, style);
		int                 rowIndex    = 0;
		createTitle(styleTitle, rowIndex, Arrays.asList("Регистрационные данные;Телефон;Данные Telegram".split(Const.SPLIT)));
		List<List<String>> info         = users.stream().map(user -> {
			List<String> list           = new ArrayList<>();
			list.add(user.getFullName());
			list.add(user.getPhone());
			list.add(user.getUserName());
			return list;
		}).collect(Collectors.toList());
		addInfo(info, rowIndex);
		sendFile(chatId,bot);
	}
	
	private void sendFile(Long chatId, DefaultAbsSender bot) throws IOException, TelegramApiException {
		String fileName = "Список пользователей за: " + DateUtil.getDayDate(new Date()) + ".xlsx";
		String path = "C:\\test\\" + fileName;
		path += new Date().getTime();
		try(FileOutputStream stream = new FileOutputStream(path)){
			workbook.write(stream);
		} catch (IOException e) {
			log.error("Can't send file error", e);
		}
		sendFile(chatId,bot,fileName,path);
	}
	
	private void sendFile(Long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
		File file = new File(path);
		try(FileInputStream stream = new FileInputStream(file)){
			bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, stream));
		}
		file.delete();
	}
	
	private void addInfo(List<List<String>> users, int rowIndex) {
		int cellIndex;
		for (List<String> user : users){
			sheets.createRow(++rowIndex);
			insertToRow(rowIndex, user, style);
		}
		cellIndex = 0;
		sheets.autoSizeColumn(cellIndex++);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.setColumnWidth(cellIndex++, 4000);
		sheets.autoSizeColumn(cellIndex++);
	}
	
	private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
		sheets.createRow(rowIndex);
		insertToRow(rowIndex, title, styleTitle);
	}
	
	private void insertToRow(int rowIndex, List<String> cellValues, CellStyle cellStyle) {
		int cellIndex = 0;
		for (String cellValue :
				cellValues) {
			addCellValue(rowIndex, cellIndex++, cellValue, cellStyle);
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
}
