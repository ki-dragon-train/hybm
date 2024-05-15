import { Link, useNavigate } from "react-router-dom";
import Button from "../components/common/Button";
import Header from "../components/common/Header";
import styles from "../styles/receiptPage/ReceiptPage.module.css";
import home from "../assets/images/home.png";
import FoodSection from "../components/common/FoodSection";
import { useFoodCategoryStore } from "../stores/useFoodStore";
import useAuthStore from "../stores/useAuthStore";
import { useMutation } from "@tanstack/react-query";
import { postFoodByReceipt, postReceipt } from "../api/receiptApi";
import { useEffect } from "react";

interface namePriceType {
  name: string;
  price: number;
}

function ReceiptPage() {
  const navigate = useNavigate();
  const { bigCategoryList } = useFoodCategoryStore();
  const { image } = useAuthStore();

  const { mutate: mutatePostReceipt, data: namePriceList, status } = useMutation({
    mutationFn: postReceipt,
  });

  const { mutate: mutatePostFoodByReceipt } = useMutation({
    mutationFn: postFoodByReceipt,
    onSuccess: () => {
      navigate("/");
    }
  });

  const handlePostFoodByReceipt = () => {
    console.log(mutatePostFoodByReceipt)
    // mutatePostFoodByReceipt();
  }

  

  useEffect(() => {
    mutatePostReceipt(image!);
  }, [namePriceList])
  console.log(bigCategoryList);
  
  if (status === "pending") {
    return <div>Loading...</div>
  }

  return (
    <div className={styles.wrapper}>
      <div className={styles.white_wrapper}>
        <Header title="영수증 등록" />
        <Link to="/">
          <img className={styles.home_img} src={home} alt="" />
        </Link>
        <section className={styles.food_list_section}>
          {namePriceList.map((namePrice: namePriceType) => {
            console.log(namePrice)
            return <FoodSection option="active"/>
          })}
          
        </section>
        <Button content="완료" color="red" onClick={handlePostFoodByReceipt} disabled={false}/>
      </div>
    </div>
  );
}

export default ReceiptPage;
