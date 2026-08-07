import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

// Helper to format date nicely
const formatDate = (dateStr) => {
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-GB", { day: "2-digit", month: "short" });
};

const BarChartComponent = ({ dataList, activeFilter }) => {
  let chartData = [];

  if (["lastNDays", "month", "year"].includes(activeFilter)) {
    // Group by date (supports both expenseDate and incomeDate)
    chartData = dataList.reduce((acc, curr) => {
      const rawDate = curr.expenseDate || curr.incomeDate; // pick whichever exists
      if (!rawDate) return acc; // skip if neither date exists

      const dateKey = new Date(rawDate).toISOString().split("T")[0]; // yyyy-mm-dd
      const existing = acc.find((item) => item.date === dateKey);

      if (existing) {
        existing.amount += curr.amount;
      } else {
        acc.push({ date: dateKey, amount: curr.amount });
      }
      return acc;
    }, []);

    // Sort by date ascending
    chartData.sort((a, b) => new Date(a.date) - new Date(b.date));
  }

  if (activeFilter === "date") {
    // Group by category
    chartData = dataList.reduce((acc, curr) => {
      const categoryKey = `${curr.icon ?? ""} ${curr.category}`;
      const existing = acc.find((item) => item.category === categoryKey);
      if (existing) {
        existing.amount += curr.amount;
      } else {
        acc.push({ category: categoryKey.trim(), amount: curr.amount });
      }
      return acc;
    }, []);
  }

  return (
    <div className="w-full h-[400px]">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={chartData}
          margin={{ top: 20, right: 30, left: 10, bottom: 10 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey={
              ["lastNDays", "month", "year"].includes(activeFilter)
                ? "date"
                : "category"
            }
            tickFormatter={(val) =>
              ["lastNDays", "month", "year"].includes(activeFilter)
                ? formatDate(val)
                : val
            }
          />
          <YAxis />
          <Tooltip
            formatter={(value) => [`₹${value.toFixed(2)}`, "Amount"]}
            labelFormatter={(val) =>
              ["lastNDays", "month", "year"].includes(activeFilter)
                ? formatDate(val)
                : val
            }
          />
          <Bar dataKey="amount" fill="#3b82f6" radius={[6, 6, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default BarChartComponent;
