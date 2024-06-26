import instance from "./axios";

interface GetReportDataParams {
  year: number;
  month: number;
}

// 월간 지출 조회
const getReportData = async ({ year, month }: GetReportDataParams) => {
  try {
    const res = await instance.get(
      `/api/statistics/month?year=${year}&&month=${month}`
    );
    return res.data;
  } catch (e) {
    console.log(e);
  }
};

export { getReportData };
