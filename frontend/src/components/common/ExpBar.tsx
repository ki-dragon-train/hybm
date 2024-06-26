import { useEffect, useState } from "react";
import styles from "../../styles/common/ExpBar.module.css";
import barBackground from "../../assets/images/barBackground.png";
import barHeart from "../../assets/images/barHeart.png";
import expBar from "../../assets/images/expBar.png";
import notification from "../../assets/images/notification.png";
import Modal from "./Modal";
import NotificationModal from "../mainPage/NotificationModal";
import userBtn from "../../assets/images/userBtn.png";
import { getLevelAndExp } from "../../api/userApi";
import {
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  deleteAllNotification,
  getIsNewNotification,
} from "../../api/notificationApi";
import { signOut } from "../../api/userApi";
import DropDown from "./DropDown";
import ConfirmModal from "./ConfirmModal";
import useAuthStore from "../../stores/useAuthStore";
import LevelUpModal from "../mainPage/LevelUpModal";
import LoadingSpinner from "./LoadingSpinner";

interface IsNewNotificationType {
  hasNew: boolean;
}

function ExpBar() {
  const {
    currentLevel,
    setCurrentLevel,
    isCurrentNotification,
    setIsCurrentNotification,
  } = useAuthStore();
  const [isLevelUpModalOpen, setIsLevelUpModalOpen] = useState(false);
  const [isNotificationModalOpen, setIsNotificationModalOpen] =
    useState(false);
  const [isDropdownView, setDropdownView] = useState<boolean>(false);
  const [isSignOutConfirmModalOpen, setSignOutConfirmModalOpen] =
    useState<boolean>(false);
  const { setIsLogin } = useAuthStore();

  const { mutate: mutateSignOut } = useMutation({
    mutationFn: signOut,
    onSuccess: () => {
      setIsLogin(false);
    },
  });
  const queryClient = useQueryClient();

  const handleOpenNotificationModal = () => {
    setIsNotificationModalOpen(true);
  };

  const handleCloseNotificationModal = () => {
    setIsNotificationModalOpen(false);
  };

  const handleClickUserBtn = () => {
    setDropdownView(!isDropdownView);
  };

  const handleOpenModal = () => {
    setSignOutConfirmModalOpen(true);
  };

  const handleCloseModal = () => {
    setSignOutConfirmModalOpen(false);
  };

  const handleSignOut = () => {
    mutateSignOut();
  };

  const handleCloseLevelUpModal = () => {
    setIsLevelUpModalOpen(false);
  };

  const handleOpenDeleteNotificationConfirmModal = () => {
    setIsDeleteNotificationConfirmModalOpen(true);
  };

  const [
    isDeleteNotificationConfirmModalOpen,
    setIsDeleteNotificationConfirmModalOpen,
  ] = useState(false);

  const { mutate: mutateDeleteAllNotification } = useMutation({
    mutationFn: deleteAllNotification,
    onSuccess: () => {
      setIsNotificationModalOpen(false);
      setIsDeleteNotificationConfirmModalOpen(false);
      setIsCurrentNotification(false);
      queryClient.invalidateQueries({
        queryKey: ["isNewNotification"],
      });
    },
  });

  const handleDeleteAllNotification = () => {
    mutateDeleteAllNotification();
  };

  const closeDeleteNotificationConfirmModal = () => {
    setIsDeleteNotificationConfirmModalOpen(false);
  };

  const {
    data: levelAndExp,
    isPending: isLevelAndExpPending,
    isError: isLevelAndExpError,
  } = useQuery({
    queryKey: ["levelAndExp"],
    queryFn: getLevelAndExp,
  });

  const {
    data: isNewNotification,
    isPending: isNewNotificationPending,
    isError: isNewNotificationError,
  } = useQuery<IsNewNotificationType>({
    queryKey: ["isNewNotification"],
    queryFn: getIsNewNotification,
  });
  useEffect(() => {
    if (levelAndExp) {
      if (currentLevel !== null && currentLevel < levelAndExp.level) {
        setIsLevelUpModalOpen(true);
      }
      setCurrentLevel(levelAndExp.level);
    }
  }, [levelAndExp]);

  useEffect(() => {
    if (isNewNotification) {
      setIsCurrentNotification(isNewNotification!.hasNew);
    }
  }, [isNewNotification]);

  if (isLevelAndExpPending || isNewNotificationPending) {
    return <LoadingSpinner />;
  }

  if (isLevelAndExpError) {
    return <div>get level and exp error</div>;
  } else if (isNewNotificationError) {
    return <div>get isNewNotification error</div>;
  }

  const currentExpPercent = Math.round(
    (levelAndExp.currentExp / levelAndExp.maxExp) * 100
  );

  return (
    <div className={styles.bar_box}>
      <div className={styles.bar_sub_box}>
        <img
          className={styles.bar_background}
          src={barBackground}
          alt="경험치바 배경이미지"
        />
        <div className={styles.bar_heart_box}>
          <div className={styles.bar_heart_sub_box}>
            <img
              className={styles.bar_heart}
              src={barHeart}
              alt="하트이미지"
            />
            <span>{levelAndExp.level}</span>
          </div>
        </div>
        <div className={styles.exp_bar_box}>
          <div className={styles.exp_bar_sub_box}>
            <img
              className={styles.exp_bar}
              src={expBar}
              alt="경험치바 이미지"
            />
            <div
              className={styles.current_exp}
              style={{ width: `${currentExpPercent}%` }}
            ></div>
            <span className={styles.exp_content}>
              {levelAndExp.currentExp} / {levelAndExp.maxExp} (
              {currentExpPercent}%)
            </span>
          </div>
        </div>
        <div
          className={styles.notification_box}
          onClick={handleOpenNotificationModal}
        >
          <div className={styles.notification_sub_box}>
            <img src={notification} alt="알림 이미지" />
            {isCurrentNotification && <div />}
          </div>
        </div>
        {isSignOutConfirmModalOpen && (
          <ConfirmModal
            content="정말 회원 탈퇴 하시겠습니까?"
            option1="예"
            option2="취소"
            option1Event={handleSignOut}
            option2Event={handleCloseModal}
          />
        )}
        <div className={styles.user_box}>
          <div onClick={handleClickUserBtn}>
            <img src={userBtn} alt="유저버튼" />
          </div>
          {isDropdownView && <DropDown openModal={handleOpenModal} />}
        </div>
      </div>
      {isNotificationModalOpen && (
        <Modal title="알림함" onClick={handleCloseNotificationModal}>
          <div>
            <button
              className={styles.remove_all_button}
              onClick={handleOpenDeleteNotificationConfirmModal}
            >
              알림함 비우기
            </button>
            <NotificationModal />
          </div>
        </Modal>
      )}
      {isDeleteNotificationConfirmModalOpen && (
        <ConfirmModal
          content="알림함을 비우시겠습니까?"
          option1="삭제"
          option2="취소"
          option1Event={handleDeleteAllNotification}
          option2Event={closeDeleteNotificationConfirmModal}
        />
      )}
      {isLevelUpModalOpen && (
      <Modal title="레벨업!" onClick={handleCloseLevelUpModal}>
        <LevelUpModal level={currentLevel} />
      </Modal>
      )}
    </div>
  );
}

export default ExpBar;
