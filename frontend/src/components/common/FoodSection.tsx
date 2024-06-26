/* eslint-disable */
import { useEffect, useState } from "react";
import styles from "../../styles/common/FoodSection.module.css";
import CategoryBox from "./CategoryBox";
import { getBarcodeData, getExpiredDate } from "../../api/foodApi";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import useFoodStore from "../../stores/useFoodStore";
import barcode from "../../assets/images/barcode.png";

interface FoodSectionProps {
  option: string;
}

function FoodSection({ option = "" }: FoodSectionProps) {
  const { storageName } = useParams() as { storageName: string };
  const [selectedLocation, setSelectedLocation] = useState("");
  const [isBarcodeError, setIsBarcodeError] = useState(false);

  const TITLE_LIST: { [key: string]: string } = {
    ice: "냉동칸",
    cool: "냉장칸",
    cabinet: "찬장",
  };

  const {
    inputList,
    setInputList,
    isSelected,
    setIsSelected,
    initInputList,
  } = useFoodStore();

  const { foodName, categoryId, price, expiredDate } = inputList;

  const handleCategoryIdChange = (categoryId: number) => {
    setInputList({
      ...inputList,
      categoryId: categoryId, // 선택한 카테고리의 categoryId를 업데이트
    });
    setIsBarcodeError(false);
    setIsSelected(true);
  };

  const [barcodeNumber, setBarcodeNumber] = useState(0);
  interface BarcodeResultType {
    name: string;
    categoryBigId: number;
    categoryId: number;
  }
  const [barcodeResult, setBarcodeResult] =
    useState<BarcodeResultType | null>(null);
  const { mutate: mutateGetBarcodeData, error } = useMutation({
    mutationFn: getBarcodeData,
    onSuccess: (data) => {
      setBarcodeResult(data);
    },
  });

  useEffect(() => {
    if (error?.message) {
      setBarcodeNumber(0);
      setIsBarcodeError(true);
      initInputList();
      setIsSelected(false);
    }
  }, [error?.message]);

  // 바코드 넘버 바뀌면 얘부터다 현수야
  useEffect(() => {
    if (barcodeNumber !== 0) {
      mutateGetBarcodeData(barcodeNumber);
    }
  }, [barcodeNumber]);

  // 바코드 리솔트 바뀌면 얘부터다 거북아
  useEffect(() => {
    if (barcodeResult) {
      initInputList();
      setIsSelected(false);
      setInputList({
        ...inputList,
        foodName: barcodeResult.name,
        categoryBigId: barcodeResult.categoryBigId,
        categoryId: barcodeResult.categoryId,
      });
    }
  }, [barcodeResult]);

  // setInputList 잘 먹으면 얘는 된다
  useEffect(() => {
    if (barcodeResult) {
      setBarcodeNumber(0);
      setIsSelected(true);
    }
  }, [categoryId]);

  const handleInputList = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const { name, value } = e.target;
    let newValue = value;

    if (name === "price") {
      // 입력값이 "0"으로 시작하고, 그 뒤에 다른 숫자가 오면 "0"을 제거
      if (/^0\d/.test(value)) {
        newValue = value.substring(1);
      }
      // 입력값이 숫자가 아닌 경우 또는 빈 문자열인 경우 "0"으로 처리
      newValue =
        isNaN(parseInt(newValue)) || newValue === "" ? "0" : newValue;
      if (parseInt(newValue) >= 100000000) {
        newValue = newValue.slice(0, 8);
      }
    }
    setIsBarcodeError(false);
    setInputList({
      ...inputList,
      [name]: newValue,
    });
  };

  const [years, setYears] = useState<number[]>([]);

  useEffect(() => {
    const currentYear = new Date().getFullYear();
    const yearOptions: number[] = [];
    for (let i = currentYear - 1; i <= currentYear + 10; i++) {
      yearOptions.push(i);
    }
    setYears(yearOptions);
  }, []);

  const handleYearChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setInputList({
      ...inputList,
      expiredDate: {
        ...inputList.expiredDate,
        year: parseInt(e.target.value),
      },
    });
    setIsBarcodeError(false);
  };

  const handleMonthChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setInputList({
      ...inputList,
      expiredDate: {
        ...inputList.expiredDate,
        month: parseInt(e.target.value),
      },
    });
    setIsBarcodeError(false);
  };

  const handleDayChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setInputList({
      ...inputList,
      expiredDate: {
        ...inputList.expiredDate,
        day: parseInt(e.target.value),
      },
    });
    setIsBarcodeError(false);
  };

  const handleLocationChange = (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    let newLocation = "";

    switch (e.target.value) {
      case "냉동칸":
        newLocation = "ICE";
        break;
      case "냉장칸":
        newLocation = "COOL";
        break;
      case "찬장":
        newLocation = "CABINET";
        break;
      default:
        newLocation = e.target.value;
    }
    setSelectedLocation(e.target.value);
    setInputList({
      ...inputList,
      location: newLocation,
    });
    setIsBarcodeError(false);
  };

  const handleOpenCamera = () => {
    setIsBarcodeError(false);
    setBarcodeNumber(0);
    // @ts-ignore
    window.flutter_inappwebview_barcode.postMessage("barcode_camera");
  };

  const getBarcode = (barcodeNum: number) => {
    setBarcodeNumber(barcodeNum);
    // if (barcodeNum === )
    // setIsBarcodeError(true);
  };

  useEffect(() => {
    // @ts-ignore
    window.getBarcode = getBarcode;
  }, []);

  // 소비기한 확인 api
  const { data: foodExpiredDate } = useQuery({
    queryKey: ["foodExpiredDate"],
    queryFn: () => getExpiredDate(categoryId),
    enabled: isSelected,
    gcTime: 0,
  });

  useEffect(() => {
    if (foodExpiredDate && categoryId) {
      setInputList({
        ...inputList,
        expiredDate: {
          year: foodExpiredDate.year,
          month: foodExpiredDate.month,
          day: foodExpiredDate.day,
        },
      });
    }
  }, [foodExpiredDate]);

  return (
    <div className={styles.wrapper}>
      <article className={styles.food_option_box}>
        <span>상품명</span>
        <input
          name="foodName"
          value={foodName}
          onChange={handleInputList}
          placeholder="바코드를 촬영하거나 직접 입력해보세요"
        />
      </article>
      <article className={styles.food_option_box}>
        <span>분류</span>
        <CategoryBox onCategoryIdChange={handleCategoryIdChange} />
      </article>
      {isBarcodeError && (
        <span className={styles.error_text}>
          바코드를 다시 촬영해주세요.
        </span>
      )}
      <article className={styles.food_option_box}>
        <span>소비기한</span>
        <div className={styles.expiry_date_box}>
          <select
            value={expiredDate.year}
            onChange={handleYearChange}
          >
            {years.map((year) => (
              <option key={year} value={year}>
                {year}
              </option>
            ))}
          </select>
          <select
            value={expiredDate.month}
            onChange={handleMonthChange}
          >
            {Array.from({ length: 12 }, (_, index) => index + 1).map(
              (month) => (
                <option key={month} value={month}>
                  {month}
                </option>
              )
            )}
          </select>
          <select value={expiredDate.day} onChange={handleDayChange}>
            {Array.from({ length: 31 }, (_, index) => index + 1).map(
              (day) => (
                <option key={day} value={day}>
                  {day}
                </option>
              )
            )}
          </select>
        </div>
      </article>
      <article className={styles.food_option_box}>
        <span>가격</span>
        <div className={styles.price_box}>
          <input
            name="price"
            type="number"
            value={price}
            onChange={handleInputList}
          />
          <span>원</span>
        </div>
      </article>
      <article className={styles.food_option_box}>
        <span>위치</span>
        {option === "detail" || option === "receipt" ? (
          <select
            name="location"
            value={
              selectedLocation === ""
                ? inputList.location
                : selectedLocation
            }
            onChange={handleLocationChange}
          >
            <option value="냉동칸">냉동칸</option>
            <option value="냉장칸">냉장칸</option>
            <option value="찬장">찬장</option>
          </select>
        ) : (
          <div className={styles.barcode_section}>
            <div className={styles.storage_box}>
              {TITLE_LIST[storageName]}
            </div>

            <div onClick={handleOpenCamera}>
              <img src={barcode} alt="바코드아이콘" />
            </div>
          </div>
        )}
      </article>
    </div>
  );
}

export default FoodSection;
