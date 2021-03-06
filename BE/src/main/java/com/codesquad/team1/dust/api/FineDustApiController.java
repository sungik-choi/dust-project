package com.codesquad.team1.dust.api;

import com.codesquad.team1.dust.domain.DustStatus;
import com.codesquad.team1.dust.domain.Forecast;
import com.codesquad.team1.dust.domain.Image;
import com.codesquad.team1.dust.domain.StationLocation;
import com.codesquad.team1.dust.util.ResourceUtils;
import com.codesquad.team1.dust.util.KakaoAPIUtils;
import com.codesquad.team1.dust.util.PublicAPIUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FineDustApiController {

    private static final Logger log = LoggerFactory.getLogger(FineDustApiController.class);

    @GetMapping("/locations")
    public List<StationLocation> showStationLocations() {
        List<StationLocation> stationLocations = new ArrayList<>();

        stationLocations.add(new StationLocation("강남구", "서울 강남구 학동로 426강남구청 별관 1동"));
        stationLocations.add(new StationLocation("구로구", "서울 구로구 가마산로 27길 45구로고등학교"));

        log.debug("list: {}", stationLocations);

        return stationLocations;
    }

    @GetMapping("/{stationName}/daily-dust-status")
    public List<DustStatus> showDailyDustStatus(@PathVariable String stationName) throws URISyntaxException, JsonProcessingException {
        List<DustStatus> dustStatusList = PublicAPIUtils.getDailyDustStatusJSONArray(stationName);

        log.debug("stationName: {}", stationName);
        log.debug("dustStatusList: {}", dustStatusList);

        return dustStatusList;
    }

    @GetMapping("/location/@={latitude},{longitude}")
    public StationLocation showNearestMeasureStationLocation(@PathVariable String latitude,
                                                             @PathVariable String longitude) throws URISyntaxException, JsonProcessingException {
        log.debug("위도: {}, 경도: {}", latitude, longitude);

        StationLocation stationLocation = PublicAPIUtils.getNearestStationLocation(KakaoAPIUtils.getTmCoordinateSystem(latitude, longitude));
        log.debug("stationLocation: {}", stationLocation);

        return stationLocation;
    }

    @GetMapping("/forecast")
    public Forecast showForecastOfFineDust() throws URISyntaxException, JsonProcessingException {
        String today = LocalDate.now().toString();
        JsonNode forecastObject = PublicAPIUtils.getForecastJSONObject(today);
        List<Image> images = ResourceUtils.getImages();
        Forecast forecast = new Forecast(forecastObject, images);

        log.debug("forecast: {}", forecast);

        return forecast;
    }

    @GetMapping("/daily-dust-status/@={latitude},{longitude}")
    public List<DustStatus> showDailyDustStatusOfGPS(@PathVariable String latitude,
                                                     @PathVariable String longitude) throws URISyntaxException, JsonProcessingException {
        log.debug("위도: {}, 경도: {}", latitude, longitude);

        StationLocation stationLocation = PublicAPIUtils.getNearestStationLocation(KakaoAPIUtils.getTmCoordinateSystem(latitude, longitude));
        log.debug("stationLocation: {}", stationLocation);

        List<DustStatus> dustStatusList = PublicAPIUtils.getDailyDustStatusJSONArray(stationLocation.getStationName());
        log.debug("dustStatusList: {}", dustStatusList);

        return dustStatusList;
    }
}
