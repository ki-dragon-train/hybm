import { useState } from "react";
import Modal from "../components/common/Modal";
import CreateFoodModal from "../components/storagePage/CreateFoodModal";
import styles from "../styles/storagePage/StoragePage.module.css";
import { Link, useParams } from "react-router-dom";
import home from "../assets/images/home.png";
import edit from "../assets/images/edit.png";
import plus from "../assets/images/plus.png";
import cancel from "../assets/images/cancel.png";
import eat from "../assets/images/eat.png";
import throwAway from "../assets/images/throwAway.png";
import ItemBox from "../components/common/ItemBox";
import FoodStateSection from "../components/storagePage/FoodStateSection";
import { deleteFood, getFoodStorageItemList } from "../api/foodApi";
import {
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import useFoodStore from "../stores/useFoodStore";
import ConfirmModal from "../components/common/ConfirmModal";

function StoragePage() {
  const queryClient = useQueryClient();
  const [isCreateFoodModalOpen, setIsCreateFoodModalOpen] =
    useState(false);
  const [isEatenFoodConfirmModal, setIsEatenFoodConfirmModal] =
    useState(false);
  const [isThrownFoodConfirmModal, setIsThrownFoodConfirmModal] =
    useState(false);
  const [isFoodEdit, setIsFoodEdit] = useState(false);
  const [clickedIndexesBySection, setClickedIndexesBySection] =
    useState<{ [key: string]: number[] }>({});
  const { setInputList, initialInputList } = useFoodStore();
  const { storageName } = useParams() as { storageName: string };

  const handleOpenCreateFoodModal = () => {
    setIsCreateFoodModalOpen(true);
  };

  const handleCloseCreateFoodModal = () => {
    setIsCreateFoodModalOpen(false);
    setInputList(initialInputList);
  };

  // 편집창 열고 닫기
  const handleEditFood = () => {
    setIsFoodEdit(!isFoodEdit);
    setClickedIndexesBySection({});
  };

  // 먹음 확인 모달창
  const handleEatenFoodConfirmModal = () => {
    setIsEatenFoodConfirmModal(!isEatenFoodConfirmModal);
  };
  // 버림 확인 모달창
  const handleThrownFoodConfirmModal = () => {
    setIsThrownFoodConfirmModal(!isThrownFoodConfirmModal);
  };

  // 아이템 클릭
  const handleClickItemBox = (sectionTitle: string, idx: number) => {
    const clickedIndexes =
      clickedIndexesBySection[sectionTitle] || [];
    if (clickedIndexes.includes(idx)) {
      setClickedIndexesBySection({
        ...clickedIndexesBySection,
        [sectionTitle]: clickedIndexes.filter(
          (index) => index !== idx
        ),
      });
    } else {
      setClickedIndexesBySection({
        ...clickedIndexesBySection,
        [sectionTitle]: [...clickedIndexes, idx],
      });
    }
  };
  const foodIds = Object.values(clickedIndexesBySection).reduce(
    (acc, arr) => acc.concat(arr),
    []
  );

  const TITLE_LIST: { [key: string]: string } = {
    ice: "냉동실",
    cool: "냉장실",
    cabinet: "찬장",
  };

  const SECTION_TITLE_LIST: { [key: string]: string } = {
    rotten: "소비기한 지남 (D+)😥",
    danger: "위험! (D-3)",
    warning: "경고 (D-7)",
    fresh: "신선😊",
  };

  interface FoodItemType {
    foodId: number;
    name: string;
    categoryImgSrc: string;
    dday: number;
  }

  // 먹음/버림 api
  const { mutate: mutateDeleteFood } = useMutation({
    mutationFn: deleteFood,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["foodStorageItemList"],
      });
      setClickedIndexesBySection({});
      {
        variables.option === "eaten"
          ? setIsEatenFoodConfirmModal(false)
          : setIsThrownFoodConfirmModal(false);
      }
    },
  });

  const handleDeleteFood = (option: string) => {
    mutateDeleteFood({ foodIdList: foodIds, option });
  };

  // 내부 식품 칸별 조회 api
  const {
    data: foodStorageItemList,
    isPending: isFoodStorageItemListPending,
    isError: isFoodStorageItemListError,
  } = useQuery({
    queryKey: ["foodStorageItemList"],
    queryFn: () => getFoodStorageItemList(storageName),
  });

  if (isFoodStorageItemListPending) {
    return <div>isLoding...</div>;
  }
  if (isFoodStorageItemListError) {
    return <div>error</div>;
  }

  return (
    <div className={styles.wrapper}>
      <div className={styles.white_wrapper}>
        <h1>{TITLE_LIST[storageName]}</h1>
        <Link to="/">
          <img
            className={styles.home_img}
            src={home}
            alt="홈 이미지"
          />
        </Link>
        <section className={styles.main_section}>
          {Object.keys(foodStorageItemList).map(
            (sectionTitle, idx) => (
              <FoodStateSection
                key={idx}
                sectionTitle={SECTION_TITLE_LIST[sectionTitle]}
                sectionClass={sectionTitle}
              >
                {foodStorageItemList[sectionTitle].map(
                  (item: FoodItemType) => (
                    <ItemBox
                      key={item.foodId}
                      name={item.name}
                      content={
                        item.dday === 0
                          ? "D-day"
                          : item.dday > 0
                          ? `D+${item.dday}`
                          : `D${item.dday}`
                      }
                      option={
                        clickedIndexesBySection[
                          sectionTitle
                        ]?.includes(item.foodId) && isFoodEdit
                          ? "clicked"
                          : sectionTitle === "rotten"
                          ? "inactive"
                          : ""
                      }
                      imgSrc={item.categoryImgSrc}
                      onClick={() =>
                        handleClickItemBox(sectionTitle, item.foodId)
                      }
                    />
                  )
                )}
              </FoodStateSection>
            )
          )}
        </section>
        {isFoodEdit ? (
          <section className={styles.btn_section}>
            <div className={styles.btn_box} onClick={handleEditFood}>
              <img src={cancel} alt="취소 버튼" />
              <span>취소</span>
            </div>
            <div className={styles.right_btn_box}>
              <div
                className={styles.btn_box}
                onClick={handleEatenFoodConfirmModal}
              >
                <img src={eat} alt="먹음 버튼" />
                <span>먹었어요</span>
              </div>
              <div
                className={styles.btn_box}
                onClick={handleThrownFoodConfirmModal}
              >
                <img src={throwAway} alt="버림 버튼" />
                <span>버렸어요</span>
              </div>
            </div>
          </section>
        ) : (
          <section className={styles.btn_section}>
            <div className={styles.btn_box} onClick={handleEditFood}>
              <img src={edit} alt="편집 버튼" />
              <span>편집</span>
            </div>
            <div
              className={styles.btn_box}
              onClick={handleOpenCreateFoodModal}
            >
              <img src={plus} alt="식품 추가 버튼" />
              <span>식품 추가</span>
            </div>
          </section>
        )}
      </div>
      {isEatenFoodConfirmModal && (
        <ConfirmModal
          content="정말로 먹었습니까?"
          option1="네"
          option1Event={() => handleDeleteFood("eaten")}
          option2="아니오"
          option2Event={handleEatenFoodConfirmModal}
        />
      )}
      {isThrownFoodConfirmModal && (
        <ConfirmModal
          content="정말로 버렸습니까?"
          option1="네"
          option1Event={() => handleDeleteFood("thrown")}
          option2="아니오"
          option2Event={handleThrownFoodConfirmModal}
        />
      )}
      {isCreateFoodModalOpen && (
        <Modal title="식품 등록" onClick={handleCloseCreateFoodModal}>
          <CreateFoodModal
            handleCloseCreateFoodModal={handleCloseCreateFoodModal}
          />
        </Modal>
      )}
    </div>
  );
}

export default StoragePage;
