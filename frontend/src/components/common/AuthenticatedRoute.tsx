import { Navigate, Outlet } from "react-router-dom";
import useAuthStore from "../../stores/useAuthStore";
import styles from "../../styles/landingPage/LandingPage.module.css";

function AuthenticatedRoute() {
  const isLogin = useAuthStore((state) => state.isLogin);
  if (isLogin) {
    return <Navigate to="/" />;
  }
  return (
    <div className={styles.wrapperHeight}>
      <Outlet />
    </div>
  );
}

export default AuthenticatedRoute;
