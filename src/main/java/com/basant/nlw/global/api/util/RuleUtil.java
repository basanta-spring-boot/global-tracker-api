package com.basant.nlw.global.api.util;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.basant.nlw.global.api.constant.Constants;
import com.basant.nlw.global.api.exception.NewsApiException;
import com.basanta.nlw.global.api.dto.DurationTimeWrapper;
import com.basanta.nlw.global.api.dto.Location;
import com.basanta.nlw.global.api.dto.NewsResponse;
import com.basanta.nlw.global.api.dto.NewsSourceResponse;
import com.basanta.nlw.global.api.search.dto.NearestSearchResponse;
import com.basanta.nlw.global.api.search.dto.SearchResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Component
@PropertySource(value = { "classpath:googleMap.properties", "classpath:news.properties" })
public class RuleUtil {
	@Autowired
	private Environment environment;
	private RestTemplate template;
	ObjectMapper mapper = null;
	HttpHeaders headers = null;

	private static String API_KEY = "";
	private static String URL = "";
	@Value("${news.api.root.url}")
	private String API_URL;
	@Value("${news.api.key}")
	private String NEWS_API_KEY;
	@Value("${news.api.source.url}")
	private String NEWS_SOURCE_URL;

	@PostConstruct
	public void init() {
		template = new RestTemplate();
		mapper = new ObjectMapper();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		template.getMessageConverters().add(new StringHttpMessageConverter());
	}

	public String getAddressDetails(double lat, double lan)
			throws JsonParseException, JsonMappingException, IOException {
		String jsonExpression = "$.results[0].formatted_address";
		API_KEY = environment.getProperty(Constants.GET_ADDRESS_API_KEY);
		URL = environment.getProperty(Constants.GET_ADDRESS_URL) + lat + Constants.COMMA_SEPARATOR + lan
				+ Constants.KEY_CONSTANT + API_KEY;
		String result = template.getForObject(URL, String.class);
		String formattedAddress = JsonPath.parse(result).read(jsonExpression, String.class);
		return formattedAddress;
	}

	public DurationTimeWrapper getDuration(String source, String destination)
			throws JsonParseException, JsonMappingException, IOException {
		String distance_JsonExpression = "$.routes[0].legs[0].distance.text";
		String duration_JsonExpression = "$.routes[0].legs[0].duration.text";
		API_KEY = environment.getProperty(Constants.GET_DURATION_API_KEY);
		URL = environment.getProperty(Constants.GET_DURATION_URL) + source + Constants.COMMA_SEPARATOR
				+ Constants.DESTINATION_CONSTANT + destination + Constants.KEY_CONSTANT + API_KEY;
		String result = template.getForObject(URL, String.class);
		String distance = JsonPath.parse(result).read(distance_JsonExpression, String.class);
		String duration = JsonPath.parse(result).read(duration_JsonExpression, String.class);
		return new DurationTimeWrapper(distance, duration);

	}

	public Location getLatitudeLongitude(String address) throws JsonParseException, JsonMappingException, IOException {
		String jsonExpression = "$.results[0].geometry.location";
		API_KEY = environment.getProperty(Constants.GET_LATLANG_API_KEY);
		URL = environment.getProperty(Constants.GET_LATLANG_URL) + address + Constants.KEY_CONSTANT + API_KEY;
		String result = template.getForObject(URL, String.class);
		Location location = JsonPath.parse(result).read(jsonExpression, Location.class);
		return location;
	}

	public List<NearestSearchResponse> getNearestPlace(String searchType, String location)
			throws JsonParseException, JsonMappingException, IOException {
		API_KEY = environment.getProperty(Constants.GET_NEAREST_PLACE_API_KEY);
		URL = environment.getProperty(Constants.GET_NEAREST_PLACE_URL) + searchType + "+in+" + location
				+ Constants.KEY_CONSTANT + API_KEY;
		String result = template.getForObject(URL, String.class);
		SearchResponse response = mapper.readValue(result, SearchResponse.class);
		List<NearestSearchResponse> resp = ServiceUtils.getSearchInfo(response, searchType);
		return resp;
	}

	// WEATHER-API
	public com.basanta.nlw.global.api.dto.WeatherResponse getWeatherInfo(String location)
			throws com.basant.nlw.global.api.exception.WeatherException {
		String API_KEY = "8d2d7acbf163f320";
		String URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/CA/" + location + ".json";
		String result = template.getForObject(URL, String.class);
		// System.out.println(result);
		com.basanta.nlw.global.api.dto.WeatherResponse response = null;
		try {
			response = mapper.readValue(result, com.basanta.nlw.global.api.dto.WeatherResponse.class);
		} catch (IOException e) {
			throw new com.basant.nlw.global.api.exception.WeatherException(
					"Weather Forecast Service Failed..Try Again !");
		}
		return response;
	}
	// NEWS-API

	public NewsResponse getNews(String source, String sortBy) throws NewsApiException {
		String URL = API_URL + Constants.SOURCE + Constants.EQUAL + source + Constants.AND + Constants.SORTBY
				+ Constants.EQUAL + sortBy + Constants.AND + Constants.API_KEY + Constants.EQUAL + NEWS_API_KEY;
		NewsResponse response = null;
		try {
			String result = template.getForObject(URL, String.class);
			response = mapper.readValue(result, NewsResponse.class);
		} catch (Exception e) {
			throw new NewsApiException("News API Service Gateway Failed");
		}
		return response;
	}

	public NewsSourceResponse getNewsSources() throws NewsApiException {
		String URL = NEWS_SOURCE_URL;
		NewsSourceResponse response = null;
		try {
			String result = template.getForObject(URL, String.class);
			response = mapper.readValue(result, NewsSourceResponse.class);
		} catch (Exception e) {
			throw new NewsApiException("News API Service Gateway Failed");
		}
		return response;
	}
}
