-- 1) 필수 과목 등록
INSERT INTO dating_exam_subject (name, type, is_public)
VALUES ('가치관', 'REQUIRED', TRUE);
SET
@sub1 = LAST_INSERT_ID();

INSERT INTO dating_exam_subject (name, type, is_public)
VALUES ('데이트', 'REQUIRED', TRUE);
SET
@sub2 = LAST_INSERT_ID();

INSERT INTO dating_exam_subject (name, type, is_public)
VALUES ('취향', 'REQUIRED', TRUE);
SET
@sub3 = LAST_INSERT_ID();

-- ===================================
-- 2) ‘가치관’ 문항 & 보기 (subject_id = @sub1)
-- 질문 1
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 나에게 가장 중요한 가치는 무엇인가?');
SET
@q1 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q1, '물질적 행복'),
       (@q1, '자기계발'),
       (@q1, '건강'),
       (@q1, '기타');

-- 질문 2
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 내가 회사를 볼 때 중요하게 여기는 것은?');
SET
@q2 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q2, '높은 연봉'),
       (@q2, '성장가능성'),
       (@q2, '워라벨'),
       (@q2, '기타');

-- 질문 3
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 내가 생각하는 연애를 하면서 꼭 필요한 감정 요소는?');
SET
@q3 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q3, '안정감'),
       (@q3, '설렘'),
       (@q3, '약간의 긴장감'),
       (@q3, '기타');

-- 질문 4
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 편식이 심한 연인을 위해 내가 할 수 있는 것은?');
SET
@q4 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q4, '편식을 극복할 수 있도록 응원하고 도와준다.'),
       (@q4, '싫어하는 음식들은 모두 제외하고 먹으며 내가 맞춰준다.'),
       (@q4, '밥은 따로 먹고 만나자며 방안을 제시한다.'),
       (@q4, '나는 음식이 정말 중요해서 인연을 이어갈 수 없다.');

-- 질문 5
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 나와 다른 종교를 강요하는 연인에 대한 나의 행동은?');
SET
@q5 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q5, '연인이 하자고 하는 대로 종교활동에 따라 간다.'),
       (@q5, '종교의 자유는 지켜주길 바라며 설득한다.'),
       (@q5, '종교 강요는 참을 수 없기에 더이상 만날 수 없다.'),
       (@q5, '종교는 종교일 뿐, 연인의 말을 무시하고 평소처럼 지낸다.');

-- 질문 6
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '연인이 3년간 해외출장을 가게 되었을 때의 나의 생각은?');
SET
@q6 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q6, '안 가면 안 되는 건지 물어보고 가지말라고 애원한다.'),
       (@q6, '연인의 커리어를 위해서라면 보내주고 기다린다.'),
       (@q6, '3년씩은 좀… 좋은 사람 만나라며 헤어진다.'),
       (@q6, '나도 함께 따라간다.');

-- 질문 7
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 내가 생각하는 바람의 기준과 가장 근접한 것은?');
SET
@q7 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q7, '매일 1:1로 연락하기'),
       (@q7, '만날 때마다 단둘이 만나기'),
       (@q7, '정신과 마음이 다른 사람에게 집중할 때'),
       (@q7, '육체적인 관계');

-- 질문 8
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '[보기]에서 사랑에 관한 명언 중 가장 나의 마음을 동요하게 만든 명언은?');
SET
@q8 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q8, '사랑은 눈으로 보지 않고 마음으로 보는 것이다 - 윌리엄 셰익스피어'),
       (@q8, '중요한 것은 사랑을 받는 것이 아니라 사랑을 주는 것이였다 - 서머셋 모옴'),
       (@q8, '사랑은 두 개의 마음이 하나의 공간에서 춤을 추는 것이다 - 알프레드 텐니슨'),
       (@q8, '내가 이해하는 모든 것은 내가 사랑하기 때문에 이해하는 것이다 - 레프 톨스토이');

-- 질문 9
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 연인이 이성친구과 단둘이 만날 때 내가 이해할 수 있는 범위는?');
SET
@q9 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q9, '카페'),
       (@q9, '식사'),
       (@q9, '술집'),
       (@q9, '모두 이해할 수 없다');

-- 질문 10
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub1, '다음 중 내가 ‘연인에게 사랑을 받고 있구나’ 하고 가장 느낄 수 있는 포인트는?');
SET
@q10 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q10, '나에 대한 애정어린 질문과 안부'),
       (@q10, '보고 싶다며 직접 찾아와주는 모습'),
       (@q10, '나를 생각하며 샀다던 선물'),
       (@q10, '나와 함께하는 미래를 얘기하는 모습');

