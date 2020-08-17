package baliviya.com.github.eduBot.util.XChart;

import baliviya.com.github.eduBot.dao.DaoFactory;
import baliviya.com.github.eduBot.dao.impl.QuestDao;
import baliviya.com.github.eduBot.dao.impl.SurveyDao;
import baliviya.com.github.eduBot.entity.custom.Quest;
import baliviya.com.github.eduBot.entity.custom.Survey;
import baliviya.com.github.eduBot.entity.enums.Language;
import baliviya.com.github.eduBot.util.Const;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BarChart implements ExampleChart<CategoryChart> {

    private DefaultAbsSender bot;
    private long chatId;
    private Date dateBegin;
    private Date dateEnd;
    private DaoFactory factory = DaoFactory.getInstance();
    private SurveyDao surveyDao = factory.getSurveyDao();
    private QuestDao questDao = factory.getQuestDao();


    public BarChart(DefaultAbsSender bot, long chatId, Date dateBegin, Date dateEnd){
        this.bot = bot;
        this.chatId = chatId;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
    }

    @Override
    public CategoryChart getChart() throws TelegramApiException, IOException {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(500).title("Экспресс опрос").yAxisTitle("").build();
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(100.0);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        chart.getStyler().setLegendLayout(Styler.LegendLayout.Vertical);
        List<Quest> question = questDao.getAll();
        int count = surveyDao.getCount();
        List<Survey> all = surveyDao.getAll();
        List<Integer> allAnswersCount = new ArrayList<>();
        for(int index = 0; index < all.size(); index++){
            if (allAnswersCount.size() == 0){
                Arrays.asList(all.get(index).getAnswers().split(Const.SPLIT)).forEach( e -> allAnswersCount.add(Integer.parseInt(e)));
            } else{
                List<String> list = Arrays.asList(all.get(index).getAnswers().split(Const.SPLIT));
                for (int indexAnswerCount = 0; indexAnswerCount < allAnswersCount.size(); indexAnswerCount++){
                    allAnswersCount.set(indexAnswerCount, allAnswersCount.get(indexAnswerCount) + Integer.parseInt(list.get(indexAnswerCount)));
                }
            }
        }
        for(int index = 0; index < question.size(); index++){
            chart.addSeries(question.get(index).getName(), Arrays.asList(0), Arrays.asList(allAnswersCount.get(index) * 100 / count));
        }
        try{
            BitmapEncoder.saveJPGWithQuality(chart,"/photo.jpg", 0.95f);
        } catch (Exception e){
            e.printStackTrace();
        }
        try(FileInputStream fileInputStream = new FileInputStream(new File("/photo.jpg"))){
            bot.execute(new SendPhoto().setPhoto("photo",fileInputStream).setChatId(chatId));
        }

        Path path = Paths.get("./photo.jpg");
        Files.delete(path);
        return chart;

    }

    @Override
    public String getExampleChartName() {
        return getClass().getSimpleName() + "- Missing Point in Series";
    }
}
