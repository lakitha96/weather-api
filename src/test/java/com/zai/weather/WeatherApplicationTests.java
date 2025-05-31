package com.zai.weather;

import com.zai.weather.controller.WeatherReportControllerTest;
import com.zai.weather.provider.OpenWeatherMapProviderTest;
import com.zai.weather.provider.WeatherStackProviderTest;
import com.zai.weather.service.WeatherReportServiceImplTest;
import com.zai.weather.util.WebClientHelperTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;


@Suite
@IncludeEngines("junit-jupiter")
@SelectClasses({
		WeatherReportControllerTest.class,
		WeatherReportServiceImplTest.class,
		OpenWeatherMapProviderTest.class,
		WeatherStackProviderTest.class,
		WebClientHelperTest.class
})
public class WeatherApplicationTests { }
