/* eslint-disable */
import Button from "../common/Button";
import styles from "../../styles/storagePage/CreateFoodModal.module.css";
import barcode from "../../assets/images/barcode.png";
import camera from "../../assets/images/camera.png";
import FoodSection from "../common/FoodSection";
import useFoodStore from "../../stores/useFoodStore";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { postFood } from "../../api/foodApi";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { formatDashDate } from "../../utils/formatting";
import useAuthStore from "../../stores/useAuthStore";

interface CreateFoodModalProps {
  handleCloseCreateFoodModal: () => void;
}

function CreateFoodModal({
  handleCloseCreateFoodModal,
}: CreateFoodModalProps) {
  const navigate = useNavigate();
  const { setImage } = useAuthStore();
  const queryClient = useQueryClient();
  const { storageName } = useParams() as { storageName: string };
  const { inputList, setInputList, initialInputList, setIsSelected } =
    useFoodStore();

  const [foodData, setFoodData] = useState({
    name: "",
    categoryId: 0,
    price: 0,
    expiredDate: "",
    location: "",
    isManual: true,
  });

  useEffect(() => {
    setFoodData({
      name: inputList.foodName,
      categoryId: inputList.categoryId,
      price: inputList.price,
      expiredDate: formatDashDate(inputList.expiredDate),
      location: storageName,
      isManual: true,
    });
  }, [inputList]);

  // 식품 등록 api
  const { mutate: mutateFood } = useMutation({
    mutationFn: postFood,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["foodStorageItemList"],
      });
      setInputList(initialInputList);
      handleCloseCreateFoodModal();
      setIsSelected(false);
    },
  });

  const handleCreateFood = () => {
    mutateFood(foodData);
  };

  const handleOpenCamera = () => {
    // @ts-ignore
    window.flutter_inappwebview.postMessage("button_clicked");
  };

  // 파일 경로를 받아 Blob으로 변환하고 이를 File 객체로 변환하는 함수
  const convertToBlobAndSetImage = async (imagePath: string) => {
    const response = await fetch(`file://${imagePath}`);
    const blob = await response.blob();
    const file = new File([blob], "receipt.jpg", { type: blob.type });
    setImage(file);
    navigate("/receipt");
  };

  useEffect(() => {
    // @ts-ignore
    window.sendReceipt = convertToBlobAndSetImage;
  }, []);

  return (
    <div className={styles.wrapper}>
      <section className={styles.main_section}>
        <FoodSection option="create" />
        <section className={styles.btn_section}>
          <div>
            <img src={barcode} alt="바코드아이콘" />
          </div>
          <div onClick={handleOpenCamera}>
            <img src={camera} alt="카메라아이콘" />
          </div>
          <div></div>
        </section>
      </section>
      <span>
        * 분류에 따른 <span>예상 소비기한</span>이 제공되나
        <br />
        상이할 수 있습니다.
      </span>
      <Button
        content="완료"
        color="red"
        onClick={handleCreateFood}
        disabled={foodData.name == "" || foodData.categoryId == 0}
      />
    </div>
  );
}

export default CreateFoodModal;
