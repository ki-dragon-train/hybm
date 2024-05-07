import styles from "../../styles/mainPage/NotificationModal.module.css";
import meat from "../../assets/images/meat.png";
import { formatDate } from "../../utils/formatting";
import { deleteNotification } from "../../api/notificationApi";
import { useMutation } from "@tanstack/react-query";
import { deleteFood } from "../../api/foodApi";

interface NotificationItemProps {
  foodId: number;
  noticeId: number;
  content: string;
  isChecked: boolean;
  foodImgSrc: string;
  createdAt: string;
}

function NotificationItem({
  foodId,
  noticeId,
  content,
  isChecked,
  foodImgSrc,
  createdAt,
}: NotificationItemProps) {
  const formattedDate = formatDate(createdAt);

  const { mutate: mutateDeleteNotification } = useMutation({
    mutationFn: deleteNotification,
    onSuccess: () => {
      // navigate('/group');
      // closeRewardModal()
    },
    onError: (error) => {
      console.error("에러났습니다.", error);
    },
  });

  const { mutate: mutateDeleteFood } = useMutation({
    mutationFn: deleteFood,
    onSuccess: () => {
      // navigate('/group');
      // closeRewardModal()
    },
    onError: (error) => {
      console.error("에러났습니다.", error);
    },
  });

  const handleDeleteNotification = () => {
    mutateDeleteNotification(noticeId);
  };

  const handleDeleteFood = (option: string) => {
    mutateDeleteFood({ foodIdList: [foodId], option });
  };

  return (
    <div className={styles.notification_item}>
      <div className={styles.main_section}>
        <div className={styles.image_section}>
          <img src={meat} alt={foodImgSrc} />
        </div>
        <div className={styles.content_section}>
          <span>{content}</span>
          <div>
            <span>{formattedDate}</span>
            <div>
              <button onClick={() => handleDeleteFood("eaten")}>
                이미 먹었어요
              </button>
              <button onClick={() => handleDeleteFood("thrown")}>
                버렸어요
              </button>
            </div>
          </div>
        </div>
        <div
          className={styles.button_section}
          onClick={handleDeleteNotification}
        >
          x
        </div>
      </div>
    </div>
  );
}

export default NotificationItem;
