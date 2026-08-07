import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import React from "react";

const PieChartComponent = ({
  data,
  centerTextHeading,
  centerTextValue,
  renderLegend = () => {},
}) => {
  return (
    <div className="relative w-[400px] h-[400px]">
      <PieChart width={400} height={400}>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          innerRadius={100}
          outerRadius={130}
          fill="#8884d8"
          paddingAngle={1}
          dataKey="value"
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${entry.name}`} fill={entry.color} />
          ))}
        </Pie>
        <Tooltip formatter={(value, name) => [`$${value}`, name]} />
        <Legend content={renderLegend} />
        {/* Center text inside SVG */}
        <text
          x="50%"
          y="42%"
          textAnchor="middle"
          dominantBaseline="middle"
          className="text-gray-900"
        >
          <tspan className="block text-lg" x="50%">
            {centerTextHeading}
          </tspan>
          <tspan className="block text-2xl font-semibold" x="50%" dy="1.4em">
            {centerTextValue}
          </tspan>
        </text>
      </PieChart>

      {/* Center overlay with Tailwind */}
      {/* <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
        <span className="text-gray-700 font-semibold text-lg">
          {centerTextHeading}
        </span>
        <span className="text-gray-900 font-bold text-2xl">
          {centerTextValue}
        </span>
      </div> */}
    </div>
  );
};

export default PieChartComponent;
