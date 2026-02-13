package com.kh.spring.attendance.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.attendance.model.vo.Attendance;
import com.kh.spring.attendance.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 출석 체크 API
     * POST /attendance/check
     * param: userId (로그인 구현 시 세션/토큰에서 가져와야 하지만, 일단 파라미터로 받음)
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAttendance(@RequestParam int userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int earnedPoints = attendanceService.checkAttendance(userId);

            if (earnedPoints == -1) {
                response.put("status", "fail");
                response.put("message", "이미 오늘 출석 체크를 완료했습니다.");
            } else {
                response.put("status", "success");
                response.put("earnedPoints", earnedPoints);
                response.put("message", "출석 체크 완료! " + earnedPoints + "P가 지급되었습니다.");
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "출석 체크 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 출석 내역 조회 API (캘린더용)
     * GET /attendance/list
     * param: userId, yearMonth(YYYY-MM)
     */
    @GetMapping("/list")
    public ResponseEntity<List<Attendance>> getAttendanceList(
            @RequestParam int userId,
            @RequestParam(required = false) String yearMonth) {

        if (yearMonth == null) {
            yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        List<Attendance> list = attendanceService.getAttendanceHistory(userId, yearMonth);
        return ResponseEntity.ok(list);
    }

    /**
     * [테스트 전용] 포인트 강제 지급 API
     * POST /attendance/grant-points
     */
    @PostMapping("/grant-points")
    public ResponseEntity<String> grantPoints(@RequestParam int userId, @RequestParam int points) {
        boolean success = attendanceService.grantAdminPoints(userId, points);
        return success ? ResponseEntity.ok("포인트 획득 성공 (총 " + points + "P)") : ResponseEntity.badRequest().body("포인트 획득 실패");
    }

    /**
     * [테스트 전용] 로그인 아이디로 포인트 강제 지급 API
     * POST /attendance/grant-points-by-loginid
     */
    @PostMapping("/grant-points-by-loginid")
    public ResponseEntity<String> grantPointsByLoginId(@RequestParam String loginId, @RequestParam int points) {
        boolean success = attendanceService.grantAdminPointsByLoginId(loginId, points);
        return success ? ResponseEntity.ok("아이디 [" + loginId + "]님에게 포인트 지급 성공 (총 " + points + "P)") : ResponseEntity.badRequest().body("포인트 지급 실패");
    }
}
