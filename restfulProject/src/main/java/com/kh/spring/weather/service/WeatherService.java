package com.kh.spring.weather.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kh.spring.weather.model.vo.DustDto;
import com.kh.spring.weather.model.vo.ObsDto;
import com.kh.spring.weather.model.vo.UvDto;

@Service
public class WeatherService {

    public List<ObsDto> getObsList() {
        StringBuilder response = new StringBuilder();

        try {
            // 1. API 호출 설정
            URL url = new URL(
                    "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm3.php?tm1=202601280000&tm2=202601290000&stn=108&help=0&authKey=KaG2mDn1S7ihtpg59Su46A");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            
            // 2. 응답 읽기 (줄바꿈 포함)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"))) { 
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n"); // 줄바꿈을 꼭 넣어줘야 나중에 분리가 가능합니다.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // 에러 시 빈 리스트 반환
        }

        // 3. 파싱 로직 호출
        return parseTextToObsList(response.toString());
    }

    // 텍스트를 DTO 리스트로 변환하는 핵심 로직
    private List<ObsDto> parseTextToObsList(String rawData) {
        return rawData.lines()
        .filter(line -> !line.startsWith("#")) // 주석 라인 제거
        .filter(line -> line.trim().length() > 50) // 알맹이 없는 짧은 줄 제거
        .map(line -> {
            String[] t = line.trim().split("\\s+"); // 여러 공백을 하나로 처리하여 분리
            
            // 빌더를 사용하여 DTO에 값 담기 (인덱스 순서 주의)
            return ObsDto.builder()
            .tm(t[0])
            .stn(parseInt(t[1]))
            .wd(parseInt(t[2]))
            .ws(parseDouble(t[3]))
            .pa(parseDouble(t[7]))
            .ps(parseDouble(t[8]))
            .ta(parseDouble(t[11])) // 12번째: 기온
            .td(parseDouble(t[12])) // 13번째: 이슬점
            .hm(parseDouble(t[13])) // 14번째: 습도
            .vs(parseInt(t[32])) // 33번째: 시정
            .ts(parseDouble(t[36])) // 37번째: 지면온도
            .build();
        })
        .collect(Collectors.toList());
    }
    // --- 결측치(-9, -9.0) 및 빈값 처리를 위한 헬퍼 메소드 ---
    private Double parseDouble(String s) {
        if (s == null || s.equals("-9") || s.equals("-9.0") || s.equals("-"))
            return null;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInt(String s) {
        if (s == null || s.equals("-9") || s.equals("-")) return null;
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }
    
    public List<DustDto> getDustList() {
        //https://apihub.kma.go.kr/api/typ01/url/kma_pm10.php?tm1=202601280000&tm2=202601290000&stn=108&authKey=KaG2mDn1S7ihtpg59Su46A
        StringBuilder response = new StringBuilder();
        
        try {
            // 1. API 호출 설정
            URL url = new URL(
                "https://apihub.kma.go.kr/api/typ01/url/kma_pm10.php?tm1=202601280000&tm2=202601290000&stn=108&authKey=KaG2mDn1S7ihtpg59Su46A");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            
            // 2. 응답 읽기 (줄바꿈 포함)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"))) { 
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n"); // 줄바꿈을 꼭 넣어줘야 나중에 분리가 가능합니다.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // 에러 시 빈 리스트 반환
        }
        System.out.println(response.toString());
        // 3. 파싱 로직 호출
        return parseTextToDustList(response.toString());
    }

    // 텍스트를 DTO 리스트로 변환하는 핵심 로직
    private List<DustDto> parseTextToDustList(String rawData) {
        return rawData.lines()
                .filter(line -> !line.startsWith("#")) // 주석 라인 제거
                .filter(line -> line.trim().length() > 20) // 알맹이 없는 짧은 줄 제거 (길이 완화)
                .map(line -> {
                    // 콤마로 분리
                    String[] t = line.split(",", -1); 

                    // 빌더를 사용하여 DTO에 값 담기 (인덱스 순서 주의)
                    return DustDto.builder()
                            .tm(t[0].trim())
                            .stnId(parseInt(t[1].trim()))
                            .pm10(parseInt(t[2].trim()))
                            .flag(t[3].trim())
                            .mqc(t.length > 5 ? t[5].replace("=", "").trim() : "") // 마지막 = 제거
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<UvDto> getUvList() {
        //https://apihub.kma.go.kr/api/typ01/url/kma_sfctm_uv.php?tm=20260128&stn=108&help=1&authKey=KaG2mDn1S7ihtpg59Su46A
        StringBuilder response = new StringBuilder();
        
        try {
            // 1. API 호출 설정
            URL url = new URL(
                "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm_uv.php?tm=20260128&stn=108&help=1&authKey=KaG2mDn1S7ihtpg59Su46A");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            
            // 2. 응답 읽기 (줄바꿈 포함)
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "EUC-KR"))) { 
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n"); // 줄바꿈을 꼭 넣어줘야 나중에 분리가 가능합니다.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // 에러 시 빈 리스트 반환
        }
        System.out.println(response.toString());
        // 3. 파싱 로직 호출
        return parseTextToUvList(response.toString());
    }

    // 텍스트를 DTO 리스트로 변환하는 핵심 로직
    private List<UvDto> parseTextToUvList(String rawData) {
        return rawData.lines()
                .filter(line -> !line.startsWith("#")) // 주석 라인 제거
                .filter(line -> !line.startsWith("|")) // 헤더 라인 제거
                .filter(line -> line.trim().length() > 20) // 알맹이 없는 짧은 줄 제거 (길이 완화)
                .map(line -> {
                    // 공백으로 분리
                    String[] t = line.trim().split("\\s+");

                    // 빌더를 사용하여 DTO에 값 담기 (인덱스 순서 주의)
                    return UvDto.builder()
                            .tm(t[0])
                            .stn(parseInt(t[1]))
                            .uvb(parseDouble(t[2]))
                            .uva(parseDouble(t[3]))
                            .euv(parseDouble(t[4]))
                            .uvBIndex(parseDouble(t[5]))
                            .uvAIndex(parseDouble(t[6]))
                            .temp1(parseDouble(t[7]))
                            .temp2(parseDouble(t[8]))
                            .build();
                })
                .collect(Collectors.toList());
    }

}
