// Recharts TrendChart component per Item 46

import React from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';

interface TrendChartProps {
  data: Array<{ date: string; [key: string]: any }>;
  lines: Array<{ key: string; color: string; name: string }>;
  height?: number;
}

export const TrendChart: React.FC<TrendChartProps> = ({ data, lines, height = 280 }) => {
  return (
    <div style={{ width: '100%', height }}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="date" stroke="#64748b" fontSize={12} tickLine={false} />
          <YAxis stroke="#64748b" fontSize={12} tickLine={false} />
          <Tooltip
            contentStyle={{ backgroundColor: '#ffffff', borderRadius: '6px', border: '1px solid #cbd5e1' }}
          />
          <Legend wrapperStyle={{ fontSize: '12px' }} />
          {lines.map((l) => (
            <Line
              key={l.key}
              type="monotone"
              dataKey={l.key}
              name={l.name}
              stroke={l.color}
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};
