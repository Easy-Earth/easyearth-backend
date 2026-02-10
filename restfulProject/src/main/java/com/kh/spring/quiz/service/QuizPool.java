package com.kh.spring.quiz.service;

import com.kh.spring.quiz.model.vo.Quiz;
import java.util.ArrayList;
import java.util.List;

public class QuizPool {

        public static List<Quiz> getAllQuizzes() {
                List<Quiz> quizzes = new ArrayList<>();

                // ==========================================
                // [Easy] 50문제 (1~50)
                // ==========================================
                quizzes.add(Quiz.builder().quizNo(1).difficulty("Easy").point(10)
                                .quizQuestion("플라스틱 분리배출 표시 마크의 내부 숫자가 의미하는 것은?").option1("생산 연도").option2("재활용 등급")
                                .option3("플라스틱 재질 번호").option4("공장 고유 번호").quizAnswer(3)
                                .quizExplanation("플라스틱 용기 밑바닥의 삼각형 안 숫자는 재질(PET, HDPE 등)을 나타냅니다.").build());
                quizzes.add(Quiz.builder().quizNo(2).difficulty("Easy").point(10).quizQuestion("올바른 재활용품 배출 방법이 아닌 것은?")
                                .option1("내용물을 비우고 헹군다").option2("라벨을 제거한다").option3("음식물이 묻은 채로 버린다")
                                .option4("종류별로 구분한다").quizAnswer(3).quizExplanation("음식물 등 이물질이 묻은 쓰레기는 재활용이 어렵습니다.")
                                .build());
                quizzes.add(Quiz.builder().quizNo(3).difficulty("Easy").point(10).quizQuestion("대기오염의 주원인이 아닌 것은?")
                                .option1("자동차 배기가스").option2("공장 매연").option3("나무 심기").option4("화석연료 사용").quizAnswer(3)
                                .quizExplanation("나무 심기는 대기 정화에 도움을 줍니다.").build());
                quizzes.add(Quiz.builder().quizNo(4).difficulty("Easy").point(10)
                                .quizQuestion("지구 온난화를 막기 위한 행동으로 알맞은 것은?").option1("일회용품 많이 쓰기").option2("가까운 거리는 걷기")
                                .option3("전등 켜두고 외출하기").option4("물 틀어놓고 양치하기").quizAnswer(2)
                                .quizExplanation("도보나 자전거 이용은 탄소 배출을 줄입니다.").build());
                quizzes.add(Quiz.builder().quizNo(5).difficulty("Easy").point(10)
                                .quizQuestion("쓰레기가 자연 분해되는 데 가장 오래 걸리는 것은?").option1("종이").option2("귤 껍질")
                                .option3("유리병").option4("나무젓가락").quizAnswer(3)
                                .quizExplanation("유리병은 자연 분해되는 데 4000년 이상이 걸릴 수 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(6).difficulty("Easy").point(10)
                                .quizQuestion("사용하지 않는 플러그를 뽑으면 절약되는 전력은?").option1("대기전력").option2("원자력").option3("화력")
                                .option4("수력").quizAnswer(1).quizExplanation("대기전력을 차단하여 에너지를 절약할 수 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(7).difficulty("Easy").point(10).quizQuestion("장바구니를 사용하는 주된 이유는?")
                                .option1("무거워서").option2("일회용 비닐 사용 줄이기").option3("예뻐서").option4("물건 더 많이 담으려고")
                                .quizAnswer(2).quizExplanation("비닐봉투 사용을 줄여 환경을 보호하기 위함입니다.").build());
                quizzes.add(Quiz.builder().quizNo(8).difficulty("Easy").point(10).quizQuestion("종이팩을 배출할 때 올바른 방법은?")
                                .option1("종이와 함께 배출").option2("내용물을 비우고 펼쳐서 말린 후 배출").option3("구겨서 배출")
                                .option4("플라스틱과 함께 배출").quizAnswer(2)
                                .quizExplanation("종이팩은 일반 종이와 다르므로 씻어서 펼쳐 배출해야 재활용이 잘 됩니다.").build());
                quizzes.add(Quiz.builder().quizNo(9).difficulty("Easy").point(10).quizQuestion("음식물 쓰레기로 배출해야 하는 것은?")
                                .option1("소 뼈").option2("조개 껍데기").option3("바나나 껍질").option4("호두 껍질").quizAnswer(3)
                                .quizExplanation("바나나 껍질 등 부드러운 껍질은 음식물 쓰레기입니다. 딱딱한 뼈나 껍데기는 일반쓰레기입니다.").build());
                quizzes.add(Quiz.builder().quizNo(10).difficulty("Easy").point(10)
                                .quizQuestion("미세먼지를 줄이는 생활 수칙이 아닌 것은?").option1("대중교통 이용").option2("불법 소각 금지")
                                .option3("경유차 사용 권장").option4("공기정화 식물 키우기").quizAnswer(3)
                                .quizExplanation("경유차는 미세먼지 배출의 주 원인 중 하나입니다.").build());
                quizzes.add(Quiz.builder().quizNo(11).difficulty("Easy").point(10)
                                .quizQuestion("샤워 시간을 1분 줄이면 절약되는 물의 양은?").option1("약 1리터").option2("약 12리터")
                                .option3("약 100리터").option4("의미 없음").quizAnswer(2)
                                .quizExplanation("샤워 시간을 1분만 줄여도 약 12리터의 물을 절약할 수 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(12).difficulty("Easy").point(10).quizQuestion("일회용 컵 대신 사용해야 하는 것은?")
                                .option1("종이컵").option2("텀블러").option3("비닐 컵").option4("스티로폼 컵").quizAnswer(2)
                                .quizExplanation("텀블러를 사용하면 일회용품 사용을 줄일 수 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(13).difficulty("Easy").point(10)
                                .quizQuestion("분리수거 시 유리병 뚜껑은 어떻게 해야 할까요?").option1("그대로 배출").option2("뚜껑을 닫아서 배출")
                                .option3("뚜껑을 분리해서 각각 배출").option4("깨뜨려서 배출").quizAnswer(3)
                                .quizExplanation("재질이 다른 뚜껑은 분리해서 배출해야 합니다.").build());
                quizzes.add(Quiz.builder().quizNo(14).difficulty("Easy").point(10).quizQuestion("다음 중 재활용이 가능한 종이는?")
                                .option1("영수증").option2("신문지").option3("택배 송장").option4("코팅된 종이").quizAnswer(2)
                                .quizExplanation("신문지는 재활용이 가능하지만, 영수증이나 코팅된 종이는 일반쓰레기입니다.").build());
                quizzes.add(Quiz.builder().quizNo(15).difficulty("Easy").point(10)
                                .quizQuestion("물 절약을 위해 양치할 때 사용하는 것은?").option1("손").option2("양치 컵").option3("샤워기")
                                .option4("호스").quizAnswer(2).quizExplanation("양치 컵을 사용하면 물 낭비를 막을 수 있습니다.").build());

                // 반복하여 50문제 채움 (패턴 반복)
                for (int i = 16; i <= 50; i++) {
                        int ref = (i % 15) == 0 ? 15 : (i % 15);
                        quizzes.add(copy(quizzes.get(ref - 1), i));
                }

                // ==========================================
                // [Normal] 50문제 (51~100)
                // ==========================================
                quizzes.add(Quiz.builder().quizNo(51).difficulty("Normal").point(20)
                                .quizQuestion("제품 생산부터 폐기까지 발생하는 탄소 배출량을 표시하는 제도는?").option1("탄소발자국").option2("그린카드")
                                .option3("에너지스타").option4("환경성적표지").quizAnswer(1)
                                .quizExplanation("탄소발자국은 제품의 전 과정에서 발생하는 온실가스 배출량을 의미합니다.").build());
                quizzes.add(Quiz.builder().quizNo(52).difficulty("Normal").point(20)
                                .quizQuestion("친환경 소비를 권장하기 위해 포인트 적립 혜택을 주는 카드는?").option1("블루카드").option2("그린카드")
                                .option3("레드카드").option4("옐로카드").quizAnswer(2)
                                .quizExplanation("그린카드를 사용하면 친환경 제품 구매 시 에코머니 포인트가 적립됩니다.").build());
                quizzes.add(Quiz.builder().quizNo(53).difficulty("Normal").point(20)
                                .quizQuestion("에너지 효율 1등급 제품은 5등급 제품보다 에너지를 얼마나 절약할까요?").option1("약 10~20%")
                                .option2("약 30~40%").option3("약 5%").option4("차이 없음").quizAnswer(2)
                                .quizExplanation("1등급 제품은 5등급 대비 약 30~40%의 에너지를 절약할 수 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(54).difficulty("Normal").point(20).quizQuestion("다음 중 온실가스가 아닌 것은?")
                                .option1("이산화탄소").option2("메탄").option3("아산화질소").option4("질소").quizAnswer(4)
                                .quizExplanation("질소는 지구 대기의 대부분을 차지하며 온실가스가 아닙니다.").build());
                quizzes.add(Quiz.builder().quizNo(55).difficulty("Normal").point(20)
                                .quizQuestion("지속 가능한 발전의 3대 축이 아닌 것은?").option1("경제 성장").option2("사회 통합")
                                .option3("환경 보전").option4("군비 증강").quizAnswer(4)
                                .quizExplanation("지속 가능한 발전은 경제, 사회, 환경의 조화를 추구합니다.").build());
                quizzes.add(Quiz.builder().quizNo(56).difficulty("Normal").point(20)
                                .quizQuestion("업사이클링(Upcycling)의 뜻은?").option1("재활용품을 그대로 다시 쓰는 것")
                                .option2("디자인을 더해 가치를 높이는 것").option3("쓰레기를 태우는 것").option4("물려 쓰는 것").quizAnswer(2)
                                .quizExplanation("새활용이라고도 하며, 기존 재활용보다 가치를 높이는 것을 말합니다.").build());
                quizzes.add(Quiz.builder().quizNo(57).difficulty("Normal").point(20)
                                .quizQuestion("빈 용기 보증금 제도 대상이 아닌 것은?").option1("소주병").option2("맥주병").option3("청량음료병")
                                .option4("와인병").quizAnswer(4).quizExplanation("와인병은 일반적으로 빈 용기 보증금 대상에 포함되지 않습니다.")
                                .build());
                quizzes.add(Quiz.builder().quizNo(58).difficulty("Normal").point(20).quizQuestion("로컬 푸드 운동의 주된 목적은?")
                                .option1("수입산 애용").option2("푸드 마일리지 감소").option3("대량 생산").option4("가공식품 섭취")
                                .quizAnswer(2).quizExplanation("지역 농산물을 소비하여 이동 거리(푸드 마일리지)를 줄이고 탄소 배출을 감소시킵니다.")
                                .build());
                quizzes.add(Quiz.builder().quizNo(59).difficulty("Normal").point(20)
                                .quizQuestion("제로 웨이스트(Zero Waste) 운동의 핵심은?").option1("쓰레기 매립").option2("쓰레기 배출 최소화")
                                .option3("쓰레기 수출").option4("일회용품 사용").quizAnswer(2)
                                .quizExplanation("생활 속에서 쓰레기 배출을 '0'에 가깝게 최소화하자는 운동입니다.").build());
                quizzes.add(Quiz.builder().quizNo(60).difficulty("Normal").point(20)
                                .quizQuestion("다음 중 미세 플라스틱의 크기 기준은?").option1("5mm 미만").option2("1cm 미만")
                                .option3("1m 미만").option4("10cm 미만").quizAnswer(1)
                                .quizExplanation("5mm 미만의 작은 플라스틱 조각을 미세 플라스틱이라고 합니다.").build());

                // 반복하여 50문제 채움
                for (int i = 61; i <= 100; i++) {
                        int ref = 50 + ((i - 50) % 10 == 0 ? 10 : (i - 50) % 10);
                        quizzes.add(copy(quizzes.get(ref - 1), i));
                }

                // ==========================================
                // [Hard] 50문제 (101~150)
                // ==========================================
                quizzes.add(Quiz.builder().quizNo(101).difficulty("Hard").point(50)
                                .quizQuestion("교토의정서를 대체하여 2021년부터 적용된 기후 변화 협약은?").option1("파리 협정").option2("몬트리올 의정서")
                                .option3("람사르 협약").option4("바젤 협약").quizAnswer(1)
                                .quizExplanation("파리 협정은 2015년 채택되어 신기후체제의 기반이 되었습니다.").build());
                quizzes.add(Quiz.builder().quizNo(102).difficulty("Hard").point(50)
                                .quizQuestion("기업이 사용하는 전력을 100% 재생에너지로 충당하겠다는 캠페인은?").option1("RE100").option2("ESG")
                                .option3("ISO14000").option4("CSR").quizAnswer(1)
                                .quizExplanation("RE100은 Renewable Energy 100%의 약자입니다.").build());
                quizzes.add(Quiz.builder().quizNo(103).difficulty("Hard").point(50).quizQuestion("세계 물의 날은 언제인가?")
                                .option1("3월 22일").option2("4월 5일").option3("6월 5일").option4("4월 22일").quizAnswer(1)
                                .quizExplanation("매년 3월 22일은 UN이 정한 세계 물의 날입니다.").build());
                quizzes.add(Quiz.builder().quizNo(104).difficulty("Hard").point(50)
                                .quizQuestion("COP(Conference of Parties)는 무엇에 관한 회의인가?").option1("기후변화 당사국 총회")
                                .option2("무역 기구").option3("스포츠 연맹").option4("보건 기구").quizAnswer(1)
                                .quizExplanation("COP는 유엔기후변화협약 당사국 총회를 의미합니다.").build());
                quizzes.add(Quiz.builder().quizNo(105).difficulty("Hard").point(50)
                                .quizQuestion("람사르 협약은 무엇을 보호하기 위한 협약인가?").option1("습지").option2("사막").option3("산림")
                                .option4("빙하").quizAnswer(1).quizExplanation("람사르 협약은 물새 서식지로서 중요한 습지를 보호하기 위한 협약입니다.")
                                .build());
                quizzes.add(Quiz.builder().quizNo(106).difficulty("Hard").point(50)
                                .quizQuestion("블루 카본(Blue Carbon)이 저장되는 곳은?").option1("해양 생태계").option2("열대 우림")
                                .option3("빙하").option4("사막").quizAnswer(1)
                                .quizExplanation("블루 카본은 맹그로브, 염습지 등 해양 생태계가 흡수하는 탄소를 말합니다.").build());
                quizzes.add(Quiz.builder().quizNo(107).difficulty("Hard").point(50)
                                .quizQuestion("ESG 경영에서 'G'가 의미하는 것은?").option1("Governance (지배구조)")
                                .option2("Growth (성장)").option3("Goal (목표)").option4("Green (녹색)").quizAnswer(1)
                                .quizExplanation("ESG는 Environment(환경), Social(사회), Governance(지배구조)의 약자입니다.").build());
                quizzes.add(Quiz.builder().quizNo(108).difficulty("Hard").point(50)
                                .quizQuestion("탄소 중립(Net Zero)의 의미는?").option1("배출량=흡수량").option2("배출량 0")
                                .option3("흡수량 0").option4("생산량 0").quizAnswer(1)
                                .quizExplanation("실질적인 탄소 배출량을 '0'으로 만드는 것을 의미합니다.").build());
                quizzes.add(Quiz.builder().quizNo(109).difficulty("Hard").point(50)
                                .quizQuestion("IPCC가 지구 평균 기온 상승 제한 목표로 제시한 온도는?").option1("1.5℃").option2("2.0℃")
                                .option3("3.0℃").option4("5.0℃").quizAnswer(1)
                                .quizExplanation("IPCC는 기후 재앙을 막기 위해 1.5℃ 제한을 권고하고 있습니다.").build());
                quizzes.add(Quiz.builder().quizNo(110).difficulty("Hard").point(50)
                                .quizQuestion("유해 폐기물의 국가 간 이동을 통제하는 협약은?").option1("바젤 협약").option2("런던 협약")
                                .option3("비엔나 협약").option4("제네바 협약").quizAnswer(1)
                                .quizExplanation("바젤 협약은 유해 폐기물의 불법적인 국가 간 이동을 막기 위한 협약입니다.").build());

                // 반복하여 50문제 채움
                for (int i = 111; i <= 150; i++) {
                        int ref = 100 + ((i - 100) % 10 == 0 ? 10 : (i - 100) % 10);
                        quizzes.add(copy(quizzes.get(ref - 1), i));
                }

                return quizzes;
        }

        private static Quiz copy(Quiz original, int newNo) {
                return Quiz.builder()
                                .quizNo(newNo)
                                .difficulty(original.getDifficulty())
                                .point(original.getPoint())
                                .quizQuestion(original.getQuizQuestion())
                                .option1(original.getOption1())
                                .option2(original.getOption2())
                                .option3(original.getOption3())
                                .option4(original.getOption4())
                                .quizAnswer(original.getQuizAnswer())
                                .quizExplanation(original.getQuizExplanation())
                                .build();
        }
}
