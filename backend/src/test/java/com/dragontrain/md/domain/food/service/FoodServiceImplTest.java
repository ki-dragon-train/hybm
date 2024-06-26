package com.dragontrain.md.domain.food.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.LongStream;

import com.dragontrain.md.common.config.properties.ExpProperties;
import com.dragontrain.md.common.service.EventPublisher;
import com.dragontrain.md.domain.food.event.EatenCountRaised;
import com.dragontrain.md.domain.food.service.port.CategoryBigRepository;
import com.dragontrain.md.domain.refrigerator.domain.RefrigeratorEatenCount;
import com.dragontrain.md.domain.refrigerator.service.port.RefrigeratorEatenCountRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dragontrain.md.common.service.TimeService;
import com.dragontrain.md.domain.food.controller.request.FoodRegister;
import com.dragontrain.md.domain.food.controller.response.BarcodeInfo;
import com.dragontrain.md.domain.food.controller.response.ExpectedExpirationDate;
import com.dragontrain.md.domain.food.domain.Barcode;
import com.dragontrain.md.domain.food.domain.CategoryBig;
import com.dragontrain.md.domain.food.domain.CategoryDetail;
import com.dragontrain.md.domain.food.domain.Food;
import com.dragontrain.md.domain.food.exception.FoodErrorCode;
import com.dragontrain.md.domain.food.exception.FoodException;
import com.dragontrain.md.domain.food.service.port.BarcodeRepository;
import com.dragontrain.md.domain.food.service.port.CategoryDetailRepository;
import com.dragontrain.md.domain.food.service.port.FoodRepository;
import com.dragontrain.md.domain.refrigerator.domain.Refrigerator;
import com.dragontrain.md.domain.refrigerator.exception.RefrigeratorErrorCode;
import com.dragontrain.md.domain.refrigerator.exception.RefrigeratorException;
import com.dragontrain.md.domain.refrigerator.service.port.RefrigeratorRepository;
import com.dragontrain.md.domain.user.domain.User;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {

	@Mock
	RefrigeratorRepository refrigeratorRepository;
	@Mock
	FoodRepository foodRepository;
	@Mock
	CategoryDetailRepository categoryDetailRepository;
	//
	@Mock
	CategoryBigRepository categoryBigRepository;
	@Mock
	RefrigeratorEatenCountRepository refrigeratorEatenCountRepository;
	@Mock
	EventPublisher eventPublisher;
	//
	@Mock
	BarcodeRepository barcodeRepository;
	@Mock
	CrawlService crawlService;
	@Mock ExpProperties expProperties;
	@Mock
	TimeService timeService;

	@InjectMocks
	FoodServiceImpl foodService;

	@DisplayName("바코드 정보 조회 테스트 성공 - 바코드가 이미 등록된 경우")
	@Test
	void getBarcodeInfoSuccessAlreadyRegisteredBarcodeTest() throws Exception {
		// given
		Long givenBarcode = 10101000L;
		int categoryBigId = 10;
		CategoryBig categoryBig = CategoryBig.builder()
			.categoryBigId(categoryBigId)
			.name("축산류")
			.imgSrc("1234")
			.build();
		int categoryDetailId = 10;
		CategoryDetail categoryDetail = CategoryDetail.builder()
			.categoryDetailId(categoryDetailId)
			.imgSrc("1234")
			.name("돼지고기")
			.kanCode(101011)
			.categoryBig(categoryBig)
			.expirationDate(365)
			.build();
		String barcodeName = "참이슬 빨간뚜껑";
		given(barcodeRepository.findByBarcodeId(anyLong()))
			.willReturn(Optional.of(Barcode.builder()
				.name(barcodeName)
				.categoryDetail(categoryDetail)
				.build()));
		// when
		BarcodeInfo sut = foodService.getBarcodeInfo(givenBarcode);
		// then
		SoftAssertions sa = new SoftAssertions();
		sa.assertThat(sut.getName()).isEqualTo(barcodeName);
		sa.assertThat(sut.getCategoryId()).isSameAs(categoryDetailId);
		sa.assertThat(sut.getCategoryBigId()).isSameAs(categoryBigId);
		sa.assertAll();
	}

	@DisplayName("바코드 정보 조회 테스트 성공 - 바코드를 크롤링한 경우")
	@Test
	void getBarcodeInfoSuccessCrawlBarcodeTest() throws Exception {
		// given
		Long givenBarcode = 10101000L;
		int categoryBigId = 10;
		CategoryBig categoryBig = CategoryBig.builder()
			.categoryBigId(categoryBigId)
			.name("축산류")
			.imgSrc("1234")
			.build();
		int categoryDetailId = 10;
		int kanCode = 101011;
		CategoryDetail categoryDetail = CategoryDetail.builder()
			.categoryDetailId(categoryDetailId)
			.imgSrc("1234")
			.name("돼지고기")
			.kanCode(kanCode)
			.categoryBig(categoryBig)
			.expirationDate(365)
			.build();
		String barcodeName = "참이슬 빨간뚜껑";
		Barcode barcode = Barcode.builder()
			.name(barcodeName)
			.categoryDetail(categoryDetail)
			.build();
		given(barcodeRepository.findByBarcodeId(anyLong()))
			.willReturn(Optional.empty());

		given(crawlService.crawlBarcode(anyLong()))
			.willReturn(Optional.of(BarcodeCreate.builder()
				.name(barcodeName)
				.kanCode(kanCode)
				.build()));

		given(categoryDetailRepository.findByKanCode(anyInt()))
			.willReturn(Optional.of(categoryDetail));

		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 1, 20, 29));
		// when
		BarcodeInfo sut = foodService.getBarcodeInfo(givenBarcode);
		// then
		SoftAssertions sa = new SoftAssertions();
		sa.assertThat(sut.getName()).isEqualTo(barcodeName);
		sa.assertThat(sut.getCategoryId()).isSameAs(categoryDetailId);
		sa.assertThat(sut.getCategoryBigId()).isSameAs(categoryBigId);
		sa.assertAll();
		then(barcodeRepository).should(atLeastOnce()).save(any());
	}

	@DisplayName("바코드 정보 조회 테스트 실패 - 알 수 없는 바코드인 경우")
	@Test
	void getBarcodeInfoFailUnknownBarcodeTest() throws Exception {
		// given
		Long givenBarcode = 10101000L;

		given(barcodeRepository.findByBarcodeId(anyLong()))
			.willReturn(Optional.empty());

		given(crawlService.crawlBarcode(anyLong()))
			.willReturn(Optional.empty());

		// when	// then
		assertThatThrownBy(() -> foodService.getBarcodeInfo(givenBarcode))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.UNKNOWN_BARCODE);
		then(categoryDetailRepository).should(never()).findByKanCode(anyInt());
		then(barcodeRepository).should(never()).save(any());

	}

	@DisplayName("바코드 정보 조회 테스트 실패 - 알 수 없는 칸코드인 경우")
	@Test
	void getBarcodeInfoFailUnknownKanCodeTest() throws Exception {
		// given
		Long givenBarcode = 10101000L;
		int kanCode = 101011;
		String barcodeName = "참이슬 빨간뚜껑";

		given(barcodeRepository.findByBarcodeId(anyLong()))
			.willReturn(Optional.empty());

		given(crawlService.crawlBarcode(anyLong()))
			.willReturn(Optional.of(BarcodeCreate.builder()
				.name(barcodeName)
				.kanCode(kanCode)
				.build()));

		given(categoryDetailRepository.findByKanCode(anyInt()))
			.willReturn(Optional.empty());

		// when	// then
		assertThatThrownBy(() -> foodService.getBarcodeInfo(givenBarcode))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.UNKNOWN_KAN_CODE);
		then(barcodeRepository).should(never()).save(any());

	}

	@DisplayName("예상 소비기한 조회 테스트 성공")
	@Test
	void getExpectedExpirationDateSuccessTest() throws Exception {
		// given
		int categoryDetailId = 10;
		int year = 2024;
		int month = 5;
		int day = 1;
		CategoryDetail categoryDetail = CategoryDetail.builder()
			.categoryDetailId(categoryDetailId)
			.imgSrc("1234")
			.name("돼지고기")
			.expirationDate(14)
			.build();
		given(categoryDetailRepository.findById(anyInt()))
			.willReturn(Optional.of(categoryDetail));
		// when
		ExpectedExpirationDate sut = foodService.getExpectedExpirationDate(categoryDetailId, year,
			month, day);
		// then
		SoftAssertions sa = new SoftAssertions();
		sa.assertThat(sut.getYear()).isEqualTo(year);
		sa.assertThat(sut.getMonth()).isEqualTo(month);
		sa.assertThat(sut.getDay()).isEqualTo(day + categoryDetail.getExpirationDate());
		sa.assertAll();
	}

	@DisplayName("예상 소비기한 조회 테스트 실패 - 날짜 형식이 맞지 않는 경우")
	@Test
	void getExpectedExpirationDateFailCurrentDateFormatErrorTest() throws Exception {
		// given
		int categoryDetailId = 10;
		int year = 2024;
		int month = 4;
		int day = 31;
		// when // then
		assertThatThrownBy(() -> foodService.getExpectedExpirationDate(categoryDetailId, year,
			month, day))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.INVALID_DATE_FORMAT);
		then(categoryDetailRepository).should(never()).findById(categoryDetailId);
	}

	@DisplayName("예상 소비기한 조회 테스트 실패 - 소분류 아이디가 없는 경우")
	@Test
	void getExpectedExpirationDateFailCategoryDetailIdNotFoundTest() throws Exception {
		// given
		int categoryDetailId = 10;
		int year = 2024;
		int month = 5;
		int day = 1;
		given(categoryDetailRepository.findById(anyInt()))
			.willReturn(Optional.empty());
		// when // then
		assertThatThrownBy(() -> foodService.getExpectedExpirationDate(categoryDetailId, year,
			month, day))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.CATEGORY_DETAIL_NOT_FOUND);
	}

	@DisplayName("예상 소비기한 조회 테스트 실패 - 소비 기한 정보가 없는 경우")
	@Test
	void getExpectedExpirationDateFailExpirationNotFoundTest() throws Exception {
		// given
		int categoryDetailId = 10;
		int year = 2024;
		int month = 5;
		int day = 1;
		CategoryDetail categoryDetail = CategoryDetail.builder()
			.categoryDetailId(categoryDetailId)
			.imgSrc("1234")
			.name("과자세트")
			.expirationDate(0)
			.build();
		given(categoryDetailRepository.findById(anyInt()))
			.willReturn(Optional.of(categoryDetail));
		// when
		assertThatThrownBy(() -> foodService.getExpectedExpirationDate(categoryDetailId, year,
			month, day))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.EXPIRATION_DATE_NOT_FOUND);
	}

	@DisplayName("음식 생성 테스트 성공")
	@Test
	void registerFoodSuccessTest() throws Exception {
		// given

		FoodRegister register = FoodRegister.builder()
			.categoryId(10)
			.location("ice")
			.expiredDate("2024-06-02")
			.name("삼겹살")
			.price(10_000)
			.build();

		User user = User.builder()
			.userId(1L)
			.build();

		Refrigerator refrigerator = Refrigerator.builder()
			.refrigeratorId(1L)
			.user(user)
			.build();
		CategoryDetail categoryDetail = CategoryDetail.builder()
			.categoryDetailId(1)
			.build();

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(refrigerator));
		given(categoryDetailRepository.findById(anyInt()))
			.willReturn(Optional.of(categoryDetail));
		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 2, 8, 28));
		// when
		foodService.registerFood(register, user);
		//
		then(foodRepository).should(atLeastOnce()).save(any());
	}

	@DisplayName("음식 생성 테스트 실패 - 냉장고를 찾을 수 없는 경우")
	@Test
	void registerFoodFailRefrigeratorNotFoundTest() throws Exception {
		// given

		FoodRegister register = FoodRegister.builder()
			.categoryId(10)
			.location("ice")
			.expiredDate("2024-06-02")
			.name("삼겹살")
			.price(10_000)
			.build();
		User user = User.builder()
			.userId(1L)
			.build();

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.empty());

		// when
		assertThatThrownBy(() -> foodService.registerFood(register, user))
			.isInstanceOf(RefrigeratorException.class)
			.hasFieldOrPropertyWithValue("errorCode", RefrigeratorErrorCode.REFRIGERATOR_NOT_FOUND);
		//
		then(categoryDetailRepository).should(never()).findById(anyInt());
		then(foodRepository).should(never()).save(any());
	}

	@DisplayName("음식 생성 테스트 실패 - 소분류를 찾을 수 없는 경우")
	@Test
	void registerFoodFailCateogryDetailIdNotFoundTest() throws Exception {
		// given

		FoodRegister register = FoodRegister.builder()
			.categoryId(10)
			.location("ice")
			.expiredDate("2024-06-02")
			.name("삼겹살")
			.price(10_000)
			.build();

		User user = User.builder()
			.userId(1L)
			.build();

		Refrigerator refrigerator = Refrigerator.builder()
			.refrigeratorId(1L)
			.user(user)
			.build();

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(refrigerator));
		given(categoryDetailRepository.findById(anyInt()))
			.willReturn(Optional.empty());

		// when 		//
		assertThatThrownBy(() -> foodService.registerFood(register, user))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.CATEGORY_DETAIL_NOT_FOUND);
		then(foodRepository).should(never()).save(any());
	}

	@DisplayName("음식 생성 테스트 실패 - 유효하지 못한 날짜를 입력한 경우")
	@Test
	void registerFoodFailInvalidDateFormatTest() throws Exception {
		// given

		FoodRegister register = FoodRegister.builder()
			.categoryId(10)
			.location("ice")
			.expiredDate("2024-04-31")
			.name("삼겹살")
			.price(10_000)
			.build();

		User user = User.builder()
			.userId(1L)
			.build();

		// when
		assertThatThrownBy(() -> foodService.registerFood(register, user))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.INVALID_DATE_FORMAT);
		then(refrigeratorRepository).should(never()).findByUserId(anyLong());
		then(categoryDetailRepository).should(never()).findById(anyInt());
		then(foodRepository).should(never()).save(any());
	}

	@DisplayName("음식 처리 테스트 성공")
	@Test
	void foodDeletedSuccessTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.build();

		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 4, 13, 25));

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		for (long foodId : foodIds) {
			given(foodRepository.findById(foodId))
				.willReturn(Optional.of(Food.builder()
					.foodId(foodId)
					.refrigerator(givenRefrigerator)
					.build()));
		}
		//when
		foodService.deleteFood(deleteType, foodIds, givenUser);
		//then

	}

	@DisplayName("음식 처리 테스트 실패 - 음식 아이디가 중복된 경우")
	@Test
	void foodDeletedFailFoodIdDuplicatedTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = new Long[] {1L, 1L, 1L};
		User givenUser = User.builder()
			.userId(1L)
			.build();
		//when then
		assertThatThrownBy(() -> foodService.deleteFood(deleteType, foodIds, givenUser))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.DUPLICATED_FOOD_ID);
	}

	@DisplayName("음식 처리 테스트 실패 - 냉장고를 찾을 수 없는 경우")
	@Test
	void foodDeletedFailRefrigeratorNotFoundedTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> foodService.deleteFood(deleteType, foodIds, givenUser))
			.isInstanceOf(RefrigeratorException.class)
			.hasFieldOrPropertyWithValue("errorCode", RefrigeratorErrorCode.REFRIGERATOR_NOT_FOUND);
		//then
	}

	@DisplayName("음식 처리 테스트 실패 - 음식 찾을 수 없는 경우")
	@Test
	void foodDeletedFailFoodNotFoundedTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.build();
		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		given(foodRepository.findById(anyLong()))
			.willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> foodService.deleteFood(deleteType, foodIds, givenUser))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.FOOD_NOT_FOUND);

	}

	@DisplayName("음식 처리 테스트 실패 - 음식이 이미 삭제된 경우")
	@Test
	void foodDeletedFailFoodAlreadyDeletedTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.build();

		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 4, 13, 25));

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		given(foodRepository.findById(anyLong()))
			.willReturn(Optional.of(Food.builder()
				.deletedAt(LocalDateTime.of(2024, 5, 3, 12, 45))
				.foodId(1L)
				.refrigerator(givenRefrigerator)
				.build()));

		//when
		assertThatThrownBy(() -> foodService.deleteFood(deleteType, foodIds, givenUser))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.ALREADY_DELETED_FOOD);
	}

	@DisplayName("음식 처리 테스트 실패 - 음식이 내 것이 아닌 경우")
	@Test
	void foodDeletedFailFoodIsNotMineTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.refrigeratorId(1L)
			.build();

		Refrigerator elseRefrigerator = Refrigerator.builder()
			.refrigeratorId(2L)
			.build();

		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 4, 13, 25));

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		given(foodRepository.findById(anyLong()))
			.willReturn(Optional.of(Food.builder()
				.foodId(1L)
				.refrigerator(elseRefrigerator)
				.build()));

		//when
		assertThatThrownBy(() -> foodService.deleteFood(deleteType, foodIds, givenUser))
			.isInstanceOf(FoodException.class)
			.hasFieldOrPropertyWithValue("errorCode", FoodErrorCode.INVALID_ACCESS);
	}

	@DisplayName("음식 먹음 후 카운트 이벤트 보내는지 테스트 성공")
	@Test
	void foodDeletedAndPublishEventSuccessTest() throws Exception {
		//given
		final String deleteType = "eaten";
		Long[] foodIds = LongStream.range(1, 5).boxed().toArray(Long[]::new);
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.build();

		CategoryBig givenCategoryBig = CategoryBig.builder().build();

		CategoryDetail givenCategoryDetail = CategoryDetail.builder()
			.categoryBig(givenCategoryBig)
			.build();

		given(timeService.localDateTimeNow())
			.willReturn(LocalDateTime.of(2024, 5, 4, 13, 25));

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		for (long foodId : foodIds) {
			given(foodRepository.findById(foodId))
				.willReturn(Optional.of(Food.builder()
					.foodId(foodId)
					.categoryDetail(givenCategoryDetail)
					.refrigerator(givenRefrigerator)
					.build()));

		}
		//when
		foodService.deleteFood(deleteType, foodIds, givenUser);
		//then
		then(eventPublisher).should(times(4)).publish(any());
	}

	@DisplayName("먹음 카운트 증가 성공 및 이벤트 전송 테스트")
	@Test
	void EatenCountRaisedEventSuccessTest() throws Exception {
		//given
		User givenUser = User.builder()
			.userId(1L)
			.build();

		Refrigerator givenRefrigerator = Refrigerator.builder()
			.build();

		CategoryBig givenCategoryBig = CategoryBig.builder()
			.categoryBigId(1)
			.build();

		CategoryDetail givenCategoryDetail = CategoryDetail.builder()
			.categoryBig(givenCategoryBig)
			.build();

		Food givenFood = Food.builder()
			.foodId(1L)
			.categoryDetail(givenCategoryDetail)
			.refrigerator(givenRefrigerator).build();

		given(refrigeratorRepository.findByUserId(anyLong()))
			.willReturn(Optional.of(givenRefrigerator));

		given(foodRepository.findById(anyLong()))
			.willReturn(Optional.of(givenFood));

		RefrigeratorEatenCount givenRefrigeratorEatenCount = RefrigeratorEatenCount.builder()
			.eatenCount(9).build();

		given(expProperties.getEatenAmount()).willReturn(20);

		given(refrigeratorEatenCountRepository.findByRefrigeratorIdAndCategoryBigId(
			givenRefrigerator.getRefrigeratorId(),
			givenCategoryBig.getCategoryBigId()))
			.willReturn(Optional.of(givenRefrigeratorEatenCount));

		//when
		foodService.raiseEatenCount(givenUser.getUserId(), givenFood.getFoodId());

		//then
		assertThat(givenRefrigeratorEatenCount.getEatenCount()).isEqualTo(10);
		then(eventPublisher).should(times(2)).publish(any());

	}


}
