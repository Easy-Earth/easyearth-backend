package com.kh.spring.quiz.service;

import com.kh.spring.quiz.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

        private final QuizMapper quizMapper;
        private final java.util.List<com.kh.spring.quiz.model.vo.Quiz> quizList = new java.util.ArrayList<>();

        public QuizService(QuizMapper quizMapper) {
                this.quizMapper = quizMapper;
                initializeQuizData();
        }

        private void initializeQuizData() {
                // [Easy] 5문제 (환경 상식)
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(1).difficulty("Easy").point(10)
                                .quizQuestion("플라스틱 분리배출 표시 마크의 내부 숫자가 의미하는 것은?")
                                .option1("생산 연도").option2("재활용 등급").option3("플라스틱 재질 번호").option4("공장 고유 번호")
                                .quizAnswer(3).quizExplanation("플라스틱 용기 밑바닥의 삼각형 안 숫자는 재질(PET, HDPE 등)을 나타냅니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(2).difficulty("Easy").point(10)
                                .quizQuestion("올바른 재활용품 배출 방법이 아닌 것은?")
                                .option1("내용물을 비우고 헹군다").option2("라벨을 제거한다").option3("음식물이 묻은 채로 버린다")
                                .option4("종류별로 구분한다")
                                .quizAnswer(3).quizExplanation("음식물 등 이물질이 묻은 쓰레기는 재활용이 어렵습니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(3).difficulty("Easy").point(10)
                                .quizQuestion("대기오염의 주원인이 아닌 것은?")
                                .option1("자동차 배기가스").option2("공장 매연").option3("나무 심기").option4("화석연료 사용")
                                .quizAnswer(3).quizExplanation("나무 심기는 대기 정화에 도움을 줍니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(4).difficulty("Easy").point(10)
                                .quizQuestion("지구 온난화를 막기 위한 행동으로 알맞은 것은?")
                                .option1("일회용품 많이 쓰기").option2("가까운 거리는 걷기").option3("전등 켜두고 외출하기")
                                .option4("물 틀어놓고 양치하기")
                                .quizAnswer(2).quizExplanation("도보나 자전거 이용은 탄소 배출을 줄입니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(5).difficulty("Easy").point(10)
                                .quizQuestion("쓰레기가 자연 분해되는 데 가장 오래 걸리는 것은?")
                                .option1("종이").option2("귤 껍질").option3("유리병").option4("나무젓가락")
                                .quizAnswer(3).quizExplanation("유리병은 자연 분해되는 데 4000년 이상이 걸릴 수 있습니다.")
                                .build());

                // [Normal] 5문제 (환경 용어)
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(6).difficulty("Normal").point(20)
                                .quizQuestion("제품 생산부터 폐기까지 발생하는 탄소 배출량을 표시하는 제도는?")
                                .option1("탄소발자국").option2("그린카드").option3("에너지스타").option4("환경성적표지")
                                .quizAnswer(1).quizExplanation("탄소발자국은 제품의 전 과정에서 발생하는 온실가스 배출량을 의미합니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(7).difficulty("Normal").point(20)
                                .quizQuestion("친환경 소비를 권장하기 위해 포인트 적립 혜택을 주는 카드는?")
                                .option1("블루카드").option2("그린카드").option3("레드카드").option4("옐로카드")
                                .quizAnswer(2).quizExplanation("그린카드를 사용하면 친환경 제품 구매 시 에코머니 포인트가 적립됩니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(8).difficulty("Normal").point(20)
                                .quizQuestion("지속 가능한 발전을 위한 UN의 목표는?")
                                .option1("SDGs").option2("FIFA").option3("WHO").option4("UNESCO")
                                .quizAnswer(1).quizExplanation("SDGs는 Sustainable Development Goals(지속가능발전목표)의 약자입니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(9).difficulty("Normal").point(20)
                                .quizQuestion("미세먼지 비상저감조치 발령 시 제한되는 차량 등급은?")
                                .option1("배출가스 5등급").option2("전기차").option3("하이브리드차").option4("수소차")
                                .quizAnswer(1).quizExplanation("미세먼지가 심한 날에는 배출가스 5등급 차량의 운행이 제한됩니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(10).difficulty("Normal").point(20)
                                .quizQuestion("친환경 농산물 인증 마크가 아닌 것은?")
                                .option1("유기농").option2("무농약").option3("HACCP").option4("저탄소")
                                .quizAnswer(3).quizExplanation("HACCP은 식품안전관리인증기준으로, 친환경 농산물 인증과는 다릅니다.")
                                .build());

                // [Hard] 5문제 (심화 지식)
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(11).difficulty("Hard").point(50)
                                .quizQuestion("교토의정서를 대체하여 2021년부터 적용된 기후 변화 협약은?")
                                .option1("파리 협정").option2("몬트리올 의정서").option3("람사르 협약").option4("바젤 협약")
                                .quizAnswer(1).quizExplanation("파리 협정은 2015년 채택되어 신기후체제의 기반이 되었습니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(12).difficulty("Hard").point(50)
                                .quizQuestion("기업이 사용하는 전력을 100% 재생에너지로 충당하겠다는 캠페인은?")
                                .option1("RE100").option2("ESG").option3("ISO14000").option4("CSR")
                                .quizAnswer(1).quizExplanation("RE100은 Renewable Energy 100%의 약자입니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(13).difficulty("Hard").point(50)
                                .quizQuestion("세계 물의 날은 언제인가?")
                                .option1("3월 22일").option2("4월 5일").option3("6월 5일").option4("4월 22일")
                                .quizAnswer(1).quizExplanation("매년 3월 22일은 UN이 정한 세계 물의 날입니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(14).difficulty("Hard").point(50)
                                .quizQuestion("COP(Conference of Parties)는 무엇에 관한 회의인가?")
                                .option1("기후변화 당사국 총회").option2("무역 기구").option3("스포츠 연맹").option4("보건 기구")
                                .quizAnswer(1).quizExplanation("COP는 유엔기후변화협약 당사국 총회를 의미합니다.")
                                .build());
                quizList.add(com.kh.spring.quiz.model.vo.Quiz.builder()
                                .quizNo(15).difficulty("Hard").point(50)
                                .quizQuestion("람사르 협약은 무엇을 보호하기 위한 협약인가?")
                                .option1("습지").option2("사막").option3("산림").option4("빙하")
                                .quizAnswer(1).quizExplanation("람사르 협약은 물새 서식지로서 중요한 습지를 보호하기 위한 협약입니다.")
                                .build());
        }

        public java.util.List<com.kh.spring.quiz.model.vo.Quiz> getQuizByDifficulty(String difficulty) {
                java.util.List<com.kh.spring.quiz.model.vo.Quiz> result = new java.util.ArrayList<>();
                for (com.kh.spring.quiz.model.vo.Quiz q : quizList) {
                        if (q.getDifficulty().equalsIgnoreCase(difficulty)) {
                                result.add(q);
                        }
                }
                return result;
        }
}
