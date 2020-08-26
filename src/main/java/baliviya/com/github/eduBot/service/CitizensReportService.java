package baliviya.com.github.eduBot.service;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.CitizensEmployeeDao;
import baliviya.com.github.eduBot.dao.impl.CitizensRegistrationDao;
import baliviya.com.github.eduBot.dao.impl.UserDao;
import baliviya.com.github.eduBot.entity.custom.CitizensRegistration;
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
public class CitizensReportService {
	
	private XSSFWorkbook                    workbook                = new XSSFWorkbook();
	private XSSFCellStyle                   style                   = workbook.createCellStyle();
	private Sheet                           sheets;
	private Sheet                           sheet;
	private Language                        currentLanguage         = Language.ru;
	private DaoFactory                      daoFactory              = DaoFactory.getInstance();
	private CitizensRegistrationDao         citizensRegistrationDao = daoFactory.getCitizensRegistrationDao();
	private CitizensEmployeeDao             citizensEmployeeDao     = daoFactory.getCitizensEmployeeDao();
	private UserDao                         userDao                 = daoFactory.getUserDao();
	
	public void sendCitizensReport(Long chatId, DefaultAbsSender bot, Date start, Date end, int preview) {
		currentLanguage = LanguageService.getLanguage(chatId);
		try {
			sendCitizenReport(chatId, bot, start, end, preview);
		} catch (Exception e) {
			log.error("Can't create/send report", e);
			try {
				bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
			} catch (TelegramApiException telegramApiException) {
				log.error("Can't send message", telegramApiException);
			}
		}
	}
	
	private void sendCitizenReport(Long chatId, DefaultAbsSender bot, Date start, Date end, int preview) throws TelegramApiException, IOException {
		sheets                  = workbook.createSheet("Зарегестрированных");
		sheet                   = workbook.getSheetAt(0);
		List<CitizensRegistration> registrations    = citizensRegistrationDao.getRegistrationByTime(start, end, citizensEmployeeDao.getByChatId(chatId).getReceptionId());
		if (registrations == null || registrations.size() == 0) {
			bot.execute(new DeleteMessage(chatId, preview));
			bot.execute(new SendMessage(chatId, "За выбранный период заявки отсутствуют"));
			return;
		}
		BorderStyle     thin                = BorderStyle.THIN;
		short           black               = IndexedColors.BLACK.getIndex();
		XSSFCellStyle   styleTitle          = setStyle(workbook, thin, black, style);
		int             rowIndex            = 0;
		createTitle(styleTitle, rowIndex, Arrays.asList("ФИО;Контактный номер;Характер вопроса;Статус;Дата и время; Дата регистрации".split(Const.SPLIT)));
		List<List<String>> info             = registrations.stream().map(citizensRegistration -> {
			List<String> list               = new ArrayList<>();
			list.add(userDao.getUserByChatId(citizensRegistration.getChatId()).getFullName());
			list.add(userDao.getUserByChatId(citizensRegistration.getChatId()).getPhone());
			list.add(citizensRegistration.getQuestion());
			list.add(citizensRegistration.getStatus());
			list.add(DateUtil.getDayDate(citizensRegistration.getCitizensDate()) + " " + citizensRegistration.getCitizensTime());
			list.add(DateUtil.getDayDate(citizensRegistration.getDate()));
			return list;
		}).collect(Collectors.toList());
		addInfo(info, rowIndex);
		sendFile(chatId, bot, start, end);
	}
	
	private void sendFile(Long chatId, DefaultAbsSender bot, Date start, Date end) throws TelegramApiException, IOException {
		String fileName = "Регистрации за: " + DateUtil.getDayDate(start) + " - " + DateUtil.getDayDate(end) + ".xlsx";
		String path     = "C:\\test\\" + fileName;
		path            += new Date().getTime();
		try (FileOutputStream stream = new FileOutputStream(path)) {
			workbook.write(stream);
		} catch (IOException e) {
			log.error("Can't send File error: ", e);
		}
		sendFile(chatId, bot, fileName, path);
	}
	private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws TelegramApiException, IOException {
		File file = new File(path);
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
		}
		file.delete();
	}
	
	private void addInfo(List<List<String>> reports, int rowIndex) {
		for (List<String> report : reports){
			sheets.createRow(++rowIndex);
			insertToRow(rowIndex, report, style);
		}
		for (int index = 0; index < 6; index ++){
			sheets.autoSizeColumn(index);
		}
	}
	
	private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
		sheets.createRow(rowIndex);
		insertToRow(rowIndex, title, styleTitle);
	}
	
	private void insertToRow(int rowIndex, List<String> cellValues, CellStyle cellStyle) {
		int cellIndex = 0;
		for (String cellValue: cellValues){
			addCellValues(rowIndex, cellIndex++, cellValue, cellStyle);
		}
	}
	
	private void addCellValues(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
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
