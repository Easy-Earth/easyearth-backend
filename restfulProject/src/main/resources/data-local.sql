-- ─── Point Wallet 테스트 유저 (userId=1) ───
INSERT INTO POINT_WALLET (MEMBER_ID, NOW_POINT, TOTAL_EARNED, TOTAL_SPENT, UPDATED_AT) VALUES (1, 2000, 0, 0, CURRENT_TIMESTAMP);

-- ─── Quiz 테스트 데이터 (총 150문항) ───
-- [Easy] 1~50: 실생활 분리배출 및 에너지 절약
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (1, 'Easy', 10, '투명 페트병을 분리배출할 때 가장 올바른 방법은?', '라벨을 뗀 후 압착해서 배출', '그대로 버리기', '가위로 잘라서 배출', '발로 밟아서 그냥 배출', 1, '이물질을 제거하고 라벨을 떼는 것이 재활용의 핵심입니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (2, 'Easy', 10, '음식물이 묻어 지워지지 않는 배달용 플라스틱 용기는?', '플라스틱 수거함', '일반 쓰레기(종량제)', '종이 수거함', '비닐 수거함', 2, '오염된 용기는 재활용 공정을 방해하므로 일반쓰레기로 배출해야 합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (3, 'Easy', 10, '안 쓰는 가전제품의 플러그를 뽑으면 어떤 효과가 있나요?', '제품 고장 방지', '대기전력 차단 및 에너지 절약', '인테리어 효과', '청소 시간 단축', 2, '대기전력은 전체 가정 에너지 소비의 상당 부분을 차지합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (4, 'Easy', 10, '사용한 종이컵은 어디에 버려야 재활용이 가능할까요?', '일반 종이와 함께', '종이팩 전용 수거함', '플라스틱함', '수거함 없음', 2, '종이컵은 내부 코팅 처리가 되어 있어 일반 종이와 따로 모아야 합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (5, 'Easy', 10, '외출 시 전등을 끄는 습관이 환경에 주는 영향은?', '전등 수명 단축', '탄소 배출 감소 및 에너지 절약', '어둠을 즐김', '도둑 방지', 2, '불필요한 전력 소모를 줄이는 것이 환경 보호의 시작입니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (6, 'Easy', 10, '양치질을 할 때 물을 아끼는 가장 좋은 방법은?', '빨리 하기', '양치 컵 사용하기', '침으로만 하기', '틀어놓기', 2, '컵을 쓰면 낭비되는 물을 70% 이상 줄일 수 있습니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (7, 'Easy', 10, '우유팩을 깨끗이 씻어 배출하면 무엇으로 재활용되나요?', '화장지 또는 키친타월', '철근', '자동차 타이어', '유어서', 1, '우유팩은 고급 펄프로 만들어져 질 좋은 화장지의 원료가 됩니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (8, 'Easy', 10, '장 볼 때 비닐봉지 사용을 줄이기 위한 대안은?', '현금 사용', '장바구니(에코백)', '카드 결제', '대량 구매', 2, '비닐 쓰레기를 줄이기 위해 다회용 장바구니 사용을 생활화합시다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (9, 'Easy', 10, '폐건전지는 어디에 버려야 환경 오염을 막을 수 있나요?', '일반 쓰레기', '폐건전지 전용 수거함', '음식물 쓰레기', '플라스틱함', 2, '건전지의 중금속 유출을 막기 위해 반드시 전용 수거함에 버려야 합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (10, 'Easy', 10, '과자 봉지나 라면 봉지는 어떤 수거함에 버려야 할까요?', '종이류', '비닐류', '플라스틱류', '캔류', 2, '깨끗한 비닐은 비닐류로, 오염된 비닐은 일반쓰레기로 배출합니다.', CURRENT_TIMESTAMP);
-- (11~50 Easy placeholders with variatons, No numbering in question)
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) 
SELECT x+10, 'Easy', 10, 
       CASE WHEN MOD(x, 5) = 1 THEN '아이스팩을 가장 올바르게 처리하는 방법은?'
            WHEN MOD(x, 5) = 2 THEN '알루미늄 캔을 배출할 때 좋은 방법은?'
            WHEN MOD(x, 5) = 3 THEN '냉장고의 적정 식료품 보관 용량은?'
            WHEN MOD(x, 5) = 4 THEN '에어컨 필터를 청소하면 생기는 효과는?'
            ELSE '샤워 시간을 1분 줄이면 아낄 수 있는 물의 양은?' END,
       CASE WHEN MOD(x, 5) = 1 THEN '그대로 쓰레기통' WHEN MOD(x, 5) = 2 THEN '그냥 버림' WHEN MOD(x, 5) = 3 THEN '가득 채우기' WHEN MOD(x, 5) = 4 THEN '냉방력 약화' ELSE '약 1L' END,
       CASE WHEN MOD(x, 5) = 1 THEN '지자체 전용 수거함' WHEN MOD(x, 5) = 2 THEN '압착해서 배출' WHEN MOD(x, 5) = 3 THEN '60~70%만 채우기' WHEN MOD(x, 5) = 4 THEN '에너지 효율 상승' ELSE '약 12L' END,
       CASE WHEN MOD(x, 5) = 1 THEN '변기에 버림' WHEN MOD(x, 5) = 2 THEN '이물질 포함 배출' WHEN MOD(x, 5) = 3 THEN '비워두기' WHEN MOD(x, 5) = 4 THEN '전력 소모 증가' ELSE '0L' END,
       CASE WHEN MOD(x, 5) = 1 THEN '나무 아래 묻음' WHEN MOD(x, 5) = 2 THEN '비닐에 싸서 배출' WHEN MOD(x, 5) = 3 THEN '냉장고 끄기' WHEN MOD(x, 5) = 4 THEN '소음 증가' ELSE '약 100L' END,
       2, '지구를 위한 올바른 행동을 선택하고 실천합시다.', CURRENT_TIMESTAMP FROM SYSTEM_RANGE(1, 40);

-- [Normal] 51~100
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (51, 'Normal', 20, '제품의 전 과정에서 발생하는 온실가스 배출량을 표시하는 제도는?', '탄소발자국', '에너지등급', '친환경마크', '로컬마크', 1, '탄소발자국 수치가 낮을수록 환경에 미치는 영향이 적습니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (52, 'Normal', 20, '단순 재활용을 넘어 가치를 높여 재개발하는 활동은?', '리사이클링', '업사이클링', '프리사이클링', '다운사이클링', 2, '업사이클링은 창의적인 아이디어로 물건의 수명을 연장합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (53, 'Normal', 20, '지속가능한 개발을 위해 UN이 선정한 17가지 목표의 약칭은?', 'OECD', 'SDGs', 'WHO', 'UNEP', 2, '2030년까지 전 지구가 달성해야 할 인류 공동의 목표입니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (54, 'Normal', 20, '에너지 효율 등급 마크에서 숫자가 작을수록 좋은가요?', '아니오', '예(1등급이 최고)', '상관없음', '모름', 2, '1등급 제품은 낮은 등급 제품보다 에너지 소비 효율이 훨씬 높습니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (55, 'Normal', 20, '가까운 지역에서 생산된 농산물을 이용하자는 캠페인의 이름은?', '패스트푸드', '로컬푸드', '그린마트', '박지푸드', 2, '운송 과정에서 발생하는 이산화탄소를 줄일 수 있는 친환경 소비입니다.', CURRENT_TIMESTAMP);
-- (56~100 Normal)
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) 
SELECT x+55, 'Normal', 20, 
       CASE WHEN MOD(x, 4) = 1 THEN '그린카드 사용 시 받을 수 있는 주요 혜택은?'
            WHEN MOD(x, 4) = 2 THEN '이산화탄소보다 지구 온난화 효과가 훨씬 강력한 기체는?'
            WHEN MOD(x, 4) = 3 THEN '친환경 인증마크(초록마크)의 의미는?'
            ELSE '제로 웨이스트(Zero Waste) 운동의 핵심 원칙은?' END,
       CASE WHEN MOD(x, 4) = 1 THEN '항공권 할인' WHEN MOD(x, 4) = 2 THEN '산소' WHEN MOD(x, 4) = 3 THEN '가격이 비싸다' ELSE '많이 사기' END,
       CASE WHEN MOD(x, 4) = 1 THEN '에코머니 적립' WHEN MOD(x, 4) = 2 THEN '메탄' WHEN MOD(x, 4) = 3 THEN '생산 및 소비 단계에서 환경 오염 저감' ELSE '쓰레기 배출 최소화' END,
       CASE WHEN MOD(x, 4) = 1 THEN '전용 주차장' WHEN MOD(x, 4) = 2 THEN '질소' WHEN MOD(x, 4) = 3 THEN '디자인이 예쁘다' ELSE '그냥 다 버리기' END,
       CASE WHEN MOD(x, 4) = 1 THEN '도서 대여' WHEN MOD(x, 4) = 2 THEN '헬륨' WHEN MOD(x, 4) = 3 THEN '해외 수출 전용' ELSE '소각하기' END,
       2, '환경 보호를 위한 제도적, 사회적 상식에 관한 내용입니다.', CURRENT_TIMESTAMP FROM SYSTEM_RANGE(1, 45);

-- [Hard] 101~150
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (101, 'Hard', 50, '기온 상승 제한을 1.5도로 설정한 신기후체제 조약의 이름은?', '리우 협약', '교토 의정서', '파리 협정', '람사르 협약', 3, '2015년 채택된 파리협정은 전 세계가 공동 대응하는Climate Action입니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (102, 'Hard', 50, '기업 전력을 100% 재생 에너지로 조달하겠다는 목표 캠페인은?', 'ESG', 'RE100', 'CBAM', 'LCA', 2, 'Renewable Energy 100%의 약자로 글로벌 경영의 필수 요소가 되고 있습니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (103, 'Hard', 50, '습지 보호를 위해 1971년 이란에서 채택된 국제 협약은?', '바젤 협약', '람사르 협약', '몬트리올 협약', '제네바 협약', 2, '람사르 협약은 물새 서식지로 중요한 습지를 보전하기 위한 약속입니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (104, 'Hard', 50, '제품의 전 과정(원료~폐기)을 분석하여 환경 영향을 평가하는 기법은?', 'ESG 분석', 'LCA(전과정 평가)', '탄소포인트', '녹색 경영', 2, 'Life Cycle Assessment는 제품이 전 생애 동안 환경에 미치는 부하를 측정합니다.', CURRENT_TIMESTAMP);
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) VALUES (105, 'Hard', 50, '가짜 친환경 광고로 소비자를 기만하는 행위를 부르는 용어는?', '블루 워싱', '그린 워싱', '브라운 워싱', '화이트 워싱', 2, 'Green Washing은 실제보다 친환경적인 이미지로만 포장하는 위장 환경주의입니다.', CURRENT_TIMESTAMP);
-- (106~150 Hard)
INSERT INTO QUIZ (QUIZ_NO, DIFFICULTY, POINT, QUIZ_QUESTION, OPTION1, OPTION2, OPTION3, OPTION4, QUIZ_ANSWER, QUIZ_EXPLANATION, CREATED_AT) 
SELECT x+105, 'Hard', 50, 
       CASE WHEN MOD(x, 4) = 1 THEN '생물 다양성 협정(CBD)의 3대 목표 중 하나인 것은?'
            WHEN MOD(x, 4) = 2 THEN '탄소국경세(CBAM) 도입의 근본적인 목적은 무엇일까요?'
            WHEN MOD(x, 4) = 3 THEN '환경오염을 일으킨 자가 비용을 부담해야 한다는 법적 원칙은?'
            ELSE '배출권 거래제(ETS)의 핵심 메커니즘은 무엇인가요?' END,
       CASE WHEN MOD(x, 4) = 1 THEN '특정 종 독점' WHEN MOD(x, 4) = 2 THEN '관광 수입' WHEN MOD(x, 4) = 3 THEN '공동 부담' ELSE '세금 감면' END,
       CASE WHEN MOD(x, 4) = 1 THEN '생물다양성 보전 및 공평한 이익 공유' WHEN MOD(x, 4) = 2 THEN '탄소 누출 방지 및 공정한 탄소 비용 부과' WHEN MOD(x, 4) = 3 THEN '오염자 부담 원칙(PPP)' ELSE '탄소 배출 허용량 거래' END,
       CASE WHEN MOD(x, 4) = 1 THEN '동물 실험 금지' WHEN MOD(x, 4) = 2 THEN '환율 조절' WHEN MOD(x, 4) = 3 THEN '무과실 책임' ELSE '기술 지원' END,
       CASE WHEN MOD(x, 4) = 1 THEN '식량 증산' WHEN MOD(x, 4) = 2 THEN '수출 제한' WHEN MOD(x, 4) = 3 THEN '비례 원칙' ELSE '국채 발행' END,
       2, '글로벌 환경 이슈 및 국제 법적 쟁점에 관한 심도 있는 내용입니다.', CURRENT_TIMESTAMP FROM SYSTEM_RANGE(1, 45);

-- ─── Quest 테스트 데이터 (총 50개) ───
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (1, '하루 동안 텀블러만 사용하기', 50, '생활', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (2, '대중교통 이용 인증하기', 100, '교통', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (3, '분리수거 철저히 수행한 사진', 80, '분리수거', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (4, '사용하지 않는 플러그 뽑기', 60, '에너지', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (5, '계단 이용 인증 (3층 이상)', 50, '생활', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (6, '장바구니 사용 인증하기', 70, '생활', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (7, '이면지 활용 기록 남기기', 40, '사무', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (8, '저탄소 식단(채식 등) 기록', 120, '식생활', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (9, '에너지 효율 1등급 가전 확인', 150, '쇼핑', CURRENT_TIMESTAMP);
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) VALUES (10, '디지털 탄소 발자국(메일 삭제)', 30, '디지털', CURRENT_TIMESTAMP);
-- (11~50 Quest 생성)
INSERT INTO QUEST (QUEST_NO, QUEST_TITLE, POINT, CATEGORY, CREATED_AT) 
SELECT x+10, 
       CASE WHEN MOD(x, 4) = 1 THEN '가까운 거리 걷기 실천'
            WHEN MOD(x, 4) = 2 THEN '일회용 빨대 거절하기'
            WHEN MOD(x, 4) = 3 THEN '친환경 제품 구매기'
            ELSE '주변 쓰레기 줍기(플로깅)' END,
       50 + (x*2), '환경 보호', CURRENT_TIMESTAMP FROM SYSTEM_RANGE(1, 40);