-- ===================================
-- 3) ‘데이트’ 문항 & 보기 (subject_id = @sub2)
-- 질문 1
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '다음 중 선호하는 데이트 시간은?');
SET
@q11 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q11, '아침 일찍'),
       (@q11, '오후 늦게'),
       (@q11, '하루종일'),
       (@q11, '기타');

-- 질문 2
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '다음 중 하루 데이트 비용으로 적당한 것은?');
SET
@q12 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q12, '3만원 이하'),
       (@q12, '5만원 내외'),
       (@q12, '10만원 이상'),
       (@q12, '기타');

-- 질문 3
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '다음 중 지각한 연인을 너그럽게 용서할 수 있는 시간은?');
SET
@q13 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q13, '약속시간으로 부터 10분'),
       (@q13, '약속시간으로 부터 30분'),
       (@q13, '약속시간으로 부터 1시간'),
       (@q13, '정당한 이유가 있다면 상관없다');

-- 질문 4
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '나와 연인과의 메세지 대화 간격으로 가장 알맞은 것은?');
SET
@q14 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q14, '즉시 답장'),
       (@q14, '3시간 미만'),
       (@q14, '12시간 미만'),
       (@q14, '생존신고만 하면 된다');

-- 질문 5
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '적합하다고 생각하는 데이트 횟수는?');
SET
@q15 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q15, '주 3회 이상'),
       (@q15, '주 1 ~ 2회'),
       (@q15, '월 4회 미만'),
       (@q15, '기타');

-- 질문 6
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '연인이 같이 가고 싶은 장소라고 하지만 나는 정말 가기 싫을 때의 내가 할 행동은?');
SET
@q16 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q16, '다른 대체 장소를 추천하며 설득한다'),
       (@q16, '그래도 나와 함께 가고 싶은 곳이라니 한 번 따라 가본다'),
       (@q16, '‘그럼 다음에는 내가 가고 싶은 곳 가자’ 하며 조건을 제시한다'),
       (@q16, '기타');

-- 질문 7
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '데이트 중 연인이 지인을 만나 반갑게 대화 나눌 때, 내가 취할 행동은?');
SET
@q17 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q17, 'OO의 애인이라며 먼저 인사하며 대화에 참여한다'),
       (@q17, '연인이 편하게 대화 나눌 수 있도록 잠시 자리를 비켜준다'),
       (@q17, '연인에게 이동하자며 인사나눈 후 함께 자리를 뜬다'),
       (@q17, '병풍처럼 가만히 서있는다');

-- 질문 8
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '연인과의 데이트날, 연인이 갑자기 급한 일정이 생겼다며 다음으로 미룬다. 내가 취할 행동은?');
SET
@q18 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q18, '무슨 일인지 설명부터 들어야 한다'),
       (@q18, '일단 급한 것 같으니 데이트를 미루고 나중에 무슨 일인지 물어본다'),
       (@q18, '나보다 더 급한 것이 있다는 생각에 화를 낸다'),
       (@q18, '기타');

-- 질문 9
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '매번 같은 패턴의 데이트가 지겹다는 연인에게 내가 먼저 취할 행동으로 가장 가까운 것은?');
SET
@q19 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q19, '주변 지인들에게 어떤 데이트를 하는지 물어보고 참고한다'),
       (@q19, '연인이 데이트의 소중함을 모르는 것 같으니 한동안 만나지 않는다'),
       (@q19, '함께 어떤 것을 하면 좋을지 의논한다'),
       (@q19, '어떤 것을 원하는지 먼저 연인에게 물어본다');

-- 질문 10
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub2, '다음 중 긴장되는 첫 데이트 전 나의 행동과 가장 가까운 것을 고르시오.');
SET
@q20 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q20, '유명한 맛집과 효율적으로 이동할 수 있는 동선을 계획한다.'),
       (@q20, '어떤 옷을 입을지 고민하며 쇼핑한다'),
       (@q20, '극도로 긴장이 될 것 같아 청심환을 구비한다'),
       (@q20, '기타');

-- ===================================
-- 4) ‘취향’ 문항 & 보기 (subject_id = @sub3)
-- 질문 1
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 연인에게 받으면 좋을 것 같은 선물은?');
SET
@q21 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q21, '정성이 담긴 선물'),
       (@q21, '실용적인 선물'),
       (@q21, '명품과 같은 비싼 선물'),
       (@q21, '기타');

-- 질문 2
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 연인과 맞았으면 하는 영화 취향은?');
SET
@q22 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q22, '달달한 로맨스 영화'),
       (@q22, '잔잔한 힐링 영화'),
       (@q22, '흥미진진한 스릴러'),
       (@q22, '기타');

