import styles from "../styles/mainPage/MainPage.module.css";
import background from "../assets/images/background.png";
import recipe from "../assets/images/recipe.png";
import trashCan from "../assets/images/trashCan.png";
import { Link, useLocation } from "react-router-dom";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import Modal from "../components/common/Modal";
import LevelUpModal from "../components/mainPage/LevelUpModal";
import ExpBar from "../components/common/ExpBar";
import { deleteAllFood, getBigCategoryList } from "../api/foodApi";
import { useFoodCategoryStore } from "../stores/useFoodStore";
import { getCurrentDesign } from "../api/fridgeApi";
import ConfirmModal from "../components/common/ConfirmModal";
import { getCurrentBadgeList } from "../api/badgeApi";
import useFridgeStore from "../stores/useFridgeStore";
import plus from "../assets/images/plus.png";
import useAttachedBadgeStore, {
  useBadgeStore,
} from "../stores/useBadgeStore";

interface StorageType {
  id: number;
  imgSrc: string;
}

interface CurrentDesignType {
  ice: StorageType;
  cool: StorageType;
  cabinet: StorageType;
}

interface BadgeType {
  badgeId: number;
  src: string;
  position: number;
}

function MainPage() {
  const location = useLocation();
  const { setBigCategoryList } = useFoodCategoryStore();

  const { appliedDesign, setAppliedDesign } = useFridgeStore();
  const { attachedBadgeList, setAttachedBadgeList } =
    useAttachedBadgeStore();
  const { selectedBadge, initSelectedBadge } = useBadgeStore();
  const [isLevelUpModalOpen, setIsLevelUpModalOpen] = useState(false);
  const [
    isDeleteAllFoodConfirmModalOpen,
    setIsDeleteAllFoodConfirmModalOpen,
  ] = useState(false);

  // const handleOpenLevelUpModal = () => {
  //   setIsLevelUpModalOpen(true);
  // };

  const handleCloseLevelUpModal = () => {
    setIsLevelUpModalOpen(false);
  };

  const {
    data: currentDesign,
    isPending: isCurrentDesignPending,
    isError: isCurrentDesignError,
  } = useQuery<CurrentDesignType>({
    queryKey: ["currentDesign"],
    queryFn: getCurrentDesign,
  });

  const {
    data: bigCategoryList,
    isPending: isBigCategoryListPending,
    isError: isBigCategoryListError,
  } = useQuery({
    queryKey: ["bigCategoryList"],
    queryFn: getBigCategoryList,
  });

  const {
    data: currentBadgeList,
    isPending: iscurrentBadgeListPending,
    isError: iscurrentBadgeListError,
  } = useQuery<BadgeType[]>({
    queryKey: ["currentBadge"],
    queryFn: getCurrentBadgeList,
  });
  // console.log("현재부착:", currentBadgeList)
  // console.log(attachedBadgeList)
  const { mutate: mutateDeleteAllFood } = useMutation({
    mutationFn: deleteAllFood,
    onSuccess: () => {
      setIsDeleteAllFoodConfirmModalOpen(false);
    },
  });

  const handleDeleteAllFood = () => {
    mutateDeleteAllFood();
  };

  const closeDeleteAllFoodConfirmModal = () => {
    setIsDeleteAllFoodConfirmModalOpen(false);
  };

  const handleOpenDeleteAllFoodConfirmModal = () => {
    setIsDeleteAllFoodConfirmModalOpen(true);
  };

  // - 버튼을 눌렀을 때 부착한 배지 제거
  const handleDeleteAttachedBadge = (badgeId: number) => {
    console.log("attacthedBadgeList and badgeId:", attachedBadgeList, badgeId)
    const updatedAttachedBadgeList = attachedBadgeList.map((badge) => {
      if (badge.badgeId !== badgeId) {
        return badge
      } else {
        return {
          badgeId: badge.badgeId,
          position: null,
          src: badge.src
        }
      }
    })
    setAttachedBadgeList(updatedAttachedBadgeList)
  }

  const handleSelectAttachedBadege = (position: number) => {
    if (selectedBadge.badgeId !== 0) {
      setAttachedBadgeList([
        ...attachedBadgeList,
        {
          badgeId: selectedBadge.badgeId,
          src: selectedBadge.src,
          position
        },
      ]);
    }
    initSelectedBadge();
  };

  useEffect(() => {
    if (bigCategoryList) {
      setBigCategoryList(bigCategoryList);
    }
  }, [bigCategoryList]);

  // 메인페이지에서 현재 적용중인 디자인 및 배지를 적용
  useEffect(() => {
    if (currentDesign) {
      setAppliedDesign({
        ice: {
          imgSrc: currentDesign.ice.imgSrc,
          designId: currentDesign.ice.id,
        },
        cool: {
          imgSrc: currentDesign.cool.imgSrc,
          designId: currentDesign.cool.id,
        },
        cabinet: {
          imgSrc: currentDesign.cabinet.imgSrc,
          designId: currentDesign.cabinet.id,
        },
      });
    }
    if (currentBadgeList) {
      setAttachedBadgeList(currentBadgeList);
    } 
  }, [currentDesign, currentBadgeList]);

  if (
    isBigCategoryListPending ||
    isCurrentDesignPending ||
    iscurrentBadgeListPending
  ) {
    return <div>MainPage Loding...</div>;
  }

  if (isBigCategoryListError) {
    return <div>bigCategoryList error</div>;
  } else if (isCurrentDesignError) {
    return <div>currentDesign error</div>;
  } else if (iscurrentBadgeListError) {
    return <div>currentBadge error</div>;
  }

  let content: any = [];
  if (location.pathname === "/badge") {
    content = [1, 2, 3, 4, 5, 6, 7, 8].map((position, idx) => {
      const appliedBadge = attachedBadgeList.find(
        (badge) => badge.position === position
      );
      console.log("appliedBadge:", appliedBadge)
      if (appliedBadge) {
        return (
          <div
            key={appliedBadge.badgeId}
            className={styles[`badge${appliedBadge.position}`]}
          >
            <div>
              <img
                src={appliedBadge.src}
                alt={`배지${appliedBadge.position} 이미지`}
              />
              <div className={styles.minus_button} onClick={() => handleDeleteAttachedBadge(appliedBadge.badgeId)}>-</div>
            </div>
          </div>
        );
      } else {
        return (
          <img
            key={idx}
            className={styles[`badge${position}`]}
            src={plus}
            alt={`배지${position} 이미지`}
            onClick={() => handleSelectAttachedBadege(position)}
          />
        );
      }
    });
  } else {
    content = attachedBadgeList.map((tempAppliedBadge) => {
      return (
        <img
          className={styles[`badge${tempAppliedBadge.position}`]}
          src={tempAppliedBadge.src}
          alt={`배지${tempAppliedBadge.position} 이미지`}
        />
      );
    });
  }
  return (
    <div className={styles.wrapper}>
      <ExpBar />
      <Link to="/storage/cabinet">
        <img
          className={styles.cabinet}
          src={appliedDesign.cabinet.imgSrc}
          alt="찬장이미지"
        />
      </Link>
      <Link to="/recipe">
        <img
          className={styles.recipe}
          src={recipe}
          alt="레시피이미지"
        />
      </Link>
      <Link to="/storage/cool">
        <img
          className={styles.cool}
          src={appliedDesign.cool.imgSrc}
          alt="냉장이미지"
        />
      </Link>
      <Link to="/storage/ice">
        <img
          className={styles.ice}
          src={appliedDesign.ice.imgSrc}
          alt="냉동이미지"
        />
      </Link>
      <img
        className={styles.background}
        src={background}
        alt="배경이미지"
      />
      <Link to="/receipt">
        <div className={styles.receipt}>영수증</div>
      </Link>
      <Link to="/design">
        <div className={styles.design}>디자인</div>
      </Link>
      <Link to="/report">
        <div className={styles.report}>보고서</div>
      </Link>
      <img
        className={styles.trash_can}
        onClick={handleOpenDeleteAllFoodConfirmModal}
        src={trashCan}
        alt="쓰레기통 이미지"
      />
      {content}
      {isLevelUpModalOpen && (
        <Modal title="레벨업!" clickEvent={handleCloseLevelUpModal}>
          <LevelUpModal />
        </Modal>
      )}
      {isDeleteAllFoodConfirmModalOpen && (
        <ConfirmModal
          content="모든 식품을 삭제하시겠습니까?"
          option1="삭제"
          option2="취소"
          option1Event={handleDeleteAllFood}
          option2Event={closeDeleteAllFoodConfirmModal}
        />
      )}
    </div>
  );
}

export default MainPage;
