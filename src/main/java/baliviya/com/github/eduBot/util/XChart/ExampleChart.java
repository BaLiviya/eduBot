package baliviya.com.github.eduBot.util.XChart;


import org.knowm.xchart.internal.chartpart.Chart;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public interface ExampleChart<C extends Chart<?, ?>> {
    C getChart() throws TelegramApiException, IOException;

    String getExampleChartName();
}