-- 질문 3
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 기념일 중 선물을 꼭 주고받아야 한다면?');
SET
@q23 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q23, '크리스마스'),
       (@q23, '생일'),
       (@q23, 'OOO데이'),
       (@q23, 'n주년');

-- 질문 4
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 나의 데이트 타입과 가장 가까운 것은?');
SET
@q24 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q24, '클라이밍, 등산 등 활동적인 데이트'),
       (@q24, '전시회, 공연 등 감각적인 데이트'),
       (@q24, '집 나가면 고생. 집 데이트'),
       (@q24, '이 모든걸 번갈아보며 해보는 데이트');

-- 질문 5
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '내가 제일 좋아하는 스킨십은?');
SET
@q25 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q25, '포옹하기'),
       (@q25, '손 잡기'),
       (@q25, '뽀뽀하기'),
       (@q25, '팔짱');

-- 질문 6
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 연인과 맞았으면 하는 것은?');
SET
@q26 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q26, '취미'),
       (@q26, '취향'),
       (@q26, '연락 빈도'),
       (@q26, '유머 코드');

-- 질문 7
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '이성을 볼 때 성격을 제외하고 가장 중요하게 생각하는 것은?');
SET
@q27 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q27, '스타일'),
       (@q27, '유머 감각'),
       (@q27, '목소리'),
       (@q27, '얼굴');

-- 질문 8
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '평소에 선호하는 옷 색깔는?');
SET
@q28 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q28, '어두운 계열'),
       (@q28, '밝은 계열'),
       (@q28, '파스텔 컬러'),
       (@q28, '트렌드를 따라간다');

-- 질문 9
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 가장 선호하는 음식은?');
SET
@q29 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q29, '한식'),
       (@q29, '중식'),
       (@q29, '일식'),
       (@q29, '양식');

-- 질문 10
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub3, '다음 중 데이트 중 정말 먹고 싶지 않은 음식은?');
SET
@q30 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q30, '마라탕'),
       (@q30, '국밥'),
       (@q30, '파스타'),
       (@q30, '기타');

-- 5) 선택 과목 등록
INSERT INTO dating_exam_subject (name, type, is_public)
VALUES ('밸런스', 'OPTIONAL', TRUE);
SET
@sub4 = LAST_INSERT_ID();

INSERT INTO dating_exam_subject (name, type, is_public)
VALUES ('결혼', 'OPTIONAL', TRUE);
SET
@sub5 = LAST_INSERT_ID();

-- ===================================
-- 6) ‘밸런스’ 문항 & 보기 (subject_id = @sub4)
-- 문항 1
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '내가 모르는 이성 집에 있는 연인 vs 연인 집에 있는 내가 모르는 이성');
SET
@q31 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q31, '내가 모르는 이성 집에 있는 연인'),
       (@q31, '연인 집에 있는 내가 모르는 이성');

-- 문항 2
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '연인의 이성친구와 연인만 가는 1박2일 여행 vs 연인과 연인의 전 연인과의 술자리');
SET
@q32 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q32, '연인의 이성친구와 연인만 가는 1박2일 여행'),
       (@q32, '연인과 연인의 전 연인과의 술자리');

-- 문항 3
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '죄를 저지른 연인을 신고하고 1억 포상금 받기 vs 연인과 평생 행복하게 살기');
SET
@q33 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q33, '죄를 저지른 연인을 신고하고 1억 포상금 받기'),
       (@q33, '연인과 평생 행복하게 살기');

-- 문항 4
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '내가 좋아하는 사람과 연애하기 vs 나를 좋아하는 사람과 연애하기');
SET
@q34 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q34, '내가 좋아하는 사람과 연애하기'),
       (@q34, '나를 좋아하는 사람과 연애하기');

-- 문항 5
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '100명 있는 단톡방에서 고백받기 vs 100명 있는 길거리에서 고백받기');
SET
@q35 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q35, '100명 있는 단톡방에서 고백받기'),
       (@q35, '100명 있는 길거리에서 고백받기');

-- 문항 6
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '계속 잠만 자는 연인 vs 계속 먹기만 하는 연인');
SET
@q36 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q36, '계속 잠만 자는 연인'),
       (@q36, '계속 먹기만 하는 연인');

-- 문항 7
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '노래방을 좋아하지만 음치인 연인 vs PC방을 좋아하지만 게임 못하는 연인');
SET
@q37 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q37, '노래방을 좋아하지만 음치인 연인'),
       (@q37, 'PC방을 좋아하지만 게임 못하는 연인');

-- 문항 8
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '함께 가는 식당마다 웨이팅 3시간 기다려야 하는 연인 vs 함께 가는 식당마다 맛이 없는 연인');
SET
@q38 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q38, '함께 가는 식당마다 웨이팅 3시간 기다려야 하는 연인'),
       (@q38, '함께 가는 식당마다 맛이 없는 연인');

-- 문항 9
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '내 입맛에 딱 맞는 음식 잘 만드는 연인 vs 내가 만든 음식 잘 먹는 연인');
SET
@q39 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q39, '내 입맛에 딱 맞는 음식 잘 만드는 연인'),
       (@q39, '내가 만든 음식 잘 먹는 연인');

-- 문항 10
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '맞춤법 계속 틀리는 연인 vs 내 맞춤법 계속 지적하는 연인');
SET
@q40 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q40, '맞춤법 계속 틀리는 연인'),
       (@q40, '내 맞춤법 계속 지적하는 연인');

-- 문항 11
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '한 달에 한 번 만나자고 하는 연인 vs 주 5회 만나자고 하는 연인');
SET
@q41 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q41, '한 달에 한 번 만나자고 하는 연인'),
       (@q41, '주 5회 만나자고 하는 연인');

-- 문항 12
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '게임 중독 연인 vs 쇼핑 중독 연인');
SET
@q42 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q42, '게임 중독 연인'),
       (@q42, '쇼핑 중독 연인');

-- 문항 13
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub4, '5초에 한 번 우는 연인 vs 5초에 한 번 화내는 연인');
SET
@q43 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q43, '5초에 한 번 우는 연인'),
       (@q43, '5초에 한 번 화내는 연인');

-- ===================================
-- 7) ‘결혼’ 문항 & 보기 (subject_id = @sub5)
-- 질문 1
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 신혼여행으로 가길 원하는 장소는?');
SET
@q44 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q44, '국내여행'),
       (@q44, '유럽여행'),
       (@q44, '동남아여행'),
       (@q44, '기타');

-- 질문 2
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 꿈꾸는 결혼식 형태가 있다면?');
SET
@q45 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q45, '주변 지인들만 초대하는 스몰웨딩'),
       (@q45, '성대한 호텔결혼식'),
       (@q45, '남들 하는 정도의 평균적인 결혼식'),
       (@q45, '자연과 함께하는 야외 결혼식');

-- 질문 3
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 노후에 살고 싶은 삶은?');
SET
@q46 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q46, '시골에 내려가 농사 짓고 사는 삶'),
       (@q46, '세계 곳곳을 여행 다니는 삶'),
       (@q46, '바쁘게 일을 하며 지내는 삶'),
       (@q46, '스스로 가꿔갈 미래가 기대되는 홀로 있어도 좋을 것 같은 삶');

-- 질문 4
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 배우자의 조건으로 좋은 사람은?');
SET
@q47 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q47, '능력이 좋은 사람'),
       (@q47, '듬직하고 건장한 사람'),
       (@q47, '생각이 깊고 배려심이 많은 사람'),
       (@q47, '센스가 넘치는 사람');

-- 질문 5
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 배우자가 친한 친구들과의 여행을 간다고 할 때 허락해줄 수 있는 범위는?');
SET
@q48 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q48, '동성친구끼리의 여행만 허락한다'),
       (@q48, '이성친구가 포함되어도 허락한다'),
       (@q48, '동성친구라도 외박은 허락하기 어렵다'),
       (@q48, '기타');

-- 질문 6
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 아내 혹은 남편이 맡아줬으면 하는 집안일은?');
SET
@q49 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q49, '쓰레기 분리수거'),
       (@q49, '화장실 청소하기'),
       (@q49, '설거지하기'),
       (@q49, '요리');

-- 질문 7
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 집안일 배분이 옳다고 생각되는 비율은?');
SET
@q50 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q50, '여자 50 : 남자 50'),
       (@q50, '여자 70 : 남자 30'),
       (@q50, '여자 30 : 남자 70'),
       (@q50, '상황에 따라 배분하기');

-- 질문 8
INSERT INTO dating_exam_question (subject_id, content)
VALUES (@sub5, '다음 중 내가 맡으면 자신 있는 집안일은?');
SET
@q51 = LAST_INSERT_ID();
INSERT INTO dating_exam_answer (question_id, content)
VALUES (@q51, '쓰레기 분리수거'),
       (@q51, '화장실 청소하기'),
       (@q51, '설거지하기'),
       (@q51, '요리');
